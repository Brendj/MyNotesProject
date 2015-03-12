/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.summarySalesToSchools;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 23.09.13
 * Time: 12:26
 */

public class SSTSItem {

    private Long orgId;
    private String orgName;
    private String orgAddress;
    private Long sumBuffet;
    private Long sumComplex;
    private Long sumComplexBenefit;
    private Long sumProductVending;
    private Long sumCommercialFood;

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgAddress() {
        return orgAddress;
    }

    public void setOrgAddress(String orgAddress) {
        this.orgAddress = orgAddress;
    }

    public Long getSumBuffet() {
        return sumBuffet;
    }

    public void setSumBuffet(Long sumBuffet) {
        this.sumBuffet = sumBuffet;
    }

    public Long getSumComplex() {
        return sumComplex;
    }

    public void setSumComplex(Long sumComplex) {
        this.sumComplex = sumComplex;
    }

    public Long getSumComplexBenefit() {
        return sumComplexBenefit;
    }

    public void setSumComplexBenefit(Long sumComplexBenefit) {
        this.sumComplexBenefit = sumComplexBenefit;
    }

    public Long getSumProductVending() {
        return sumProductVending;
    }

    public void setSumProductVending(Long sumProductVending) {
        this.sumProductVending = sumProductVending;
    }

    public Long getSumCommercialFood() {
        return sumCommercialFood;
    }

    public void setSumCommercialFood(Long sumCommercialFood) {
        this.sumCommercialFood = sumCommercialFood;
    }
}
