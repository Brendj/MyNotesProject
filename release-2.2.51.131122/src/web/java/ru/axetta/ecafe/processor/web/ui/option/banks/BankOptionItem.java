/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.banks;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Bank;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractEntityItem;
import ru.axetta.ecafe.processor.web.ui.option.OptionPage;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 08.08.12
 * Time: 11:14
 * To change this template use File | Settings | File Templates.
 */
public class BankOptionItem {

    private    Long idOfBank;
    private    String name;
    private    String logoUrl;
    private    String terminalsUrl;
    private    Double rate;
    private    Double minRate;
    private    String enrollmentType;

    public BankOptionItem() {}

    public void fill(Bank bank) {
        idOfBank=bank.getIdOfBank();
        name = bank.getName();
        logoUrl = bank.getLogoUrl();
        terminalsUrl = bank.getTerminalsUrl();
        rate = bank.getRate();
        minRate = bank.getMinRate();
        enrollmentType = bank.getEnrollmentType();
    }



    public Long getIdOfBank() {
        return idOfBank;
    }

    public void setIdOfBank(Long idOfBank) {
        this.idOfBank = idOfBank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getTerminalsUrl() {
        return terminalsUrl;
    }

    public void setTerminalsUrl(String terminalsUrl) {
        this.terminalsUrl = terminalsUrl;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getMinRate() {
        return minRate;
    }

    public void setMinRate(Double minRate) {
        this.minRate = minRate;
    }

    public String getEnrollmentType() {
        return enrollmentType;
    }

    public void setEnrollmentType(String enrollmentType) {
        this.enrollmentType = enrollmentType;
    }

}
