/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.accessories;

import ru.axetta.ecafe.processor.core.persistence.OrgInventory;

import org.apache.commons.lang.StringUtils;

/**
 * Created by i.semenov on 19.05.2017.
 */
public class OrgInventoryItem {
    private String amount_armadmin;
    private String amount_armcontroller;
    private String amount_turnstiles;
    private String amount_elocks;
    private String amount_ereaders;
    private String amount_infopanels;
    private String amount_armoperator;
    private String amount_infokiosks;
    private String amount_armlibrary;

    public OrgInventoryItem(OrgInventory orgInventory) {
        if (orgInventory == null) return;
        this.amount_armadmin = orgInventory.getAmount_armadmin() == null ? "" : orgInventory.getAmount_armadmin().toString();
        this.amount_armcontroller = orgInventory.getAmount_armcontroller() == null ? "" : orgInventory.getAmount_armcontroller().toString();
        this.amount_armlibrary = orgInventory.getAmount_armlibrary() == null ? "" : orgInventory.getAmount_armlibrary().toString();
        this.amount_armoperator = orgInventory.getAmount_armoperator() == null ? "" : orgInventory.getAmount_armoperator().toString();
        this.amount_elocks = orgInventory.getAmount_elocks() == null ? "" : orgInventory.getAmount_elocks().toString();
        this.amount_ereaders = orgInventory.getAmount_ereaders() == null ? "" : orgInventory.getAmount_ereaders().toString();
        this.amount_turnstiles = orgInventory.getAmount_turnstiles() == null ? "" : orgInventory.getAmount_turnstiles().toString();
        this.amount_infokiosks = orgInventory.getAmount_infokiosks() == null ? "" : orgInventory.getAmount_infokiosks().toString();
        this.amount_infopanels = orgInventory.getAmount_infopanels() == null ? "" : orgInventory.getAmount_infopanels().toString();
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(amount_armadmin) && StringUtils.isEmpty(amount_armcontroller) && StringUtils.isEmpty(amount_armoperator)
                && StringUtils.isEmpty(amount_armlibrary) && StringUtils.isEmpty(amount_turnstiles) && StringUtils.isEmpty(amount_elocks)
                && StringUtils.isEmpty(amount_ereaders) && StringUtils.isEmpty(amount_infopanels) && StringUtils.isEmpty(amount_infokiosks);
    }

    public String getAmount_armadmin() {
        return amount_armadmin;
    }

    public void setAmount_armadmin(String amount_armadmin) {
        this.amount_armadmin = amount_armadmin;
    }

    public String getAmount_armcontroller() {
        return amount_armcontroller;
    }

    public void setAmount_armcontroller(String amount_armcontroller) {
        this.amount_armcontroller = amount_armcontroller;
    }

    public String getAmount_turnstiles() {
        return amount_turnstiles;
    }

    public void setAmount_turnstiles(String amount_turnstiles) {
        this.amount_turnstiles = amount_turnstiles;
    }

    public String getAmount_elocks() {
        return amount_elocks;
    }

    public void setAmount_elocks(String amount_elocks) {
        this.amount_elocks = amount_elocks;
    }

    public String getAmount_ereaders() {
        return amount_ereaders;
    }

    public void setAmount_ereaders(String amount_ereaders) {
        this.amount_ereaders = amount_ereaders;
    }

    public String getAmount_infopanels() {
        return amount_infopanels;
    }

    public void setAmount_infopanels(String amount_infopanels) {
        this.amount_infopanels = amount_infopanels;
    }

    public String getAmount_armoperator() {
        return amount_armoperator;
    }

    public void setAmount_armoperator(String amount_armoperator) {
        this.amount_armoperator = amount_armoperator;
    }

    public String getAmount_infokiosks() {
        return amount_infokiosks;
    }

    public void setAmount_infokiosks(String amount_infokiosks) {
        this.amount_infokiosks = amount_infokiosks;
    }

    public String getAmount_armlibrary() {
        return amount_armlibrary;
    }

    public void setAmount_armlibrary(String amount_armlibrary) {
        this.amount_armlibrary = amount_armlibrary;
    }
}
