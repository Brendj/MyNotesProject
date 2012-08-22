/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.dashboard;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.SyncHistory;
import ru.axetta.ecafe.processor.dashboard.data.DashboardResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.*;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
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
@Component
@Scope("singleton")
public class DashboardServiceBean {

    private static final int ID_OF_ORG_PARAM_INDEX = 0;
    private static final int ORG_NAME_PARAM_INDEX = ID_OF_ORG_PARAM_INDEX + 1;
    private static final int LAST_SUCCESSFUL_BALANCE_SYNC_TYME_PARAM_INDEX = ORG_NAME_PARAM_INDEX + 1;
    private static final int LAST_UNSUCCESSFUL_BALANCE_SYNC_TYME_PARAM_INDEX =
            LAST_SUCCESSFUL_BALANCE_SYNC_TYME_PARAM_INDEX + 1;
    private static final int FIRST_FULL_SYNC_TIME_PARAM_INDEX = LAST_UNSUCCESSFUL_BALANCE_SYNC_TYME_PARAM_INDEX + 1;
    private static final int SYNC_HISTORY_PARAM_INDEX = FIRST_FULL_SYNC_TIME_PARAM_INDEX + 1;
    private static final int NUM_OF_STUDENTS_PARAM_INDEX = SYNC_HISTORY_PARAM_INDEX + 1;
    private static final int NUM_OF_STAFF_PARAM_INDEX = NUM_OF_STUDENTS_PARAM_INDEX + 1;
    private static final int NUM_OF_STUDENT_ENTER_EVENTS_PARAM_INDEX = NUM_OF_STAFF_PARAM_INDEX + 1;
    private static final int NUM_OF_STAFF_ENTER_EVENTS_PARAM_INDEX = NUM_OF_STUDENT_ENTER_EVENTS_PARAM_INDEX + 1;
    private static final int NUM_OF_STUDENT_SOC_MENU_PARAM_INDEX = NUM_OF_STAFF_ENTER_EVENTS_PARAM_INDEX + 1;
    private static final int NUM_OF_STUDENT_MENU_PARAM_INDEX = NUM_OF_STUDENT_SOC_MENU_PARAM_INDEX + 1;
    private static final int NUM_OF_STAFF_SOC_MENU_PARAM_INDEX = NUM_OF_STUDENT_MENU_PARAM_INDEX + 1;
    private static final int NUM_OF_STAFF_MENU_PARAM_INDEX = NUM_OF_STAFF_SOC_MENU_PARAM_INDEX + 1;

    private static final int ID_OF_CONTRAGENT_PARAM_INDEX = 0;
    private static final int CONTRAGENT_NAME_PARAM_INDEX = ID_OF_CONTRAGENT_PARAM_INDEX + 1;
    private static final int LAST_OPERATION_TIME_PARAM_INDEX = CONTRAGENT_NAME_PARAM_INDEX + 1;
    private static final int NUM_OF_OPERATIONS_PARAM_INDEX = LAST_OPERATION_TIME_PARAM_INDEX + 1;

    @Autowired
    PlatformTransactionManager txManager;

    @PersistenceContext
    EntityManager entityManager;

    private DashboardResponse prepareDashboardResponse() {
        DashboardResponse dashboardResponse = new DashboardResponse();
        dashboardResponse.setEduInstItemInfoList(new LinkedList<DashboardResponse.EduInstItemInfo>());
        dashboardResponse.setPaymentSystemItemInfoList(new LinkedList<DashboardResponse.PaymentSystemItemInfo>());
        return dashboardResponse;
    }

    private DashboardResponse getOrgInfo(DashboardResponse dashboardResponse) throws Exception {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txManager.getTransaction(def);

        try {
            Query query = entityManager.createQuery(
                    "SELECT DISTINCT " + "org.idOfOrg, org.officialName, " + "org.lastSuccessfulBalanceSync, "
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
                            + "FROM Org org LEFT OUTER JOIN org.syncHistoriesInternal sh GROUP BY org");

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
            Date timestamp = new Date();

            List<DashboardResponse.EduInstItemInfo> eduInstItemInfoList = dashboardResponse.getEduInstItemInfoList();
            for (Object object : queryResult) {
                DashboardResponse.EduInstItemInfo eduInstItemInfo = new DashboardResponse.EduInstItemInfo();
                try {
                    eduInstItemInfo.setTimestamp(timestamp);
                    Object[] result = (Object[]) object;

                    eduInstItemInfo.setIdOfOrg((Long) result[ID_OF_ORG_PARAM_INDEX]);
                    eduInstItemInfo.setOrgName((String) result[ORG_NAME_PARAM_INDEX]);
                    eduInstItemInfo.setLastSuccessfulBalanceSyncTime(
                            (Date) result[LAST_SUCCESSFUL_BALANCE_SYNC_TYME_PARAM_INDEX]);
                    eduInstItemInfo.setLastUnSuccessfulBalanceSyncTime(
                            (Date) result[LAST_UNSUCCESSFUL_BALANCE_SYNC_TYME_PARAM_INDEX]);
                    eduInstItemInfo.setFirstFullSyncTime((Date) result[FIRST_FULL_SYNC_TIME_PARAM_INDEX]);

                    SyncHistory syncHistory = (SyncHistory) result[SYNC_HISTORY_PARAM_INDEX];
                    if (syncHistory != null) {
                        eduInstItemInfo.setLastFullSyncTime(syncHistory.getSyncStartTime());
                    }
                    eduInstItemInfo.setLastSyncErrors(syncHistory.getSyncResult() != 0);

                    long numOfStudents = (Long) result[NUM_OF_STUDENTS_PARAM_INDEX];
                    long numOfStaff = (Long) result[NUM_OF_STAFF_PARAM_INDEX];

                    long numOfStudentEnterEvents = (Long) result[NUM_OF_STUDENT_ENTER_EVENTS_PARAM_INDEX];
                    long numOfStaffEnterEvents = (Long) result[NUM_OF_STAFF_ENTER_EVENTS_PARAM_INDEX];

                    long numOfStudentSocMenu = (Long) result[NUM_OF_STUDENT_SOC_MENU_PARAM_INDEX];
                    long numOfStudentMenu = (Long) result[NUM_OF_STUDENT_MENU_PARAM_INDEX];

                    long numOfStaffSocMenu = (Long) result[NUM_OF_STAFF_SOC_MENU_PARAM_INDEX];
                    long numOfStaffMenu = (Long) result[NUM_OF_STAFF_MENU_PARAM_INDEX];

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
        } catch (Exception e) {
            txManager.rollback(status);
            throw e;
        }
        txManager.commit(status);

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

    private DashboardResponse getPaymentSystemInfo(DashboardResponse dashboardResponse) throws Exception {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txManager.getTransaction(def);
        try {
            Query query = entityManager.createQuery(
                    "SELECT DISTINCT contragent.idOfContragent, contragent.contragentName, "
                            + "max(clientPayment.createTime), "
                            + "(SELECT count(clientPayment) FROM clientPayment WHERE clientPayment.createTime BETWEEN :dayStart AND :dayEnd) "
                            + "FROM Contragent contragent LEFT OUTER JOIN contragent.clientPaymentsInternal clientPayment GROUP BY contragent.idOfContragent");


            Calendar currentTimeStamp = Calendar.getInstance();
            Date dayStart = getCurrentDayStartTime(currentTimeStamp);
            Date dayEnd = getCurrentDayEndTime(currentTimeStamp);

            query.setParameter("dayStart", dayStart);
            query.setParameter("dayEnd", dayEnd);

            List queryResult = query.getResultList();
            Date timestamp = new Date();

            List<DashboardResponse.PaymentSystemItemInfo> paymentSystemItemInfoList = dashboardResponse
                    .getPaymentSystemItemInfoList();
            for (Object object : queryResult) {
                DashboardResponse.PaymentSystemItemInfo paymentSystemItemInfo = new DashboardResponse.PaymentSystemItemInfo();
                try {
                    paymentSystemItemInfo.setTimestamp(timestamp);
                    Object[] result = (Object[]) object;
                    paymentSystemItemInfo.setIdOfContragent((Long) result[ID_OF_CONTRAGENT_PARAM_INDEX]);
                    paymentSystemItemInfo.setContragentName((String) result[CONTRAGENT_NAME_PARAM_INDEX]);
                    paymentSystemItemInfo.setLastOperationTime((Date) result[LAST_OPERATION_TIME_PARAM_INDEX]);
                    paymentSystemItemInfo.setNumOfOperations((Long) result[NUM_OF_OPERATIONS_PARAM_INDEX]);
                } catch (Exception e) {
                    paymentSystemItemInfo.setError(e.getMessage());
                } finally {
                    paymentSystemItemInfoList.add(paymentSystemItemInfo);
                }
            }
        } catch (Exception e) {
            txManager.rollback(status);
            throw e;
        }
        txManager.commit(status);
        return dashboardResponse;
    }

    public DashboardResponse getInfoForDashboard() throws Exception {

        DashboardResponse dashboardResponse = prepareDashboardResponse();
        dashboardResponse = getOrgInfo(dashboardResponse);
        dashboardResponse = getPaymentSystemInfo(dashboardResponse);
        return dashboardResponse;

    }
}
