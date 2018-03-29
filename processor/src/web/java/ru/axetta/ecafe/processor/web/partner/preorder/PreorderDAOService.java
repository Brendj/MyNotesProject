/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SubscriberFeedingSettingSettingValue;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientSummaryBase;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientSummaryBaseListResult;
import ru.axetta.ecafe.processor.web.partner.preorder.dataflow.*;

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

    @Transactional(readOnly = true)
    public PreorderListWithComplexesGroupResult getPreorderComplexesWithMenuList(Long contractId, Date date) {
        PreorderListWithComplexesGroupResult groupResult = new PreorderListWithComplexesGroupResult();
        PreorderListWithComplexesResult result = new PreorderListWithComplexesResult();
        Client client = getClientByContractId(contractId);
        Set<CategoryDiscount> clientDiscounts = client.getCategories();
        Boolean hasDiscount = false;
        for (CategoryDiscount categoryDiscount : clientDiscounts) {
            hasDiscount |= (categoryDiscount.getCategoryType() == CategoryDiscountEnumType.CATEGORY_WITH_DISCOUNT);
        }

        Query query = emReport.createNativeQuery("select ci.idofcomplexinfo, pc.amount, pc.deletedState, o.organizationtype "
                + " from cf_preorder_complex pc inner join cf_clients c on pc.idofclient = c.idofclient "
                + " right outer join cf_complexinfo ci on (c.idoforg = ci.idoforg and ci.menudate = pc.preorderdate and ci.idofcomplex = pc.armcomplexid) "
                + " inner join cf_orgs o on o.IdOfOrg = ci.IdOfOrg "
                + " where (pc.idofclient = :idOfClient or pc.idofclient is null) and (ci.MenuDate between :startDate and :endDate) "
                + " and (ci.UsedSpecialMenu=1 or ci.ModeFree=1) and ci.IdOfOrg=:idOfOrg "
                + " and (o.OrganizationType = :school or o.OrganizationType = :professional) order by ci.modeOfAdd");
        query.setParameter("idOfClient", client.getIdOfClient());
        query.setParameter("startDate", CalendarUtils.startOfDay(date).getTime());
        query.setParameter("endDate", CalendarUtils.endOfDay(date).getTime());
        query.setParameter("idOfOrg", client.getOrg().getIdOfOrg());
        query.setParameter("school", OrganizationType.SCHOOL.getCode());
        query.setParameter("professional", OrganizationType.PROFESSIONAL.getCode());
        Map<String, Map<String, List<PreorderComplexItemExt>>> map = new TreeMap<String, Map<String, List<PreorderComplexItemExt>>>();
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
            complexItemExt.setMenuItemExtList(menuItemExtList);
            list.add(complexItemExt);
        }
        result.setComplexItemExtList(list);
        for (PreorderComplexItemExt item : list) {
            if (isAcceptableComplex(item, client, hasDiscount)) {
                String group = getPreorderComplexGroup(item);
                if (group == null) continue;
                String subgroup = getPreorderComplexSubgroup(item);
                Map<String, List<PreorderComplexItemExt>> submap = map.get(group);
                if (submap == null)
                    submap = new TreeMap<String, List<PreorderComplexItemExt>>();
                List<PreorderComplexItemExt> list2 = submap.get(subgroup);
                if (list2 == null)
                    list2 = new ArrayList<PreorderComplexItemExt>();
                list2.add(item);
                submap.put(subgroup, list2);
                map.put(group, submap);
            }
        }

        groupResult.setComplexesWithGroups(map);
        return groupResult;
    }

    private String getPreorderComplexGroup(PreorderComplexItemExt item) {
        if (match(item, "завтрак")) {
            return "1.Завтрак";
        }
        if (match(item, "обед")) {
            return "2.Обед";
        }
        if (match(item, "полдник")) {
            return "3.Полдник";
        }
        if (match(item, "ужин")) {
            return "4.Ужин";
        }
        return null;
    }

    private String getPreorderComplexSubgroup(PreorderComplexItemExt item) {
        if (item.getDiscount()) {
            return "1.За счет средств бюджета города Москвы";
        } else {
            return "2.За счет средств представителей обучающихся";
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

    private Client getClientByContractId(Long contractId) {
        Query query = emReport.createQuery("select c from Client c where c.contractId = :contractId");
        query.setParameter("contractId", contractId);
        return (Client)query.getSingleResult();
    }

    public SpecialDate getSpecialDate(Date date, Long idOfOrg) {
        try {
            Query query = emReport.createQuery(
                    "select sd from SpecialDate sd where sd.compositeIdOfSpecialDate.date = :date and sd.compositeIdOfSpecialDate.idOfOrg = :idOfOrg");
            query.setParameter("date", date);
            query.setParameter("idOfOrg", idOfOrg);
            return (SpecialDate) query.getSingleResult();
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

    public long nextVersionByPreorderComplex() {
        Session session = (Session)em.getDelegate();
        return DAOUtils.nextVersionByPreorderComplex(session);
    }

    @Transactional(readOnly = true)
    public boolean existPreordersByDate(Long contractId, Date date) {
        Client client = getClientByContractId(contractId);
        Query query = emReport.createQuery("select count(p.idOfPreorderComplex) from PreorderComplex p "
                + "where p.amount > 0 and p.client.idOfClient = :idOfClient and p.preorderDate between :startDate and :endDate");
        query.setParameter("idOfClient", client.getIdOfClient());
        query.setParameter("startDate", CalendarUtils.startOfDay(date));
        query.setParameter("endDate", CalendarUtils.endOfDay(date));
        Long cnt = (Long) query.getSingleResult();
        if (cnt > 0) return true;
        query = emReport.createQuery("select count(p.idOfPreorderMenuDetail) from PreorderMenuDetail p "
                + "where p.amount > 0 and p.client.idOfClient = :idOfClient and p.preorderDate between :startDate and :endDate");
        query.setParameter("idOfClient", client.getIdOfClient());
        query.setParameter("startDate", CalendarUtils.startOfDay(date));
        query.setParameter("endDate", CalendarUtils.endOfDay(date));
        Long cnt2 = (Long) query.getSingleResult();
        if (cnt2 > 0) return true;
        else return false;
    }

    private boolean isAcceptableComplex(PreorderComplexItemExt complex, Client client, Boolean hasDiscount) {
        String clientGroupName = client.getClientGroup().getGroupName();
        if (hasDiscount) {
            // 1.2.1.1. если навазвание группы начинается на 1-, 2-, 3-, 4-, то выводим следующие комплексы:
            if (clientGroupName.startsWith("1-") || clientGroupName.startsWith("2-") ||
                    clientGroupName.startsWith("3-") || clientGroupName.startsWith("4-")) {
                // а) комплексы с парамтером discount = true при условии наличия у них в названии "Завтрак" + "(1-4)"
                if (complex.getDiscount() && complex.getComplexName().toLowerCase().contains("завтрак")
                        && complex.getComplexName().contains("1-4") ||
                        // б) комплексы с параметром discount = false при условии наличия у них в названии "(1-4)"
                        !complex.getDiscount() && complex.getComplexName().contains("1-4") ||
                        // в) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                        !complex.getDiscount() && (!complex.getComplexName().contains("1-4") ||
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
                        !complex.getDiscount() && (!complex.getComplexName().contains("1-4") ||
                                !complex.getComplexName().contains("5-11"))) {
                    return true;
                }
            }
            // 1.2.1.3. если навазвание группы начинается на другие символы (отличные от двух предыдущих условий), то выводим следующие комплексы:
            else {
                // а) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                if (!complex.getDiscount() && (!complex.getComplexName().contains("4-11")
                        || !complex.getComplexName().contains("5-11"))) {
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
                        !complex.getDiscount() && (!complex.getComplexName().contains("1-4") ||
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
                        !complex.getDiscount() && (!complex.getComplexName().contains("1-4") ||
                                !complex.getComplexName().contains("5-11"))) {
                    return true;
                }
            }
            // 1.2.2.3. если навазвание группы начинается на другие символы (отличные от двух предыдущих условий), то выводим следующие комплексы:
            else {
                // а) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                if (!complex.getDiscount() && (!complex.getComplexName().contains("(1-4)") ||
                        !complex.getComplexName().contains("(5-11)"))) {
                    return true;
                }
            }
        }
        return false;
    }
}
