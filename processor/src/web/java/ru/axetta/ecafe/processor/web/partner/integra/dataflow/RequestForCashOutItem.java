/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import java.util.Date;

/**
 * Created by nuc on 11.10.2018.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestForCashOutItem")
public class RequestForCashOutItem {
    @XmlAttribute(name = "sum")
    private Long sum;

    @XmlAttribute(name = "date")
    @XmlSchemaType(name = "date")
    private Date date;

    @XmlAttribute(name = "lastUpdate")
    @XmlSchemaType(name = "date")
    private Date lastUpdate;

    @XmlAttribute(name = "requestStatus")
    private Integer requestStatus;

    public Long getSum() {
        return sum;
    }

    public void setSum(Long sum) {
        this.sum = sum;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(Integer requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
