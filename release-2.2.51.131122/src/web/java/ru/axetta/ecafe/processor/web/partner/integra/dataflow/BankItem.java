/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.option.OptionPage;


import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 06.08.12
 * Time: 16:46
 * To change this template use File | Settings | File Templates.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BankItem")

public class BankItem {

    @XmlAttribute(name = "Name")
    protected String name;
    @XmlAttribute(name = "LogoUrl")
    protected String logoUrl;
    @XmlAttribute(name = "TerminalsUrl")
    protected String terminalsUrl;
    @XmlAttribute(name = "Rate")
    protected Double rate;
    @XmlAttribute(name = "MinRate")
    protected  Double minRate;
    @XmlAttribute(name = "EnrollmentType")
    protected  String enrollmentType;

    protected Long idOfBank;


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
