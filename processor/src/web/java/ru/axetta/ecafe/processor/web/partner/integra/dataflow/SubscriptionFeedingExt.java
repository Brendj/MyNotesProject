
/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubscriptionFeedingExt")
public class SubscriptionFeedingExt {
    @XmlElement(name = "IdOfSubscriptionFeeding")
    private Long idOfSubscriptionFeeding;
    @XmlElement(name = "guid")
    private String guid;
    @XmlElement(name = "DateCreateService")
    @XmlSchemaType(name = "dateTime")
    private Date dateCreateService;
    @XmlElement(name = "DateActivateSubscription")
    @XmlSchemaType(name = "dateTime")
    private Date dateActivateSubscription;
    @XmlElement(name = "LastDatePauseSubscription")
    @XmlSchemaType(name = "dateTime")
    private Date lastDatePauseSubscription;
    @XmlElement(name = "DateDeactivateService")
    @XmlSchemaType(name = "dateTime")
    private Date dateDeactivateService;
    @XmlElement(name = "UpdateDate")
    @XmlSchemaType(name = "dateTime")
    private Date updateDate;
    @XmlElement(name = "WasSuspended")
    private Boolean wasSuspended;
    @XmlElement(name = "ChangesPlace")
    private Boolean changesPlace;

    public SubscriptionFeedingExt(SubscriptionFeeding subscriptionFeeding) {
        this.idOfSubscriptionFeeding = subscriptionFeeding.getGlobalId();
        this.guid = subscriptionFeeding.getGuid();
        this.dateCreateService = subscriptionFeeding.getDateCreateService();
        this.dateActivateSubscription = subscriptionFeeding.getDateActivateSubscription();
        this.lastDatePauseSubscription = subscriptionFeeding.getLastDatePauseSubscription();
        this.dateDeactivateService = subscriptionFeeding.getDateDeactivateService();
        this.wasSuspended = subscriptionFeeding.getWasSuspended();
        if(subscriptionFeeding.getLastUpdate()==null){
            this.updateDate = subscriptionFeeding.getCreatedDate();
        } else {
            this.updateDate = subscriptionFeeding.getLastUpdate();
        }
        this.changesPlace = subscriptionFeeding.getStaff() != null;
    }

    public Long getIdOfSubscriptionFeeding() {
        return idOfSubscriptionFeeding;
    }

    public void setIdOfSubscriptionFeeding(Long idOfSubscriptionFeeding) {
        this.idOfSubscriptionFeeding = idOfSubscriptionFeeding;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Date getDateCreateService() {
        return dateCreateService;
    }

    public void setDateCreateService(Date dateCreateService) {
        this.dateCreateService = dateCreateService;
    }

    public Date getDateActivateSubscription() {
        return dateActivateSubscription;
    }

    public void setDateActivateSubscription(Date dateActivateSubscription) {
        this.dateActivateSubscription = dateActivateSubscription;
    }

    public Date getLastDatePauseSubscription() {
        return lastDatePauseSubscription;
    }

    public void setLastDatePauseSubscription(Date lastDatePauseSubscription) {
        this.lastDatePauseSubscription = lastDatePauseSubscription;
    }

    public Date getDateDeactivateService() {
        return dateDeactivateService;
    }

    public void setDateDeactivateService(Date dateDeactivateService) {
        this.dateDeactivateService = dateDeactivateService;
    }

    public Boolean getWasSuspended() {
        return wasSuspended;
    }

    public void setWasSuspended(Boolean wasSuspended) {
        this.wasSuspended = wasSuspended;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Boolean getChangesPlace() {
        return changesPlace;
    }

    public void setChangesPlace(Boolean changesPlace) {
        this.changesPlace = changesPlace;
    }

    public SubscriptionFeedingExt() {
    }
}
