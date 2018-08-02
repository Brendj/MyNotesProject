/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.scud;

import generated.spb.SCUD.ObjectFactory;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;

public class HeaderHandler extends AbstractSoapInterceptor {
    private static final Logger logger = LoggerFactory
            .getLogger(ru.axetta.ecafe.processor.core.service.scud.HeaderHandler.class);
    private static String LOGIN = RuntimeContext.getInstance().getScudLogin();
    private static String PASSWORD = RuntimeContext.getInstance().getScudPassword();

    public HeaderHandler() {
        super(Phase.PREPARE_SEND);

    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        try {
            message.getHeaders().add(new Header(ObjectFactory._Login_QNAME, LOGIN, new JAXBDataBinding(String.class)));
            message.getHeaders().add(new Header(ObjectFactory._Password_QNAME, PASSWORD, new JAXBDataBinding(String.class)));
        } catch (JAXBException e) {
            logger.info("Error handling message: ", e);
        }
    }

}
