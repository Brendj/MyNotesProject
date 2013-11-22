/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 27.07.12
 * Time: 15:04
 * To change this template use File | Settings | File Templates.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChronopayConfigExt")
public class ChronopayConfigExt {
    @XmlAttribute(name = "SharedSec")protected String sharedSec;
    @XmlAttribute(name = "Rate")protected Double rate;
    @XmlAttribute(name = "IP")protected  String ip;
    @XmlAttribute(name = "ContragentName")protected String contragentName;
    @XmlAttribute(name = "PurchaseUri")protected  String purchaseUri;
    @XmlAttribute(name = "CallbackUrl")protected String callbackUrl;
    @XmlAttribute(name = "Show")protected Boolean show;

    public String getSharedSec() {
        return sharedSec;
    }

    public void setSharedSec(String sharedSec) {
        this.sharedSec = sharedSec;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getContragentName() {
        return contragentName;
    }

    public void setContragentName(String contragentName) {
        this.contragentName = contragentName;
    }

    public String getPurchaseUri() {
        return purchaseUri;
    }

    public void setPurchaseUri(String purchaseUri) {
        this.purchaseUri = purchaseUri;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }
}
