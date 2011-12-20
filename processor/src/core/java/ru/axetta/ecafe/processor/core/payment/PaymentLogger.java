/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.payment;

import org.w3c.dom.Document;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 31.07.2009
 * Time: 12:40:14
 * To change this template use File | Settings | File Templates.
 */
public interface PaymentLogger {

    void registerPaymentRequest(Document requestDocument, long idOfContragent, String idOfSync);

    void registerPaymentResponse(Document responseDocument, long idOfContragent, String idOfSync);
}