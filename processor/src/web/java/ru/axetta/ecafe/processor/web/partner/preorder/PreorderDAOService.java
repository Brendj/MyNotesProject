/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.IPreorderDAOOperations;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodAgeGroupType;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.PreorderUtils;
import ru.axetta.ecafe.processor.core.service.PreorderRequestsReportService;
import ru.axetta.ecafe.processor.core.service.PreorderRequestsReportServiceParam;
import ru.axetta.ecafe.processor.core.service.SubscriptionFeedingService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientSummaryBase;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientSummaryBaseListResult;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientWithAddInfo;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientsWithResultCode;
import ru.axetta.ecafe.processor.web.partner.preorder.dataflow.*;
import ru.axetta.ecafe.processor.web.partner.preorder.soap.RegularPreorderItem;
import ru.axetta.ecafe.processor.web.partner.preorder.soap.RegularPreordersList;

import org.apache.cxf.common.util.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.math.BigInteger;
import java.util.*;

import static ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomControllerWS.processSummaryBase;


/**
 * Created by i.semenov on 05.03.2018.
 */
@Component
@Scope("singleton")
@DependsOn({"daoService", "runtimeContext"})
public class PreorderDAOService {
    private static final Logger logger = LoggerFactory.getLogger(PreorderDAOService.class);
    private final String NEW_LINE_DELIMITER = ";";
    public static final long BASE_ID_MENU_VALUE_FOR_MODIFY = 7700000;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;
    @PersistenceContext(unitName = "reportsPU")
    private EntityManager emReport;

    @PostConstruct
    public void createPreorderDAOOperationsImpl() {
        IPreorderDAOOperations impl = new IPreorderDAOOperations() {
            @Override
            public void generatePreordersBySchedule(PreorderRequestsReportServiceParam params) {
                generatePreordersByScheduleInternal(params);
            }

            @Override
            public void relevancePreorders(PreorderRequestsReportServiceParam params) {
                RuntimeContext.getAppContext().getBean(PreorderOperationsService.class).relevancePreorders(params);
            }
        };
        RuntimeContext.getAppContext().getBean(DAOService.class).setPreorderDAOOperationsImpl(impl);
    }

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
            if(!categoryDiscount.getCategoryName().toLowerCase().contains("резерв")){
                hasDiscount |= (categoryDiscount.getCategoryType() == CategoryDiscountEnumType.CATEGORY_WITH_DISCOUNT);
            }
        }

        Query query = emReport.createNativeQuery("select ci.idofcomplexinfo, pc.amount, pc.deletedState, pc.state, pc.idofregularpreorder, "
                + "coalesce(pc.modeofadd, ci.modeofadd) as modeofadd, coalesce(pc.modefree, ci.modefree) as modefree, ci.idofcomplex, "
                + "coalesce(pc.complexname, ci.complexname) as complexname, "
                + "coalesce(pc.complexprice, ci.currentprice) as currentprice, pc.idofpreordercomplex "
                + " from cf_complexinfo ci join cf_orgs o on o.idoforg = ci.idoforg "
                + " left outer join (select * from cf_preorder_complex pc where pc.idofclient = :idOfClient "
                + " and pc.preorderdate between :startDate and :endDate and pc.deletedstate = 0) as pc on (ci.idoforg = :idOfOrg and ci.menudate = pc.preorderdate and ci.idofcomplex = pc.armcomplexid) "
                + " where ci.MenuDate between :startDate and :endDate "
                + " and (ci.UsedSpecialMenu=1 or ci.ModeFree=1) and ci.idoforg = :idOfOrg "
                + " and (o.OrganizationType = :school or o.OrganizationType = :professional) and ci.modevisible = 1 and (pc.deletedstate is null or pc.deletedstate = 0) "
                + "union "
                + "select cast(-1 as bigint) as idofcomplexinfo, pc.amount, pc.deletedState, pc.state, pc.idofregularpreorder, pc.modeofadd, pc.modefree, "
                + "pc.armcomplexid, pc.complexname, pc.complexprice, pc.idofpreordercomplex "
                + "from cf_preorder_complex pc where pc.idofclient = :idOfClient and pc.preorderdate between :startDate and :endDate and pc.deletedstate = 0 "
                + "and not exists (select idofcomplexinfo from cf_complexinfo ci2 where ci2.idoforg = :idOfOrg and ci2.menudate = pc.preorderdate and ci2.idofcomplex = pc.armcomplexid "
                + " and ci2.modevisible = 1 and (ci2.usedspecialmenu = 1 or ci2.modefree = 1)) "
                + "order by modeOfAdd");
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
            //ComplexInfo ci = emReport.find(ComplexInfo.class, id);
            Integer amount = (Integer) row[1];
            Integer state = (Integer) row[3];
            Long idOfRegularPreorder = row[4] == null ? null : ((BigInteger)row[4]).longValue();
            Integer modeOfAdd = (Integer) row[5];
            Integer modeFree = (Integer) row[6];
            Integer idOfComplex = (Integer) row[7];
            String complexName = (String) row[8];
            Long complexPrice = ((BigInteger)row[9]).longValue();
            Long idOfPreorderComplex = (row[10] == null ? null : ((BigInteger)row[10]).longValue());
            PreorderComplexItemExt complexItemExt = new PreorderComplexItemExt(idOfComplex, complexName, complexPrice, modeOfAdd, modeFree);
            complexItemExt.setAmount(amount == null ? 0 : amount);
            complexItemExt.setState(state == null ? 0 : state);
            complexItemExt.setIsRegular(idOfRegularPreorder == null ? false : true);

            List<PreorderMenuItemExt> menuItemExtList = getMenuItemsExt(id, client.getIdOfClient(), date, idOfPreorderComplex);
            if (menuItemExtList.size() > 0) {
                complexItemExt.setMenuItemExtList(menuItemExtList);
                list.add(complexItemExt);
            }
        }
        for (PreorderComplexItemExt item : list) {
            PreorderGoodParamsContainer complexParams = getComplexParams(item, client, date);
            if (isAcceptableComplex(item, client.getClientGroup(), hasDiscount, complexParams, null, null)) {
                String groupName = getPreorderComplexGroup(item, complexParams);
                if (groupName.isEmpty()) continue;
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

    public PreorderGoodParamsContainer getComplexParams(PreorderComplexItemExt item, Client client, Date date) {
        Integer goodTypeCode = GoodType.UNSPECIFIED.getCode();
        Integer ageGroupCode = GoodAgeGroupType.UNSPECIFIED.getCode();
        try {
            Query query = emReport.createNativeQuery("SELECT g.goodtype, g.agegroup "
                    + " FROM  cf_clients c "
                    + " INNER JOIN cf_complexinfo ci ON c.idoforg = ci.idoforg "
                    + " INNER JOIN cf_goods g ON ci.idofgood = g.idofgood "
                    + " WHERE ci.idofcomplex = :idOfComplex "
                    + " AND c.contractid = :contractID "
                    + " AND ci.complexname like :nameOfComplex "
                    + " AND ci.MenuDate BETWEEN :startDate AND :endDate ");
            query.setParameter("idOfComplex", item.getIdOfComplexInfo());
            query.setParameter("contractID", client.getContractId());
            query.setParameter("nameOfComplex", item.getComplexName());
            query.setParameter("startDate", CalendarUtils.startOfDay(date).getTime());
            query.setParameter("endDate", CalendarUtils.endOfDay(date).getTime());
            query.setMaxResults(1);
            Object[] result = (Object[]) query.getSingleResult();
            if (result.length != 0) {
                goodTypeCode = (Integer) result[0];
                ageGroupCode = (Integer) result[1];
            }
        } catch (Exception e){
            logger.debug("Can't get result from DB: " + e.getMessage());
        }
        return new PreorderGoodParamsContainer(goodTypeCode, ageGroupCode);
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

    private String getPreorderComplexGroup(PreorderComplexItemExt item, PreorderGoodParamsContainer container) {
        String groupName = "";
        if(!container.getGoodType().equals(GoodType.UNSPECIFIED.getCode())) {
            groupName = GoodType.fromInteger(container.getGoodType()).toString();
        } else {
            if (match(item, "завтрак")) {
                groupName = "Завтрак";
            }
            else if (match(item, "обед")) {
                groupName = "Обед";
            }
            else if (match(item, "полдник")) {
                groupName = "Полдник";
            }
            else if (match(item, "ужин")) {
                groupName = "Ужин";
            }
        }
        return groupName;
    }

    private String getPreorderComplexSubgroup(PreorderComplexItemExt item) {
        if (item.getDiscount()) {
            return "За счет средств бюджета города Москвы";
        } else {
            return "За счет родителей (представителей) обучающегося. " + NEW_LINE_DELIMITER
                 + "В случае, если вашему ребенку предоставляется льготное питание, то вы в дополнение можете заказать платное питание.";
        }
    }

    private boolean match(PreorderComplexItemExt item, String str) {
        return item.getComplexName().toLowerCase().indexOf(str) > -1;
    }

    private List<PreorderMenuItemExt> getMenuItemsExt (Long idOfComplexInfo, Long idOfClient, Date date, Long idOfPreorderComplex) {
        List<PreorderMenuItemExt> menuItemExtList = new ArrayList<PreorderMenuItemExt>();
        Query query = null;
        if (idOfPreorderComplex == null) {
            query = emReport.createNativeQuery("SELECT md.idofmenudetail, pmd.amount, pmd.idofregularpreorder, pmd.state, g.dailysale, pmd.idofpreordermenudetail "
                    + "FROM CF_MenuDetails md INNER JOIN CF_ComplexInfoDetail cid ON cid.IdOfMenuDetail = md.IdOfMenuDetail "
                    + "JOIN CF_Goods g ON md.IdOfGood = g.IdOfGood "
                    + "left join (SELECT pmd.amount, pmd.idofregularpreorder, pmd.state, pmd.armidofmenu, pmd.idofpreordermenudetail "
                    + "FROM cf_preorder_menudetail pmd WHERE pmd.idofclient = :idOfClient "
                    + "AND pmd.preorderdate BETWEEN :startDate AND :endDate AND pmd.deletedstate = 0) as pmd on pmd.armidofmenu = md.localidofmenu "
                    + "WHERE cid.IdOfComplexInfo = :idOfComplexInfo and md.itemcode is not null and md.itemcode <> ''");
            query.setParameter("idOfComplexInfo", idOfComplexInfo);
            query.setParameter("idOfClient", idOfClient);
            query.setParameter("startDate", CalendarUtils.startOfDay(date).getTime());
            query.setParameter("endDate", CalendarUtils.endOfDay(date).getTime());
        } else {
            query = emReport.createNativeQuery("select cast(-1 as bigint) as idofmenudetail, pmd.amount, pmd.idofregularpreorder, pmd.state, "
                    + "cast(0 as integer) as dailysale, pmd.idofpreordermenudetail "
                    + "from cf_preorder_menudetail pmd where pmd.idofpreordercomplex = :idOfPreorderComplex and pmd.deletedState = 0 " //and pmd.amount > 0 "
                    + "union "
                    + "select md.idofmenudetail, null, null, null, g.dailysale, null from cf_menudetails md INNER JOIN CF_ComplexInfoDetail cid ON cid.IdOfMenuDetail = md.IdOfMenuDetail "
                    + "JOIN CF_Goods g ON md.IdOfGood = g.IdOfGood "
                    + "WHERE cid.IdOfComplexInfo = :idOfComplexInfo and md.itemcode is not null and md.itemcode <> ''"
                    + "and not exists (select idofpreordermenudetail from cf_preorder_menudetail pmd2 "
                    + "where pmd2.armidofmenu = md.localidofmenu and pmd2.preorderdate between :startDate AND :endDate and pmd2.idofclient = :idOfClient and pmd2.deletedstate = 0 and pmd2.amount > 0) ");
            query.setParameter("idOfPreorderComplex", idOfPreorderComplex);
            query.setParameter("idOfComplexInfo", idOfComplexInfo);
            query.setParameter("idOfClient", idOfClient);
            query.setParameter("startDate", CalendarUtils.startOfDay(date).getTime());
            query.setParameter("endDate", CalendarUtils.endOfDay(date).getTime());
        }

        List res = query.getResultList();
        Set<Long> set = new HashSet<Long>();
        for (Object o : res) {
            Object[] row = (Object[]) o;
            Long id = ((BigInteger)row[0]).longValue();
            Integer amount = (Integer) row[1];
            Long idOfRegularPreorder = row[2] == null ? null : ((BigInteger)row[2]).longValue();
            Integer state = (Integer) row[3];
            Boolean isAvailableForRegular = (Integer) row[4] == 1;
            Long idOfPreorderMenuDetail = (row[5] == null ? null : ((BigInteger)row[5]).longValue());
            PreorderMenuItemExt menuItemExt = null;
            if (idOfPreorderMenuDetail == null) {
                MenuDetail menuDetail = emReport.find(MenuDetail.class, id);
                menuItemExt = new PreorderMenuItemExt(menuDetail);
            } else {
                PreorderMenuDetail pmd = em.find(PreorderMenuDetail.class, idOfPreorderMenuDetail);
                menuItemExt = new PreorderMenuItemExt(pmd);
            }
            menuItemExt.setAmount(amount == null ? 0 : amount);
            menuItemExt.setState(state == null ? 0 : state);
            menuItemExt.setIsRegular(idOfRegularPreorder == null ? false : true);
            menuItemExt.setAvailableForRegular(isAvailableForRegular);
            if (!set.contains(menuItemExt.getIdOfMenuDetail())) {
                menuItemExtList.add(menuItemExt);
                set.add(menuItemExt.getIdOfMenuDetail());
            }
        }
        return menuItemExtList;
    }

    public Client getClientByContractId(Long contractId) {
        try {
            Query query = emReport.createQuery("select c from Client c where c.contractId = :contractId");
            query.setParameter("contractId", contractId);
            return (Client) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isEditedDay(Date date, Client client) throws Exception {
        boolean result = false;
        Date today = CalendarUtils.startOfDay(new Date());
        Integer syncCountDays = PreorderComplex.getDaysOfRegularPreorders();
        Map<String, Integer[]> sd = getSpecialDates(CalendarUtils.addHours(today, 12), syncCountDays,
                client.getOrg().getIdOfOrg(), client);
        for (Map.Entry<String, Integer[]> entry : sd.entrySet()) {
            if (date.equals(CalendarUtils.parseDate(entry.getKey()))) {
                result = (entry.getValue())[0].equals(0);
                break;
            }
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public void savePreorderComplexes(PreorderSaveListParam list, String guardianMobile) throws Exception {
        //Собираем коллекцию в нужном виде
        Map<Integer, ComplexListParam> map = new HashMap<Integer, ComplexListParam>();
        for (ComplexListParam complex : list.getComplexes()) {
            Integer idOfComplex = complex.getIdOfComplex();
            ComplexListParam param = map.get(idOfComplex);
            if (param == null) {
                param = new ComplexListParam();
                param.setAmount(complex.getAmount());
                param.setIdOfComplex(complex.getIdOfComplex());
                param.setRegularComplex(complex.getRegularComplex());
                param.setMenuItems(complex.getMenuItems());
                map.put(complex.getIdOfComplex(), param);
            } else {
                if (param.getMenuItems() != null) {
                    param.getMenuItems().addAll(complex.getMenuItems());
                } else {
                    param.setMenuItems(complex.getMenuItems());
                }
            }
        }
        List<ComplexListParam> complexes = new ArrayList<ComplexListParam>();
        for (Map.Entry<Integer, ComplexListParam> entry : map.entrySet()) {
            ComplexListParam param = new ComplexListParam();
            param.setIdOfComplex(entry.getKey());
            param.setAmount(entry.getValue().getAmount());
            param.setRegularComplex(entry.getValue().getRegularComplex());
            param.setMenuItems(entry.getValue().getMenuItems());
            complexes.add(param);
        }
        list.setComplexes(complexes);
        //собрали

        Long contractId = list.getContractId();
        Client client = getClientByContractId(contractId);
        Date date = CalendarUtils.parseDate(list.getDate());
        Date startDate = CalendarUtils.startOfDay(date);
        Date endDate = CalendarUtils.endOfDay(date);
        long nextVersion = nextVersionByPreorderComplex();
        Session session = (Session)emReport.getDelegate();
        List<Client> clientsByMobile = PreorderUtils.getClientsByMobile(session, client.getIdOfClient(), guardianMobile);
        Integer value = PreorderUtils.getClientGroupResult(session, clientsByMobile);
        PreorderMobileGroupOnCreateType mobileGroupOnCreate;
        if (value >= PreorderUtils.SOAP_RC_CLIENT_NOT_FOUND) {
            mobileGroupOnCreate = PreorderMobileGroupOnCreateType.UNKNOWN;
        } else {
            mobileGroupOnCreate = PreorderMobileGroupOnCreateType.fromInteger(value);
            if (mobileGroupOnCreate.equals(PreorderMobileGroupOnCreateType.PARENT_EMPLOYEE)) {
                if (client.getMobile() != null && guardianMobile != null && client.getMobile().equals(guardianMobile)) {
                    mobileGroupOnCreate = PreorderMobileGroupOnCreateType.EMPLOYEE;
                } else {
                    mobileGroupOnCreate = PreorderMobileGroupOnCreateType.PARENT;
                }
            }
        }

        if (!isEditedDay(date, client)) throw new NotEditedDayException("День недоступен для редактирования предзаказа");

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
            boolean regularMenuItems = false;
            if (complex.getMenuItems() != null) {
                for (MenuItemParam menuItem : complex.getMenuItems()) {
                    if (menuItem.getAmount() > 0) {
                        complexSelected = true;
                    }
                    if (menuItem.getRegularMenuDetail() != null) {
                        regularMenuItems = true;
                    }
                }
            }
            RegularPreorderParam regularComplex = complex.getRegularComplex();
            if (regularComplex != null) {
                if (regularComplex.getEnabled() && complex.getAmount() > 0) {
                    createRegularPreorder(client, regularComplex, complex.getAmount(),
                            complex.getIdOfComplex(), date, true, null, guardianMobile, mobileGroupOnCreate);
                } else {
                    deleteRegularPreorder(client, complex.getIdOfComplex(), true, null, date, guardianMobile);
                }
                continue;
            }

            PreorderComplex preorderComplex = null;
            if (!regularMenuItems) {
                queryComplexSelect.setParameter("idOfComplexInfo", idOfComplex);
                try {
                    preorderComplex = (PreorderComplex) queryComplexSelect.getSingleResult();
                    if (!preorderComplex.getAmount().equals(complex.getAmount())) {
                        preorderComplex.setMobile(guardianMobile);
                        preorderComplex.setMobileGroupOnCreate(mobileGroupOnCreate);
                        updateMobileGroupOnCreateOnMenuDetails(preorderComplex, guardianMobile, mobileGroupOnCreate);
                    }
                    preorderComplex.setAmount(complex.getAmount());
                    preorderComplex.setLastUpdate(new Date());
                    preorderComplex.setDeletedState(!complexSelected);
                    preorderComplex.setVersion(nextVersion);
                } catch (NoResultException e) {
                    if (complexSelected) {
                        preorderComplex = createPreorderComplex(idOfComplex, client, date, complexAmount, null,
                                nextVersion, guardianMobile, mobileGroupOnCreate);
                        if (complex.getMenuItems() == null) {
                            //создаем детализацию предзаказа по блюдам меню, т.к. ее нет в запросе
                            Set<PreorderMenuDetail> set = createPreorderMenuDetails(idOfComplex, client, date, preorderComplex,
                                    guardianMobile, mobileGroupOnCreate);
                            preorderComplex.setPreorderMenuDetails(set);
                            em.merge(preorderComplex);
                            continue;
                        }
                    }
                }

                if (preorderComplex == null)
                    continue;
            }

            Set<PreorderMenuDetail> set = new HashSet<PreorderMenuDetail>();
            if (complex.getMenuItems() != null) {
                for (MenuItemParam menuItem : complex.getMenuItems()) {
                    RegularPreorderParam regularMenuItem = menuItem.getRegularMenuDetail();
                    if (regularMenuItem != null) {
                        if (regularMenuItem.getEnabled() && menuItem.getAmount() > 0) {
                            createRegularPreorder(client, regularMenuItem, menuItem.getAmount(), idOfComplex, date,
                                    false, menuItem.getIdOfMenuDetail(), guardianMobile, mobileGroupOnCreate);
                        } else {
                            deleteRegularPreorder(client, null, false, menuItem.getIdOfMenuDetail(), date, guardianMobile);
                        }
                        continue;
                    }
                    queryMenuSelect.setParameter("idOfPreorderComplex", preorderComplex.getIdOfPreorderComplex());
                    queryMenuSelect.setParameter("armIdOfMenu", menuItem.getIdOfMenuDetail());
                    boolean menuSelected = (menuItem.getAmount() > 0);
                    PreorderMenuDetail preorderMenuDetail;
                    try {
                        preorderMenuDetail = (PreorderMenuDetail) queryMenuSelect.getSingleResult();
                        if (!preorderMenuDetail.getAmount().equals(menuItem.getAmount())) {
                            preorderMenuDetail.setMobile(guardianMobile);
                            preorderMenuDetail.setMobileGroupOnCreate(mobileGroupOnCreate);
                        }
                        preorderMenuDetail.setAmount(menuItem.getAmount());
                        preorderMenuDetail.setDeletedState(!menuSelected);
                        em.merge(preorderMenuDetail);
                    } catch (NoResultException e) {
                        preorderMenuDetail = createPreorderMenuDetail(client, preorderComplex, null, date, menuItem.getIdOfMenuDetail(),
                                menuItem.getAmount(), guardianMobile, mobileGroupOnCreate);
                    }
                    set.add(preorderMenuDetail);
                }
            }
            if (preorderComplex != null) {
                if (set.size() > 0) {
                    preorderComplex.setPreorderMenuDetails(set);
                }
                if (preorderComplex.getDeletedState()) {
                    Query delQuery = em.createQuery("update PreorderMenuDetail set deletedState = true, amount = 0 where preorderComplex.idOfPreorderComplex = :idOfPreorderComplex");
                    delQuery.setParameter("idOfPreorderComplex", preorderComplex.getIdOfPreorderComplex());
                    delQuery.executeUpdate();
                }
                em.merge(preorderComplex);
            }
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private void updateMobileGroupOnCreateOnMenuDetails(PreorderComplex preorderComplex, String mobile,
            PreorderMobileGroupOnCreateType mobileGroupOnCreate) {
        Query query = em.createQuery("update PreorderMenuDetail pmd set pmd.mobile = :mobile, pmd.mobileGroupOnCreate = :mobileGroupOnCreate "
                + "where pmd.preorderComplex = :preorderComplex");
        query.setParameter("mobile", mobile);
        query.setParameter("mobileGroupOnCreate", mobileGroupOnCreate);
        query.setParameter("preorderComplex", preorderComplex);
        query.executeUpdate();
    }

    private boolean regularEquals(RegularPreorderParam regularComplex, RegularPreorder regularPreorder) {
        return regularComplex.getMonday().equals(regularPreorder.getMonday())
                && regularComplex.getTuesday().equals(regularPreorder.getTuesday())
                && regularComplex.getWednesday().equals(regularPreorder.getWednesday())
                && regularComplex.getThursday().equals(regularPreorder.getThursday())
                && regularComplex.getFriday().equals(regularPreorder.getFriday())
                && regularComplex.getSaturday().equals(regularPreorder.getSaturday())
                && regularComplex.getStartDate().equals(regularPreorder.getStartDate())
                && regularComplex.getEndDate().equals(regularPreorder.getEndDate());
    }

    private void createRegularPreorder(Client client, RegularPreorderParam regularComplex,
            Integer amount, Integer idOfComplex, Date date, boolean isComplex, Long idOfMenu, String guardianMobile,
            PreorderMobileGroupOnCreateType mobileGroupOnCreate) throws Exception {
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
            if (regularEquals(regularComplex, regularPreorder) && regularPreorder.getAmount().equals(amount)) return;
            regularPreorder.setAmount(amount);
            regularPreorder.setMonday(regularComplex.getMonday());
            regularPreorder.setTuesday(regularComplex.getTuesday());
            regularPreorder.setWednesday(regularComplex.getWednesday());
            regularPreorder.setThursday(regularComplex.getThursday());
            regularPreorder.setFriday(regularComplex.getFriday());
            regularPreorder.setSaturday(regularComplex.getSaturday());
            regularPreorder.setStartDate(regularComplex.getStartDate());
            regularPreorder.setEndDate(regularComplex.getEndDate());
            regularPreorder.setMobile(guardianMobile);
            regularPreorder.setLastUpdate(new Date());
            em.merge(regularPreorder);
        } catch (NoResultException e) {
            if (isComplex) {
                ComplexInfo ci = getComplexInfo(client, idOfComplex, date);
                if (getMenuDetailList(ci.getIdOfComplexInfo()).size() == 0) {
                    throw new MenuDetailNotExistsException("Не найдены блюда для комплекса с ид.=" + idOfComplex.toString());
                }
                String complexName = null;
                Long complexPrice = null;
                if (ci != null) {
                    complexName = ci.getComplexName();
                    complexPrice = ci.getCurrentPrice();
                }
                regularPreorder = new RegularPreorder(client, regularComplex.getStartDate(), regularComplex.getEndDate(), null, idOfComplex,
                        amount, complexName, regularComplex.getMonday(), regularComplex.getTuesday(), regularComplex.getWednesday(),
                        regularComplex.getThursday(), regularComplex.getFriday(), regularComplex.getSaturday(), complexPrice, guardianMobile,
                        RegularPreorderState.CHANGE_BY_USER, mobileGroupOnCreate);
                em.persist(regularPreorder);
            } else {
                regularPreorder = new RegularPreorder(client, regularComplex.getStartDate(), regularComplex.getEndDate(), itemCode, idOfComplex,
                        amount, menuDetailName, regularComplex.getMonday(), regularComplex.getTuesday(), regularComplex.getWednesday(),
                        regularComplex.getThursday(), regularComplex.getFriday(), regularComplex.getSaturday(), menuDetailPrice, guardianMobile,
                        RegularPreorderState.CHANGE_BY_USER, mobileGroupOnCreate);
                em.persist(regularPreorder);
            }
        }
        createPreordersFromRegular(regularPreorder, true);
    }

    private void deleteRegularPreorder(Client client, Integer idOfComplex, boolean isComplex, Long idOfMenu, Date date, String guardianMobile) throws Exception {
        String condition = isComplex ? " and m.idOfComplex = :idOfComplex " : " and m.itemCode = :itemCode ";
        Query regularPreorderSelect = em.createQuery("select m from RegularPreorder m "
                + "where m.client = :client " + condition + " and m.deletedState = false");
        regularPreorderSelect.setParameter("client", client);
        if (isComplex)
            regularPreorderSelect.setParameter("idOfComplex", idOfComplex);
        else {
            MenuDetail menuDetail = getMenuDetail(client, idOfMenu, date);
            regularPreorderSelect.setParameter("itemCode", menuDetail.getItemCode());
        }
        RegularPreorder regularPreorder = (RegularPreorder) regularPreorderSelect.getSingleResult();
        deleteRegularPreorderInternal((Session)em.getDelegate(), regularPreorder, PreorderState.OK, guardianMobile, RegularPreorderState.CHANGE_BY_USER);
    }

    private void deleteRegularPreorderInternal(Session session, RegularPreorder regularPreorder, PreorderState state,
            String guardianMobile, RegularPreorderState regularPreorderState) throws Exception {
        deleteGeneratedPreordersByRegular(session, regularPreorder, state);

        regularPreorder.setState(regularPreorderState);
        regularPreorder.setDeletedState(true);
        regularPreorder.setLastUpdate(new Date());
        regularPreorder.setMobile(guardianMobile);
        session.update(regularPreorder);
    }

    @Transactional
    public void relevancePreordersToOrgs(PreorderRequestsReportServiceParam params) {
        logger.info("Start relevancePreordersToOrgs process");
        long nextVersion;
        Query query = em.createQuery("select pc, c.org.idOfOrg from PreorderComplex pc join pc.client c "
                + "where pc.preorderDate > :date and pc.deletedState = false "
                + params.getJPACondition()
                + "order by pc.preorderDate");
        query.setParameter("date", new Date());
        List list = query.getResultList();
        for (Object obj : list) {
            Object[] row = (Object[]) obj;
            PreorderComplex preorderComplex = (PreorderComplex) row[0];
            Long idOfOrg = (Long) row[1];
            if (preorderComplex.getIdOfGoodsRequestPosition() != null) continue;
            if (preorderComplex.getIdOfOrgOnCreate() != null && !preorderComplex.getIdOfOrgOnCreate().equals(idOfOrg)) {
                nextVersion = nextVersionByPreorderComplex();
                testAndDeletePreorderComplex(nextVersion, preorderComplex, PreorderState.CHANGE_ORG, true, false);
                continue;
            }
        }
        logger.info("End relevancePreordersToOrgs process");
    }

    @Transactional
    public List<PreorderComplex> getPreorderComplexListForRelevanceToMenu(PreorderRequestsReportServiceParam params) {
        Query query = em.createQuery("select pc from PreorderComplex pc left join fetch pc.preorderMenuDetails join fetch pc.client c join fetch c.org "
                + "where pc.preorderDate > :date and pc.deletedState = false "
                + params.getJPACondition()
                + "order by pc.preorderDate");
        query.setParameter("date", new Date());
        return query.getResultList();
    }

    @Transactional
    public List<ModifyMenu> relevancePreordersToMenu(PreorderComplex preorderComplex, long nextVersion) {
        List<ModifyMenu> modifyMenuList = new ArrayList<>();

        ComplexInfo complexInfo = getComplexInfo(preorderComplex.getClient(), preorderComplex.getArmComplexId(), preorderComplex.getPreorderDate());
        if (complexInfo == null) {
            testAndDeletePreorderComplex(nextVersion, preorderComplex, PreorderState.DELETED, false, true);
            return null;
        }
        if (preorderComplex.getPreorderMenuDetails().size() == 0 || getMenuDetailList(complexInfo.getIdOfComplexInfo()).size() == 0) {
            testAndDeletePreorderComplex(nextVersion, preorderComplex, PreorderState.DELETED, false, false);
            return null;
        }
        if (preorderComplex.getAmount() > 0) {
            if (!preorderComplex.getComplexPrice().equals(complexInfo.getCurrentPrice())) {
                testAndDeletePreorderComplex(nextVersion, preorderComplex, PreorderState.CHANGED_PRICE, false, false);
                return null;
            }
        } else {
            for (PreorderMenuDetail preorderMenuDetail : preorderComplex.getPreorderMenuDetails()) {
                if (preorderMenuDetail.getIdOfGoodsRequestPosition() != null) continue;
                if (!preorderMenuDetail.getDeletedState() && preorderMenuDetail.getAmount() > 0) {
                    MenuDetail menuDetail = getMenuDetail(preorderComplex.getClient(), preorderMenuDetail.getItemCode(),
                            preorderMenuDetail.getPreorderDate(), null, complexInfo.getIdOfComplexInfo());
                    if (menuDetail == null) {
                        testAndDeletePreorderComplex(nextVersion, preorderComplex, PreorderState.DELETED, false, false);
                        break;
                    } else {
                        if (!preorderMenuDetail.getMenuDetailPrice().equals(menuDetail.getPrice())) {
                            testAndDeletePreorderComplex(nextVersion, preorderComplex, PreorderState.CHANGED_PRICE, false, false);
                            break;
                        } else if (!preorderMenuDetail.getArmIdOfMenu().equals(menuDetail.getLocalIdOfMenu())) {
                            logger.info(String.format("Change localIdOfMenu %s to %s at preorder %s adding to process list",
                                    preorderMenuDetail.getArmIdOfMenu(), menuDetail.getLocalIdOfMenu(), preorderComplex));
                            ModifyMenu modifyMenu = new ModifyMenu(menuDetail.getLocalIdOfMenu(),
                                    preorderMenuDetail.getIdOfPreorderMenuDetail(), preorderComplex.getIdOfPreorderComplex());
                            modifyMenuList.add(modifyMenu);
                        }
                    }
                }
            }
        }
        return modifyMenuList;
    }

    @Transactional
    public void changeLocalIdOfMenu(List<ModifyMenu> modifyMenuList, Long nextVersion) {
        logger.info("Start change localIdOfMenu");
        doChangeLocalIdOfMenu(modifyMenuList, BASE_ID_MENU_VALUE_FOR_MODIFY, nextVersion, false);
        doChangeLocalIdOfMenu(modifyMenuList, 0, nextVersion, true);
        logger.info("End change localIdOfMenu");
    }

    private void doChangeLocalIdOfMenu(List<ModifyMenu> modifyMenuList, long offset, Long nextVersion, boolean updatePreorderComplex) {
        for (ModifyMenu modifyMenu : modifyMenuList) {
            Query query = em.createQuery("update PreorderMenuDetail set armIdOfMenu= :armIdOfMenu where idOfPreorderMenuDetail = :idOfPreorderMenuDetail");
            query.setParameter("armIdOfMenu", offset + modifyMenu.getNewIdOfMenu());
            query.setParameter("idOfPreorderMenuDetail", modifyMenu.getIdOfPreorderMenuDetail());
            query.executeUpdate();
            if (updatePreorderComplex) {
                Query query2 = em.createQuery("update PreorderComplex set version = :version, lastUpdate = :lastUpdate where idOfPreorderComplex = :idOfPreorderComplex");
                query2.setParameter("version", nextVersion);
                query2.setParameter("lastUpdate", new Date());
                query2.setParameter("idOfPreorderComplex", modifyMenu.getIdOfPreorderComplex());
                query2.executeUpdate();
            }
        }
    }

    @Transactional
    public void relevancePreordersToOrgFlag(PreorderRequestsReportServiceParam params) {
        logger.info("Start relevancePreordersToOrgFlag process");
        long nextVersion;
        Query query = em.createQuery("select pc from PreorderComplex pc join pc.client.org o "
                + "where pc.preorderDate > :date and pc.deletedState = false and o.preordersEnabled = false "
                + params.getJPACondition()
                + "order by pc.preorderDate");
        query.setParameter("date", new Date());
        List<PreorderComplex> list = query.getResultList();
        for (PreorderComplex preorderComplex : list) {
            if (preorderComplex.getIdOfGoodsRequestPosition() != null) continue;
            nextVersion = nextVersionByPreorderComplex();
            testAndDeletePreorderComplex(nextVersion, preorderComplex, PreorderState.PREORDER_OFF, true, false);
        }

        logger.info("End relevancePreordersToOrgFlag process");
    }

    private void testAndDeletePreorderComplex(long nextVersion, PreorderComplex preorderComplex, PreorderState preorderState,
            boolean deleteRegular, boolean forceDelete) {
        try {
            Boolean doDelete = false;
            Date today = CalendarUtils.startOfDay(new Date());
            Integer syncCountDays = PreorderComplex.getDaysOfRegularPreorders();
            Map<String, Integer[]> sd = getSpecialDates(CalendarUtils.addHours(today, 12), syncCountDays,
                    preorderComplex.getClient().getOrg().getIdOfOrg(), preorderComplex.getClient());
            for (Map.Entry<String, Integer[]> entry : sd.entrySet()) {
                if (preorderComplex.getPreorderDate().equals(CalendarUtils.parseDate(entry.getKey()))) {
                    doDelete = (entry.getValue())[0].equals(0);
                    break;
                }
            }
            if (doDelete || forceDelete) {
                deletePreorderComplex(preorderComplex, nextVersion, preorderState);
                if (deleteRegular && preorderComplex.getRegularPreorder() != null) {
                    deleteRegularPreorderInternal((Session)em.getDelegate(), preorderComplex.getRegularPreorder(),
                            preorderState, null, RegularPreorderState.CHANGE_BY_SERVICE);
                }
                logger.info("Deleted preorder " + preorderComplex.toString() + (forceDelete ? " (force delete = true)" : ""));
            } else {
                logger.info("Preoder can't be deleted " + preorderComplex.toString());
            }
        } catch (Exception e) {
            logger.error("Error in testAndDeletePreorderComplex: ", e);
        }
    }

    private void generatePreordersByScheduleInternal(PreorderRequestsReportServiceParam params) {
        try {
            logger.info("Start of generating regular preorders");
            if (params.isEmpty()) {
                RuntimeContext.getAppContext().getBean(PreorderOperationsService.class).generatePreordersBySchedule(params);
            }
            logger.info("Successful end of generating regular preorders");
        } catch (Exception e) {
            logger.error("Error in generating regular preorders: ", e);
        }
        try {
            logger.info("Start additional tasks for preorders");
            RuntimeContext.getAppContext().getBean(PreorderOperationsService.class).additionalTasksForPreorders(params);
            logger.info("Successful end additional tasks for preorders");
        } catch (Exception e) {
            logger.error("Error in additional tasks for preorders: ", e);
        }
        try {
            logger.info("Start additional tasks for regulars");
            if (params.isEmpty()) {
                RuntimeContext.getAppContext().getBean(PreorderOperationsService.class).additionalTasksForRegulars(params);
            }
            logger.info("Successful end additional tasks for regulars");
        } catch (Exception e) {
            logger.error("Error in additional tasks for regualrs: ", e);
        }

    }

    private void deleteGeneratedPreordersByRegular(Session session, RegularPreorder regularPreorder, PreorderState state) throws Exception {
        Date dateFrom = getStartDateForGeneratePreordersInternal(regularPreorder.getClient());
        long nextVersion = DAOUtils.nextVersionByPreorderComplex(session);
        org.hibernate.Query delQuery = session.createQuery("update PreorderComplex set deletedState = true, lastUpdate = :lastUpdate, amount = 0, state = :state, version = :version "
                + "where regularPreorder = :regularPreorder and preorderDate > :dateFrom and idOfGoodsRequestPosition is null");
        delQuery.setParameter("lastUpdate", new Date());
        delQuery.setParameter("regularPreorder", regularPreorder);
        delQuery.setParameter("dateFrom", dateFrom);
        delQuery.setParameter("state", state);
        delQuery.setParameter("version", nextVersion);
        delQuery.executeUpdate();

        delQuery = session.createQuery("update PreorderMenuDetail set deletedState = true, amount = 0, state = :state "
                + "where preorderComplex.idOfPreorderComplex in (select idOfPreorderComplex from PreorderComplex "
                + "where regularPreorder = :regularPreorder) and preorderDate > :dateFrom and idOfGoodsRequestPosition is null");
        delQuery.setParameter("regularPreorder", regularPreorder);
        delQuery.setParameter("dateFrom", dateFrom);
        delQuery.setParameter("state", state);
        delQuery.executeUpdate();
    }

    private Date getMaxPreorderDate(RegularPreorder regularPreorder) {
        Query query = em.createQuery("select max(pc.preorderDate) from PreorderComplex pc where pc.regularPreorder = :regularPreorder");
        query.setParameter("regularPreorder", regularPreorder);
        return (Date)query.getSingleResult();
    }

    @Transactional
    private List<OrgGoodRequest> getOrgGoodRequests(long idOfOrg, Date startDate, Date endDate) {
        return emReport.createQuery("select ogr from OrgGoodRequest ogr where ogr.idOfOrg = :idOfOrg and ogr.day between :startDate and :endDate order by ogr.day")
                .setParameter("idOfOrg",idOfOrg)
                .setParameter("startDate", startDate)
                .setParameter("endDate",endDate)
                .getResultList();
    }

    private boolean orgGoodRequestExists(List<OrgGoodRequest> list, Date date) {
        for (OrgGoodRequest org : list) {
            if (org.getDay().equals(date)) return true;
        }
        return false;
    }

    @Transactional
    public void processAdditionalTaskForPreorder(PreorderComplex preorderComplex, List<ProductionCalendar> productionCalendar,
            List<SpecialDate> specialDates, Long idOfClientGroup) throws Exception {
        Boolean isWeekend = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class)
                .isWeekendByProductionCalendar(preorderComplex.getPreorderDate(), productionCalendar);
        isWeekend = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class).isWeekendBySpecialDateAndSixWorkWeek(
                isWeekend, preorderComplex.getPreorderDate(), idOfClientGroup, preorderComplex.getIdOfOrgOnCreate(), specialDates);
        if (!isWeekend) isWeekend = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class)
                .isHolidayByProductionCalendar(preorderComplex.getPreorderDate(), productionCalendar);
        if (isWeekend) {
            if (isEditedDay(preorderComplex.getPreorderDate(), preorderComplex.getClient())) {
                logger.info(String.format("Delete preorderComplex %s by change of calendar", preorderComplex.toString()));
                long nextVersion = nextVersionByPreorderComplex();
                deletePreorderComplex(preorderComplex, nextVersion, PreorderState.CHANGED_CALENDAR);
            } else {
                logger.info(String.format("PreorderComplex %s must be deleted by change of calendar, but not editable day", preorderComplex));
            }
        }
    }

    @Transactional
    public void deleteExpiredRegularPreorder(RegularPreorder regularPreorder) {
        Date currentDate = new Date();
        if (currentDate.before(regularPreorder.getEndDate())) return;
        regularPreorder.setState(RegularPreorderState.CHANGE_BY_SERVICE);
        regularPreorder.setDeletedState(true);
        em.merge(regularPreorder);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void createPreordersFromRegular(RegularPreorder regularPreorder, boolean doDeleteExisting) throws Exception {
        //генерация предзаказов по ид. регулярного заказа
        //для блюд - по коду товара и цене
        //для комплексов - по ид. комплеса + цена.
        if (regularPreorder == null || regularPreorder.getDeletedState()) return;
        if (!em.contains(regularPreorder)) regularPreorder = em.find(RegularPreorder.class, regularPreorder.getIdOfRegularPreorder());
        //проверка на даты: от текущего дня пропускаем дни запрета редактирвоания и генерируем на 2 недели вперед
        Date dateTo = CalendarUtils.addDays(new Date(), PreorderComplex.getDaysOfRegularPreorders()-1);
        if (dateTo.after(regularPreorder.getEndDate())) dateTo = regularPreorder.getEndDate();
        Date currentDate = CalendarUtils.startOfDay(new Date());
        long nextVersion = nextVersionByPreorderComplex();

        List<SpecialDate> specialDates = DAOReadonlyService.getInstance().getSpecialDates(currentDate, dateTo, regularPreorder.getClient().getOrg().getIdOfOrg());//данные из производственного календаря за период
        List<OrgGoodRequest> preorderRequests = getOrgGoodRequests(regularPreorder.getClient().getOrg().getIdOfOrg(), currentDate, dateTo);
        currentDate = getStartDateForGeneratePreordersInternal(regularPreorder.getClient());
        if (currentDate.before(regularPreorder.getStartDate())) currentDate = regularPreorder.getStartDate();
        if (doDeleteExisting) {
            deleteGeneratedPreordersByRegular((Session) em.getDelegate(), regularPreorder, PreorderState.OK);
        }

        List<PreorderComplex> preorderComplexes = getPreorderComplexesByRegular(regularPreorder, currentDate, dateTo); //получаем список всех предзаказов (в т.ч. удаленные)

        List<ProductionCalendar> productionCalendar = DAOReadonlyService.getInstance().getProductionCalendar(new Date(), dateTo);
        currentDate = CalendarUtils.startOfDayInUTC(CalendarUtils.addHours(currentDate, 12));
        while (currentDate.before(dateTo) || currentDate.equals(dateTo)) {
            logger.info(String.format("Processing regular preorder %s on date %s...", regularPreorder, CalendarUtils.dateToString(currentDate)));
            if (orgGoodRequestExists(preorderRequests, currentDate)) {
                //если на тек день есть заявка, то этот день пропускаем
                logger.info("OrgGoodRequest exists");
                currentDate = CalendarUtils.addDays(currentDate, 1);
                continue;
            }
            Boolean isWeekend = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class).isWeekendByProductionCalendar(currentDate, productionCalendar);
            isWeekend = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class).isWeekendBySpecialDateAndSixWorkWeek(
                    isWeekend, currentDate, regularPreorder.getClient().getIdOfClientGroup(), regularPreorder.getClient().getOrg().getIdOfOrg(), specialDates);
            if (!isWeekend) isWeekend = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class).isHolidayByProductionCalendar(currentDate, productionCalendar);
            if (isWeekend) {
                PreorderComplex pc = findPreorderComplexOnDate(preorderComplexes, currentDate, false);
                if (pc != null) {
                    logger.info("Delete by change of calendar");
                    deletePreorderComplex(pc, nextVersion, PreorderState.CHANGED_CALENDAR);
                }
            }

            boolean doGenerate = doGenerate(currentDate, regularPreorder);  //генерить ли предзаказ по дню недели в регулярном заказе
            if (isWeekend || !doGenerate) {
                logger.info("Weekend or not generated day");
                currentDate = CalendarUtils.addDays(currentDate, 1);
                continue;
            }

            //предзаказ на комплекс
            PreorderComplex preorderComplex = findPreorderComplexOnDate(preorderComplexes, currentDate, null);
            MenuDetail menuDetail = null;
            ComplexInfo complexInfo = getComplexInfo(regularPreorder.getClient(), regularPreorder.getIdOfComplex(), currentDate); //комплекс на дату и с ценой рег. заказа
            if (complexInfo == null) {
                if (preorderComplex != null && !preorderComplex.getDeletedState()) {
                    //предзаказ есть, но комплекса нет - удаляем ранее сгенерированный предзаказ
                    logger.info("Delete by complex not exists");
                    deletePreorderComplex(preorderComplex, nextVersion, PreorderState.DELETED);
                } else {
                    logger.info("Not generate by complex not exists");
                }
                currentDate = CalendarUtils.addDays(currentDate, 1);
                continue;
            }

            List menuDetails = getMenuDetailList(complexInfo.getIdOfComplexInfo());
            if (menuDetails.size() == 0) {
                logger.info("No menu details found");
                currentDate = CalendarUtils.addDays(currentDate, 1);
                continue;
            }

            if ((preorderComplex == null || (preorderComplex != null && allowCreateNewPreorderComplex(preorderComplex)))
                    && !forcePreorderComplexExists(regularPreorder, currentDate)) {
                //на искомую дату нет предзаказа, надо создавать

                boolean comparePrice = !isMenuDetailPreorder(regularPreorder); //здесь сравниваем по цене если заказ на комплекс, а не на блюдо
                if (!comparePrice) {
                    menuDetail = getMenuDetail(regularPreorder.getClient(), regularPreorder.getItemCode(), currentDate, regularPreorder.getPrice(), complexInfo.getIdOfComplexInfo());
                    if (menuDetail == null) {
                        logger.info("Not found menu detail");
                        currentDate = CalendarUtils.addDays(currentDate, 1);
                        continue;
                    }
                }
                if (comparePrice ? !complexInfo.getCurrentPrice().equals(regularPreorder.getPrice()) : false) { //не найден комплекс или цена не совпадает с рег. заказом
                    logger.info("Complex price not not match to regular");
                    currentDate = CalendarUtils.addDays(currentDate, 1);
                    continue;
                }
                logger.info("===Create preorder complex from regular===");
                preorderComplex = createPreorderComplex(regularPreorder.getIdOfComplex(), regularPreorder.getClient(),
                        complexInfo.getMenuDate(), !isMenuDetailPreorder(regularPreorder) ? regularPreorder.getAmount() : 0, complexInfo,
                        nextVersion, regularPreorder.getMobile(), regularPreorder.getMobileGroupOnCreate());
                preorderComplex.setRegularPreorder(regularPreorder);
                em.persist(preorderComplex);
            } else if (!isMenuDetailPreorder(regularPreorder)) {
                logger.info("Preorder complex exists or deleted by user");
                currentDate = CalendarUtils.addDays(currentDate, 1);
                continue;
            }
            if (isMenuDetailPreorder(regularPreorder)) {
                if (preorderComplex == null) {
                    preorderComplex = findPreorderComplex(currentDate, regularPreorder.getClient(), regularPreorder.getIdOfComplex());
                }
                if (preorderComplex != null && !preorderComplex.getDeletedState() && menuDetail == null) {
                    menuDetail = getMenuDetail(regularPreorder.getClient(), regularPreorder.getItemCode(), currentDate, regularPreorder.getPrice(), complexInfo.getIdOfComplexInfo());
                    if (menuDetail == null) {
                        logger.info("Not found menu detail");
                        deletePreorderComplex(preorderComplex, nextVersion, PreorderState.DELETED); //не нашли блюдо из предзаказа - удаляем предзаказ
                        currentDate = CalendarUtils.addDays(currentDate, 1);
                        continue;
                    }
                }
                PreorderMenuDetail preorderMenuDetail = findPreorderMenuDetail(currentDate, regularPreorder.getClient(), menuDetail.getLocalIdOfMenu());
                if ((preorderMenuDetail == null || (preorderMenuDetail != null && allowCreateNewPreorderMenuDetail(preorderMenuDetail)))
                        && !forcePreorderMenuDetailExists(regularPreorder, currentDate)) {
                    //на искомую дату нет предзаказа, надо создавать
                    logger.info("===Create preorder menudetail from regular===");
                    preorderMenuDetail = createPreorderMenuDetail(regularPreorder.getClient(), preorderComplex, menuDetail,
                            menuDetail.getMenu().getMenuDate(), menuDetail.getLocalIdOfMenu(), regularPreorder.getAmount(),
                            regularPreorder.getMobile(), regularPreorder.getMobileGroupOnCreate());
                    preorderMenuDetail.setRegularPreorder(regularPreorder);
                    em.persist(preorderMenuDetail);
                }
            }
            Set<PreorderMenuDetail> set = createPreorderMenuDetails(menuDetails, regularPreorder.getClient(),
                    complexInfo.getMenuDate(), preorderComplex, regularPreorder.getMobile(), regularPreorder.getMobileGroupOnCreate());
            preorderComplex.setPreorderMenuDetails(set);
            preorderComplex.setVersion(nextVersion);
            em.merge(preorderComplex);
            currentDate = CalendarUtils.addDays(currentDate, 1);
        }
        //Проверяем есть ли от сегодняшнего дня актуальные предзаказы. если нет ни одного - удаляем рег правило
        testAndDeleteRegularPreorder(regularPreorder);
    }

    private boolean isMenuDetailPreorder(RegularPreorder regularPreorder) {
        return !StringUtils.isEmpty(regularPreorder.getItemCode());
    }

    private boolean forcePreorderComplexExists(RegularPreorder regularPreorder, Date date) {
        //существует ли актуальный предзаказ на комплекс без привязки к регуляру
        return em.createQuery("select pc.idOfPreorderComplex from PreorderComplex pc "
                + "where pc.client = :client and pc.preorderDate = :date and pc.armComplexId = :complexId and pc.deletedState = false")
                .setParameter("client", regularPreorder.getClient())
                .setParameter("date", date)
                .setParameter("complexId", regularPreorder.getIdOfComplex())
                .getResultList().size() > 0;
    }

    private boolean forcePreorderMenuDetailExists(RegularPreorder regularPreorder, Date date) {
        //существует ли актуальный предзаказ на блюдо без привязки к регуляру
        return em.createQuery("select pmd.idOfPreorderMenuDetail from PreorderMenuDetail pmd "
                + "where pmd.client = :client and pmd.preorderDate = :date and pmd.itemCode = :itemCode and pmd.deletedState = false")
                .setParameter("client", regularPreorder.getClient())
                .setParameter("date", date)
                .setParameter("itemCode", regularPreorder.getItemCode())
                .getResultList().size() > 0;
    }

    private boolean allowCreateNewPreorderComplex(PreorderComplex preorderComplex) {
        return preorderComplex.getDeletedState() &&
                !(preorderComplex.getState().equals(PreorderState.OK)
                        || preorderComplex.getState().equals(PreorderState.CHANGE_ORG)
                        || preorderComplex.getState().equals(PreorderState.PREORDER_OFF));
    }

    private boolean allowCreateNewPreorderMenuDetail(PreorderMenuDetail preorderMenuDetail) {
        return preorderMenuDetail.getDeletedState() &&
                !(preorderMenuDetail.getState().equals(PreorderState.OK)
                        || preorderMenuDetail.getState().equals(PreorderState.CHANGE_ORG)
                        || preorderMenuDetail.getState().equals(PreorderState.PREORDER_OFF));
    }

    private PreorderComplex findPreorderComplexOnDate(List<PreorderComplex> preorderComplexes, Date currentDate, Boolean deletedState) {
        for (PreorderComplex pc : preorderComplexes) {
            if (pc.getPreorderDate().equals(currentDate) && (deletedState == null ? true : pc.getDeletedState() == deletedState)) return pc;
        }
        return null;
    }

    private List<PreorderComplex> getPreorderComplexesByRegular(RegularPreorder regularPreorder, Date dateFrom, Date dateTo) {
        return em.createQuery("select pc from PreorderComplex pc "
                + "where pc.client = :client and pc.regularPreorder = :regularPreorder "
                + "order by pc.createdDate desc")
                .setParameter("client", regularPreorder.getClient())
                .setParameter("regularPreorder", regularPreorder)
                .getResultList();
    }

    private void testAndDeleteRegularPreorder(RegularPreorder regularPreorder) {
        Query query = em.createQuery("select pc.idOfPreorderComplex from PreorderComplex pc "
                + "where pc.regularPreorder = :regularPreorder and pc.preorderDate > :date and pc.deletedState = false and pc.idOfGoodsRequestPosition is null");
        query.setParameter("regularPreorder", regularPreorder);
        query.setParameter("date", new Date());
        if (query.getResultList().size() == 0) {
            regularPreorder.setDeletedState(true);
            regularPreorder.setLastUpdate(new Date());
            em.merge(regularPreorder);
        }
    }

    private void deletePreorderComplex(PreorderComplex preorderComplex, long nextVersion, PreorderState preorderState) {
        for (PreorderMenuDetail pmd : preorderComplex.getPreorderMenuDetails()) {
            pmd.deleteByReason(nextVersion, true, preorderState);
            em.merge(pmd);
        }
        preorderComplex.deleteByReason(nextVersion, true, preorderState);
        em.merge(preorderComplex);
    }

    @Transactional
    public List<RegularPreorder> getRegularPreorders(PreorderRequestsReportServiceParam params) {
        Query query = em.createQuery("select r from RegularPreorder r join fetch r.client c "
                + "where r.deletedState = false and r.endDate > :date" + params.getRegularPreorderJPACondition());
        query.setParameter("date", new Date());
        return query.getResultList();
    }

    @Transactional
    public List getAllActualPreorders(PreorderRequestsReportServiceParam params) {
        Query query = em.createQuery("select pc, pc.client.clientGroup.compositeIdOfClientGroup.idOfClientGroup from PreorderComplex pc join fetch pc.client c "
                + "where pc.deletedState = false and pc.preorderDate > :date" + params.getJPACondition());
        query.setParameter("date", new Date());
        return query.getResultList();
    }

    @Transactional
    public List<RegularPreorder> getExpiredRegularPreorders(PreorderRequestsReportServiceParam params) {
        Query query = em.createQuery("select r from RegularPreorder r join fetch r.client c "
                + "where r.deletedState = false and r.endDate < :date" + params.getRegularPreorderJPACondition());
        query.setParameter("date", new Date());
        return query.getResultList();
    }

    private Date getStartDateForGeneratePreordersInternal(Client client) throws Exception {
        Date currentDate = CalendarUtils.startOfDay(new Date());
        Date dateTo = CalendarUtils.addDays(new Date(), PreorderComplex.getDaysOfRegularPreorders()-1);
        List<SpecialDate> specialDates = DAOReadonlyService.getInstance().getSpecialDates(currentDate, dateTo, client.getOrg().getIdOfOrg());//данные из производственного календаря за период
        Integer forbiddenDays = DAOUtils.getPreorderFeedingForbiddenDays(client); //настройка - количество дней запрета редактирования
        if (forbiddenDays == null) {
            forbiddenDays = PreorderComplex.DEFAULT_FORBIDDEN_DAYS;
        }
        String groupName = DAOReadonlyService.getInstance().getClientGroupName(client.getOrg().getIdOfOrg(), client.getIdOfClientGroup());
        boolean isSixWorkWeek = DAOReadonlyService.getInstance().isSixWorkWeek(client.getOrg().getIdOfOrg(), groupName);
        int i = 0;
        Date result = CalendarUtils.addDays(currentDate, 1);
        while (i < forbiddenDays) {
            boolean isWorkDate = getIsWorkDate(isSixWorkWeek, result, specialDates, client);
            if (isWorkDate) i++;
            result = CalendarUtils.addDays(result, 1);
        }
        return result;
    }

    private boolean getIsWorkDate(boolean isSixWorkWeek, Date currentDate, List<SpecialDate> specialDates, Client client) {
        boolean isWorkDate = CalendarUtils.isWorkDateWithoutParser(isSixWorkWeek, currentDate); //без учета проиводственного календаря
        for (SpecialDate specialDate : specialDates) {
            if (CalendarUtils.betweenDate(specialDate.getDate(), CalendarUtils.startOfDay(currentDate), CalendarUtils.endOfDay(currentDate))) {
                if (specialDate.getIdOfClientGroup() != null && specialDate.getIdOfClientGroup().equals(client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup())) {
                    isWorkDate = !specialDate.getIsWeekend();
                    break; //нашли в таблице календаря запись по группе клиента - выходим
                }
                if (specialDate.getIdOfClientGroup() == null) {
                    isWorkDate = !specialDate.getIsWeekend(); //нашли в таблице календаря запись по ОО клиента (у записи не указана какая-либо группа)
                }
            }
        }
        return isWorkDate;
    }

    private void deletePreorders(RegularPreorder regularPreorder, Date currentDate, Long nextVersion) {
        Query query = em.createQuery("update PreorderComplex pc set pc.deletedState = true, pc.amount = 0, pc.lastUpdate = :lastUpdate, pc.version = :version "
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
                + "where pmd.client = :client and pmd.preorderDate between :startDate and :endDate and pmd.armIdOfMenu = :armIdOfMenu");
        query.setParameter("client", client);
        query.setParameter("startDate", CalendarUtils.startOfDay(date));
        query.setParameter("endDate", CalendarUtils.endOfDay(date));
        query.setParameter("armIdOfMenu", armIdOfMenu);
        List<PreorderMenuDetail> list = query.getResultList();
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    private PreorderMenuDetail findPreorderMenuDetail(Date date, Client client, String itemCode) {
        Query query = em.createQuery("select pmd from PreorderMenuDetail pmd "
                + "where pmd.client = :client and pmd.preorderDate between :startDate and :endDate and pmd.itemCode = :itemCode and pmd.deletedState = false");
        query.setParameter("client", client);
        query.setParameter("startDate", CalendarUtils.startOfDay(date));
        query.setParameter("endDate", CalendarUtils.endOfDay(date));
        query.setParameter("itemCode", itemCode);
        List<PreorderMenuDetail> list = query.getResultList();
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    private PreorderComplex createPreorderComplex(Integer idOfComplex, Client client, Date date, Integer complexAmount, ComplexInfo ci,
            Long nextVersion, String guardianMobile, PreorderMobileGroupOnCreateType mobileGroupOnCreate) throws MenuDetailNotExistsException {
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
        preorderComplex.setIdOfOrgOnCreate(client.getOrg().getIdOfOrg());
        preorderComplex.setMobile(guardianMobile);
        preorderComplex.setMobileGroupOnCreate(mobileGroupOnCreate);
        if (ci == null) {
            ci = getComplexInfo(client, idOfComplex, date);
            if (ci != null && getMenuDetailList(ci.getIdOfComplexInfo()).size() == 0) {
                throw new MenuDetailNotExistsException("Не найдены блюда для комплекса с ид.=" + idOfComplex.toString());
            }
        }
        if (ci != null) {
            preorderComplex.setComplexName(ci.getComplexName());
            preorderComplex.setComplexPrice(ci.getCurrentPrice());
            preorderComplex.setModeFree(ci.getModeFree());
            preorderComplex.setModeOfAdd(ci.getModeOfAdd());
        } else {
            throw new MenuDetailNotExistsException("Не найден комплекс с ид.=" + idOfComplex.toString());
        }
        return preorderComplex;
    }

    private boolean preorderMenuDetailExists(PreorderComplex preorderComplex, Client client, Date date, Long idOfMenu) {
        Query query = em.createQuery("select pmd.idOfPreorderMenuDetail from PreorderMenuDetail pmd where pmd.preorderComplex.idOfPreorderComplex = :preorderComplex "
                + "and pmd.client = :client and pmd.preorderDate = :preorderDate and pmd.armIdOfMenu = :idOfMenu and pmd.deletedState = false");
        query.setParameter("preorderComplex", preorderComplex.getIdOfPreorderComplex());
        query.setParameter("client", client);
        query.setParameter("preorderDate", date);
        query.setParameter("idOfMenu", idOfMenu);
        try {
            query.getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        } catch (NonUniqueResultException e) {
            logger.error("Error in preorderMenuDetailExists: ", e);
            return true;
        }
    }

    private List getMenuDetailList(Long idOfComplexInfo) {
        Query query = emReport.createNativeQuery("SELECT md.idofmenudetail, md.LocalIdOfMenu "
                + "FROM CF_MenuDetails md INNER JOIN CF_ComplexInfoDetail cid ON cid.IdOfMenuDetail = md.IdOfMenuDetail "
                + "WHERE cid.IdOfComplexInfo = :idOfComplexInfo");
        query.setParameter("idOfComplexInfo", idOfComplexInfo);
        return query.getResultList();
    }

    private Set<PreorderMenuDetail> createPreorderMenuDetails(List list, Client client, Date date,
            PreorderComplex preorderComplex, String mobile, PreorderMobileGroupOnCreateType mobileGroupOnCreate) throws Exception {
        Set<PreorderMenuDetail> result = new HashSet<PreorderMenuDetail>();
        for (Object o : list) {
            Object[] row = (Object[]) o;
            Long idOfMenu = ((BigInteger) row[1]).longValue();
            if (!preorderMenuDetailExists(preorderComplex, client, date, idOfMenu)) {
                PreorderMenuDetail pmd = createPreorderMenuDetail(client, preorderComplex, null, date, idOfMenu, 0,
                        mobile, mobileGroupOnCreate);
                result.add(pmd);
            }
        }
        return result;
    }

    private Set<PreorderMenuDetail> createPreorderMenuDetails(Integer idOfComplex, Client client, Date date,
            PreorderComplex preorderComplex, String mobile, PreorderMobileGroupOnCreateType mobileGroupOnCreate) throws Exception {
        ComplexInfo ci = getComplexInfo(client, idOfComplex, date);
        List list = getMenuDetailList(ci.getIdOfComplexInfo());
        return createPreorderMenuDetails(list, client, date, preorderComplex, mobile, mobileGroupOnCreate);
    }

    private PreorderMenuDetail createPreorderMenuDetail(Client client, PreorderComplex preorderComplex, MenuDetail md,
            Date date, Long idOfMenu, Integer amount, String mobile, PreorderMobileGroupOnCreateType mobileGroupOnCreate) throws MenuDetailNotExistsException {
        PreorderMenuDetail preorderMenuDetail = new PreorderMenuDetail();
        preorderMenuDetail.setPreorderComplex(preorderComplex);
        preorderMenuDetail.setArmIdOfMenu(idOfMenu);
        preorderMenuDetail.setClient(client);
        preorderMenuDetail.setPreorderDate(date);
        preorderMenuDetail.setAmount(amount);
        preorderMenuDetail.setDeletedState(false);
        preorderMenuDetail.setState(PreorderState.OK);
        if (md == null) md = getMenuDetail(client, idOfMenu, date);
        if (md == null) {
            throw new MenuDetailNotExistsException("Не найдено блюдо с ид.=" + idOfMenu.toString());
        }
        preorderMenuDetail.setMenuDetailName(md.getMenuDetailName());
        preorderMenuDetail.setMenuDetailPrice(md.getPrice());
        preorderMenuDetail.setItemCode(md.getItemCode());
        preorderMenuDetail.setAvailableNow(md.getAvailableNow());
        preorderMenuDetail.setCalories(md.getCalories());
        preorderMenuDetail.setCarbohydrates(md.getCarbohydrates());
        preorderMenuDetail.setFat(md.getFat());
        preorderMenuDetail.setGroupName(md.getGroupName());
        preorderMenuDetail.setMenuDetailOutput(md.getMenuDetailOutput());
        preorderMenuDetail.setProtein(md.getProtein());
        preorderMenuDetail.setShortName(md.getShortName());
        preorderMenuDetail.setIdOfGood(md.getIdOfGood());
        preorderMenuDetail.setMobile(mobile);
        preorderMenuDetail.setMobileGroupOnCreate(mobileGroupOnCreate);
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
            logger.error(String.format("Cant find complexInfo idOfComplex=%s, date=%s, idOfClient=%s", idOfComplex, date.getTime(), client.getIdOfClient()), e);
            return null;
        }
    }

    private MenuDetail getMenuDetail(Client client, Long idOfMenu, Date date) {
        Query query = emReport.createQuery("select md from MenuDetail md where md.menu.org.idOfOrg = :idOfOrg "
                + "and md.localIdOfMenu = :idOfMenu and md.menu.menuDate between :startDate and :endDate");
        query.setParameter("idOfOrg", client.getOrg().getIdOfOrg());
        query.setParameter("idOfMenu", idOfMenu);
        query.setParameter("startDate", CalendarUtils.startOfDay(date));
        query.setParameter("endDate", CalendarUtils.endOfDay(date));
        try {
            return (MenuDetail)query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private MenuDetail getMenuDetail(Client client, String itemCode, Date date, Long price, Long idOfComplexInfo) {
        String priceCondition = (price == null ? "" : " and cid.menuDetail.price = :price");
        Query query = emReport.createQuery("select cid.menuDetail from ComplexInfoDetail cid where cid.complexInfo.idOfComplexInfo = :idOfComplexInfo and cid.complexInfo.org.idOfOrg = :idOfOrg "
                + "and cid.menuDetail.itemCode = :itemCode and cid.complexInfo.menuDate between :startDate and :endDate" + priceCondition);
        query.setParameter("idOfComplexInfo", idOfComplexInfo);
        query.setParameter("idOfOrg", client.getOrg().getIdOfOrg());
        query.setParameter("itemCode", itemCode);
        query.setParameter("startDate", CalendarUtils.startOfDay(date));
        query.setParameter("endDate", CalendarUtils.endOfDay(date));
        if (price != null) query.setParameter("price", price);
        try {
            return (MenuDetail)query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public String getAddress(Integer idOfOrg) {
        Query query = emReport.createQuery("select o.shortAddress from Org o where o.idOfOrg = :idOfOrg");
        query.setParameter("idOfOrg", idOfOrg.longValue());
        return (String)query.getSingleResult();
    }

    @Transactional
    public Long getPreordersSum(Client client, Date startDate, Date endDate) {
        Query query = emReport.createQuery("select pc from PreorderComplex pc join fetch pc.preorderMenuDetails pmd "
                + "where pc.client.idOfClient = :idOfClient and pc.preorderDate between :startDate and :endDate "
                + "and pc.deletedState = false and pmd.deletedState = false");
        query.setParameter("idOfClient", client.getIdOfClient());
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        List<PreorderComplex> list = query.getResultList();
        Long sum = 0L;
        Set<Long> set = new HashSet<Long>();
        for (PreorderComplex complex : list) {
            if (!set.contains(complex.getIdOfPreorderComplex())) {
                sum += complex.getComplexPrice() * complex.getAmount() - complex.getUsedSum();
                set.add(complex.getIdOfPreorderComplex());
                for (PreorderMenuDetail pmd : complex.getPreorderMenuDetails()) {
                    sum += pmd.getMenuDetailPrice() * pmd.getAmount();
                }
            }
        }
        return sum; // client.getBalance() - sum;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public long nextVersionByPreorderComplex() {
        Session session = (Session)em.getDelegate();
        return DAOUtils.nextVersionByPreorderComplex(session);
    }

    @Transactional(readOnly = true)
    public Map<Date, Long[]> existPreordersByDate(Long idOfClient, Date startDate, Date endDate) {
        Map map = new HashMap<Date, Long[]>();
        Query query = emReport.createQuery("select sum(p.amount), p.preorderDate, coalesce(p.idOfOrgOnCreate, p.client.org.idOfOrg) as org from PreorderComplex p "
                + "where p.client.idOfClient = :idOfClient and p.preorderDate between :startDate and :endDate and p.deletedState = false "
                + "group by p.preorderDate, coalesce(p.idOfOrgOnCreate, p.client.org.idOfOrg)");
        query.setParameter("idOfClient", idOfClient);
        query.setParameter("startDate", CalendarUtils.startOfDay(startDate));
        query.setParameter("endDate", CalendarUtils.endOfDay(endDate));
        List result = query.getResultList();
        if (result != null) {
            for (Object obj : result) {
                Object[] row = (Object[]) obj;
                Long[] arr = new Long[2];
                arr[0] = (Long)row[0];//количество заказов
                arr[1] = (Long)row[2];//ид ОО
                map.put((Date)row[1], arr);
            }
        }

        query = emReport.createQuery("select sum(p.amount), p.preorderDate, coalesce(p.preorderComplex.idOfOrgOnCreate, p.client.org.idOfOrg) from PreorderMenuDetail p "
                + "where p.client.idOfClient = :idOfClient and p.preorderDate between :startDate and :endDate and p.preorderComplex.deletedState = false "
                + "group by p.preorderDate, coalesce(p.preorderComplex.idOfOrgOnCreate, p.client.org.idOfOrg)");
        query.setParameter("idOfClient", idOfClient);
        query.setParameter("startDate", CalendarUtils.startOfDay(startDate));
        query.setParameter("endDate", CalendarUtils.endOfDay(endDate));
        List result2 = query.getResultList();
        if (result2 != null) {
            for (Object obj : result2) {
                Object[] row = (Object[]) obj;
                Date date2 = (Date)row[1];
                Long amount = (Long)row[0];
                Long idOfOrg = (Long)row[2];;
                if (map.containsKey(date2)) {
                    amount = amount + ((Long[])map.get(date2))[0];
                }
                Long[] arr = new Long[2];
                arr[0] = amount;
                arr[1] = idOfOrg;
                map.put(date2, arr);
            }
        }
        return map;
    }

    public boolean isAcceptableComplex(PreorderComplexItemExt complex, ClientGroup clientGroup,
            Boolean hasDiscount, PreorderGoodParamsContainer container, String ageTypeGroup,
            List<CategoryDiscount> clientDiscountsList) {
        if (clientGroup == null) return false;
        ////Если комплекс не льготный и нет централизованной видимости, то не включаем его в результат
        //if (!complex.getDiscount() && complex.getModeVisible() != 1)
        //    return false;
        String clientGroupName = clientGroup.getGroupName();
        Integer goodType = container.getGoodType();
        Integer ageGroup = container.getAgeGroup();

        //Для дошкольников
        if (ageTypeGroup != null && ageTypeGroup.toUpperCase().contains("ДОШКОЛ"))
        {
            //У дошкольника есть льгота и комплекс НЕ платный
            if (hasDiscount && complex.getDiscount()) {
                // тип возрастной группы 1,5-3 ИЛИ 3-7
                if (ageGroup.equals(GoodAgeGroupType.G_1_5_3.getCode()) || ageGroup.equals(GoodAgeGroupType.G_3_7.getCode())
                     //в названии комплекса присутствует %1,5-3% ИЛИ %3-7%
                    || complex.getComplexName().contains("1,5-3") || complex.getComplexName().contains("3-7")) {
                    //Смотрим льготы дошкольника
                    for (CategoryDiscount categoryDiscount: clientDiscountsList)
                    {
                        //Если льготы указаны правильно, то...
                        if ((categoryDiscount.getCategoryName().contains("1,5-3") && ageGroup.equals(GoodAgeGroupType.G_1_5_3.getCode()))
                                || (categoryDiscount.getCategoryName().contains("3-7") && ageGroup.equals(GoodAgeGroupType.G_3_7.getCode())))
                        {
                            return true;
                        }
                    }
                    return false;
                }
            }
            return false;
        }

        if(!container.getAgeGroup().equals(GoodAgeGroupType.UNSPECIFIED.getCode())
                && !container.getGoodType().equals(GoodType.UNSPECIFIED.getCode())){
            if (!hasDiscount) {
                // 1.2.1.1. если навазвание группы начинается на 1-, 2-, 3-, 4-, то выводим следующие комплексы:
                if (clientGroupName.startsWith("1-") || clientGroupName.startsWith("2-") || clientGroupName.startsWith("3-") || clientGroupName.startsWith("4-")) {
                    // а) комплексы с парамтером discount = true при условии наличия у них в названии "Завтрак" + "(1-4)"
                    if ((complex.getDiscount() && goodType.equals(GoodType.BREAKFAST.getCode()) && ageGroup.equals(GoodAgeGroupType.G_1_4.getCode())) ||
                            // б) комплексы с параметром discount = false при условии наличия у них в названии "(1-4)"
                            (!complex.getDiscount() && ageGroup.equals(GoodAgeGroupType.G_1_4.getCode())) ||
                            // в) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                            (!complex.getDiscount()
                                    && ageGroup.equals(GoodAgeGroupType.UNSPECIFIED.getCode()))) {
                        return true;
                    }
                }
                // 1.2.1.2. если навазвание группы начинается на 5-, 6-, 7-, 8-, 9-, 10-, 11-, 12-, то выводим следующие комплексы:
                else if (clientGroupName.startsWith("5-") || clientGroupName.startsWith("6-") || clientGroupName.startsWith("7-") || clientGroupName.startsWith("8-") || clientGroupName.startsWith("9-")
                        || clientGroupName.startsWith("10-") || clientGroupName.startsWith("11-") || clientGroupName.startsWith("12-")) {
                    // а) комплексы с параметром discount = false при условии наличия у них в названии "(5-11)"
                    if ((!complex.getDiscount() && ageGroup.equals(GoodAgeGroupType.G_5_11.getCode())) ||
                    // в) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11) ("
                            (!complex.getDiscount()
                            && ageGroup.equals(GoodAgeGroupType.UNSPECIFIED.getCode()))) {
                        return true;
                    }
                }
                // 1.2.1.3. если навазвание группы начинается на другие символы (отличные от двух предыдущих условий), то выводим следующие комплексы:
                else {
                    // а) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                    if (!complex.getDiscount()
                            && ageGroup.equals(GoodAgeGroupType.UNSPECIFIED.getCode())) {
                        return true;
                    }
                }
            } else {
                // 1.2.2.1. если навазвание группы начинается на 1-, 2-, 3-, 4-, то выводим следующие комплексы:
                if (clientGroupName.startsWith("1-") || clientGroupName.startsWith("2-") || clientGroupName.startsWith("3-") || clientGroupName.startsWith("4-")) {
                    // а) комплексы с парамтером discount = true при условии наличия у них в названии "(1-4)"
                    if ((complex.getDiscount() && ageGroup.equals(GoodAgeGroupType.G_1_4.getCode())) ||
                            // б) комплексы с параметром discount = false при условии наличия у них в названии "(1-4)"
                            (!complex.getDiscount() && ageGroup.equals(GoodAgeGroupType.G_1_4.getCode())) ||
                            // в) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                            (!complex.getDiscount() && ageGroup.equals(GoodAgeGroupType.UNSPECIFIED.getCode()))) {
                        return true;
                    }
                }
                // 1.2.2.2. если навазвание группы начинается на 5-, 6-, 7-, 8-, 9-, 10-, 11-, 12-, то выводим следующие комплексы:
                else if (clientGroupName.startsWith("5-") || clientGroupName.startsWith("6-") || clientGroupName.startsWith("7-") || clientGroupName.startsWith("8-") || clientGroupName.startsWith("9-")
                        || clientGroupName.startsWith("10-") || clientGroupName.startsWith("11-") || clientGroupName.startsWith("12-")) {
                    // а) комплексы с парамтером discount = true при условии наличия у них в названии "(5-11)"
                    if ((complex.getDiscount() && ageGroup.equals(GoodAgeGroupType.G_5_11.getCode())) ||
                            // б) комплексы с параметром discount = false при условии наличия у них в названии "(5-11)"
                            (!complex.getDiscount() && ageGroup.equals(GoodAgeGroupType.G_5_11.getCode())) ||
                            // в) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                            (!complex.getDiscount() && ageGroup.equals(GoodAgeGroupType.UNSPECIFIED.getCode()))) {
                        return true;
                    }
                }
                // 1.2.2.3. если навазвание группы начинается на другие символы (отличные от двух предыдущих условий), то выводим следующие комплексы:
                else {
                    // а) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                    if (!complex.getDiscount() && ageGroup.equals(GoodAgeGroupType.UNSPECIFIED.getCode())) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            if (!hasDiscount) {
                // 1.2.1.1. если навазвание группы начинается на 1-, 2-, 3-, 4-, то выводим следующие комплексы:
                if (clientGroupName.startsWith("1-") || clientGroupName.startsWith("2-") || clientGroupName.startsWith("3-") || clientGroupName.startsWith("4-")) {
                    // а) комплексы с парамтером discount = true при условии наличия у них в названии "Завтрак" + "(1-4)"
                    if (complex.getDiscount() && complex.getComplexName().toLowerCase().contains("завтрак") && complex.getComplexName().contains("1-4") ||
                            // б) комплексы с параметром discount = false при условии наличия у них в названии "(1-4)"
                            !complex.getDiscount() && complex.getComplexName().contains("1-4") ||
                            // в) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                            !complex.getDiscount() && (!complex.getComplexName().contains("1-4") && !complex.getComplexName().contains("5-11"))) {
                        return true;
                    }
                }
                // 1.2.1.2. если навазвание группы начинается на 5-, 6-, 7-, 8-, 9-, 10-, 11-, 12-, то выводим следующие комплексы:
                else if (clientGroupName.startsWith("5-") || clientGroupName.startsWith("6-") || clientGroupName.startsWith("7-") || clientGroupName.startsWith("8-") || clientGroupName.startsWith("9-")
                        || clientGroupName.startsWith("10-") || clientGroupName.startsWith("11-") || clientGroupName.startsWith("12-")) {
                    // а) комплексы с параметром discount = false при условии наличия у них в названии "(5-11)"
                    if (!complex.getDiscount() && complex.getComplexName().contains("5-11") ||
                            // б) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                            !complex.getDiscount() && (!complex.getComplexName().contains("1-4") && !complex.getComplexName().contains("5-11"))) {
                        return true;
                    }
                }
                // 1.2.1.3. если навазвание группы начинается на другие символы (отличные от двух предыдущих условий), то выводим следующие комплексы:
                else {
                    // а) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                    if (!complex.getDiscount() && (!complex.getComplexName().contains("1-4") && !complex.getComplexName().contains("5-11"))) {
                        return true;
                    }
                }
            } else {
                // 1.2.2.1. если навазвание группы начинается на 1-, 2-, 3-, 4-, то выводим следующие комплексы:
                if (clientGroupName.startsWith("1-") || clientGroupName.startsWith("2-") || clientGroupName.startsWith("3-") || clientGroupName.startsWith("4-")) {
                    // а) комплексы с парамтером discount = true при условии наличия у них в названии "(1-4)"
                    if (complex.getDiscount() && complex.getComplexName().contains("1-4") ||
                            // б) комплексы с параметром discount = false при условии наличия у них в названии "(1-4)"
                            !complex.getDiscount() && complex.getComplexName().contains("1-4") ||
                            // в) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                            !complex.getDiscount() && (!complex.getComplexName().contains("1-4") && !complex.getComplexName().contains("5-11"))) {
                        return true;
                    }
                }
                // 1.2.2.2. если навазвание группы начинается на 5-, 6-, 7-, 8-, 9-, 10-, 11-, 12-, то выводим следующие комплексы:
                else if (clientGroupName.startsWith("5-") || clientGroupName.startsWith("6-") || clientGroupName.startsWith("7-") || clientGroupName.startsWith("8-") || clientGroupName.startsWith("9-")
                        || clientGroupName.startsWith("10-") || clientGroupName.startsWith("11-") || clientGroupName.startsWith("12-")) {
                    // а) комплексы с парамтером discount = true при условии наличия у них в названии "(5-11)"
                    if (complex.getDiscount() && complex.getComplexName().contains("5-11") ||
                            // б) комплексы с параметром discount = false при условии наличия у них в названии "(5-11)"
                            !complex.getDiscount() && complex.getComplexName().contains("5-11") ||
                            // в) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                            !complex.getDiscount() && (!complex.getComplexName().contains("1-4") && !complex.getComplexName().contains("5-11"))) {
                        return true;
                    }
                }
                // 1.2.2.3. если навазвание группы начинается на другие символы (отличные от двух предыдущих условий), то выводим следующие комплексы:
                else {
                    // а) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                    if (!complex.getDiscount() && (!complex.getComplexName().contains("(1-4)") && !complex.getComplexName().contains("(5-11)"))) {
                        return true;
                    }
                }
            }
            return false;
        }
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
                for (Map.Entry<Client, ClientWithAddInfo> entry : cd.getClients().entrySet()) {
                    if (entry.getValue() == null) continue;
                    ClientSummaryBase base = processSummaryBase(entry.getKey());
                    base.setGuardianCreatedWhere(entry.getValue().getClientCreatedFrom().getValue());
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
            Map<Client, ClientWithAddInfo> clients = extractClientsFromGuardByGuardMobile(Client.checkAndConvertMobile(mobile));
            if (!clients.isEmpty()) {
                boolean onlyNotActiveCG = true;
                for (Map.Entry<Client, ClientWithAddInfo> entry : clients.entrySet()) {
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

    public Map<Client, ClientWithAddInfo> extractClientsFromGuardByGuardMobile(String guardMobile) throws Exception {
        Map<Client, ClientWithAddInfo> result = new HashMap<Client, ClientWithAddInfo>();
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
                        ClientWithAddInfo addInfo = new ClientWithAddInfo();
                        if (!cg.isDisabled()) {
                            addInfo.setClientCreatedFrom(cg.getCreatedFrom());
                            addInfo.setInformedSpecialMenu(cg.getInformedSpecialMenu() ? 1 : 0);
                            //result.put((Client) row[0], cg.getCreatedFrom());
                        }
                        result.put((Client) row[0], addInfo);
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
                        ClientWithAddInfo addInfo = new ClientWithAddInfo();
                        addInfo.setClientCreatedFrom(ClientCreatedFromType.DEFAULT);
                        addInfo.setInformedSpecialMenu(0);
                        result.put(c, addInfo);
                    }
                }
            }
        }

        return result;
    }

    private Calendar getCalendar() {
        TimeZone timeZone = RuntimeContext.getInstance().getLocalTimeZone(null);
        Calendar c = Calendar.getInstance();
        c.setTimeZone(timeZone);
        return c;
    }



    @Transactional(readOnly = true)
    public Map<String, Integer[]> getSpecialDates(Date today, Integer syncCountDays, Long orgId, Client client) throws Exception {
        Comparator comparator = new PreorderDateComparator();
        Map map = new TreeMap(comparator);

        Calendar c = getCalendar();
        c.setTime(today);

        Date endDate = CalendarUtils.addDays(today, syncCountDays);                   //14 календарных дней вперед

        Map<Date, Long[]> usedAmounts = existPreordersByDate(client.getIdOfClient(), today, endDate);                 //для показа есть ли предзаказы по датам
        List<SpecialDate> specialDates = DAOReadonlyService.getInstance().getSpecialDates(today, endDate, orgId);   //выходные дни по ОО в целом или ее группам
        Integer forbiddenDays = DAOUtils.getPreorderFeedingForbiddenDays(client);                                   //дни запрета редактирования
        List<ProductionCalendar> productionCalendar = DAOReadonlyService.getInstance().getProductionCalendar(today, endDate);

        int two_days = 0;
        while (c.getTimeInMillis() < endDate.getTime() ){
            Date currentDate = CalendarUtils.startOfDayInUTC(c.getTime());
            Boolean isWeekend = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class).isWeekendByProductionCalendar(currentDate, productionCalendar);
            if (isWeekend && two_days == 0) two_days++;

            if (two_days <= forbiddenDays) {
                c.add(Calendar.DATE, 1);
                map.put(CalendarUtils.dateToString(currentDate), new Integer[] {1, usedAmounts.get(currentDate) == null ? 0 : usedAmounts.get(currentDate)[0].intValue(),
                        usedAmounts.get(currentDate) == null ? client.getOrg().getIdOfOrg().intValue() : usedAmounts.get(currentDate)[1].intValue()});
                if (!isWeekend) {
                    two_days++;
                }
                continue; //находимся в днях запрета редактирования
            }

            /*int day = CalendarUtils.getDayOfWeek(currentDate);
            Boolean isWeekendSD = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class)
                    .isWeekendBySpecialDate(currentDate, client, specialDates); //выходной по данным таблицы SpecialDates
            if (isWeekendSD == null) { //нет данных по дню в КУД
                if (day == Calendar.SATURDAY) {
                    String groupName = DAOReadonlyService.getInstance().getClientGroupName(client);
                    isWeekend = !DAOReadonlyService.getInstance().isSixWorkWeek(orgId, groupName);
                }
            } else {
                isWeekend = isWeekendSD;
            }*/
            isWeekend = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class)
                    .isWeekendBySpecialDateAndSixWorkWeek(isWeekend, currentDate, client.getIdOfClientGroup(), client.getOrg().getIdOfOrg(), specialDates);
            if (!isWeekend) isWeekend = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class)
                    .isHolidayByProductionCalendar(currentDate, productionCalendar); //если праздничный день по производственному календарю - то запрет редактирования

            c.add(Calendar.DATE, 1);
            map.put(CalendarUtils.dateToString(currentDate), new Integer[] {isWeekend ? 1 : 0, usedAmounts.get(currentDate) == null ? 0 : usedAmounts.get(currentDate)[0].intValue(),
                    usedAmounts.get(currentDate) == null ? client.getOrg().getIdOfOrg().intValue() : usedAmounts.get(currentDate)[1].intValue()});
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
    public void deletePreordersByClient(Client client) throws Exception {
        Query query = em.createQuery("select pc from PreorderComplex pc where pc.client = :client and pc.preorderDate > :date and pc.deletedState = false");
        query.setParameter("client", client);
        query.setParameter("date", new Date());
        List<PreorderComplex> list = query.getResultList();
        if (list.size() == 0) return;
        /*Integer days = DAOUtils.getPreorderFeedingForbiddenDays(client.getOrg().getIdOfOrg());
        if (days != null) days++;
        else {
            days = PreorderComplex.DEFAULT_FORBIDDEN_DAYS + 1;
        }
        Date dateFrom = CalendarUtils.startOfDay(CalendarUtils.addDays(new Date(), days));*/
        Date dateFrom = getStartDateForGeneratePreordersInternal(client);
        long nextVersion = nextVersionByPreorderComplex();
        for (PreorderComplex preorderComplex : list) {
            preorderComplex.deleteByChangeOrg(nextVersion, preorderComplex.getPreorderDate().after(dateFrom));
            for (PreorderMenuDetail preorderMenuDetail : preorderComplex.getPreorderMenuDetails()) {
                preorderMenuDetail.deleteByChangeOrg(nextVersion, preorderComplex.getPreorderDate().after(dateFrom));
                em.merge(preorderMenuDetail);
            }
            em.merge(preorderComplex);
        }
        query = em.createQuery("update RegularPreorder set deletedState = true, lastUpdate = :date where client = :client and endDate > :date");
        query.setParameter("date", new Date());
        query.setParameter("client", client);
        query.executeUpdate();
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
