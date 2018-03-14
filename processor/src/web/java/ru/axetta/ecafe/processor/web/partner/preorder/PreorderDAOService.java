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
import ru.axetta.ecafe.processor.web.partner.preorder.dataflow.PreorderComplexItemExt;
import ru.axetta.ecafe.processor.web.partner.preorder.dataflow.PreorderListWithComplexesResult;
import ru.axetta.ecafe.processor.web.partner.preorder.dataflow.PreorderMenuItemExt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public PreorderListWithComplexesResult getPreorderComplexesWithMenuList(Long contractId, Date date) {
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
        //query.setParameter("deletedState", false);
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
        return result;
    }

    private List<PreorderMenuItemExt> getMenuItemsExt (Long idOfComplexInfo, Long idOfClient) {
        List<PreorderMenuItemExt> menuItemExtList = new ArrayList<PreorderMenuItemExt>();
        //Query query = emReport.createQuery("select md from ComplexInfoDetail cid join cid.menuDetail md where cid.complexInfo.idOfComplexInfo = :idOfComplexInfo")
        //        .setParameter("idOfComplexInfo", idOfComplexInfo);
        /*Query query = emReport.createQuery("select md from ComplexInfoDetail cid, MenuDetail md, PreorderMenuDetail pmd "
                + "where cid.menuDetail.idOfMenuDetail = md.idOfMenuDetail and (md.idOfMenuDetail = pmd.menuDetail.idOfMenuDetail or pmd.menuDetail.idOfMenuDetail is null) "
                + "and cid.complexInfo.idOfComplexInfo = :idOfComplexInfo and (pmd.client.idOfClient = :idOfClient or pmd.client.idOfClient is null)");*/
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
}
