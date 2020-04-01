/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.acquiropay.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import java.util.Date;

/**
 * Created by nuc on 25.09.2019.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class StatusInfo {
    @XmlAttribute(name = "RegularPaymentID")
    private Long idOfRegularPayment;
    @XmlAttribute(name = "ErrorCode")
    private int errorCode;
    @XmlAttribute(name = "ErrorDesc")
    private String errorDesc;
    @XmlAttribute(name = "Date")
    @XmlSchemaType(name = "dateTime")
    private Date date;

    public StatusInfo() {

    }

    public StatusInfo(Long idOfRegularPayment, int errorCode, String errorDesc) {
        this.idOfRegularPayment = idOfRegularPayment;
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getIdOfRegularPayment() {
        return idOfRegularPayment;
    }

    public void setIdOfRegularPayment(Long idOfRegularPayment) {
        this.idOfRegularPayment = idOfRegularPayment;
    }
}
