/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 22.10.14
 * Time: 15:01
 */

public class ComplexInfoForPlan {
    public Integer idOfComplex;
    public Long idOfRule;
    public String complexName;
    public Long idOfOrg;

    public ComplexInfoForPlan(Integer idOfComplex, Long idOfRule, String complexName, Long idOfOrg) {
        this.idOfComplex = idOfComplex;
        this.idOfRule = idOfRule;
        this.complexName = complexName;
        this.idOfOrg = idOfOrg;
    }

    public Integer getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(Integer idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public Long getIdOfRule() {
        return idOfRule;
    }

    public void setIdOfRule(Long idOfRule) {
        this.idOfRule = idOfRule;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }
}
