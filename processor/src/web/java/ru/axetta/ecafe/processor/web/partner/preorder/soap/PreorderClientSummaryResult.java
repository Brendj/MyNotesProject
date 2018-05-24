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

    @XmlElement(name = "balanceWithPreorders")
    private Long balanceWithPreorders;

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

    public Long getBalanceWithPreorders() {
        return balanceWithPreorders;
    }

    public void setBalanceWithPreorders(Long balanceWithPreorders) {
        this.balanceWithPreorders = balanceWithPreorders;
    }

    public PreorderCalendar getCalendar() {
        return calendar;
    }

    public void setCalendar(PreorderCalendar calendar) {
        this.calendar = calendar;
    }
}
