/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.util;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.LogServiceType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.phase.Phase;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class ClientRoomControllerLoggingInterceptor extends AbstractSoapInterceptor {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ClientRoomControllerLoggingInterceptor.class);

    public ClientRoomControllerLoggingInterceptor() {
        super(Phase.RECEIVE);
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        if (!RuntimeContext.getInstance().isLogInfoService()) return;
        try {
            InputStream is = message.getContent(InputStream.class);
            CachedOutputStream os = new CachedOutputStream();
            IOUtils.copy(is, os);
            os.flush();
            message.setContent(InputStream.class, os.getInputStream());
            is.close();
            String request = new String(os.getBytes());
            for (String method : RuntimeContext.getInstance().getMethodsInfoService()) {
                if (request.contains(method + ">")) {
                    DAOService.getInstance().saveLogServiceMessage(request, LogServiceType.CLIENT_ROOM_CONTROLLER);
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("Error in handle ClientRoomController message: ", e);
        }
    }
}
