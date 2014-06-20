
/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import java.util.Date;

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
    @XmlElement(name = "DateActivate")
    @XmlSchemaType(name = "dateTime")
    private Date dateActivate;
    @XmlElement(name = "LastDatePause")
    @XmlSchemaType(name = "dateTime")
    private Date lastDatePause;
    @XmlElement(name = "DateDeactivate")
    @XmlSchemaType(name = "dateTime")
    private Date dateDeactivate;
    @XmlElement(name = "UpdateDate")
    @XmlSchemaType(name = "dateTime")
    private Date updateDate;
    @XmlElement(name = "Suspended")
    private Boolean suspended;
    @XmlElement(name = "ChangesPlace")
    private Boolean changesPlace;

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

    public Date getDateActivate() {
        return dateActivate;
    }

    public void setDateActivate(Date dateActivate) {
        this.dateActivate = dateActivate;
    }

    public Date getLastDatePause() {
        return lastDatePause;
    }

    public void setLastDatePause(Date lastDatePause) {
        this.lastDatePause = lastDatePause;
    }

    public Date getDateDeactivate() {
        return dateDeactivate;
    }

    public void setDateDeactivate(Date dateDeactivate) {
        this.dateDeactivate = dateDeactivate;
    }

    public Boolean getSuspended() {
        return suspended;
    }

    public void setSuspended(Boolean suspended) {
        this.suspended = suspended;
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
