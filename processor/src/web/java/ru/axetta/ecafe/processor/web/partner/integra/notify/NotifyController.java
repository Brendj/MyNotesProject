/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.notify;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * User: shamil
 * Date: 20.11.14
 * Time: 17:07
 */
@WebService
public interface NotifyController {

    @WebMethod(operationName = "refillFunds")
    public NotifyResult notify(
            @WebParam(name = "accountN") long accountNumber,
            @WebParam(name = "eventCode") int eventCode);
}
