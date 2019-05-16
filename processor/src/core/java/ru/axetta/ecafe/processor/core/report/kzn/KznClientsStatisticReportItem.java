/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.kzn;


import ru.axetta.ecafe.processor.core.persistence.KznClientsStatistic;

public class KznClientsStatisticReportItem {

    private Long idOfKznClientsStatistic;
    private String orgName;
    private Long studentsCountTotal;
    private Long studentsCountYoung;
    private Long studentsCountMiddle;
    private Long studentsCountOld;
    private Long benefitStudentsCountYoung;
    private Long benefitStudentsCountMiddle;
    private Long benefitStudentsCountOld;
    private Long benefitStudentsCountTotal;
    private Long employeeCount;

    public KznClientsStatisticReportItem() {

    }

    public KznClientsStatisticReportItem(KznClientsStatistic kznClientsStatistic) {
        this.idOfKznClientsStatistic = kznClientsStatistic.getIdOfKznClientsStatistic();
        this.orgName = kznClientsStatistic.getOrg().getShortNameInfoService();
        this.studentsCountTotal = kznClientsStatistic.getStudentsCountTotal();
        this.studentsCountYoung = kznClientsStatistic.getStudentsCountYoung();
        this.studentsCountMiddle = kznClientsStatistic.getStudentsCountMiddle();
        this.studentsCountOld = kznClientsStatistic.getStudentsCountOld();
        this.benefitStudentsCountYoung = kznClientsStatistic.getBenefitStudentsCountYoung();
        this.benefitStudentsCountMiddle = kznClientsStatistic.getBenefitStudentsCountMiddle();
        this.benefitStudentsCountOld = kznClientsStatistic.getBenefitStudentsCountOld();
        this.benefitStudentsCountTotal = kznClientsStatistic.getBenefitStudentsCountTotal();
        this.employeeCount = kznClientsStatistic.getEmployeeCount();
    }

    public Long getIdOfKznClientsStatistic() {
        return idOfKznClientsStatistic;
    }

    public void setIdOfKznClientsStatistic(Long idOfKznClientsStatistic) {
        this.idOfKznClientsStatistic = idOfKznClientsStatistic;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getStudentsCountTotal() {
        return studentsCountTotal;
    }

    public void setStudentsCountTotal(Long studentsCountTotal) {
        this.studentsCountTotal = studentsCountTotal;
    }

    public Long getStudentsCountYoung() {
        return studentsCountYoung;
    }

    public void setStudentsCountYoung(Long studentsCountYoung) {
        this.studentsCountYoung = studentsCountYoung;
    }

    public Long getStudentsCountMiddle() {
        return studentsCountMiddle;
    }

    public void setStudentsCountMiddle(Long studentsCountMiddle) {
        this.studentsCountMiddle = studentsCountMiddle;
    }

    public Long getStudentsCountOld() {
        return studentsCountOld;
    }

    public void setStudentsCountOld(Long studentsCountOld) {
        this.studentsCountOld = studentsCountOld;
    }

    public Long getBenefitStudentsCountYoung() {
        return benefitStudentsCountYoung;
    }

    public void setBenefitStudentsCountYoung(Long benefitStudentsCountYoung) {
        this.benefitStudentsCountYoung = benefitStudentsCountYoung;
    }

    public Long getBenefitStudentsCountMiddle() {
        return benefitStudentsCountMiddle;
    }

    public void setBenefitStudentsCountMiddle(Long benefitStudentsCountMiddle) {
        this.benefitStudentsCountMiddle = benefitStudentsCountMiddle;
    }

    public Long getBenefitStudentsCountOld() {
        return benefitStudentsCountOld;
    }

    public void setBenefitStudentsCountOld(Long benefitStudentsCountOld) {
        this.benefitStudentsCountOld = benefitStudentsCountOld;
    }

    public Long getBenefitStudentsCountTotal() {
        return benefitStudentsCountTotal;
    }

    public void setBenefitStudentsCountTotal(Long benefitStudentsCountTotal) {
        this.benefitStudentsCountTotal = benefitStudentsCountTotal;
    }

    public Long getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(Long employeeCount) {
        this.employeeCount = employeeCount;
    }
}
