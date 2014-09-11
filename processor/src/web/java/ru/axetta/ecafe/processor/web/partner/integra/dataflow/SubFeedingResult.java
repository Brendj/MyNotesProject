/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 27.11.13
 * Time: 13:44
 */

@XmlRootElement(name = "SubscriptionFeedingInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubFeedingResult extends Result implements Serializable {

    @XmlElement(name = "IdOfSubscriptionFeeding")
    private Long idOfSubscriptionFeeding;
    @XmlElement(name = "DateCreateService")
    @XmlSchemaType(name = "dateTime")
    private Date dateCreateService;
    @XmlElement(name = "DateActivateSubscription")
    @XmlSchemaType(name = "dateTime")
    private Date dateActivateSubscription;
    @XmlElement(name = "LastDatePause")
    @XmlSchemaType(name = "dateTime")
    private Date lastDatePause;
    @XmlElement(name = "DateDeactivate")
    @XmlSchemaType(name = "dateTime")
    private Date dateDeactivate;
    @XmlElement(name = "Suspended")
    private Boolean suspended;

    public Long getIdOfSubscriptionFeeding() {
        return idOfSubscriptionFeeding;
    }

    public void setIdOfSubscriptionFeeding(Long idOfSubscriptionFeeding) {
        this.idOfSubscriptionFeeding = idOfSubscriptionFeeding;
    }

    public Date getDateActivateSubscription() {
        return dateActivateSubscription;
    }

    public void setDateActivateSubscription(Date dateActivateSubscription) {
        this.dateActivateSubscription = dateActivateSubscription;
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

    public Date getDateCreateService() {
        return dateCreateService;
    }

    public void setDateCreateService(Date dateCreateService) {
        this.dateCreateService = dateCreateService;
    }
}
