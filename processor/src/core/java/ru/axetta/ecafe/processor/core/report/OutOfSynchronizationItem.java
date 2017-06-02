/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 22.01.16
 * Time: 16:40
 * To change this template use File | Settings | File Templates.
 */
public class OutOfSynchronizationItem implements Comparable<OutOfSynchronizationItem> {

    private String condition;
    private Long idOfOrg;
    private String shortName;
    private String address;

    private String lastSuccessfulBalanceSync;
    private String version;
    private String remoteAddr;
    private Long number;
    private String statusDetailing;
    private String introductionQueue;
    private String district;
    private String isWorkInSummerTime;

    public OutOfSynchronizationItem(String condition, Long idOfOrg, String shortName, String address, boolean isWorkInSummerTime,
            String lastSuccessfulBalanceSync, String version, String remoteAddr, Long number, String statusDetailing,
            String introductionQueue, String district) {
        this.condition = condition;
        this.idOfOrg = idOfOrg;
        this.shortName = shortName;
        this.address = address;
        this.isWorkInSummerTime = isWorkInSummerTime ? "Да" : "Нет";
        this.lastSuccessfulBalanceSync = lastSuccessfulBalanceSync;
        this.version = version;
        this.remoteAddr = remoteAddr;
        this.number = number;
        this.statusDetailing = statusDetailing;
        this.introductionQueue = introductionQueue;
        this.district = district;
    }

    public OutOfSynchronizationItem() {
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLastSuccessfulBalanceSync() {
        return lastSuccessfulBalanceSync;
    }

    public void setLastSuccessfulBalanceSync(String lastSuccessfulBalanceSync) {
        this.lastSuccessfulBalanceSync = lastSuccessfulBalanceSync;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getStatusDetailing() {
        return statusDetailing;
    }

    public void setStatusDetailing(String statusDetailing) {
        this.statusDetailing = statusDetailing;
    }

    public String getIntroductionQueue() {
        return introductionQueue;
    }

    public void setIntroductionQueue(String introductionQueue) {
        this.introductionQueue = introductionQueue;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getIsWorkInSummerTime() {
        return isWorkInSummerTime;
    }

    public void setIsWorkInSummerTime(String isWorkInSummerTime) {
        this.isWorkInSummerTime = isWorkInSummerTime;
    }

    @Override
    public int compareTo(OutOfSynchronizationItem o) {
        int retCode = this.number.compareTo(o.getNumber());
        return retCode;
    }
}
