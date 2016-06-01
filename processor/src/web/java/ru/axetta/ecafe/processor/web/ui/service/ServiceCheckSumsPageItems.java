/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 30.05.16
 * Time: 12:47
 */

public class ServiceCheckSumsPageItems {

    private String checkSumsDate;
    private String distributionVersion;
    private String checkSumsMd5;

    public ServiceCheckSumsPageItems() {
    }

    public ServiceCheckSumsPageItems(String checkSumsDate, String distributionVersion, String checkSumsMd5) {
        this.checkSumsDate = checkSumsDate;
        this.distributionVersion = distributionVersion;
        this.checkSumsMd5 = checkSumsMd5;
    }

    public String getCheckSumsDate() {
        return checkSumsDate;
    }

    public void setCheckSumsDate(String checkSumsDate) {
        this.checkSumsDate = checkSumsDate;
    }

    public String getDistributionVersion() {
        return distributionVersion;
    }

    public void setDistributionVersion(String distributionVersion) {
        this.distributionVersion = distributionVersion;
    }

    public String getCheckSumsMd5() {
        return checkSumsMd5;
    }

    public void setCheckSumsMd5(String checkSumsMd5) {
        this.checkSumsMd5 = checkSumsMd5;
    }
}
