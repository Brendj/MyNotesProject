/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 27.07.12
 * Time: 15:13
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RBKMoneyConfigExt")
public class RBKMoneyConfigExt {


    @XmlAttribute(name = "EshopId")protected  String eshopId;
    @XmlAttribute(name = "ServiceName")protected  String serviceName;
    @XmlAttribute(name = "ContragentName")protected  String contragentName;
    @XmlAttribute(name = "PurchaseUri")protected  String purchaseUri;
    @XmlAttribute(name = "SecretKey")protected  String secretKey;
    @XmlAttribute(name = "Rate")protected   Double rate;
    @XmlAttribute(name = "Show")protected Boolean show;

    public String getEshopId() {
        return eshopId;
    }

    public void setEshopId(String eshopId) {
        this.eshopId = eshopId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }
}
