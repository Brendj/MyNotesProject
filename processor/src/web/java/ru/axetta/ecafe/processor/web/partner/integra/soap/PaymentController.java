/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.soap;


import ru.axetta.ecafe.processor.web.partner.integra.dataflow.PaymentResult;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface PaymentController {
    PaymentResult process(@WebParam(name="request") String request)
            throws Exception;
}
