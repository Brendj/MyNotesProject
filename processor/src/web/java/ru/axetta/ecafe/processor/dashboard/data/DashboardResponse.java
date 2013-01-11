/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.dashboard.data;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 02.08.12
 * Time: 19:15
 * To change this template use File | Settings | File Templates.
 */
public class DashboardResponse {

    public static class OrgBasicStatItem {

        long idOfOrg;
        String orgName;
        String orgNameNumber;
        String orgTag;
        String orgDistrict;
        String orgLocation;
        Date lastSuccessfulBalanceSyncTime;
        long numberOfEnterEvents;
        long numberOfDiscountOrders;
        long numberOfPayOrders;
        long numberOfStudentClients;
        long numberOfNonStudentClients;
        double numberOfStudentsWithEnterEventsPercent;
        double numberOfEmployeesWithEnterEventsPercent;
        double numberOfStudentsWithDiscountOrdersPercent;
        double numberOfEmployeesWithDiscountOrdersPercent;
        double numberOfStudentsWithPayedOrdersPercent;
        double numberOfEmployeesWithPayedOrdersPercent;
        private Long numberOfClientsWithoutCard;

        public long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public String getOrgTag() {
            return orgTag;
        }

        public void setOrgTag(String orgTag) {
            this.orgTag = orgTag;
        }

        public String getOrgNameNumber() {
            return orgNameNumber;
        }

        public void setOrgNameNumber(String orgNameNumber) {
            this.orgNameNumber = orgNameNumber;
        }

        public Date getLastSuccessfulBalanceSyncTime() {
            return lastSuccessfulBalanceSyncTime;
        }

        public void setLastSuccessfulBalanceSyncTime(Date lastSuccessfulBalanceSyncTime) {
            this.lastSuccessfulBalanceSyncTime = lastSuccessfulBalanceSyncTime;
        }

        public long getNumberOfEnterEvents() {
            return numberOfEnterEvents;
        }

        public void setNumberOfEnterEvents(long numberOfEnterEvents) {
            this.numberOfEnterEvents = numberOfEnterEvents;
        }

        public long getNumberOfDiscountOrders() {
            return numberOfDiscountOrders;
        }

        public void setNumberOfDiscountOrders(long numberOfDiscountOrders) {
            this.numberOfDiscountOrders = numberOfDiscountOrders;
        }

        public long getNumberOfPayOrders() {
            return numberOfPayOrders;
        }

        public void setNumberOfPayOrders(long numberOfPayOrders) {
            this.numberOfPayOrders = numberOfPayOrders;
        }

        public long getNumberOfStudentClients() {
            return numberOfStudentClients;
        }

        public void setNumberOfStudentClients(long numberOfStudentClients) {
            this.numberOfStudentClients = numberOfStudentClients;
        }

        public long getNumberOfNonStudentClients() {
            return numberOfNonStudentClients;
        }

        public void setNumberOfNonStudentClients(long numberOfNonStudentClients) {
            this.numberOfNonStudentClients = numberOfNonStudentClients;
        }

        public String getOrgDistrict() {
            return orgDistrict;
        }

        public void setOrgDistrict(String orgDistrict) {
            this.orgDistrict = orgDistrict;
        }

        public String getOrgLocation() {
            return orgLocation;
        }

        public void setOrgLocation(String orgLocation) {
            this.orgLocation = orgLocation;
        }

        public void setNumberOfClientsWithoutCard(Long numberOfClientsWithoutCard) {
            this.numberOfClientsWithoutCard = numberOfClientsWithoutCard;
        }

        public Long getNumberOfClientsWithoutCard() {
            return numberOfClientsWithoutCard;
        }

        public double getNumberOfStudentsWithEnterEventsPercent() {
            return numberOfStudentsWithEnterEventsPercent;
        }

        public void setNumberOfStudentsWithEnterEventsPercent(double numberOfStudentsWithEnterEventsPercent) {
            this.numberOfStudentsWithEnterEventsPercent = numberOfStudentsWithEnterEventsPercent;
        }

        public double getNumberOfEmployeesWithEnterEventsPercent() {
            return numberOfEmployeesWithEnterEventsPercent;
        }

        public void setNumberOfEmployeesWithEnterEventsPercent(double numberOfEmployeesWithEnterEventsPercent) {
            this.numberOfEmployeesWithEnterEventsPercent = numberOfEmployeesWithEnterEventsPercent;
        }

        public double getNumberOfStudentsWithPayedOrdersPercent() {
            return numberOfStudentsWithPayedOrdersPercent;
        }

        public void setNumberOfStudentsWithPayedOrdersPercent(double numberOfStudentsWithPayedOrdersPercent) {
            this.numberOfStudentsWithPayedOrdersPercent = numberOfStudentsWithPayedOrdersPercent;
        }

        public double getNumberOfEmployeesWithPayedOrdersPercent() {
            return numberOfEmployeesWithPayedOrdersPercent;
        }

        public void setNumberOfEmployeesWithPayedOrdersPercent(double numberOfEmployeesWithPayedOrdersPercent) {
            this.numberOfEmployeesWithPayedOrdersPercent = numberOfEmployeesWithPayedOrdersPercent;
        }

        public double getNumberOfStudentsWithDiscountOrdersPercent() {
            return numberOfStudentsWithDiscountOrdersPercent;
        }

        public void setNumberOfStudentsWithDiscountOrdersPercent(double numberOfStudentsWithDiscountOrdersPercent) {
            this.numberOfStudentsWithDiscountOrdersPercent = numberOfStudentsWithDiscountOrdersPercent;
        }

        public double getNumberOfEmployeesWithDiscountOrdersPercent() {
            return numberOfEmployeesWithDiscountOrdersPercent;
        }

        public void setNumberOfEmployeesWithDiscountOrdersPercent(double numberOfEmployeesWithDiscountOrdersPercent) {
            this.numberOfEmployeesWithDiscountOrdersPercent = numberOfEmployeesWithDiscountOrdersPercent;
        }
    }

    public static class OrgBasicStats {

        LinkedList<OrgBasicStatItem> orgBasicStatItems = new LinkedList<OrgBasicStatItem>();

        public LinkedList<OrgBasicStatItem> getOrgBasicStatItems() {
            return orgBasicStatItems;
        }

        public void setOrgBasicStatItems(LinkedList<OrgBasicStatItem> orgBasicStatItems) {
            this.orgBasicStatItems = orgBasicStatItems;
        }
    }


    public static class PaymentSystemStatItem {

        long idOfContragent;
        String contragentName;
        Date lastOperationTime;
        long numOfOperations;

        String error;
        Date timestamp;

        public long getIdOfContragent() {
            return idOfContragent;
        }

        public void setIdOfContragent(long idOfContragent) {
            this.idOfContragent = idOfContragent;
        }

        public String getContragentName() {
            return contragentName;
        }

        public void setContragentName(String contragentName) {
            this.contragentName = contragentName;
        }

        public Date getLastOperationTime() {
            return lastOperationTime;
        }

        public void setLastOperationTime(Date lastOperationTime) {
            this.lastOperationTime = lastOperationTime;
        }

        public long getNumOfOperations() {
            return numOfOperations;
        }

        public void setNumOfOperations(long numOfOperations) {
            this.numOfOperations = numOfOperations;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }
    }

    public static class PaymentSystemStats {

        LinkedList<PaymentSystemStatItem> paymentSystemItemInfos = new LinkedList<PaymentSystemStatItem>();

        public LinkedList<PaymentSystemStatItem> getPaymentSystemItemInfos() {
            return paymentSystemItemInfos;
        }

        public void setPaymentSystemItemInfos(LinkedList<PaymentSystemStatItem> paymentSystemItemInfos) {
            this.paymentSystemItemInfos = paymentSystemItemInfos;
        }
    }


    public static class OrgSyncStatItem {

        private String orgName;
        private Date lastSuccessfulBalanceSync;
        private Date lastUnSuccessfulBalanceSync;
        private String remoteAddr;
        private String version;

        public OrgSyncStatItem(String orgName, Date lastSuccessfulBalanceSync, Date lastUnSuccessfulBalanceSync,
                String remoteAddr, String version) {
            this.orgName = orgName;
            this.lastSuccessfulBalanceSync = lastSuccessfulBalanceSync;
            this.lastUnSuccessfulBalanceSync = lastUnSuccessfulBalanceSync;
            this.remoteAddr = remoteAddr;
            this.version = version;
        }

        public String getOrgName() {
            return orgName;
        }

        public Date getLastSuccessfulBalanceSync() {
            return lastSuccessfulBalanceSync;
        }

        public Date getLastUnSuccessfulBalanceSync() {
            return lastUnSuccessfulBalanceSync;
        }

        public String getRemoteAddr() {
            return remoteAddr;
        }

        public String getVersion() {
            return version;
        }
    }

    public static class OrgSyncStats {

        LinkedList<OrgSyncStatItem> orgSyncStatItems;

        public LinkedList<OrgSyncStatItem> getOrgSyncStatItems() {
            return orgSyncStatItems;
        }

        public void setOrgSyncStatItems(LinkedList<OrgSyncStatItem> orgSyncStatItems) {
            this.orgSyncStatItems = orgSyncStatItems;
        }
    }


    public static class EduInstItemInfo {

        long idOfOrg;
        String orgName;
        String orgNameNumber;
        Date lastFullSyncTime;
        Date firstFullSyncTime;
        Date lastSuccessfulBalanceSyncTime;
        Date lastUnSuccessfulBalanceSyncTime;
        boolean lastSyncErrors;


        double numberOfPassagesPerNumOfStaff;
        double numberOfPassagesPerNumOfStudents;

        double numberOfPaidMealsPerNumOfStaff;
        double numberOfPaidMealsPerNumOfStudents;

        double numberOfReducedPriceMealsPerNumOfStaff;
        double numberOfReducedPriceMealsPerNumOfStudents;

        String error;
        Date timestamp;

        public long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public String getOrgNameNumber() {
            return orgNameNumber;
        }

        public void setOrgNameNumber(String orgNameNumber) {
            this.orgNameNumber = orgNameNumber;
        }

        public Date getLastFullSyncTime() {
            return lastFullSyncTime;
        }

        public void setLastFullSyncTime(Date lastFullSyncTime) {
            this.lastFullSyncTime = lastFullSyncTime;
        }

        public Date getFirstFullSyncTime() {
            return firstFullSyncTime;
        }

        public void setFirstFullSyncTime(Date firstFullSyncTime) {
            this.firstFullSyncTime = firstFullSyncTime;
        }

        public Date getLastSuccessfulBalanceSyncTime() {
            return lastSuccessfulBalanceSyncTime;
        }

        public void setLastSuccessfulBalanceSyncTime(Date lastSuccessfulBalanceSyncTime) {
            this.lastSuccessfulBalanceSyncTime = lastSuccessfulBalanceSyncTime;
        }

        public Date getLastUnSuccessfulBalanceSyncTime() {
            return lastUnSuccessfulBalanceSyncTime;
        }

        public void setLastUnSuccessfulBalanceSyncTime(Date lastUnSuccessfulBalanceSyncTime) {
            this.lastUnSuccessfulBalanceSyncTime = lastUnSuccessfulBalanceSyncTime;
        }

        public boolean isLastSyncErrors() {
            return lastSyncErrors;
        }

        public void setLastSyncErrors(boolean lastSyncErrors) {
            this.lastSyncErrors = lastSyncErrors;
        }

        public double getNumberOfPassagesPerNumOfStaff() {
            return numberOfPassagesPerNumOfStaff;
        }

        public void setNumberOfPassagesPerNumOfStaff(double numberOfPassagesPerNumOfStaff) {
            this.numberOfPassagesPerNumOfStaff = numberOfPassagesPerNumOfStaff;
        }

        public double getNumberOfPassagesPerNumOfStudents() {
            return numberOfPassagesPerNumOfStudents;
        }

        public void setNumberOfPassagesPerNumOfStudents(double numberOfPassagesPerNumOfStudents) {
            this.numberOfPassagesPerNumOfStudents = numberOfPassagesPerNumOfStudents;
        }

        public double getNumberOfPaidMealsPerNumOfStaff() {
            return numberOfPaidMealsPerNumOfStaff;
        }

        public void setNumberOfPaidMealsPerNumOfStaff(double numberOfPaidMealsPerNumOfStaff) {
            this.numberOfPaidMealsPerNumOfStaff = numberOfPaidMealsPerNumOfStaff;
        }

        public double getNumberOfPaidMealsPerNumOfStudents() {
            return numberOfPaidMealsPerNumOfStudents;
        }

        public void setNumberOfPaidMealsPerNumOfStudents(double numberOfPaidMealsPerNumOfStudents) {
            this.numberOfPaidMealsPerNumOfStudents = numberOfPaidMealsPerNumOfStudents;
        }

        public double getNumberOfReducedPriceMealsPerNumOfStaff() {
            return numberOfReducedPriceMealsPerNumOfStaff;
        }

        public void setNumberOfReducedPriceMealsPerNumOfStaff(double numberOfReducedPriceMealsPerNumOfStaff) {
            this.numberOfReducedPriceMealsPerNumOfStaff = numberOfReducedPriceMealsPerNumOfStaff;
        }

        public double getNumberOfReducedPriceMealsPerNumOfStudents() {
            return numberOfReducedPriceMealsPerNumOfStudents;
        }

        public void setNumberOfReducedPriceMealsPerNumOfStudents(double numberOfReducedPriceMealsPerNumOfStudents) {
            this.numberOfReducedPriceMealsPerNumOfStudents = numberOfReducedPriceMealsPerNumOfStudents;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }
    }

    private List<EduInstItemInfo> eduInstItemInfoList = new LinkedList<EduInstItemInfo>();

    private PaymentSystemStats paymentSystemStats;


    public List<EduInstItemInfo> getEduInstItemInfoList() {
        return eduInstItemInfoList;
    }

    public void setEduInstItemInfoList(List<EduInstItemInfo> eduInstItemInfoList) {
        this.eduInstItemInfoList = eduInstItemInfoList;
    }

    public PaymentSystemStats getPaymentSystemStats() {
        return paymentSystemStats;
    }

    public void setPaymentSystemStats(PaymentSystemStats paymentSystemStats) {
        this.paymentSystemStats = paymentSystemStats;
    }
}
