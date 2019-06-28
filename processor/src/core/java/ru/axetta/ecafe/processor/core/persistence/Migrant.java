/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 11.05.16
 * Time: 09:58
 */

public class Migrant implements Serializable{
    public static final int NOT_SYNCHRONIZED = 0;
    public static final int SYNCHRONIZED = 1;
    public static final int CLOSED = 3;

    private CompositeIdOfMigrant compositeIdOfMigrant;
    private Org orgRegistry; // Организация регистрации
    private Contragent orgRegVendor; // Поставщик в организации регистрации
    private String requestNumber;
    private Client clientMigrate;
    private Org orgVisit; // Организация посещения
    private Date visitStartDate;
    private Date visitEndDate;
    private Integer syncState;
    private MigrantInitiatorEnum initiator;
    private String section;
    private Long resolutionCodeGroup;
    private Set<VisitReqResolutionHist> visitReqResolutionHists;

    public Migrant() {
    }

    public Migrant(CompositeIdOfMigrant compositeIdOfMigrant, Contragent orgRegVendor, String requestNumber,
            Client clientMigrate, Org orgVisit, Date visitStartDate, Date visitEndDate, Integer syncState) {
        this.compositeIdOfMigrant = compositeIdOfMigrant;
        this.orgRegVendor = orgRegVendor;
        this.requestNumber = requestNumber;
        this.clientMigrate = clientMigrate;
        this.orgVisit = orgVisit;
        this.visitStartDate = visitStartDate;
        this.visitEndDate = visitEndDate;
        this.syncState = syncState;
        this.initiator = MigrantInitiatorEnum.INITIATOR_ORG;
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

    public String getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(String requestNumber) {
        this.requestNumber = requestNumber;
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

    @Override
    public String toString() {
        return "Migrant{" +
                "compositeIdOfMigrant=" + compositeIdOfMigrant +
                ", orgRegistry=" + orgRegistry.getIdOfOrg() +
                ", orgRegVendor=" + orgRegVendor.getIdOfContragent() +
                ", clientMigrate=" + clientMigrate.getIdOfClient() +
                ", orgVisit=" + orgVisit.getIdOfOrg() +
                ", visitStartDate=" + visitStartDate +
                ", visitEndDate=" + visitEndDate +
                ", syncState=" + syncState +
                '}';
    }

    public MigrantInitiatorEnum getInitiator() {
        return initiator;
    }

    public void setInitiator(MigrantInitiatorEnum initiator) {
        this.initiator = initiator;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Long getResolutionCodeGroup() {
        return resolutionCodeGroup;
    }

    public void setResolutionCodeGroup(Long resolutionCodeGroup) {
        this.resolutionCodeGroup = resolutionCodeGroup;
    }

    public Set<VisitReqResolutionHist> getVisitReqResolutionHists() {
        return visitReqResolutionHists;
    }

    public void setVisitReqResolutionHists(Set<VisitReqResolutionHist> visitReqResolutionHists) {
        this.visitReqResolutionHists = visitReqResolutionHists;
    }
}
