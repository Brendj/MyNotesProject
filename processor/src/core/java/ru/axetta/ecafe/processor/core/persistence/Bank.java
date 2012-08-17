/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 07.08.12
 * Time: 11:15
 * To change this template use File | Settings | File Templates.
 */
public class Bank {


   private    Long idOfBank;
   private    String name;
   private    String logoUrl;
   private    String terminalsUrl;
   private    Double rate;
   private    Double minRate;
   private    String enrollmentType;

   public Bank(){}

    public Long getIdOfBank() {
        return idOfBank;
    }

    public void setIdOfBank(Long idOfBank) {
        this.idOfBank = idOfBank;
    }

    public String getEnrollmentType() {
        return enrollmentType;
    }

    public void setEnrollmentType(String enrollmentType) {
        this.enrollmentType = enrollmentType;
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


}
