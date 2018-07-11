/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.soap;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by baloun on 18.05.2018.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PreorderClientSummaryResult")
public class PreorderClientSummaryResult extends Result {
    @XmlElement(name = "subscriptionFeeding")
    private Integer subscriptionFeeding;

    @XmlElement(name = "forbiddenDays")
    private Integer forbiddenDays;

    @XmlElement(name = "preorderSum14Days")
    private Long preorderSum14Days;

    @XmlElement(name = "preorderSum3Days")
    private Long preorderSum3Days;

    @XmlElement(name = "calendar")
    private PreorderCalendar calendar;

    public PreorderClientSummaryResult() {

    }

    public Integer getSubscriptionFeeding() {
        return subscriptionFeeding;
    }

    public void setSubscriptionFeeding(Integer subscriptionFeeding) {
        this.subscriptionFeeding = subscriptionFeeding;
    }

    public Integer getForbiddenDays() {
        return forbiddenDays;
    }

    public void setForbiddenDays(Integer forbiddenDays) {
        this.forbiddenDays = forbiddenDays;
    }

    public PreorderCalendar getCalendar() {
        return calendar;
    }

    public void setCalendar(PreorderCalendar calendar) {
        this.calendar = calendar;
    }

    public Long getPreorderSum14Days() {
        return preorderSum14Days;
    }

    public void setPreorderSum14Days(Long preorderSum14Days) {
        this.preorderSum14Days = preorderSum14Days;
    }

    public Long getPreorderSum3Days() {
        return preorderSum3Days;
    }

    public void setPreorderSum3Days(Long preorderSum3Days) {
        this.preorderSum3Days = preorderSum3Days;
    }
}
