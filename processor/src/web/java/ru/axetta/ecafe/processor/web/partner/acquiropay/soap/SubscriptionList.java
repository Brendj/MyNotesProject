/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.acquiropay.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 23.10.13
 * Time: 18:13
 */

@XmlRootElement(name = "RegularPaymentSubscriptionList")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionList {

    @XmlElement(name = "RegularPaymentSubscriptionID")
    private List<Long> idList;
    @XmlElement(name = "SubscriptionInfo")
    private List<SubscriptionInfo> infoList;

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }

    public List<SubscriptionInfo> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<SubscriptionInfo> infoList) {
        this.infoList = infoList;
    }
}
