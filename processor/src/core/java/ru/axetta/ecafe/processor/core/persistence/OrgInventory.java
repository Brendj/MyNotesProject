/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by i.semenov on 18.05.2017.
 */
public class OrgInventory {
    private Long idOfOrgInventory;
    private Long idOfOrg;
    private Integer amount_armadmin;
    private Integer amount_armcontroller;
    private Integer amount_turnstiles;
    private Integer amount_elocks;
    private Integer amount_ereaders;
    private Integer amount_infopanels;
    private Integer amount_armoperator;
    private Integer amount_infokiosks;
    private Integer amount_armlibrary;

    public OrgInventory() {

    }

    public boolean isEmpty() {
        return amount_armadmin == null && amount_armcontroller == null && amount_armoperator == null
                && amount_armlibrary == null && amount_turnstiles == null && amount_elocks == null
                && amount_ereaders == null && amount_infopanels == null & amount_infokiosks == null;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Integer getAmount_armadmin() {
        return amount_armadmin;
    }

    public void setAmount_armadmin(Integer amount_armadmin) {
        this.amount_armadmin = amount_armadmin;
    }

    public Integer getAmount_armcontroller() {
        return amount_armcontroller;
    }

    public void setAmount_armcontroller(Integer amount_armcontroller) {
        this.amount_armcontroller = amount_armcontroller;
    }

    public Integer getAmount_turnstiles() {
        return amount_turnstiles;
    }

    public void setAmount_turnstiles(Integer amount_turnstiles) {
        this.amount_turnstiles = amount_turnstiles;
    }

    public Integer getAmount_elocks() {
        return amount_elocks;
    }

    public void setAmount_elocks(Integer amount_elocks) {
        this.amount_elocks = amount_elocks;
    }

    public Integer getAmount_ereaders() {
        return amount_ereaders;
    }

    public void setAmount_ereaders(Integer amount_ereaders) {
        this.amount_ereaders = amount_ereaders;
    }

    public Integer getAmount_infopanels() {
        return amount_infopanels;
    }

    public void setAmount_infopanels(Integer amount_infopanels) {
        this.amount_infopanels = amount_infopanels;
    }

    public Integer getAmount_armoperator() {
        return amount_armoperator;
    }

    public void setAmount_armoperator(Integer amount_armoperator) {
        this.amount_armoperator = amount_armoperator;
    }

    public Integer getAmount_infokiosks() {
        return amount_infokiosks;
    }

    public void setAmount_infokiosks(Integer amount_infokiosks) {
        this.amount_infokiosks = amount_infokiosks;
    }

    public Integer getAmount_armlibrary() {
        return amount_armlibrary;
    }

    public void setAmount_armlibrary(Integer amount_armlibrary) {
        this.amount_armlibrary = amount_armlibrary;
    }

    public Long getIdOfOrgInventory() {
        return idOfOrgInventory;
    }

    public void setIdOfOrgInventory(Long idOfOrgInventory) {
        this.idOfOrgInventory = idOfOrgInventory;
    }
}
