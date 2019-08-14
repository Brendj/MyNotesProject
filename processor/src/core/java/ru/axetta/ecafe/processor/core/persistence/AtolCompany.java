/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Set;

/**
 * Created by nuc on 14.08.2019.
 */
public class AtolCompany {
    private Long idOfAtolCompany;
    private String emailOrg;
    private String taxType;
    private String inn;
    private String place;
    private String emailCheck;
    private Set<Contragent> contragents; // = new HashSet<DiscountRule>();

    public AtolCompany() {

    }

    public Long getIdOfAtolCompany() {
        return idOfAtolCompany;
    }

    public void setIdOfAtolCompany(Long idOfAtolCompany) {
        this.idOfAtolCompany = idOfAtolCompany;
    }

    public String getEmailOrg() {
        return emailOrg;
    }

    public void setEmailOrg(String emailOrg) {
        this.emailOrg = emailOrg;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getEmailCheck() {
        return emailCheck;
    }

    public void setEmailCheck(String emailCheck) {
        this.emailCheck = emailCheck;
    }

    public Set<Contragent> getContragents() {
        return contragents;
    }

    public void setContragents(Set<Contragent> contragents) {
        this.contragents = contragents;
    }
}
