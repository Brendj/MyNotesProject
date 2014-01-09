/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.acquiropay.soap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 07.11.13
 * Time: 13:26
 */

public interface IRegularPayment {

    @WebMethod
    RequestResult regularPaymentCreateSubscription(@WebParam(name = "clientID") String clientID,
            @WebParam(name = "clientIDType") int clientIDType, @WebParam(name = "contractID") String contractID,
            @WebParam(name = "accountRegion") int accountRegion,
            @WebParam(name = "lowerLimitAmount") long lowerLimitAmount,
            @WebParam(name = "paymentAmount") long paymentAmount, @WebParam(name = "currency") int currency,
            @WebParam(name = "subscriptionPeriodOfValidity") int period);

    @WebMethod
    RequestResult regularPaymentDeleteSubscription(
            @WebParam(name = "regularPaymentSubscriptionID") Long regularPaymentSubscriptionID);

    @WebMethod
    RequestResult regularPaymentEditSubscription(
            @WebParam(name = "regularPaymentSubscriptionID") Long regularPaymentSubscriptionID,
            @WebParam(name = "lowerLimitAmount") long lowerLimitAmount,
            @WebParam(name = "paymentAmount") long paymentAmount, @WebParam(name = "currency") int currency,
            @WebParam(name = "subscriptionPeriodOfValidity") int period);

    @WebMethod
    RequestResult regularPaymentReadSubscriptionList(@WebParam(name = "clientID") String clientID,
            @WebParam(name = "clientIDType") int clientIDType);

    @WebMethod
    RequestResult regularPaymentReadSubscriptionListWithInfo(@WebParam(name = "clientID") String clientID,
            @WebParam(name = "clientIDType") int clientIDType);

    @WebMethod
    RequestResult regularPaymentReadSubscription(
            @WebParam(name = "regularPaymentSubscriptionID") Long regularPaymentSubscriptionID);

    @WebMethod
    RequestResult regularPaymentReadPayments(@WebParam(name = "regularPaymentSubscriptionID") Long subscriptionID,
            @WebParam(name = "beginDate") Date beginDate, @WebParam(name = "endDate") Date endDate);

    @WebMethod
    RequestResult regularPaymentReadSettings();
}
