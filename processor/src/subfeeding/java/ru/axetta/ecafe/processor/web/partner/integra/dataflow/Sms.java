/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 01.08.12
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Sms")
public class Sms {

    @XmlAttribute(name = "ServiceSendTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar serviceSendTime;
    @XmlAttribute(name = "Price")
    protected Long price;
    @XmlAttribute(name = "TransactionSum")
    protected Long transactionSum;
    @XmlAttribute(name = "CardNo")
    protected Long cardNo;
    @XmlAttribute(name ="DeliveryStatus")
    protected Integer deliveryStatus;
    @XmlAttribute(name ="ContentsType")
    protected Integer contentsType;

    public XMLGregorianCalendar getServiceSendTime() {
        return serviceSendTime;
    }

    public void setServiceSendTime(XMLGregorianCalendar serviceSendTime) {
        this.serviceSendTime = serviceSendTime;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getTransactionSum() {
        return transactionSum;
    }

    public void setTransactionSum(Long transactionSum) {
        this.transactionSum = transactionSum;
    }

    public Long getCardNo() {
        return cardNo;
    }

    public void setCardNo(Long cardNo) {
        this.cardNo = cardNo;
    }

    public Integer getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(Integer deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public Integer getContentsType() {
        return contentsType;
    }

    public void setContentsType(Integer contentsType) {
        this.contentsType = contentsType;
    }
}