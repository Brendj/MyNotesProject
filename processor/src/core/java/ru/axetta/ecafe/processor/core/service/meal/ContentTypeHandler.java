/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.meal;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 10.04.17
 * Time: 10:37
 */
public class ContentTypeHandler extends AbstractSoapInterceptor {

    public ContentTypeHandler() {
        super(Phase.RECEIVE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        Map<String,String> map = (Map<String, String>)message.get(Message.PROTOCOL_HEADERS);
        message.put(Message.CONTENT_TYPE, "text/xml");
        map.put("content-type", "[text/xml]");
        message.put(Message.PROTOCOL_HEADERS, map);
    }

}
