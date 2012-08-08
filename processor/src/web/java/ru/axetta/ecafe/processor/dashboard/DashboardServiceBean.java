/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.dashboard;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.dashboard.data.DashboardResponse;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.*;
import javax.transaction.SystemException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 06.08.12
 * Time: 12:39
 * To change this template use File | Settings | File Templates.
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class DashboardServiceBean {

    @PersistenceUnit(unitName = "processor")
    EntityManagerFactory entityManagerFactory;

    public DashboardResponse getInfoForDashboard() throws SystemException {
        //EntityManager entityManager = entityManagerFactory.createEntityManager();
        /*EntityManager entityManager = entityManagerFactory.createEntityManager();
        DashboardResponse result = new DashboardResponse();
        DashboardResponse.EduInstItemInfo eduInstItemInfo = new DashboardResponse.EduInstItemInfo();
        Map<String, Date> lastOperationTimePerPaymentSystem = new HashMap<String, Date>();
        lastOperationTimePerPaymentSystem.put("sberbank", new Date());
        eduInstItemInfo.setLastOperationTimePerPaymentSystem(lastOperationTimePerPaymentSystem);
        List<DashboardResponse.EduInstItemInfo> eduInstItemInfoList = new LinkedList<DashboardResponse.EduInstItemInfo>();
        eduInstItemInfoList.add(eduInstItemInfo);
        result.setEduInstItemInfoList(eduInstItemInfoList);
        return result;*/

        DashboardResponse dashboardResponse = new DashboardResponse();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction utx = entityManager.getTransaction();

        try {
            utx.begin();
            Query query = entityManager.createQuery(
                    "SELECT DISTINCT "
                            + "org.idOfOrg, "
                            + "min(sh.syncStartTime) AS firstSyncTime, "
                            + "(SELECT ish FROM SyncHistory ish WHERE ish.syncStartTime = max(sh.syncStartTime)) AS lastSyncHistoryRecord, "
                            + "count(eev), "
                            + "count(cl) AS numOfClients "
                 + "FROM Org org LEFT OUTER JOIN org.syncHistories sh LEFT OUTER JOIN org.enterEvents eev LEFT OUTER JOIN org.clients cl WHERE cl.contractState=:contractState GROUP BY org");

            query.setParameter("contractState", Client.ACTIVE_CONTRACT_STATE);
            /*Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);
            int dom = cal.get(Calendar.DAY_OF_MONTH);

            Calendar dayStart = Calendar.getInstance();
            dayStart.set(year, month, dom, 0, 0, 0);
            Calendar dayEnd = Calendar.getInstance();
            dayEnd.set(year, month, dom + 1, 0, 0, 0);

            Date dayStartDate = dayStart.getTime();
            Date dayEndDate = dayEnd.getTime();

            query.setParameter("dayStart", dayStartDate);
            query.setParameter("dayEnd", dayEndDate);*/

            List queryResult = query.getResultList();
            List<DashboardResponse.EduInstItemInfo> eduInstItemInfoList = new LinkedList<DashboardResponse.EduInstItemInfo>();
            for (Object object : queryResult) {
                DashboardResponse.EduInstItemInfo eduInstItemInfo = new DashboardResponse.EduInstItemInfo();
                eduInstItemInfo.setFirstFullSyncTime((Date) ((Object[]) object)[2]);
                eduInstItemInfo.setLastFullSyncTime((Date) ((Object[]) object)[1]);

                Object firstResult = ((Object[]) object)[0];
                Object secondResult = ((Object[]) object)[1];
                Object thirdResult = ((Object[]) object)[2];
                Object fourthResult = ((Object[]) object)[3];
                eduInstItemInfoList.add(eduInstItemInfo);
            }
            dashboardResponse.setEduInstItemInfoList(eduInstItemInfoList);
            Class clazz = queryResult.get(0).getClass();
            utx.commit();
        } catch (Exception e) {
            utx.rollback();
        }

        return dashboardResponse;
    }

}
