/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SubscriberFeedingSettingSettingValue;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.partner.preorder.dataflow.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
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
    public void saveToken(SudirToken token) {
        em.merge(token);
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
        Query query = emReport.createQuery("select c, pc.amount, pc.deletedState from PreorderComplex pc right join pc.complexInfo c "
                + "where (pc.client.idOfClient = :idOfClient or pc.client.idOfClient is null) "
                + "and (pc.deletedState = false or pc.deletedState is null) and c.menuDate between :startDate and :endDate and c.usedSpecialMenu = 1 "
                + "and c.org.idOfOrg = :idOfOrg");
        query.setParameter("idOfClient", client.getIdOfClient());
        query.setParameter("startDate", CalendarUtils.startOfDay(date));
        query.setParameter("endDate", CalendarUtils.endOfDay(date));
        query.setParameter("idOfOrg", client.getOrg().getIdOfOrg());
        Map<String, List<PreorderComplexItemExt>> map = new TreeMap<String, List<PreorderComplexItemExt>>();
        List res = query.getResultList();
        List<PreorderComplexItemExt> list = new ArrayList<PreorderComplexItemExt>();
        for (Object o : res) {
            Object[] row = (Object[]) o;
            ComplexInfo ci = (ComplexInfo) row[0];
            Integer amount = (Integer) row[1];
            Boolean deleted = (Boolean) row[2];
            PreorderComplexItemExt complexItemExt = new PreorderComplexItemExt(ci);
            complexItemExt.setAmount(amount == null ? 0 : amount);
            complexItemExt.setSelected(deleted == null ? false : !deleted);

            List<PreorderMenuItemExt> menuItemExtList = getMenuItemsExt(ci.getIdOfComplexInfo(), client.getIdOfClient());
            complexItemExt.setMenuItemExtList(menuItemExtList);
            list.add(complexItemExt);
        }
        result.setComplexItemExtList(list);
        for (PreorderComplexItemExt item : list) {
            String group = getPreorderComplexGroup(item);
            if (group == null) continue;
            List<PreorderComplexItemExt> list2 = map.get(group);
            if (list2 == null) list2 = new ArrayList<PreorderComplexItemExt>();
            list2.add(item);
            map.put(group, list2);
        }

        groupResult.setComplexesWithGroups(map);
        return groupResult;
    }

    private String getPreorderComplexGroup(PreorderComplexItemExt item) {
        if (item.getComplexType() == 4 && match(item, "завтрак")) {
            return "2.Завтрак составной из блюд на выбор";
        }
        if (item.getComplexType() == 4 && match(item, "обед")) {
            return "4.Обед составной из блюд на выбор";
        }
        if (item.getComplexType() == 4 && match(item, "полдник")) {
            return "6.Полдник составной из блюд на выбор";
        }
        if (item.getComplexType() == 4 && match(item, "ужин")) {
            return "8.Ужин составной из блюд на выбор";
        }
        if (match(item, "завтрак")) {
            return "1.Завтрак";
        }
        if (match(item, "обед")) {
            return "3.Обед";
        }
        if (match(item, "полдник")) {
            return "5.Полдник";
        }
        if (match(item, "ужин")) {
            return "7.Ужин";
        }
        return null;
    }

    private boolean match(PreorderComplexItemExt item, String str) {
        return item.getComplexName().toLowerCase().indexOf(str) > -1;
    }

    private List<PreorderMenuItemExt> getMenuItemsExt (Long idOfComplexInfo, Long idOfClient) {
        List<PreorderMenuItemExt> menuItemExtList = new ArrayList<PreorderMenuItemExt>();
        Query query = emReport.createQuery("select md, pmd.amount, pmd.deletedState from PreorderMenuDetail pmd "
                + "right join pmd.complexInfoDetail cid right join cid.menuDetail md "
                + "where cid.complexInfo.idOfComplexInfo = :idOfComplexInfo and (pmd.complexInfo.idOfComplexInfo = :idOfComplexInfo or pmd.complexInfo.idOfComplexInfo is null) "
                + "and (pmd.client.idOfClient = :idOfClient or pmd.client.idOfClient is null) "
                + "and (pmd.deletedState = false or pmd.deletedState is null)");

        query.setParameter("idOfComplexInfo", idOfComplexInfo);
        query.setParameter("idOfClient", idOfClient);
        List res = query.getResultList();
        for (Object o : res) {
            Object[] row = (Object[]) o;
            MenuDetail menuDetail = (MenuDetail) row[0];
            Integer amount = (Integer) row[1];
            Boolean deleted = (Boolean) row[2];
            PreorderMenuItemExt menuItemExt = new PreorderMenuItemExt(menuDetail);
            menuItemExt.setAmount(amount == null ? 0 : amount);
            menuItemExt.setSelected(deleted == null ? false : !deleted);
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
                if (group.getGroupName().equals(groupName)) return true;
            }
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public void savePreorderComplexes(PreorderSaveListParam list) throws Exception {
        Long contractId = list.getContractId();
        Client client = getClientByContractId(contractId);
        Date date = CalendarUtils.parseDate(list.getDate());
        Date startDate = CalendarUtils.startOfDay(date);
        Date endDate = CalendarUtils.endOfDay(date);
        long nextVersion = nextVersionByPreorderComplex();

        /*Query queryComplexUpdate = em.createQuery("update PreorderComplex p set p.amount = :amount, p.selected = :selected, p.version = :version where p.idOfPreorderComplex = :id");
        queryComplexUpdate.setParameter("version", nextVersion);*/

        Query queryComplexSelect = em.createQuery("select p from PreorderComplex p "
                + "where p.client.idOfClient = :idOfClient and p.complexInfo.idOfComplexInfo = :idOfComplexInfo and p.preorderDate between :startDate and :endDate");
        queryComplexSelect.setParameter("idOfClient", client.getIdOfClient());
        queryComplexSelect.setParameter("startDate", startDate);
        queryComplexSelect.setParameter("endDate", endDate);

        Query queryMenuSelect = em.createQuery("select m from PreorderMenuDetail m "
                + "where m.client.idOfClient = :idOfClient and m.complexInfo.idOfComplexInfo = :idOfComplexInfo and m.menuDetail.idOfMenuDetail = :idOfMenuDetail");
        queryMenuSelect.setParameter("idOfClient", client.getIdOfClient());

        for (ComplexListParam complex : list.getComplexes()) {
            Integer complexAmount = complex.getAmount();
            Long idOfComplex = complex.getIdOfComplex();
            boolean complexSelected = (complexAmount > 0);
            /*for (MenuItemParam menuItem : complex.getMenuItems()) {
                if (menuItem.getAmount() > 0) {
                    complexSelected = true;
                    break;
                }
            }*/

            ComplexInfo complexInfo = em.find(ComplexInfo.class, idOfComplex);
            queryComplexSelect.setParameter("idOfComplexInfo", idOfComplex);
            PreorderComplex preorderComplex;
            try {
                preorderComplex = (PreorderComplex) queryComplexSelect.getSingleResult();
                preorderComplex.setAmount(complex.getAmount());
                preorderComplex.setVersion(nextVersion);
                em.merge(preorderComplex);
                /*queryComplexUpdate.setParameter("id", preorderComplex.getIdOfPreorderComplex());
                queryComplexUpdate.setParameter("amount", complex.getAmount());
                queryComplexUpdate.setParameter("selected", complexSelected);
                queryComplexUpdate.executeUpdate();*/
            } catch (NoResultException e ) {

                preorderComplex = new PreorderComplex();
                preorderComplex.setComplexInfo(complexInfo);
                preorderComplex.setClient(client);
                preorderComplex.setPreorderDate(date);
                preorderComplex.setAmount(complexAmount);
                preorderComplex.setVersion(nextVersion);
                preorderComplex.setDeletedState(false);
                em.merge(preorderComplex);
            }
            for (MenuItemParam menuItem : complex.getMenuItems()) {
                queryMenuSelect.setParameter("idOfComplexInfo", preorderComplex.getComplexInfo().getIdOfComplexInfo());
                queryMenuSelect.setParameter("idOfMenuDetail", menuItem.getIdOfMenuDetail());
                PreorderMenuDetail preorderMenuDetail;
                try {
                    preorderMenuDetail = (PreorderMenuDetail)queryMenuSelect.getSingleResult();
                    preorderMenuDetail.setAmount(menuItem.getAmount());
                    em.merge(preorderMenuDetail);
                } catch (NoResultException e) {
                    MenuDetail menuDetail = em.find(MenuDetail.class, menuItem.getIdOfMenuDetail());
                    preorderMenuDetail = new PreorderMenuDetail();
                    preorderMenuDetail.setComplexInfo(complexInfo);
                    preorderMenuDetail.setMenuDetail(menuDetail);
                    preorderMenuDetail.setComplexInfoDetail(getComplexInfoDetail(complexInfo.getIdOfComplexInfo(), menuDetail.getIdOfMenuDetail()));
                    preorderMenuDetail.setClient(client);
                    preorderMenuDetail.setPreorderDate(date);
                    preorderMenuDetail.setAmount(menuItem.getAmount());
                    preorderMenuDetail.setDeletedState(false);
                    em.merge(preorderMenuDetail);
                }
            }
        }
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

    private long nextVersionByPreorderComplex() {
        long version = 0L;
        Query query = em.createQuery("select t.version from PreorderComplex as t order by t.version desc");
        query.setMaxResults(1);
        query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        try {
            Long v = (Long) query.getSingleResult();
            version = v + 1;
        } catch (NoResultException e) { }

        return version;
    }
}
