/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client.items;

import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfMigrant;
import ru.axetta.ecafe.processor.core.persistence.Migrant;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.VisitReqResolutionHist;
import ru.axetta.ecafe.processor.core.persistence.utils.MigrantsUtils;

import org.hibernate.Session;

import java.util.Date;

/**
 * Created by i.semenov on 01.02.2018.
 */
public class MigrantItem implements Comparable {
    private CompositeIdOfMigrant compositeIdOfMigrant;
    private String requestNumber;
    private Date createDateTime;
    private String initiator;
    private String fio;
    private String orgRegistry;
    private String orgVisit;
    private Date visitStartDate;
    private Date visitEndDate;
    private String resolution;
    private String guid;
    private String section;
    private Integer resolutionValue;
    private String group;
    private boolean annulled;

    public MigrantItem(Session session, Migrant migrant) {
        this.compositeIdOfMigrant = migrant.getCompositeIdOfMigrant();
        this.requestNumber = migrant.getRequestNumber();
        VisitReqResolutionHist visitReqResolutionHistLast = MigrantsUtils.getLastResolutionForMigrant(session, migrant);
        if (visitReqResolutionHistLast != null) {
            this.resolution = MigrantsUtils.getResolutionString(visitReqResolutionHistLast.getResolution());
            if (visitReqResolutionHistLast.getInitiator() != null) this.resolution += " (" + visitReqResolutionHistLast.getInitiator().toString() + ")";
            resolutionValue = visitReqResolutionHistLast.getResolution();
        }
        VisitReqResolutionHist visitReqResolutionHistFirst = MigrantsUtils.getFirstResolutionForMigrant(session, migrant);
        if (visitReqResolutionHistFirst != null) {
            this.createDateTime = visitReqResolutionHistFirst.getResolutionDateTime(); //дата создания
        }
        this.initiator = migrant.getInitiator().toString();
        this.fio = migrant.getClientMigrate().getPerson().getFullName();
        this.group = migrant.getClientMigrate().getClientGroup().getGroupName();
        this.guid = migrant.getClientMigrate().getClientGUID();
        this.orgRegistry = getOrgname(migrant.getOrgRegistry());
        this.orgVisit = getOrgname(migrant.getOrgVisit());
        this.visitStartDate = migrant.getVisitStartDate();
        this.visitEndDate = migrant.getVisitEndDate();
        this.section = migrant.getSection();
        this.annulled = false;
    }

    private String getOrgname(Org org) {
        return "(" + org.getIdOfOrg() + ") " + org.getShortNameInfoService();
    }

    public Boolean getIsActive() {
        if (resolutionValue == null) return false;
        return resolutionValue == VisitReqResolutionHist.RES_CONFIRMED || resolutionValue == VisitReqResolutionHist.RES_CREATED;
    }

    public void setCanceledResolution() {
        setResolution(MigrantsUtils.getResolutionString(VisitReqResolutionHist.RES_CANCELED));
        setResolutionValue(VisitReqResolutionHist.RES_CANCELED);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MigrantItem)) {
            return false;
        }
        final MigrantItem that = (MigrantItem) o;
        return compositeIdOfMigrant.equals(that.getCompositeIdOfMigrant());
    }

    @Override
    public int compareTo(Object o) {
        if (createDateTime == null) return -1;
        if (((MigrantItem)o).getCreateDateTime() == null) return 1;
        return createDateTime.compareTo(((MigrantItem)o).getCreateDateTime());
    }

    public CompositeIdOfMigrant getCompositeIdOfMigrant() {
        return compositeIdOfMigrant;
    }

    public void setCompositeIdOfMigrant(CompositeIdOfMigrant compositeIdOfMigrant) {
        this.compositeIdOfMigrant = compositeIdOfMigrant;
    }

    public String getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(String requestNumber) {
        this.requestNumber = requestNumber;
    }

    public Date getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getOrgRegistry() {
        return orgRegistry;
    }

    public void setOrgRegistry(String orgRegistry) {
        this.orgRegistry = orgRegistry;
    }

    public String getOrgVisit() {
        return orgVisit;
    }

    public void setOrgVisit(String orgVisit) {
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

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Integer getResolutionValue() {
        return resolutionValue;
    }

    public void setResolutionValue(Integer resolutionValue) {
        this.resolutionValue = resolutionValue;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isAnnulled() {
        return annulled;
    }

    public void setAnnulled(boolean annulled) {
        this.annulled = annulled;
    }
}
