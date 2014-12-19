/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.notify;

import ru.axetta.ecafe.processor.web.partner.autopayments.AutoPaymentResultRequest;
import ru.axetta.ecafe.processor.web.partner.autopayments.AutoPaymentResultResponse;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

/**
 * User: shamil
 * Date: 20.11.14
 * Time: 17:07
 */
@WebService
public interface NotifyController {

    @WebMethod(operationName = "NotifyRefill")
    public NotifyResult notify(
            @WebParam(name = "accountN") long accountNumber,
            @WebParam(name = "eventCode") int eventCode);


    @WebMethod(action = "AsynchronousPaymentResponse", operationName = "AsynchronousPaymentResponse")
    public List<AutoPaymentResultResponse> AsynchronousPaymentResponse(
            @WebParam(name = "opers") List<AutoPaymentResultRequest> autoPaymentResultRequestList);
}
