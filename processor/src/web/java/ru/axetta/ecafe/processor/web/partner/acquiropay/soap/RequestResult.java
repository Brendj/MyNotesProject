/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.acquiropay.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 23.10.13
 * Time: 13:36
 */

@XmlRootElement(name = "RequestResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class RequestResult {

    @XmlElement(name = "ErrorCode")
    private int errorCode;
    @XmlElement(name = "ErrorDesc")
    private String errorDesc;

    @XmlElement(name = "ParametersList")
    private ParametersList parametersList;
    @XmlElement(name = "RegularPaymentSubscriptionList")
    private SubscriptionList subscriptionList;
    @XmlElement(name = "SubscriptionInfo")
    private SubscriptionInfo subscriptionInfo;
    @XmlElement(name = "RegularPaymentList")
    private RegularPaymentList paymentList;
    @XmlElement(name = "LowerLimitAmountList")
    private LowerLimitAmountList lowerLimitAmountList;
    @XmlElement(name = "PaymentAmountList")
    private PaymentAmountList paymentAmountList;

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

    public ParametersList getParametersList() {
        return parametersList;
    }

    public void setParametersList(ParametersList parametersList) {
        this.parametersList = parametersList;
    }

    public SubscriptionList getSubscriptionList() {
        return subscriptionList;
    }

    public void setSubscriptionList(SubscriptionList subscriptionList) {
        this.subscriptionList = subscriptionList;
    }

    public SubscriptionInfo getSubscriptionInfo() {
        return subscriptionInfo;
    }

    public void setSubscriptionInfo(SubscriptionInfo subscriptionInfo) {
        this.subscriptionInfo = subscriptionInfo;
    }

    public RegularPaymentList getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(RegularPaymentList paymentList) {
        this.paymentList = paymentList;
    }

    public LowerLimitAmountList getLowerLimitAmountList() {
        return lowerLimitAmountList;
    }

    public void setLowerLimitAmountList(LowerLimitAmountList lowerLimitAmountList) {
        this.lowerLimitAmountList = lowerLimitAmountList;
    }

    public PaymentAmountList getPaymentAmountList() {
        return paymentAmountList;
    }

    public void setPaymentAmountList(PaymentAmountList paymentAmountList) {
        this.paymentAmountList = paymentAmountList;
    }
}
