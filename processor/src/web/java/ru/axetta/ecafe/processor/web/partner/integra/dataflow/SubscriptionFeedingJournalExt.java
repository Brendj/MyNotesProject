/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding;

import javax.xml.bind.annotation.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 17.09.14
 * Time: 17:22
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubscriptionFeedingJournalExt")
public class SubscriptionFeedingJournalExt {
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
    @XmlElement(name = "SubscriptionFeedingStatus")
    private String subscriptionFeedingStatus;

    public SubscriptionFeedingJournalExt(SubscriptionFeeding subscriptionFeeding) {
        this.idOfSubscriptionFeeding = subscriptionFeeding.getGlobalId();
        this.guid = subscriptionFeeding.getGuid();
        this.dateCreateService = subscriptionFeeding.getDateCreateService();
        this.dateActivateSubscription = subscriptionFeeding.getDateActivateSubscription();
        this.lastDatePauseSubscription = subscriptionFeeding.getLastDatePauseSubscription();
        this.dateDeactivateService = subscriptionFeeding.getDateDeactivateService();
        this.wasSuspended = subscriptionFeeding.getWasSuspended();
        if (subscriptionFeeding.getLastUpdate() == null) {
            this.updateDate = subscriptionFeeding.getCreatedDate();
        } else {
            this.updateDate = subscriptionFeeding.getLastUpdate();
        }
        this.changesPlace = subscriptionFeeding.getStaff() != null;
        this.subscriptionFeedingStatus = generateSubscriptionFeedingStatus(subscriptionFeeding);
    }

    private String generateSubscriptionFeedingStatus(SubscriptionFeeding subscriptionFeeding) {
        Date currentDate = new Date();
        if (subscriptionFeeding.getDateActivateSubscription() == null
                && subscriptionFeeding.getLastDatePauseSubscription() == null
                && subscriptionFeeding.getWasSuspended() == false) {
            return "Создана";
        }
        if (subscriptionFeeding.getDateActivateSubscription() != null
                && subscriptionFeeding.getLastDatePauseSubscription() == null
                && subscriptionFeeding.getWasSuspended() == false
                && subscriptionFeeding.getDateActivateSubscription().getTime() > currentDate.getTime()) {
            return "Ожидает активации";
        }
        if (subscriptionFeeding.getDateActivateSubscription() != null
                && subscriptionFeeding.getLastDatePauseSubscription() == null
                && subscriptionFeeding.getWasSuspended() == false
                && subscriptionFeeding.getDateActivateSubscription().getTime() <= currentDate.getTime()) {
            return "Активна";
        }
        if (subscriptionFeeding.getDateActivateSubscription() != null
                && subscriptionFeeding.getLastDatePauseSubscription() != null
                && subscriptionFeeding.getWasSuspended() == true
                && subscriptionFeeding.getLastDatePauseSubscription().getTime() > currentDate.getTime()) {
            return "Ожидает приостановки";
        }
        if (subscriptionFeeding.getDateActivateSubscription() != null
                && subscriptionFeeding.getLastDatePauseSubscription() != null
                && subscriptionFeeding.getWasSuspended() == true
                && subscriptionFeeding.getLastDatePauseSubscription().getTime() <= currentDate.getTime()) {
            return "Приостановлена";
        }

        return "Неизвестен";
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

    public String getSubscriptionFeedingStatus() {
        return subscriptionFeedingStatus;
    }

    public void setSubscriptionFeedingStatus(String subscriptionFeedingStatus) {
        this.subscriptionFeedingStatus = subscriptionFeedingStatus;
    }

    public SubscriptionFeedingJournalExt() {
    }
}