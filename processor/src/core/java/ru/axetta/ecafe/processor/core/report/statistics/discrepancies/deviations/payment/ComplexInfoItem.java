/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 22.10.14
 * Time: 15:01
 */

public class ComplexInfoItem {

    public Integer idOfComplex;
    public Long idOfRule;
    public String complexName;
    public Date menuDate;

    public ComplexInfoItem(Integer idOfComplex, Long idOfRule, String complexName) {
        this.idOfComplex = idOfComplex;
        this.idOfRule = idOfRule;
        this.complexName = complexName;
    }

    public ComplexInfoItem(Integer idOfComplex, Date menuDate, String complexName) {
        this.idOfComplex = idOfComplex;
        this.menuDate = menuDate;
        this.complexName = complexName;
    }


    public Integer getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(Integer idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public Date getMenuDate() {
        return menuDate;
    }

    public void setMenuDate(Date menuDate) {
        this.menuDate = menuDate;
    }

    public Long getIdOfRule() {
        return idOfRule;
    }

    public void setIdOfRule(Long idOfRule) {
        this.idOfRule = idOfRule;
    }
}
