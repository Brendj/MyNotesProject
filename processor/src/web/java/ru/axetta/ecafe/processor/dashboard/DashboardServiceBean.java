/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.dashboard;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.SyncHistory;
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

    private DashboardResponse prepareDashboardResponse() {
        DashboardResponse dashboardResponse = new DashboardResponse();
        dashboardResponse.setEduInstItemInfoList(new LinkedList<DashboardResponse.EduInstItemInfo>());
        dashboardResponse.setPaymentSystemItemInfoList(new LinkedList<DashboardResponse.PaymentSystemItemInfo>());
        return dashboardResponse;
    }

    private DashboardResponse getOrgInfo(DashboardResponse dashboardResponse) throws SystemException {

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        try {
            entityTransaction.begin();
            Query query = entityManager.createQuery(
                    "SELECT DISTINCT " + "org.idOfOrg, " + "org.lastSuccessfulBalanceSync, "
                            + "org.lastUnSuccessfulBalanceSync, " + "min(sh.syncStartTime) AS firstSyncTime, "
                            + "(SELECT ish FROM SyncHistory ish WHERE ish.syncStartTime = max(sh.syncStartTime)) AS lastSyncHistoryRecord, "
                            + "(SELECT count(*) FROM Client cl WHERE cl.org.idOfOrg = org.idOfOrg AND cl.contractState = :contractState AND cl.clientGroup.compositeIdOfClientGroup.idOfClientGroup BETWEEN :studentsMinValue AND :studentsMaxValue) AS numOfStudents, "
                            + "(SELECT count(*) FROM Client cl WHERE cl.org.idOfOrg = org.idOfOrg AND cl.contractState = :contractState AND cl.clientGroup.compositeIdOfClientGroup.idOfClientGroup BETWEEN :staffMinValue AND :staffMaxValue) AS numOfStaff, "
                            + "(SELECT count(*) FROM EnterEvent eev WHERE eev.org.idOfOrg = org.idOfOrg AND eev.client.clientGroup.compositeIdOfClientGroup.idOfClientGroup BETWEEN :studentsMinValue AND :studentsMaxValue AND eev.visitDateTime BETWEEN :dayStart AND :dayEnd) AS numOfStudentsEnterEvents, "
                            + "(SELECT count(*) FROM EnterEvent eev WHERE eev.org.idOfOrg = org.idOfOrg AND eev.client.clientGroup.compositeIdOfClientGroup.idOfClientGroup BETWEEN :staffMinValue AND :staffMaxValue AND eev.visitDateTime BETWEEN :dayStart AND :dayEnd) AS numOfStaffEnterEvents, "
                            + "(SELECT count(*) FROM Order order WHERE order.org.idOfOrg = org.idOfOrg AND order.socDiscount > 0 AND order.client.clientGroup.compositeIdOfClientGroup.idOfClientGroup BETWEEN :studentsMinValue AND :studentsMaxValue AND order.createTime BETWEEN :dayStart AND :dayEnd) AS numOfStudentSocMenu, "
                            + "(SELECT count(*) FROM Order order WHERE order.org.idOfOrg = org.idOfOrg AND order.socDiscount = 0 AND order.client.clientGroup.compositeIdOfClientGroup.idOfClientGroup BETWEEN :studentsMinValue AND :studentsMaxValue AND order.createTime BETWEEN :dayStart AND :dayEnd) AS numOfStudentMenu, "
                            + "(SELECT count(*) FROM Order order WHERE order.org.idOfOrg = org.idOfOrg AND order.socDiscount > 0 AND order.client.clientGroup.compositeIdOfClientGroup.idOfClientGroup BETWEEN :staffMinValue AND :staffMaxValue AND order.createTime BETWEEN :dayStart AND :dayEnd) AS numOfStaffSocMenu, "
                            + "(SELECT count(*) FROM Order order WHERE order.org.idOfOrg = org.idOfOrg AND order.socDiscount = 0 AND order.client.clientGroup.compositeIdOfClientGroup.idOfClientGroup BETWEEN :staffMinValue AND :staffMaxValue AND order.createTime BETWEEN :dayStart AND :dayEnd) AS numOfStaffMenu "
                            + "FROM Org org LEFT OUTER JOIN org.syncHistories sh GROUP BY org");

            query.setParameter("contractState", Client.ACTIVE_CONTRACT_STATE);
            query.setParameter("studentsMinValue", ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN.getValue());
            query.setParameter("studentsMaxValue", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("staffMinValue", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("staffMaxValue", ClientGroup.Predefined.CLIENT_PARENTS.getValue());

            Calendar cal = Calendar.getInstance();
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
            query.setParameter("dayEnd", dayEndDate);

            List queryResult = query.getResultList();
            List<DashboardResponse.EduInstItemInfo> eduInstItemInfoList = dashboardResponse.getEduInstItemInfoList();
            for (Object object : queryResult) {
                DashboardResponse.EduInstItemInfo eduInstItemInfo = new DashboardResponse.EduInstItemInfo();
                try {
                    Object[] result = (Object[]) object;

                    eduInstItemInfo.setIdOfOrg((Long) result[0]);
                    eduInstItemInfo.setLastSuccessfulBalanceSyncTime((Date) result[1]);
                    eduInstItemInfo.setLastUnSuccessfulBalanceSyncTime((Date) result[2]);
                    eduInstItemInfo.setFirstFullSyncTime((Date) result[3]);

                    SyncHistory syncHistory = (SyncHistory) result[4];
                    if (syncHistory != null) {
                        eduInstItemInfo.setLastFullSyncTime(syncHistory.getSyncStartTime());
                    }
                    eduInstItemInfo.setLastSyncErrors(syncHistory.getSyncResult() != 0);

                    long numOfStudents = (Long) result[5];
                    long numOfStaff = (Long) result[6];

                    long numOfStudentEnterEvents = (Long) result[7];
                    long numOfStaffEnterEvents = (Long) result[8];

                    long numOfStudentSocMenu = (Long) result[9];
                    long numOfStudentMenu = (Long) result[10];

                    long numOfStaffSocMenu = (Long) result[11];
                    long numOfStaffMenu = (Long) result[12];

                    if (numOfStudents > 0) {
                        eduInstItemInfo.setNumberOfPassagesPerNumOfStudents(numOfStudentEnterEvents / numOfStudents);
                        eduInstItemInfo.setNumberOfPaidMealsPerNumOfStudents(numOfStudentMenu / numOfStudents);
                        eduInstItemInfo
                                .setNumberOfReducedPriceMealsPerNumOfStudents(numOfStudentSocMenu / numOfStudents);
                    }

                    if (numOfStaff > 0) {
                        eduInstItemInfo.setNumberOfPassagesPerNumOfStaff(numOfStaffEnterEvents / numOfStaff);
                        eduInstItemInfo.setNumberOfPaidMealsPerNumOfStaff(numOfStaffMenu / numOfStaff);
                        eduInstItemInfo.setNumberOfReducedPriceMealsPerNumOfStaff(numOfStaffSocMenu / numOfStaff);
                    }
                } catch (Exception e) {
                    eduInstItemInfo.setError(e.getMessage());
                } finally {
                    eduInstItemInfoList.add(eduInstItemInfo);
                }
            }
            entityTransaction.commit();
        } catch (Exception e) {
            entityTransaction.rollback();
        }

        return dashboardResponse;
    }

    private Date getCurrentDayStartTime(Calendar currentTimeStamp) {
        int month = currentTimeStamp.get(Calendar.MONTH);
        int year = currentTimeStamp.get(Calendar.YEAR);
        int dom = currentTimeStamp.get(Calendar.DAY_OF_MONTH);

        Calendar dayStart = Calendar.getInstance();
        dayStart.set(year, month, dom, 0, 0, 0);

        Date dayStartDate = dayStart.getTime();        
        return dayStartDate;
    }
    
    private Date getCurrentDayEndTime(Calendar currentTimeStamp) {
        int month = currentTimeStamp.get(Calendar.MONTH);
        int year = currentTimeStamp.get(Calendar.YEAR);
        int dom = currentTimeStamp.get(Calendar.DAY_OF_MONTH);

        Calendar dayEnd = Calendar.getInstance();
        dayEnd.set(year, month, dom + 1, 0, 0, 0);

        Date dayEndDate = dayEnd.getTime();
        return dayEndDate;
    }

    private DashboardResponse getPaymentSystemInfo(DashboardResponse dashboardResponse) throws SystemException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        try {
            entityTransaction.begin();
            Query query = entityManager.createQuery(
                    "SELECT DISTINCT contragent.idOfContragent, " + "max(clientPayment.createTime), "
                            + "(SELECT count(clientPayment) FROM clientPayment WHERE clientPayment.createTime BETWEEN :dayStart AND :dayEnd) "
                            + "FROM Contragent contragent LEFT OUTER JOIN contragent.clientPayments clientPayment GROUP BY contragent.idOfContragent");

            
            Calendar currentTimeStamp = Calendar.getInstance();
            Date dayStart = getCurrentDayStartTime(currentTimeStamp);
            Date dayEnd = getCurrentDayEndTime(currentTimeStamp);

            query.setParameter("dayStart", dayStart);
            query.setParameter("dayEnd", dayEnd);

            List queryResult = query.getResultList();
            List<DashboardResponse.PaymentSystemItemInfo> paymentSystemItemInfoList = dashboardResponse
                    .getPaymentSystemItemInfoList();
            for (Object object : queryResult) {
                DashboardResponse.PaymentSystemItemInfo paymentSystemItemInfo = new DashboardResponse.PaymentSystemItemInfo();
                try {
                    Object[] result = (Object[]) object;
                    paymentSystemItemInfo.setIdOfContragent((Long) result[0]);
                    paymentSystemItemInfo.setLastOperationTime((Date) result[1]);
                    paymentSystemItemInfo.setNumOfOperations((Long) result[2]);
                } catch (Exception e) {
                    paymentSystemItemInfo.setError(e.getMessage());
                } finally {
                    paymentSystemItemInfoList.add(paymentSystemItemInfo);
                }
            }

            entityTransaction.commit();
        } catch (Exception e) {
            entityTransaction.rollback();
        }

        return dashboardResponse;
    }

    public DashboardResponse getInfoForDashboard() throws SystemException {

        DashboardResponse dashboardResponse = prepareDashboardResponse();
        dashboardResponse = getOrgInfo(dashboardResponse);
        dashboardResponse = getPaymentSystemInfo(dashboardResponse);
        return dashboardResponse;

    }
}
