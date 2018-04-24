/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SubscriberFeedingSettingSettingValue;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.SubscriptionFeedingService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientSummaryBase;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientSummaryBaseListResult;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientsWithResultCode;
import ru.axetta.ecafe.processor.web.partner.preorder.dataflow.*;

import org.apache.cxf.common.util.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
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

    private boolean isSpecialConfigDate(Client client, Date date) {
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
            hasDiscount |= (categoryDiscount.getCategoryType() == CategoryDiscountEnumType.CATEGORY_WITH_DISCOUNT);
        }

        Query query = emReport.createNativeQuery("select ci.idofcomplexinfo, pc.amount, pc.deletedState "
                + " from cf_complexinfo ci join cf_orgs o on o.idoforg = ci.idoforg "
                + " left outer join cf_preorder_complex pc on (ci.idoforg = :idOfOrg and pc.idOfClient = :idOfClient and ci.menudate = pc.preorderdate and ci.idofcomplex = pc.armcomplexid) "
                + " where ci.MenuDate between :startDate and :endDate "
                + " and (ci.UsedSpecialMenu=1 or ci.ModeFree=1) and ci.idoforg = :idOfOrg "
                + " and (o.OrganizationType = :school or o.OrganizationType = :professional) and ci.modevisible = 1 order by ci.modeOfAdd");
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
            Integer deleted = (Integer) row[2];
            PreorderComplexItemExt complexItemExt = new PreorderComplexItemExt(ci);
            complexItemExt.setAmount(amount == null ? 0 : amount);
            complexItemExt.setSelected(deleted == null ? false : deleted == 1);

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
                   + "AND pmd.preorderdate BETWEEN :startDate AND :endDate AND pmd.armidofmenu = md.localidofmenu) AS amount, "
                + "(SELECT pmd.deletedState FROM cf_preorder_menudetail pmd WHERE pmd.idofclient = :idOfClient "
                    + "AND pmd.preorderdate BETWEEN :startDate AND :endDate AND pmd.armidofmenu = md.localidofmenu) AS deletedState "
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
            Integer deleted = (Integer) row[2];
            PreorderMenuItemExt menuItemExt = new PreorderMenuItemExt(menuDetail);
            menuItemExt.setAmount(amount == null ? 0 : amount);
            menuItemExt.setSelected(deleted == null ? false : deleted==1);
            menuItemExtList.add(menuItemExt);
        }
        return menuItemExtList;
    }

    public Client getClientByContractId(Long contractId) {
        Query query = emReport.createQuery("select c from Client c where c.contractId = :contractId");
        query.setParameter("contractId", contractId);
        return (Client)query.getSingleResult();
    }

    public Long getIdOfClientByContractId(Long contractId) {
        Query query = emReport.createQuery("select c.idOfClient from Client c where c.contractId = :contractId");
        query.setParameter("contractId", contractId);
        return (Long)query.getSingleResult();
    }

    public List<SpecialDate> getSpecialDates(Date startDate, Date endDate, Long idOfOrg) {
        try {
            Query query = emReport.createQuery(
                    "select sd from SpecialDate sd where sd.compositeIdOfSpecialDate.date between :startDate and :endDate and sd.compositeIdOfSpecialDate.idOfOrg = :idOfOrg");
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            query.setParameter("idOfOrg", idOfOrg);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isSixWorkWeek(Long orgId) throws Exception {
        DAOService daoService = DAOService.getInstance();
        List<ECafeSettings> settings = daoService
                .geteCafeSettingses(orgId, SettingsIds.SubscriberFeeding, false);
        boolean isSixWorkWeek = false;
        if(!settings.isEmpty()){
            ECafeSettings cafeSettings = settings.get(0);
            SubscriberFeedingSettingSettingValue parser =
                    (SubscriberFeedingSettingSettingValue) cafeSettings.getSplitSettingValue();
            isSixWorkWeek = parser.isSixWorkWeek();
        }
        return isSixWorkWeek;
    }

    public boolean isWeekendByGroup(Long idOfOrg, String groupName) {
        Query query = emReport.createQuery("select gnto from GroupNamesToOrgs gnto where gnto.idOfOrg = :idOfOrg and gnto.isSixDaysWorkWeek = true");
        query.setParameter("idOfOrg", idOfOrg);
        List<GroupNamesToOrgs> list = query.getResultList() ;
        if (list != null && list.size() > 0) {
            for (GroupNamesToOrgs group : list) {
                if (group.getGroupName().equals(groupName)) return false;
            }
        }
        return true;
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
                + "where p.client.idOfClient = :idOfClient and p.armComplexId = :idOfComplexInfo and p.preorderDate between :startDate and :endDate");
        queryComplexSelect.setParameter("idOfClient", client.getIdOfClient());
        queryComplexSelect.setParameter("startDate", startDate);
        queryComplexSelect.setParameter("endDate", endDate);

        Query queryMenuSelect = em.createQuery("select m from PreorderMenuDetail m "
                + "where m.client.idOfClient = :idOfClient and m.preorderComplex.idOfPreorderComplex = :idOfPreorderComplex and m.armIdOfMenu = :armIdOfMenu");
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

            queryComplexSelect.setParameter("idOfComplexInfo", idOfComplex);
            PreorderComplex preorderComplex = null;
            try {
                preorderComplex = (PreorderComplex) queryComplexSelect.getSingleResult();
                preorderComplex.setAmount(complex.getAmount());
                preorderComplex.setDeletedState(!complexSelected);
                preorderComplex.setVersion(nextVersion);
            } catch (NoResultException e ) {
                if (complexSelected) {
                    preorderComplex = createPreorderComplex(idOfComplex, client, date, complexAmount, nextVersion);
                }
            }

            if (preorderComplex == null) continue;

            Set<PreorderMenuDetail> set = new HashSet<PreorderMenuDetail>();
            for (MenuItemParam menuItem : complex.getMenuItems()) {
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
                    preorderMenuDetail = new PreorderMenuDetail();
                    preorderMenuDetail.setPreorderComplex(preorderComplex);
                    preorderMenuDetail.setArmIdOfMenu(menuItem.getIdOfMenuDetail());
                    preorderMenuDetail.setClient(client);
                    preorderMenuDetail.setPreorderDate(date);
                    preorderMenuDetail.setAmount(menuItem.getAmount());
                    preorderMenuDetail.setDeletedState(false);
                }
                set.add(preorderMenuDetail);
            }
            preorderComplex.setPreorderMenuDetails(set);
            em.merge(preorderComplex);
        }
    }

    private PreorderComplex createPreorderComplex(Integer idOfComplex, Client client, Date date, Integer complexAmount, Long nextVersion) {
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
        return preorderComplex;
    }

    private ComplexInfoDetail getComplexInfoDetail(Long idOfComplexInfo, Long idOfMenuDetail) {
        try {
            Query query = em.createQuery("select d from ComplexInfoDetail d "
                    + "where d.complexInfo.idOfComplexInfo = :idOfComplexInfo and d.menuDetail.idOfMenuDetail = :idOfMenuDetail");
            query.setParameter("idOfComplexInfo", idOfComplexInfo);
            query.setParameter("idOfMenuDetail", idOfMenuDetail);
            return (ComplexInfoDetail) query.getSingleResult();
        } catch (Exception e) {
            logger.error(String.format("Error getComplexInfoDetail (ci=%s, md=%s)", idOfComplexInfo, idOfMenuDetail), e);
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
}
