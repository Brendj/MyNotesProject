/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.dashboard.data;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 02.08.12
 * Time: 19:15
 * To change this template use File | Settings | File Templates.
 */
public class DashboardResponse {

    public static class EduInstItemInfo {

        long idOfOrg;
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

        public long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(long idOfOrg) {
            this.idOfOrg = idOfOrg;
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
    }

    public static class PaymentSystemItemInfo {

        long idOfContragent;
        Date lastOperationTime;
        long numOfOperations;
        
        String error;

        public long getIdOfContragent() {
            return idOfContragent;
        }

        public void setIdOfContragent(long idOfContragent) {
            this.idOfContragent = idOfContragent;
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
    }

    private List<EduInstItemInfo> eduInstItemInfoList = new LinkedList<EduInstItemInfo>();

    private List<PaymentSystemItemInfo> paymentSystemItemInfoList = new LinkedList<PaymentSystemItemInfo>();


    public List<EduInstItemInfo> getEduInstItemInfoList() {
        return eduInstItemInfoList;
    }

    public void setEduInstItemInfoList(List<EduInstItemInfo> eduInstItemInfoList) {
        this.eduInstItemInfoList = eduInstItemInfoList;
    }

    public List<PaymentSystemItemInfo> getPaymentSystemItemInfoList() {
        return paymentSystemItemInfoList;
    }

    public void setPaymentSystemItemInfoList(List<PaymentSystemItemInfo> paymentSystemItemInfoList) {
        this.paymentSystemItemInfoList = paymentSystemItemInfoList;
    }
}
