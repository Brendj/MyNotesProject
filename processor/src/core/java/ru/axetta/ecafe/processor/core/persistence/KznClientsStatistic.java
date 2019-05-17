/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

public class KznClientsStatistic {

    private Long idOfKznClientsStatistic;
    private Org org;
    private Long studentsCountTotal;
    private Long studentsCountYoung;
    private Long studentsCountMiddle;
    private Long studentsCountOld;
    private Long benefitStudentsCountYoung;
    private Long benefitStudentsCountMiddle;
    private Long benefitStudentsCountOld;
    private Long benefitStudentsCountTotal;
    private Long employeeCount;

    public KznClientsStatistic() {

    }

    public KznClientsStatistic(Org org, Long studentsCountTotal, Long studentsCountYoung, Long studentsCountMiddle,
            Long studentsCountOld, Long benefitStudentsCountYoung, Long benefitStudentsCountMiddle,
            Long benefitStudentsCountOld, Long benefitStudentsCountTotal, Long employeeCount) {
        this.org = org;
        this.studentsCountTotal = studentsCountTotal;
        this.studentsCountYoung = studentsCountYoung;
        this.studentsCountMiddle = studentsCountMiddle;
        this.studentsCountOld = studentsCountOld;
        this.benefitStudentsCountYoung = benefitStudentsCountYoung;
        this.benefitStudentsCountMiddle = benefitStudentsCountMiddle;
        this.benefitStudentsCountOld = benefitStudentsCountOld;
        this.benefitStudentsCountTotal = benefitStudentsCountTotal;
        this.employeeCount = employeeCount;
    }

    public Long getIdOfKznClientsStatistic() {
        return idOfKznClientsStatistic;
    }

    public void setIdOfKznClientsStatistic(Long idOfKznClientsStatistic) {
        this.idOfKznClientsStatistic = idOfKznClientsStatistic;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
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
