/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.util;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.LogServiceType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.AbstractLoggingInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.util.logging.Logger;

public class ClientRoomControllerLoggingOutInterceptor extends AbstractLoggingInterceptor {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ClientRoomControllerLoggingOutInterceptor.class);
    public ClientRoomControllerLoggingOutInterceptor() {
        super(Phase.PRE_STREAM);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        if (!RuntimeContext.getInstance().isLogInfoService()) return;
        String request = RuntimeContext.getAppContext().getBean(SoapLoggerInfo.class).getIncomingMessage();
        if (request != null) {
            OutputStream os = message.getContent(OutputStream.class);
            CacheAndWriteOutputStream cwos = new CacheAndWriteOutputStream(os);
            message.setContent(OutputStream.class, cwos);
            cwos.registerCallback(new LoggingOutCallBack());
        }
    }

    public String getMessageString(String message, String suffix) {
        try {
            for (String method : RuntimeContext.getInstance().getMethodsInfoService()) {
                if (message.contains(method + suffix)) {
                    return message;
                }
            }
        } catch (Exception e) {
            logger.error("Error in handle ClientRoomController message: ", e);
        }
        return null;
    }

    @Override
    protected Logger getLogger ( )
    {
        // TODO Auto-generated method stub
        return null;
    }

    class LoggingOutCallBack implements CachedOutputStreamCallback {
        @Override
        public void onClose ( CachedOutputStream cos ) {
            try {
                if (cos != null) {
                    String request = RuntimeContext.getAppContext().getBean(SoapLoggerInfo.class).getIncomingMessage();
                    if (request != null) {
                        String response = getMessageString(IOUtils.toString(cos.getInputStream()), "Response");
                        DAOService.getInstance().saveLogServiceMessage(request, response, LogServiceType.CLIENT_ROOM_CONTROLLER);
                    }
                }

            } catch ( Exception e ) {
                logger.error("Error in process outgoing message: ", e);
            }
        }

        @Override
        public void onFlush ( CachedOutputStream arg0 )
        {

        }
    }
}
