/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.SubscriptionFeedingService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientSummaryBase;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientSummaryBaseListResult;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientsWithResultCode;
import ru.axetta.ecafe.processor.web.partner.preorder.dataflow.*;
import ru.axetta.ecafe.processor.web.partner.preorder.soap.RegularPreorderItem;
import ru.axetta.ecafe.processor.web.partner.preorder.soap.RegularPreordersList;

import org.apache.cxf.common.util.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.*;

import static ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomControllerWS.processSummaryBase;


/**
 * Created by i.semenov on 05.03.2018.
 */
@Component
@Scope("singleton")
public class PreorderDAOService {
    private static final Logger logger = LoggerFactory.getLogger(PreorderDAOService.class);


    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;
    @PersistenceContext(unitName = "reportsPU")
    private EntityManager emReport;



    @Transactional
    public void saveToken(SudirToken token, ClientSummaryBaseListResult clientSummary) {
        em.merge(token);
        Set<SudirTokenClient> set = new HashSet<SudirTokenClient>();
        for (ClientSummaryBase clientSummaryBase : clientSummary.getClientSummary()) {
            Long contractId = clientSummaryBase.getContractId();
            SudirTokenClient stc = new SudirTokenClient(contractId, token.getAccess_token());
            em.merge(stc);
            set.add(stc);
        }
    }

    @Transactional(readOnly = true)
    public boolean matchToken(String token, Long contractId) {
        Query query = em.createQuery("select t from SudirTokenClient t where t.access_token = :token and t.contractId = :contractId");
        query.setParameter("token", token);
        query.setParameter("contractId", contractId);
        query.setMaxResults(1);
        try {
            SudirTokenClient tokenClient = (SudirTokenClient) query.getSingleResult();
            return true;
        } catch (Exception e) {
            logger.info(String.format("Attempt to access client data contractId=%s with token=%s", contractId, token));
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void setSpecialMenuFlag(Long contractId, Integer specialMenu) throws Exception {
        Query query = em.createQuery("update Client c set c.specialMenu = :specialMenu where c.contractId = :contractId");
        query.setParameter("specialMenu", specialMenu == 0 ? false : true);
        query.setParameter("contractId", contractId);
        int res = query.executeUpdate();
        if (res != 1) throw new Exception("Client not found");
    }

    public boolean isSpecialConfigDate(Client client, Date date) {
        Long idOfOrg = client.getOrg().getIdOfOrg();
        String prop = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.preorder.specialDates", "");
        if (StringUtils.isEmpty(prop)) return false;
        ObjectMapper mapper = new ObjectMapper();
        SpecialConfigDates dates = null;
        try {
            dates = mapper.readValue(prop, SpecialConfigDates.class);

            for (SpecialConfigDate dateConfig : dates.getSpecialConfigDateList()) {
                if (idOfOrg.equals(dateConfig.getIdOfOrg())) {
                    for (String strDate : dateConfig.getDates()) {
                        if (CalendarUtils.parseDate(strDate).equals(date)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Can't get special config dates: ", e);
            return false;
        }

        return false;
    }

    @Transactional(readOnly = true)
    public PreorderListWithComplexesGroupResult getPreorderComplexesWithMenuListWithGoodsParams(Long contractId, Date date,
            Long goodType, Long ageGroup) {
        PreorderListWithComplexesGroupResult groupResult = new PreorderListWithComplexesGroupResult();
        Client client = getClientByContractId(contractId);
        if (isSpecialConfigDate(client, date)) {
            List<PreorderComplexGroup> groupList = new ArrayList<PreorderComplexGroup>();
            groupResult.setComplexesWithGroups(groupList);
            return groupResult;
        }
        Set<CategoryDiscount> clientDiscounts = client.getCategories();
        Boolean hasDiscount = false;
        for (CategoryDiscount categoryDiscount : clientDiscounts) {
            hasDiscount |= (categoryDiscount.getCategoryType() == CategoryDiscountEnumType.CATEGORY_WITH_DISCOUNT);
        }

        Query query = emReport.createNativeQuery("select ci.idofcomplexinfo, pc.amount, pc.deletedState, pc.state, pc.idofregularpreorder "
                + " from cf_complexinfo ci join cf_orgs o on o.idoforg = ci.idoforg "
                + " join cf_goods as g on ci.idofgood = g.idofgood"
                + " left outer join cf_preorder_complex pc on (ci.idoforg = :idOfOrg and pc.idOfClient = :idOfClient and ci.menudate = pc.preorderdate and ci.idofcomplex = pc.armcomplexid) "
                + " where ci.MenuDate between :startDate and :endDate "
                + " and g.goodtype = :goodType and g.ageGroup = :ageGroup"
                + " and (ci.UsedSpecialMenu=1 or ci.ModeFree=1) and ci.idoforg = :idOfOrg "
                + " and (o.OrganizationType = :school or o.OrganizationType = :professional) and ci.modevisible = 1 and (pc.deletedstate is null or pc.deletedstate = 0) order by ci.modeOfAdd");
        query.setParameter("idOfClient", client.getIdOfClient());
        query.setParameter("startDate", CalendarUtils.startOfDay(date).getTime());
        query.setParameter("endDate", CalendarUtils.endOfDay(date).getTime());
        query.setParameter("idOfOrg", client.getOrg().getIdOfOrg());
        query.setParameter("school", OrganizationType.SCHOOL.getCode());
        query.setParameter("professional", OrganizationType.PROFESSIONAL.getCode());
        query.setParameter("goodType", goodType);
        query.setParameter("ageGroup", ageGroup);
        Map<String, PreorderComplexGroup> groupMap = new HashMap<String, PreorderComplexGroup>();
        List res = query.getResultList();
        List<PreorderComplexItemExt> list = new ArrayList<PreorderComplexItemExt>();
        for (Object o : res) {
            Object[] row = (Object[]) o;
            Long id = ((BigInteger)row[0]).longValue();
            ComplexInfo ci = emReport.find(ComplexInfo.class, id);
            Integer amount = (Integer) row[1];
            Integer state = (Integer) row[3];
            Long idOfRegularPreorder = row[4] == null ? null : ((BigInteger)row[4]).longValue();
            PreorderComplexItemExt complexItemExt = new PreorderComplexItemExt(ci);
            complexItemExt.setAmount(amount == null ? 0 : amount);
            complexItemExt.setState(state == null ? 0 : state);
            complexItemExt.setIsRegular(idOfRegularPreorder == null ? false : true);

            List<PreorderMenuItemExt> menuItemExtList = getMenuItemsExt(ci.getIdOfComplexInfo(), client.getIdOfClient(), date);
            if (menuItemExtList.size() > 0) {
                complexItemExt.setMenuItemExtList(menuItemExtList);
                list.add(complexItemExt);
            }
        }
        for (PreorderComplexItemExt item : list) {
            if (isAcceptableComplex(item, client, hasDiscount)) {
                String groupName = getPreorderComplexGroup(item);
                if (groupName == null) continue;
                item.setType(getPreorderComplexSubgroup(item));
                PreorderComplexGroup group = groupMap.get(groupName);
                if (group == null) {
                    group = new PreorderComplexGroup(groupName);
                    groupMap.put(groupName, group);
                }
                group.addItem(item);
            }
        }
        List<PreorderComplexGroup> groupList = new ArrayList<PreorderComplexGroup>(groupMap.values());
        for (PreorderComplexGroup group : groupList) {
            Collections.sort(group.getItems());
        }
        Collections.sort(groupList);
        groupResult.setComplexesWithGroups(groupList);

        return groupResult;
    }

    @Transactional(readOnly = true)
    public PreorderListWithComplexesGroupResult getPreorderComplexesWithMenuList(Long contractId, Date date) {
        PreorderListWithComplexesGroupResult groupResult = new PreorderListWithComplexesGroupResult();
        Client client = getClientByContractId(contractId);
        if (isSpecialConfigDate(client, date)) {
            List<PreorderComplexGroup> groupList = new ArrayList<PreorderComplexGroup>();
            groupResult.setComplexesWithGroups(groupList);
            return groupResult;
        }
        Set<CategoryDiscount> clientDiscounts = client.getCategories();
        Boolean hasDiscount = false;
        for (CategoryDiscount categoryDiscount : clientDiscounts) {
            hasDiscount |= (categoryDiscount.getCategoryType() == CategoryDiscountEnumType.CATEGORY_WITH_DISCOUNT);
        }

        Query query = emReport.createNativeQuery("select ci.idofcomplexinfo, pc.amount, pc.deletedState, pc.state, pc.idofregularpreorder "
                + " from cf_complexinfo ci join cf_orgs o on o.idoforg = ci.idoforg "
                + " left outer join cf_preorder_complex pc on (ci.idoforg = :idOfOrg and pc.idOfClient = :idOfClient and ci.menudate = pc.preorderdate and ci.idofcomplex = pc.armcomplexid) "
                + " where ci.MenuDate between :startDate and :endDate "
                + " and (ci.UsedSpecialMenu=1 or ci.ModeFree=1) and ci.idoforg = :idOfOrg "
                + " and (o.OrganizationType = :school or o.OrganizationType = :professional) and ci.modevisible = 1 and (pc.deletedstate is null or pc.deletedstate = 0) order by ci.modeOfAdd");
        query.setParameter("idOfClient", client.getIdOfClient());
        query.setParameter("startDate", CalendarUtils.startOfDay(date).getTime());
        query.setParameter("endDate", CalendarUtils.endOfDay(date).getTime());
        query.setParameter("idOfOrg", client.getOrg().getIdOfOrg());
        query.setParameter("school", OrganizationType.SCHOOL.getCode());
        query.setParameter("professional", OrganizationType.PROFESSIONAL.getCode());
        Map<String, PreorderComplexGroup> groupMap = new HashMap<String, PreorderComplexGroup>();
        List res = query.getResultList();
        List<PreorderComplexItemExt> list = new ArrayList<PreorderComplexItemExt>();
        for (Object o : res) {
            Object[] row = (Object[]) o;
            Long id = ((BigInteger)row[0]).longValue();
            ComplexInfo ci = emReport.find(ComplexInfo.class, id);
            Integer amount = (Integer) row[1];
            Integer state = (Integer) row[3];
            Long idOfRegularPreorder = row[4] == null ? null : ((BigInteger)row[4]).longValue();
            PreorderComplexItemExt complexItemExt = new PreorderComplexItemExt(ci);
            complexItemExt.setAmount(amount == null ? 0 : amount);
            complexItemExt.setState(state == null ? 0 : state);
            complexItemExt.setIsRegular(idOfRegularPreorder == null ? false : true);

            List<PreorderMenuItemExt> menuItemExtList = getMenuItemsExt(ci.getIdOfComplexInfo(), client.getIdOfClient(), date);
            if (menuItemExtList.size() > 0) {
                complexItemExt.setMenuItemExtList(menuItemExtList);
                list.add(complexItemExt);
            }
        }
        for (PreorderComplexItemExt item : list) {
            if (isAcceptableComplex(item, client, hasDiscount)) {
                String groupName = getPreorderComplexGroup(item);
                if (groupName == null) continue;
                item.setType(getPreorderComplexSubgroup(item));
                PreorderComplexGroup group = groupMap.get(groupName);
                if (group == null) {
                    group = new PreorderComplexGroup(groupName);
                    groupMap.put(groupName, group);
                }
                group.addItem(item);
            }
        }
        List<PreorderComplexGroup> groupList = new ArrayList<PreorderComplexGroup>(groupMap.values());
        for (PreorderComplexGroup group : groupList) {
            Collections.sort(group.getItems());
        }
        Collections.sort(groupList);
        groupResult.setComplexesWithGroups(groupList);

        return groupResult;
    }

    public RegularPreordersList getRegularPreordersList(Long contractId) {
        RegularPreordersList rp = new RegularPreordersList();
        Query regularsQuery = emReport.createQuery("select r from RegularPreorder r where r.client.contractId = :contractId and r.deletedState = false");
        regularsQuery.setParameter("contractId", contractId);
        List<RegularPreorder> reglist = regularsQuery.getResultList();
        if (reglist.size() > 0) {
            List<RegularPreorderItem> items = new ArrayList<RegularPreorderItem>();
            for (RegularPreorder regularPreorder : reglist) {
                RegularPreorderItem item = new RegularPreorderItem(regularPreorder);
                items.add(item);
            }
            rp.setRegularPreorders(items);
        }
        return rp;
    }

    private String getPreorderComplexGroup(PreorderComplexItemExt item) {
        if (match(item, "завтрак")) {
            return "Завтрак";
        }
        if (match(item, "обед")) {
            return "Обед";
        }
        if (match(item, "полдник")) {
            return "Полдник";
        }
        if (match(item, "ужин")) {
            return "Ужин";
        }
        return null;
    }

    private String getPreorderComplexSubgroup(PreorderComplexItemExt item) {
        if (item.getDiscount()) {
            return "За счет средств бюджета города Москвы";
        } else {
            return "За счет средств представителей обучающихся";
        }
    }

    private boolean match(PreorderComplexItemExt item, String str) {
        return item.getComplexName().toLowerCase().indexOf(str) > -1;
    }

    private List<PreorderMenuItemExt> getMenuItemsExt (Long idOfComplexInfo, Long idOfClient, Date date) {
        List<PreorderMenuItemExt> menuItemExtList = new ArrayList<PreorderMenuItemExt>();
        Query query = emReport.createNativeQuery("SELECT md.idofmenudetail, "
                + "(SELECT pmd.amount FROM cf_preorder_menudetail pmd WHERE pmd.idofclient = :idOfClient "
                   + "AND pmd.preorderdate BETWEEN :startDate AND :endDate AND pmd.armidofmenu = md.localidofmenu and pmd.deletedstate = 0) AS amount, "
                + "(SELECT pmd.idofregularpreorder FROM cf_preorder_menudetail pmd WHERE pmd.idofclient = :idOfClient "
                    + "AND pmd.preorderdate BETWEEN :startDate AND :endDate AND pmd.armidofmenu = md.localidofmenu) AS idofregularpreorder, "
                + "(SELECT pmd.state FROM cf_preorder_menudetail pmd WHERE pmd.idofclient = :idOfClient "
                + "AND pmd.preorderdate BETWEEN :startDate AND :endDate AND pmd.armidofmenu = md.localidofmenu and pmd.deletedstate = 0) AS state "
                + "FROM CF_MenuDetails md INNER JOIN CF_ComplexInfoDetail cid ON cid.IdOfMenuDetail = md.IdOfMenuDetail "
                + "WHERE cid.IdOfComplexInfo = :idOfComplexInfo");

        query.setParameter("idOfComplexInfo", idOfComplexInfo);
        query.setParameter("idOfClient", idOfClient);
        query.setParameter("startDate", CalendarUtils.startOfDay(date).getTime());
        query.setParameter("endDate", CalendarUtils.endOfDay(date).getTime());
        List res = query.getResultList();
        for (Object o : res) {
            Object[] row = (Object[]) o;
            Long id = ((BigInteger)row[0]).longValue();
            MenuDetail menuDetail = emReport.find(MenuDetail.class, id);
            Integer amount = (Integer) row[1];
            Long idOfRegularPreorder = row[2] == null ? null : ((BigInteger)row[2]).longValue();
            Integer state = (Integer) row[2];
            PreorderMenuItemExt menuItemExt = new PreorderMenuItemExt(menuDetail);
            menuItemExt.setAmount(amount == null ? 0 : amount);
            menuItemExt.setState(state == null ? 0 : state);
            menuItemExt.setIsRegular(idOfRegularPreorder == null ? false : true);
            menuItemExtList.add(menuItemExt);
        }
        return menuItemExtList;
    }

    public Client getClientByContractId(Long contractId) {
        Query query = emReport.createQuery("select c from Client c where c.contractId = :contractId");
        query.setParameter("contractId", contractId);
        return (Client)query.getSingleResult();
    }

    @Transactional(rollbackFor = Exception.class)
    public void savePreorderComplexes(PreorderSaveListParam list) throws Exception {
        Long contractId = list.getContractId();
        Client client = getClientByContractId(contractId);
        Date date = CalendarUtils.parseDate(list.getDate());
        Date startDate = CalendarUtils.startOfDay(date);
        Date endDate = CalendarUtils.endOfDay(date);
        long nextVersion = nextVersionByPreorderComplex();

        Query queryComplexSelect = em.createQuery("select p from PreorderComplex p "
                + "where p.client.idOfClient = :idOfClient and p.armComplexId = :idOfComplexInfo "
                + "and p.preorderDate between :startDate and :endDate and p.deletedState = false");
        queryComplexSelect.setParameter("idOfClient", client.getIdOfClient());
        queryComplexSelect.setParameter("startDate", startDate);
        queryComplexSelect.setParameter("endDate", endDate);

        Query queryMenuSelect = em.createQuery("select m from PreorderMenuDetail m "
                + "where m.client.idOfClient = :idOfClient and m.preorderComplex.idOfPreorderComplex = :idOfPreorderComplex "
                + "and m.armIdOfMenu = :armIdOfMenu and m.deletedState = false");
        queryMenuSelect.setParameter("idOfClient", client.getIdOfClient());

        for (ComplexListParam complex : list.getComplexes()) {
            Integer complexAmount = complex.getAmount();
            Integer idOfComplex = complex.getIdOfComplex();
            boolean complexSelected = (complexAmount > 0);
            for (MenuItemParam menuItem : complex.getMenuItems()) {
                if (menuItem.getAmount() > 0) {
                    complexSelected = true;
                    break;
                }
            }
            RegularPreorderParam regularComplex = complex.getRegularComplex();
            if (regularComplex != null) {
                if (regularComplex.getEnabled()) {
                    createRegularPreorder(client, regularComplex, complex.getAmount(),
                            complex.getIdOfComplex(), date, true, null, null);
                } else {
                    deleteRegularPreorder(client, complex.getIdOfComplex(), true, null, date);
                }
                continue;
            }

            queryComplexSelect.setParameter("idOfComplexInfo", idOfComplex);
            PreorderComplex preorderComplex = null;
            try {
                preorderComplex = (PreorderComplex) queryComplexSelect.getSingleResult();
                preorderComplex.setAmount(complex.getAmount());
                preorderComplex.setLastUpdate(new Date());
                preorderComplex.setDeletedState(!complexSelected);
                preorderComplex.setVersion(nextVersion);
                //em.merge(preorderComplex);
            } catch (NoResultException e ) {
                if (complexSelected) {
                    preorderComplex = createPreorderComplex(idOfComplex, client, date, complexAmount, null, nextVersion);
                    //em.persist(preorderComplex);
                }
            }

            if (preorderComplex == null) continue;

            Set<PreorderMenuDetail> set = new HashSet<PreorderMenuDetail>();
            for (MenuItemParam menuItem : complex.getMenuItems()) {
                RegularPreorderParam regularMenuItem = menuItem.getRegularMenuDetail();
                if (regularMenuItem != null) {
                    if (regularMenuItem.getEnabled()) {
                        createRegularPreorder(client, regularMenuItem, menuItem.getAmount(),
                                null, date, false, menuItem.getIdOfMenuDetail(), preorderComplex);
                    } else {
                        deleteRegularPreorder(client, null, false, menuItem.getIdOfMenuDetail(), date);
                    }
                    continue;
                }
                queryMenuSelect.setParameter("idOfPreorderComplex", preorderComplex.getIdOfPreorderComplex());
                queryMenuSelect.setParameter("armIdOfMenu",menuItem.getIdOfMenuDetail());
                boolean menuSelected = (menuItem.getAmount() > 0);
                PreorderMenuDetail preorderMenuDetail;
                try {
                    preorderMenuDetail = (PreorderMenuDetail)queryMenuSelect.getSingleResult();
                    preorderMenuDetail.setAmount(menuItem.getAmount());
                    preorderMenuDetail.setDeletedState(!menuSelected);
                    em.merge(preorderMenuDetail);
                } catch (NoResultException e) {
                    preorderMenuDetail = createPreorderMenuDetail(client, preorderComplex, null, date, menuItem.getIdOfMenuDetail(), menuItem.getAmount());
                }
                set.add(preorderMenuDetail);
            }
            preorderComplex.setPreorderMenuDetails(set);
            em.merge(preorderComplex);
        }
    }

    private void createRegularPreorder(Client client, RegularPreorderParam regularComplex,
            Integer amount, Integer idOfComplex, Date date, boolean isComplex, Long idOfMenu, PreorderComplex preorderComplex) throws Exception {
        String menuDetailName = null;
        Long menuDetailPrice = null;
        String itemCode = null;
        String condition = isComplex ? " and m.idOfComplex = :idOfComplex " : " and m.itemCode = :itemCode ";
        Query query = em.createQuery("select m from RegularPreorder m "
                + "where m.client = :client " + condition + " and m.deletedState = false");
        query.setParameter("client", client);
        if (isComplex)
            query.setParameter("idOfComplex", idOfComplex);
        else {
            MenuDetail md = getMenuDetail(client, idOfMenu, date);
            menuDetailName = md.getMenuDetailName();
            menuDetailPrice = md.getPrice();
            itemCode = md.getItemCode();
            query.setParameter("itemCode", itemCode);
        }
        RegularPreorder regularPreorder = null;
        try {
            regularPreorder = (RegularPreorder) query.getSingleResult();
            regularPreorder.setMonday(regularComplex.getMonday());
            regularPreorder.setTuesday(regularComplex.getTuesday());
            regularPreorder.setWednesday(regularComplex.getWednesday());
            regularPreorder.setThursday(regularComplex.getThursday());
            regularPreorder.setFriday(regularComplex.getFriday());
            regularPreorder.setSaturday(regularComplex.getSaturday());
            regularPreorder.setStartDate(regularComplex.getStartDate());
            regularPreorder.setEndDate(regularComplex.getEndDate());
            regularPreorder.setLastUpdate(new Date());
            em.merge(regularPreorder);
        } catch (NoResultException e) {
            if (isComplex) {
                ComplexInfo ci = getComplexInfo(client, idOfComplex, date);
                String complexName = null;
                Long complexPrice = null;
                if (ci != null) {
                    complexName = ci.getComplexName();
                    complexPrice = ci.getCurrentPrice();
                }
                regularPreorder = new RegularPreorder(client, regularComplex.getStartDate(), regularComplex.getEndDate(), null, idOfComplex,
                        amount, complexName, regularComplex.getMonday(), regularComplex.getTuesday(), regularComplex.getWednesday(),
                        regularComplex.getThursday(), regularComplex.getFriday(), regularComplex.getSaturday(), complexPrice);
                em.persist(regularPreorder);
            } else {
                regularPreorder = new RegularPreorder(client, regularComplex.getStartDate(), regularComplex.getEndDate(), itemCode, null,
                        amount, menuDetailName, regularComplex.getMonday(), regularComplex.getTuesday(), regularComplex.getWednesday(),
                        regularComplex.getThursday(), regularComplex.getFriday(), regularComplex.getSaturday(), menuDetailPrice);
                em.persist(regularPreorder);
            }
        }
        createPreordersFromRegular(regularPreorder, preorderComplex);
    }

    private void deleteRegularPreorder(Client client, Integer idOfComplex, boolean isComplex, Long idOfMenu, Date date) {
        String condition = isComplex ? " and m.idOfComplex = :idOfComplex " : " and m.itemCode = :itemCode ";
        Query regularPreorderSelect = em.createQuery("select m from RegularPreorder m "
                + "where m.client = :client " + condition + " and m.deletedState = false");
        regularPreorderSelect.setParameter("client", client);
        if (isComplex)
            regularPreorderSelect.setParameter("idOfComplex", idOfComplex);
        else {
            MenuDetail menuDetail = getMenuDetail(client, idOfMenu, date);
            regularPreorderSelect.setParameter("itemCode", menuDetail.getLocalIdOfMenu());
        }
        RegularPreorder regularPreorder = (RegularPreorder) regularPreorderSelect.getSingleResult();
        regularPreorder.setDeletedState(true);
        regularPreorder.setLastUpdate(new Date());
        em.merge(regularPreorder);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void createPreordersFromRegular(RegularPreorder regularPreorder, PreorderComplex parentPreorderComplex) throws Exception {
        //генерация предзаказов по ид. регулярного заказа
        //для блюд - по коду товара и цене
        //для комплексов - по ид. комплеса + цена.
        if (regularPreorder == null || regularPreorder.getDeletedState()) return;
        //проверка на даты: от текущего дня пропускаем дни запрета редактирвоания и генерируем на 2 недели вперед
        Date dateTo = CalendarUtils.addDays(new Date(), PreorderComplex.getDaysOfRegularPreorders()-1);
        if (dateTo.after(regularPreorder.getEndDate())) dateTo = regularPreorder.getEndDate();
        Date currentDate = CalendarUtils.startOfDay(new Date());
        long nextVersion = nextVersionByPreorderComplex();

        boolean isSixWorkWeek = DAOReadonlyService.getInstance().isSixWorkWeek(regularPreorder.getClient().getOrg().getIdOfOrg()); //шестидневка в целом по ОО
        List<SpecialDate> specialDates = DAOReadonlyService.getInstance().getSpecialDates(currentDate, dateTo, regularPreorder.getClient().getOrg().getIdOfOrg());//данные из производственного календаря за период
        Integer forbiddenDays = DAOUtils.getPreorderFeedingForbiddenDays(regularPreorder.getClient()); //настройка - количество дней запрета редактирования
        if (forbiddenDays == null) {
            forbiddenDays = PreorderComplex.DEFAULT_FORBIDDEN_DAYS;
        }
        currentDate = getStartDateForGeneratePreorders(currentDate, specialDates, forbiddenDays, isSixWorkWeek, regularPreorder);
        if (currentDate.before(regularPreorder.getStartDate())) currentDate = regularPreorder.getStartDate();

        while (currentDate.before(dateTo)) {

            boolean isWorkDate = getIsWorkDate(isSixWorkWeek, currentDate, specialDates, regularPreorder);

            boolean doGenerate = doGenerate(currentDate, regularPreorder);  //генерить ли предзаказ по дню недели в регулярном заказе
            if (!isWorkDate || !doGenerate) {
                if (!doGenerate) {
                    deletePreorders(regularPreorder, currentDate, nextVersion); //удаление предзаказа на день, если этого дня нет в расписании на неделю рег заказа
                }
                currentDate = CalendarUtils.addDays(currentDate, 1);
                continue;
            }

            if (regularPreorder.getIdOfComplex() != null) {
                //предзаказ на комплекс
                PreorderComplex preorderComplex = findPreorderComplex(currentDate, regularPreorder.getClient(),
                        regularPreorder.getIdOfComplex());
                if (preorderComplex == null) {
                    //на искомую дату нет предзаказа, надо создавать
                    ComplexInfo complexInfo = getComplexInfo(regularPreorder.getClient(), regularPreorder.getIdOfComplex(), currentDate); //комплекс на дату и с ценой рег. заказа
                    if (complexInfo == null || !complexInfo.getCurrentPrice().equals(regularPreorder.getPrice())) { //не найден комплекс или цена не совпадает с рег. заказом
                        currentDate = CalendarUtils.addDays(currentDate, 1);
                        continue;
                    }
                    preorderComplex = createPreorderComplex(regularPreorder.getIdOfComplex(), regularPreorder.getClient(),
                            complexInfo.getMenuDate(), regularPreorder.getAmount(), complexInfo, nextVersion);
                    preorderComplex.setRegularPreorder(regularPreorder);
                    em.persist(preorderComplex);
                }
            } else {
                //предзаказ на блюдо
                MenuDetail menuDetail = getMenuDetail(regularPreorder.getClient(), regularPreorder.getItemCode(), currentDate, regularPreorder.getPrice());
                if (menuDetail == null) {
                    currentDate = CalendarUtils.addDays(currentDate, 1);
                    continue;
                }
                PreorderMenuDetail preorderMenuDetail = findPreorderMenuDetail(currentDate, regularPreorder.getClient(), menuDetail.getLocalIdOfMenu());
                if (preorderMenuDetail == null) {
                    //на искомую дату нет предзаказа, надо создавать
                    preorderMenuDetail = createPreorderMenuDetail(regularPreorder.getClient(), parentPreorderComplex, menuDetail,
                            menuDetail.getMenu().getMenuDate(), menuDetail.getLocalIdOfMenu(), regularPreorder.getAmount());
                    preorderMenuDetail.setRegularPreorder(regularPreorder);
                    em.persist(preorderMenuDetail);
                }
            }
            currentDate = CalendarUtils.addDays(currentDate, 1);
        }
    }

    private Date getStartDateForGeneratePreorders(Date currentDate, List<SpecialDate> specialDates, int forbiddenDays, boolean isSixWorkWeek, RegularPreorder regularPreorder) {
        int i = 0;
        Date result = CalendarUtils.addDays(currentDate, 1);
        while (i < forbiddenDays) {
            boolean isWorkDate = getIsWorkDate(isSixWorkWeek, result, specialDates, regularPreorder);
            if (isWorkDate) i++;
            result = CalendarUtils.addDays(result, 1);
        }
        return result;
    }

    private boolean getIsWorkDate(boolean isSixWorkWeek, Date currentDate, List<SpecialDate> specialDates, RegularPreorder regularPreorder) {
        boolean isWorkDate = CalendarUtils.isWorkDateWithoutParser(isSixWorkWeek, currentDate); //без учета проиводственного календаря
        for (SpecialDate specialDate : specialDates) {
            if (specialDate.getDate().equals(currentDate)) {
                if (specialDate.getIdOfClientGroup().equals(regularPreorder.getClient().getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup())) {
                    isWorkDate = !specialDate.getIsWeekend();
                    break; //нашли в таблице календаря запись по группе клиента - выходим
                }
                isWorkDate = !specialDate.getIsWeekend(); //нашли в таблице календаря запись по ОО клиента
            }
        }
        return isWorkDate;
    }

    private void deletePreorders(RegularPreorder regularPreorder, Date currentDate, Long nextVersion) {
        Query query = em.createQuery("update PreorderComplex pc set pc.deletedState = true, pc.lastUpdate = :lastUpdate, pc.version = :version "
                + "where preorderDate between :startDate and :endDate and regularPreorder = :regularPreorder");
        query.setParameter("lastUpdate", new Date());
        query.setParameter("version", nextVersion);
        query.setParameter("startDate", CalendarUtils.startOfDay(currentDate));
        query.setParameter("endDate", CalendarUtils.endOfDay(currentDate));
        query.setParameter("regularPreorder", regularPreorder);
        query.executeUpdate();
    }

    private boolean doGenerate(Date date, RegularPreorder regularPreorder) {
        boolean doGenerate = false;
        int dayOfWeek = CalendarUtils.getDayOfWeek(date);
        if (dayOfWeek == 2 && regularPreorder.getMonday()) doGenerate = true;
        if (dayOfWeek == 3 && regularPreorder.getTuesday()) doGenerate = true;
        if (dayOfWeek == 4 && regularPreorder.getWednesday()) doGenerate = true;
        if (dayOfWeek == 5 && regularPreorder.getThursday()) doGenerate = true;
        if (dayOfWeek == 6 && regularPreorder.getFriday()) doGenerate = true;
        if (dayOfWeek == 7 && regularPreorder.getSaturday()) doGenerate = true;
        return doGenerate;
    }

    private PreorderComplex findPreorderComplex(Date date, Client client, Integer idOfComplex) {
        Query query = em.createQuery("select pc from PreorderComplex pc "
                + "where pc.client = :client and pc.preorderDate between :startDate and :endDate and pc.armComplexId = :idOfComplex and pc.deletedState = false");
        query.setParameter("client", client);
        query.setParameter("startDate", CalendarUtils.startOfDay(date));
        query.setParameter("endDate", CalendarUtils.endOfDay(date));
        query.setParameter("idOfComplex", idOfComplex);
        List<PreorderComplex> list = query.getResultList();
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    private PreorderMenuDetail findPreorderMenuDetail(Date date, Client client, Long armIdOfMenu) {
        Query query = em.createQuery("select pmd from PreorderMenuDetail pmd "
                + "where pmd.client = :client and pmd.preorderDate between :startDate and :endDate and pmd.armIdOfMenu = :armIdOfMenu and pmd.deletedState = false");
        query.setParameter("client", client);
        query.setParameter("startDate", CalendarUtils.startOfDay(date));
        query.setParameter("endDate", CalendarUtils.endOfDay(date));
        query.setParameter("armIdOfMenu", armIdOfMenu);
        List<PreorderMenuDetail> list = query.getResultList();
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    private PreorderComplex createPreorderComplex(Integer idOfComplex, Client client, Date date, Integer complexAmount, ComplexInfo ci, Long nextVersion) {
        PreorderComplex preorderComplex = new PreorderComplex();
        preorderComplex.setClient(client);
        preorderComplex.setPreorderDate(date);
        preorderComplex.setAmount(complexAmount);
        preorderComplex.setVersion(nextVersion);
        preorderComplex.setDeletedState(false);
        preorderComplex.setGuid(UUID.randomUUID().toString());
        preorderComplex.setUsedSum(0L);
        preorderComplex.setUsedAmount(0L);
        preorderComplex.setArmComplexId(idOfComplex);
        preorderComplex.setCreatedDate(new Date());
        preorderComplex.setLastUpdate(new Date());
        preorderComplex.setState(PreorderState.OK);
        if (ci == null) ci = getComplexInfo(client, idOfComplex, date);
        if (ci != null) {
            preorderComplex.setComplexName(ci.getComplexName());
            preorderComplex.setComplexPrice(ci.getCurrentPrice());
        }
        return preorderComplex;
    }

    private PreorderMenuDetail createPreorderMenuDetail(Client client, PreorderComplex preorderComplex, MenuDetail md, Date date, Long idOfMenu, Integer amount) {
        PreorderMenuDetail preorderMenuDetail = new PreorderMenuDetail();
        preorderMenuDetail.setPreorderComplex(preorderComplex);
        preorderMenuDetail.setArmIdOfMenu(idOfMenu);
        preorderMenuDetail.setClient(client);
        preorderMenuDetail.setPreorderDate(date);
        preorderMenuDetail.setAmount(amount);
        preorderMenuDetail.setDeletedState(false);
        preorderMenuDetail.setState(PreorderState.OK);
        if (md == null) md = getMenuDetail(client, idOfMenu, date);
        if (md != null) {
            preorderMenuDetail.setMenuDetailName(md.getMenuDetailName());
            preorderMenuDetail.setMenuDetailPrice(md.getPrice());
            preorderMenuDetail.setItemCode(md.getItemCode());
        }
        return preorderMenuDetail;
    }

    private ComplexInfo getComplexInfo(Client client, Integer idOfComplex, Date date) {
        Query query = emReport.createQuery("select ci from ComplexInfo ci where ci.org.idOfOrg = :idOfOrg "
                + "and ci.idOfComplex = :idOfComplex and ci.menuDate between :startDate and :endDate");
        query.setParameter("idOfOrg", client.getOrg().getIdOfOrg());
        query.setParameter("idOfComplex", idOfComplex);
        query.setParameter("startDate", CalendarUtils.startOfDay(date));
        query.setParameter("endDate", CalendarUtils.endOfDay(date));
        try {
            return (ComplexInfo)query.getSingleResult();
        } catch (Exception e) {
            logger.error(String.format("Cant find complexInfo idOfComplex=%s, date=%s", idOfComplex, date.getTime()));
            return null;
        }
    }

    private MenuDetail getMenuDetail(Client client, Long idOfMenu, Date date) {
        Query query = emReport.createQuery("select md from MenuDetail md where md.menu.org.idOfOrg = :idOfOrg "
                + "and md.localIdOfMenu = :idOfMenu and md.menu.menuDate = :menuDate");
        query.setParameter("idOfOrg", client.getOrg().getIdOfOrg());
        query.setParameter("idOfMenu", idOfMenu);
        query.setParameter("menuDate", date);
        try {
            return (MenuDetail)query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private MenuDetail getMenuDetail(Client client, String itemCode, Date date, Long price) {
        Query query = emReport.createQuery("select md from MenuDetail md where md.menu.org.idOfOrg = :idOfOrg "
                + "and md.itemCode = :itemCode and md.menu.menuDate between :startDate and :endDate and md.price = :price");
        query.setParameter("idOfOrg", client.getOrg().getIdOfOrg());
        query.setParameter("itemCode", itemCode);
        query.setParameter("startDate", CalendarUtils.startOfDay(date));
        query.setParameter("endDate", CalendarUtils.endOfDay(date));
        query.setParameter("price", price);
        try {
            return (MenuDetail)query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public Long getPreordersSum(Client client, Date startDate, Date endDate) {
        Query query = emReport.createQuery("select pc, ci from PreorderComplex pc, ComplexInfo ci "
                + "where pc.client.idOfClient = :idOfClient and pc.preorderDate between :startDate and :endDate "
                + "and ci.menuDate between :startDate and :endDate "
                + "and ci.org.idOfOrg = :idOfOrg and pc.preorderDate = ci.menuDate and pc.armComplexId = ci.idOfComplex");
        query.setParameter("idOfClient", client.getIdOfClient());
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("idOfOrg", client.getOrg().getIdOfOrg());
        List list = query.getResultList();
        Long sum = 0L;
        Session session = (Session)emReport.getDelegate();
        //for (PreorderComplex complex : list) {
        for (Object obj : list) {
            Object[] row = (Object[]) obj;
            PreorderComplex complex = (PreorderComplex) row[0];
            ComplexInfo ci = (ComplexInfo) row[1];
            if (ci != null) {
                sum += ci.getCurrentPrice() * complex.getAmount() - complex.getUsedSum();
                for (PreorderMenuDetail pmd : complex.getPreorderMenuDetails()) {
                    MenuDetail menuDetail = DAOUtils.getPreorderMenuDetail(session, pmd);
                    if (menuDetail != null) {
                        sum += menuDetail.getPrice() * pmd.getAmount();
                    }
                }
            }
        }
        return client.getBalance() - sum;
    }

    public long nextVersionByPreorderComplex() {
        Session session = (Session)em.getDelegate();
        return DAOUtils.nextVersionByPreorderComplex(session);
    }

    @Transactional(readOnly = true)
    public Map<Date, Long> existPreordersByDate(Long idOfClient, Date startDate, Date endDate) {
        Map map = new HashMap<Date, Long>();
        Query query = emReport.createQuery("select sum(p.amount), p.preorderDate from PreorderComplex p "
                + "where p.client.idOfClient = :idOfClient and p.preorderDate between :startDate and :endDate "
                + "group by p.preorderDate");
        query.setParameter("idOfClient", idOfClient);
        query.setParameter("startDate", CalendarUtils.startOfDay(startDate));
        query.setParameter("endDate", CalendarUtils.endOfDay(endDate));
        List result = query.getResultList();
        if (result != null) {
            for (Object obj : result) {
                Object[] row = (Object[]) obj;
                map.put((Date)row[1], (Long)row[0]);
            }
        }

        query = emReport.createQuery("select sum(p.amount), p.preorderDate from PreorderMenuDetail p "
                + "where p.client.idOfClient = :idOfClient and p.preorderDate between :startDate and :endDate "
                + "group by p.preorderDate");
        query.setParameter("idOfClient", idOfClient);
        query.setParameter("startDate", CalendarUtils.startOfDay(startDate));
        query.setParameter("endDate", CalendarUtils.endOfDay(endDate));
        List result2 = query.getResultList();
        if (result2 != null) {
            for (Object obj : result2) {
                Object[] row = (Object[]) obj;
                Date date2 = (Date)row[1];
                Long amount = (Long)row[0];
                if (map.containsKey(date2)) {
                    amount = amount + (Long)map.get(date2);
                }
                map.put(date2, amount);
            }
        }
        return map;
    }

    private boolean isAcceptableComplex(PreorderComplexItemExt complex, Client client, Boolean hasDiscount) {
        if (client.getIdOfClientGroup() == null) return false;
        String clientGroupName = client.getClientGroup().getGroupName();
        if (!hasDiscount) {
            // 1.2.1.1. если навазвание группы начинается на 1-, 2-, 3-, 4-, то выводим следующие комплексы:
            if (clientGroupName.startsWith("1-") || clientGroupName.startsWith("2-") ||
                    clientGroupName.startsWith("3-") || clientGroupName.startsWith("4-")) {
                // а) комплексы с парамтером discount = true при условии наличия у них в названии "Завтрак" + "(1-4)"
                if (complex.getDiscount() && complex.getComplexName().toLowerCase().contains("завтрак")
                        && complex.getComplexName().contains("1-4") ||
                        // б) комплексы с параметром discount = false при условии наличия у них в названии "(1-4)"
                        !complex.getDiscount() && complex.getComplexName().contains("1-4") ||
                        // в) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                        !complex.getDiscount() && (!complex.getComplexName().contains("1-4") &&
                                !complex.getComplexName().contains("5-11"))) {
                    return true;
                }
            }
            // 1.2.1.2. если навазвание группы начинается на 5-, 6-, 7-, 8-, 9-, 10-, 11-, 12-, то выводим следующие комплексы:
            else if (clientGroupName.startsWith("5-") || clientGroupName.startsWith("6-") ||
                    clientGroupName.startsWith("7-") || clientGroupName.startsWith("8-") ||
                    clientGroupName.startsWith("9-") || clientGroupName.startsWith("10-") ||
                    clientGroupName.startsWith("11-") || clientGroupName.startsWith("12-")) {
                // а) комплексы с параметром discount = false при условии наличия у них в названии "(5-11)"
                if (!complex.getDiscount() && complex.getComplexName().contains("5-11") ||
                        // б) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                        !complex.getDiscount() && (!complex.getComplexName().contains("1-4") &&
                                !complex.getComplexName().contains("5-11"))) {
                    return true;
                }
            }
            // 1.2.1.3. если навазвание группы начинается на другие символы (отличные от двух предыдущих условий), то выводим следующие комплексы:
            else {
                // а) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                if (!complex.getDiscount() && (!complex.getComplexName().contains("4-11")
                        && !complex.getComplexName().contains("5-11"))) {
                    return true;
                }
            }
        } else {
            // 1.2.2.1. если навазвание группы начинается на 1-, 2-, 3-, 4-, то выводим следующие комплексы:
            if (clientGroupName.startsWith("1-") || clientGroupName.startsWith("2-") ||
                    clientGroupName.startsWith("3-") || clientGroupName.startsWith("4-")) {
                // а) комплексы с парамтером discount = true при условии наличия у них в названии "(1-4)"
                if (complex.getDiscount() && complex.getComplexName().contains("1-4") ||
                        // б) комплексы с параметром discount = false при условии наличия у них в названии "(1-4)"
                        !complex.getDiscount() && complex.getComplexName().contains("1-4") ||
                        // в) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                        !complex.getDiscount() && (!complex.getComplexName().contains("1-4") &&
                                !complex.getComplexName().contains("5-11"))) {
                    return true;
                }
            }
            // 1.2.2.2. если навазвание группы начинается на 5-, 6-, 7-, 8-, 9-, 10-, 11-, 12-, то выводим следующие комплексы:
            else if (clientGroupName.startsWith("5-") || clientGroupName.startsWith("6-") ||
                    clientGroupName.startsWith("7-") || clientGroupName.startsWith("8-") ||
                    clientGroupName.startsWith("9-") || clientGroupName.startsWith("10-") ||
                    clientGroupName.startsWith("11-") || clientGroupName.startsWith("12-")) {
                // а) комплексы с парамтером discount = true при условии наличия у них в названии "(5-11)"
                if (complex.getDiscount() && complex.getComplexName().contains("5-11") ||
                        // б) комплексы с параметром discount = false при условии наличия у них в названии "(5-11)"
                        !complex.getDiscount() && complex.getComplexName().contains("5-11") ||
                        // в) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                        !complex.getDiscount() && (!complex.getComplexName().contains("1-4") &&
                                !complex.getComplexName().contains("5-11"))) {
                    return true;
                }
            }
            // 1.2.2.3. если навазвание группы начинается на другие символы (отличные от двух предыдущих условий), то выводим следующие комплексы:
            else {
                // а) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                if (!complex.getDiscount() && (!complex.getComplexName().contains("(1-4)") &&
                        !complex.getComplexName().contains("(5-11)"))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Transactional
    public SubscriptionFeeding getClientSubscriptionFeeding(Client client) {
        Date date = new Date();
        Session session = (Session)emReport.getDelegate();
        return SubscriptionFeedingService
                .getInstance().getCurrentSubscriptionFeedingByClientToDay(session, client, date, null);
    }

    @Transactional(rollbackFor = Exception.class)
    public ClientSummaryBaseListResult getClientSummaryByGuardianMobileWithPreorderEnableFilter(String mobile) {
        ClientSummaryBaseListResult clientSummaryBaseListResult = new ClientSummaryBaseListResult();
        try {
            List<ClientSummaryBase> clientSummaries = new ArrayList<ClientSummaryBase>();
            ClientsWithResultCode cd = getClientsByGuardMobile(mobile);

            if (cd != null && cd.getClients() != null) {
                for (Map.Entry<Client, ClientCreatedFromType> entry : cd.getClients().entrySet()) {
                    if (entry.getValue() == null) continue;
                    ClientSummaryBase base = processSummaryBase(entry.getKey());
                    base.setGuardianCreatedWhere(entry.getValue().getValue());
                    if (base != null) {
                        clientSummaries.add(base);
                    }
                }
            }

            clientSummaryBaseListResult.setClientSummary(clientSummaries);
            clientSummaryBaseListResult.resultCode = cd.resultCode;
            clientSummaryBaseListResult.description = cd.description;

        } catch (Exception e) {
            logger.error("Error in getClientSummaryByGuardianMobileWithPreorderEnableFilter: ", e);
        }
        return clientSummaryBaseListResult;
    }

    public ClientsWithResultCode getClientsByGuardMobile(String mobile) {

        ClientsWithResultCode data = new ClientsWithResultCode();
        try {
            Map<Client, ClientCreatedFromType> clients = extractClientsFromGuardByGuardMobile(Client.checkAndConvertMobile(mobile));
            if (!clients.isEmpty()) {
                boolean onlyNotActiveCG = true;
                for (Map.Entry<Client, ClientCreatedFromType> entry : clients.entrySet()) {
                    if (entry.getValue() != null) {
                        onlyNotActiveCG = false;
                        break;
                    }
                }
                if (onlyNotActiveCG) {
                    data.description = "Связка не активна";
                } else {
                    data.setClients(clients);
                    data.description = "OK";
                }
            }
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            data.description = e.toString();
        }
        return data;
    }

    public Map<Client, ClientCreatedFromType> extractClientsFromGuardByGuardMobile(String guardMobile) throws Exception {
        Map<Client, ClientCreatedFromType> result = new HashMap<Client, ClientCreatedFromType>();
        String query = "select client.idOfClient from cf_clients client where (client.phone=:guardMobile or client.mobile=:guardMobile) "
                + "and client.IdOfClientGroup not in (:leaving, :deleted)"; //все клиенты с номером телефона
        Query q = emReport.createNativeQuery(query);
        q.setParameter("guardMobile", guardMobile);
        q.setParameter("leaving", ClientGroup.Predefined.CLIENT_LEAVING.getValue());
        q.setParameter("deleted", ClientGroup.Predefined.CLIENT_DELETED.getValue());
        List<BigInteger> clients = q.getResultList();

        if (clients != null && !clients.isEmpty()){
            for(BigInteger id : clients){
                Long londId = id.longValue();
                Query q2 = emReport.createQuery("select c, cg from ClientGuardian cg, Client c "
                        + "join c.org as o "
                        + "where cg.idOfChildren = c.idOfClient and cg.idOfGuardian = :idOfGuardian "
                        + "and cg.deletedState = false and o.preordersEnabled = true");  //все дети текущего клиента
                q2.setParameter("idOfGuardian", londId);
                List list = q2.getResultList();
                if (list != null && list.size() > 0) {
                    for (Object o : list) {
                        Object[] row = (Object[])o;
                        ClientGuardian cg = (ClientGuardian) row[1];
                        if (!cg.isDisabled()) {
                            result.put((Client) row[0], cg.getCreatedFrom());
                        } else {
                            result.put((Client) row[0], null);
                        }
                    }
                } else {
                    Query q3 = emReport.createQuery("select c from Client c "
                            + "join c.org as o "
                            + "join c.clientGroup g "
                            + "where c.idOfClient=:idOfClient and o.preordersEnabled = true "
                            + "and g.compositeIdOfClientGroup.idOfClientGroup < :employees");
                    q3.setParameter("idOfClient", londId);
                    q3.setParameter("employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
                    List list1 = q3.getResultList();
                    if (!list1.isEmpty()) {
                        Client c = (Client)list1.get(0);
                        result.put(c, ClientCreatedFromType.DEFAULT);
                    }
                }
            }
        }

        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Integer[]> getSpecialDates(Date today, Integer syncCountDays, Long orgId, Client client) throws Exception {
        Comparator comparator = new PreorderDateComparator();
        Map map = new TreeMap(comparator);
        TimeZone timeZone = RuntimeContext.getInstance().getLocalTimeZone(null);
        Calendar c = Calendar.getInstance();
        c.setTimeZone(timeZone);
        Date endDate = CalendarUtils.addDays(today, syncCountDays);
        boolean isSixWorkWeek = DAOReadonlyService.getInstance().isSixWorkWeek(orgId);
        int two_days = 0;
        Map<Date, Long> usedAmounts = existPreordersByDate(client.getIdOfClient(), today, endDate);
        List<SpecialDate> specialDates = DAOReadonlyService.getInstance().getSpecialDates(today, endDate, orgId);
        Integer forbiddenDays = DAOUtils.getPreorderFeedingForbiddenDays(client);
        if (forbiddenDays == null) {
            forbiddenDays = PreorderComplex.DEFAULT_FORBIDDEN_DAYS;
        }
        while (c.getTimeInMillis() < endDate.getTime() ){
            Date currentDate = CalendarUtils.parseDate(CalendarUtils.dateShortToStringFullYear(c.getTime()));
            if (two_days < forbiddenDays) {
                c.add(Calendar.DATE, 1);
                map.put(CalendarUtils.dateToString(currentDate), new Integer[] {1, usedAmounts.get(currentDate) == null ? 0 : usedAmounts.get(currentDate).intValue()});
                if (CalendarUtils.isWorkDateWithoutParser(isSixWorkWeek, currentDate)
                        && !isSpecialConfigDate(client, currentDate)) {
                    two_days++;
                }
                continue;
            }

            Boolean isWeekend = !CalendarUtils.isWorkDateWithoutParser(isSixWorkWeek, currentDate);
            if(specialDates != null){
                for (SpecialDate specialDate : specialDates) {
                    if (CalendarUtils.betweenOrEqualDate(specialDate.getDate(), currentDate, CalendarUtils.addDays(currentDate, 1)) && !specialDate.getDeleted()) {
                        isWeekend = specialDate.getIsWeekend();
                        break;
                    }
                }
            }
            int day = CalendarUtils.getDayOfWeek(currentDate);
            if (day == Calendar.SATURDAY && !isSixWorkWeek  && isWeekend) {
                //проверяем нет ли привязки отдельных групп к 6-ти дневной неделе
                isWeekend = DAOReadonlyService.getInstance().isWeekendByGroup(orgId, client);
            }

            c.add(Calendar.DATE, 1);
            map.put(CalendarUtils.dateToString(currentDate), new Integer[] {isWeekend ? 1 : 0, usedAmounts.get(currentDate) == null ? 0 : usedAmounts.get(currentDate).intValue()});
        }
        return map;
    }

    public List<ClientGuardian> getClientGuardian(Client child, String guardianMobile) {
        Query query = emReport.createQuery("select cg from ClientGuardian cg, Client g "
                + "where g.idOfClient = cg.idOfGuardian and cg.idOfChildren = :idOfChildren and g.mobile = :guardianMobile");
        query.setParameter("idOfChildren", child.getIdOfClient());
        query.setParameter("guardianMobile", guardianMobile);
        return query.getResultList();
    }

    @Transactional
    public Integer getMenuSyncCountDays(Long idOfOrg) {
        Org org = emReport.find(Org.class, idOfOrg);
        if (org.getConfigurationProvider() == null) {
            return PreorderComplex.DEFAULT_MENU_SYNC_COUNT_DAYS;
        } else {
            return org.getConfigurationProvider().getMenuSyncCountDays();
        }
    }

    public Org findOrg(long idOfOrg) {
        return emReport.find(Org.class, idOfOrg);
    }
}
