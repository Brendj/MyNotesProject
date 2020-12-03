/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.persistence.Contragent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("RNIPLoadPaymentsServiceV22")
@Scope("singleton")
public class RNIPLoadPaymentsServiceV22 extends RNIPLoadPaymentsServiceV21 {

    @Override
    protected RNIPSecuritySOAPHandlerV22 getSecurityHandler(String alias, String pass, Contragent contragent) {
        return new RNIPSecuritySOAPHandlerV22(alias, pass, getPacketLogger(contragent));
    }

}
