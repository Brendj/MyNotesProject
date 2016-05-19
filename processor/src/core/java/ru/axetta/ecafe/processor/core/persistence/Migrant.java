/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 11.05.16
 * Time: 09:58
 */

public class Migrant implements Serializable{
    private CompositeIdOfMigrant compositeIdOfMigrant;
    private Org orgRegistry; // Организация регистрации
    private Contragent orgRegVendor; // Поставщик в организации регистрации
    private Client clientMigrate;
    private Org orgVisit; // Организация посещения
    private Date visitStartDate;
    private Date visitEndDate;
    private Integer syncState;

    public Migrant() {
    }

    public Migrant(CompositeIdOfMigrant compositeIdOfMigrant, Contragent orgRegVendor, Client clientMigrate,
            Org orgVisit, Date visitStartDate, Date visitEndDate, Integer syncState) {
        this.compositeIdOfMigrant = compositeIdOfMigrant;
        this.orgRegVendor = orgRegVendor;
        this.clientMigrate = clientMigrate;
        this.orgVisit = orgVisit;
        this.visitStartDate = visitStartDate;
        this.visitEndDate = visitEndDate;
        this.syncState = syncState;
    }

    public CompositeIdOfMigrant getCompositeIdOfMigrant() {
        return compositeIdOfMigrant;
    }

    public void setCompositeIdOfMigrant(CompositeIdOfMigrant compositeIdOfMigrant) {
        this.compositeIdOfMigrant = compositeIdOfMigrant;
    }

    public Org getOrgRegistry() {
        return orgRegistry;
    }

    public void setOrgRegistry(Org orgRegistry) {
        this.orgRegistry = orgRegistry;
    }

    public Contragent getOrgRegVendor() {
        return orgRegVendor;
    }

    public void setOrgRegVendor(Contragent orgRegVendor) {
        this.orgRegVendor = orgRegVendor;
    }

    public Client getClientMigrate() {
        return clientMigrate;
    }

    public void setClientMigrate(Client clientMigrate) {
        this.clientMigrate = clientMigrate;
    }

    public Org getOrgVisit() {
        return orgVisit;
    }

    public void setOrgVisit(Org orgVisit) {
        this.orgVisit = orgVisit;
    }

    public Date getVisitStartDate() {
        return visitStartDate;
    }

    public void setVisitStartDate(Date visitStartDate) {
        this.visitStartDate = visitStartDate;
    }

    public Date getVisitEndDate() {
        return visitEndDate;
    }

    public void setVisitEndDate(Date visitEndDate) {
        this.visitEndDate = visitEndDate;
    }

    public Integer getSyncState() {
        return syncState;
    }

    public void setSyncState(Integer syncState) {
        this.syncState = syncState;
    }

}