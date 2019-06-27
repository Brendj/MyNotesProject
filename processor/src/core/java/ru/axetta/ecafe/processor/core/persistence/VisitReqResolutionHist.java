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
 * Time: 11:20
 */
public class VisitReqResolutionHist implements Serializable {
    public static final int NOT_SYNCHRONIZED = 0;
    public static final int SYNCHRONIZED = 1;

    public static final int RES_CREATED = 0;
    public static final int RES_CONFIRMED = 1;
    public static final int RES_REJECTED = 2;
    public static final int RES_CANCELED = 3;
    public static final int RES_OVERDUE = 4;
    public static final int RES_OVERDUE_SERVER = 5;

    private CompositeIdOfVisitReqResolutionHist compositeIdOfVisitReqResolutionHist;
    private Org orgResol; // Организация, в которой была вынесена резолюция
    private Org orgRegistry; // Организация регистрации, откуда пришёл запрос на посещение
    private Integer resolution;
    private Date resolutionDateTime;
    private String resolutionCause;
    private Client clientResol; // Клиент, вынесший резолюцию
    private String contactInfo;
    private Integer syncState;
    private Migrant migrant;
    private VisitReqResolutionHistInitiatorEnum initiator;

    public VisitReqResolutionHist() {
    }

    public VisitReqResolutionHist(CompositeIdOfVisitReqResolutionHist compositeIdOfVisitReqResolutionHist,
            Org orgRegistry, Integer resolution, Date resolutionDateTime, String resolutionCause, Client clientResol,
            String contactInfo, Integer syncState, VisitReqResolutionHistInitiatorEnum initiator) {
        this.compositeIdOfVisitReqResolutionHist = compositeIdOfVisitReqResolutionHist;
        this.orgRegistry = orgRegistry;
        this.resolution = resolution;
        this.resolutionDateTime = resolutionDateTime;
        this.resolutionCause = resolutionCause;
        this.clientResol = clientResol;
        this.contactInfo = contactInfo;
        this.syncState = syncState;
        this.initiator = initiator;
    }

    public CompositeIdOfVisitReqResolutionHist getCompositeIdOfVisitReqResolutionHist() {
        return compositeIdOfVisitReqResolutionHist;
    }

    public void setCompositeIdOfVisitReqResolutionHist(
            CompositeIdOfVisitReqResolutionHist compositeIdOfVisitReqResolutionHist) {
        this.compositeIdOfVisitReqResolutionHist = compositeIdOfVisitReqResolutionHist;
    }

    public Org getOrgResol() {
        return orgResol;
    }

    public void setOrgResol(Org orgResol) {
        this.orgResol = orgResol;
    }

    public Org getOrgRegistry() {
        return orgRegistry;
    }

    public void setOrgRegistry(Org orgRegistry) {
        this.orgRegistry = orgRegistry;
    }

    public Integer getResolution() {
        return resolution;
    }

    public void setResolution(Integer resolution) {
        this.resolution = resolution;
    }

    public Date getResolutionDateTime() {
        return resolutionDateTime;
    }

    public void setResolutionDateTime(Date resolutionDateTime) {
        this.resolutionDateTime = resolutionDateTime;
    }

    public String getResolutionCause() {
        return resolutionCause;
    }

    public void setResolutionCause(String resolutionCause) {
        this.resolutionCause = resolutionCause;
    }

    public Client getClientResol() {
        return clientResol;
    }

    public void setClientResol(Client clientResol) {
        this.clientResol = clientResol;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public Integer getSyncState() {
        return syncState;
    }

    public void setSyncState(Integer syncState) {
        this.syncState = syncState;
    }

    public Migrant getMigrant() {
        return migrant;
    }

    public void setMigrant(Migrant migrant) {
        this.migrant = migrant;
    }

    public VisitReqResolutionHistInitiatorEnum getInitiator() {
        return initiator;
    }

    public void setInitiator(VisitReqResolutionHistInitiatorEnum initiator) {
        this.initiator = initiator;
    }

    @Override
    public int hashCode() {
        return compositeIdOfVisitReqResolutionHist.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof VisitReqResolutionHist) {
            return this.hashCode() == o.hashCode();
        }
        return false;
    }
}
