/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.contragent;

/**
 * Updated with IntelliJ IDEA.
 * User: Liya
 * Date: 04.04.16
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */
public class ContragentCompletionReportItem {

    private Long orgId;
    /* короткое наименование образовательного учереждения */
    private String educationalInstitutionName;

    private String orgCity;

    private String contragentName;

    private Long paySum;

    private int paymentCount;

    public ContragentCompletionReportItem() {}

    public ContragentCompletionReportItem(Long orgId, String educationalInstitutionName, String orgCity,
            String contragentName, Long paySum, int paymentCount) {
        this.orgId = orgId;
        this.educationalInstitutionName = educationalInstitutionName;
        this.orgCity = orgCity;
        this.contragentName = contragentName;
        this.paySum = paySum;
        this.paymentCount = paymentCount;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getContragentName() {
        return contragentName;
    }

    public void setContragentName(String contragentName) {
        this.contragentName = contragentName;
    }

    public String getEducationalInstitutionName() {
        return educationalInstitutionName;
    }

    public void setEducationalInstitutionName(String educationalInstitutionName) {
        this.educationalInstitutionName = educationalInstitutionName;
    }

    public Long getPaySum() {
        return paySum;
    }

    public void setPaySum(Long paySum) {
        this.paySum = paySum;
    }

    public String getOrgCity() {
        return orgCity;
    }

    public void setOrgCity(String orgCity) {
        this.orgCity = orgCity;
    }

    public int getPaymentCount() {
        return paymentCount;
    }

    public void setPaymentCount(int paymentCount) {
        this.paymentCount = paymentCount;
    }
}
