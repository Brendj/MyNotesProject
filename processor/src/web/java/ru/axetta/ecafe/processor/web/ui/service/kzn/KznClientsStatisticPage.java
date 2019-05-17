/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.kzn;

import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.apache.commons.lang.StringUtils;

public class KznClientsStatisticPage extends OnlineReportPage {

    private String studentsCountTotal;
    private String studentsCountYoung;
    private String studentsCountMiddle;
    private String studentsCountOld;
    private String benefitStudentsCountYoung;
    private String benefitStudentsCountMiddle;
    private String benefitStudentsCountOld;
    private String benefitStudentsCountTotal;
    private String employeeCount;

    public Boolean validate() {
        return !StringUtils.isEmpty(studentsCountTotal) && !StringUtils.isEmpty(studentsCountYoung) && !StringUtils
                .isEmpty(studentsCountMiddle) && !StringUtils.isEmpty(studentsCountOld) && !StringUtils
                .isEmpty(benefitStudentsCountYoung) && !StringUtils.isEmpty(benefitStudentsCountMiddle) && !StringUtils
                .isEmpty(benefitStudentsCountOld) && !StringUtils.isEmpty(benefitStudentsCountTotal) && !StringUtils
                .isEmpty(employeeCount);
    }

    public void clear() {
        studentsCountTotal = null;
        studentsCountYoung = null;
        studentsCountMiddle = null;
        studentsCountOld = null;
        benefitStudentsCountYoung = null;
        benefitStudentsCountMiddle = null;
        benefitStudentsCountOld = null;
        benefitStudentsCountTotal = null;
        employeeCount = null;
    }

    public Long stringAsLong(String value) {
        Long longValue = null;
        try {
            longValue = Long.parseLong(value);
        } catch (NumberFormatException e) {
            getLogger().error(String.format("Unable to parse long value: %s", value), e);
        }
        return longValue;
    }

    public String getStudentsCountTotal() {
        return studentsCountTotal;
    }

    public void setStudentsCountTotal(String studentsCountTotal) {
        this.studentsCountTotal = studentsCountTotal;
    }

    public String getStudentsCountYoung() {
        return studentsCountYoung;
    }

    public void setStudentsCountYoung(String studentsCountYoung) {
        this.studentsCountYoung = studentsCountYoung;
    }

    public String getStudentsCountMiddle() {
        return studentsCountMiddle;
    }

    public void setStudentsCountMiddle(String studentsCountMiddle) {
        this.studentsCountMiddle = studentsCountMiddle;
    }

    public String getStudentsCountOld() {
        return studentsCountOld;
    }

    public void setStudentsCountOld(String studentsCountOld) {
        this.studentsCountOld = studentsCountOld;
    }

    public String getBenefitStudentsCountYoung() {
        return benefitStudentsCountYoung;
    }

    public void setBenefitStudentsCountYoung(String benefitStudentsCountYoung) {
        this.benefitStudentsCountYoung = benefitStudentsCountYoung;
    }

    public String getBenefitStudentsCountMiddle() {
        return benefitStudentsCountMiddle;
    }

    public void setBenefitStudentsCountMiddle(String benefitStudentsCountMiddle) {
        this.benefitStudentsCountMiddle = benefitStudentsCountMiddle;
    }

    public String getBenefitStudentsCountOld() {
        return benefitStudentsCountOld;
    }

    public void setBenefitStudentsCountOld(String benefitStudentsCountOld) {
        this.benefitStudentsCountOld = benefitStudentsCountOld;
    }

    public String getBenefitStudentsCountTotal() {
        return benefitStudentsCountTotal;
    }

    public void setBenefitStudentsCountTotal(String benefitStudentsCountTotal) {
        this.benefitStudentsCountTotal = benefitStudentsCountTotal;
    }

    public String getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(String employeeCount) {
        this.employeeCount = employeeCount;
    }
}
