/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 30.05.16
 * Time: 12:04
 */

public class CheckSums {

    private Long idOfCheckSums;
    private Date checkSumsDate;
    private String distributionVersion;
    private String checkSumsMd5;
    private String checkSumOnSettings;

    public CheckSums() {
    }

    public CheckSums( Date checkSumsDate, String distributionVersion, String checkSumsMd5, String checkSumOnSettings) {
        this.checkSumsDate = checkSumsDate;
        this.distributionVersion = distributionVersion;
        this.checkSumsMd5 = checkSumsMd5;
        this.checkSumOnSettings = checkSumOnSettings;
    }

    public Long getIdOfCheckSums() {
        return idOfCheckSums;
    }

    public void setIdOfCheckSums(Long idOfCheckSums) {
        this.idOfCheckSums = idOfCheckSums;
    }

    public Date getCheckSumsDate() {
        return checkSumsDate;
    }

    public void setCheckSumsDate(Date checkSumsDate) {
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

    public String getCheckSumOnSettings() {
        return checkSumOnSettings;
    }

    public void setCheckSumOnSettings(String checkSumOnSettings) {
        this.checkSumOnSettings = checkSumOnSettings;
    }
}
