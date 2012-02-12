/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.soap;


import ru.axetta.ecafe.processor.web.partner.integra.dataflow.PaymentResult;

import javax.jws.WebService;

@WebService
public interface PaymentController {
    PaymentResult balanceRequest(String pid, Long clientId, Long opId, Long termId, int paymentSystem) throws Exception;
    PaymentResult commitPaymentRequest(String pid, Long clientId, Long sum, String time, Long opId, Long termId, int paymentSystem)
            throws Exception;
}
