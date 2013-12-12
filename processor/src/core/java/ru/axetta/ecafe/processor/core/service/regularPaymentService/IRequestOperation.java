/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.regularPaymentService;

import ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription.MfrRequest;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 12.11.13
 * Time: 11:00
 */

public interface IRequestOperation {

    public static final String SUBSCRIPTION_DELETE = "subscriptionDeleteRequest";
    public static final String SUBSCRIPTION_REG = "subscriptionRegRequest";
    public static final String STATUS_CHECK = "statusCheckRequest";
    public static final String REGULAR_PAYMENT = "regularPaymentRequest";

    MfrRequest createRequest(Long subscriptionId);

    Map<String, String> getRequestParams(MfrRequest request);

    void processResponse(Long mfrRequestId, PaymentResponse paymentResponse);

    boolean postProcessResponse(Long mfrRequestId, PaymentResponse paymentResponse);

}
