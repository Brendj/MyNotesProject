/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.etpmv;

import ru.axetta.ecafe.processor.core.partner.nsi.SOAPLoggingHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 * Created by nuc on 03.12.2018.
 */
@Component
public class AISContingentMessageLogger extends SOAPLoggingHandler implements SOAPHandler<SOAPMessageContext> {

    private final Logger logger = LoggerFactory.getLogger(AISContingentMessageLogger.class);

    @Override
    protected void logToSystemOut(SOAPMessageContext smc) {
        Boolean outboundProperty = (Boolean) smc.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        try {
            final SOAPPart soapPart = smc.getMessage().getSOAPPart();
            final Document doc = soapPart.getEnvelope().getOwnerDocument();
            logger.info((outboundProperty ? "Outbound message: " : "Incoming message: ") + toString(doc));
        } catch (Exception e) {
            logger.error("Exception in handler: ", e);
        }
    }
}
