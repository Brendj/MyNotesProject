/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 11.07.14
 * Time: 10:42
 * To change this template use File | Settings | File Templates.
 */
public class WSLoggingInInterceptor extends AbstractSoapInterceptor {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(WSLoggingInInterceptor.class);

    public WSLoggingInInterceptor() {
        super(Phase.RECEIVE);
    }

    protected File getLogFile(String folder) {
        String fileName = new SimpleDateFormat("dd_MM_yyyy").format(new Date(System.currentTimeMillis())) + ".log";
        File f = new File(folder, fileName);
        if(!f.exists()) {
            try {
                if(!f.createNewFile()) {
                    return null;
                }
            } catch (IOException ioe) {
                return null;
            }
        }
        return f;
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        String folder = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_MSK_NSI_LOGGING_FOLDER);
        if(folder == null || StringUtils.isBlank(folder)) {
            return;
        }
        //get the remote address
        File f = getLogFile(folder);
        if(f == null) {
            logger.error("Failed to create WS logging file");
            return;
        }


        HttpServletRequest httpRequest = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);

        try {
            InputStream is = message.getContent(InputStream.class);
            CachedOutputStream os = new CachedOutputStream();
            IOUtils.copy(is, os);
            os.flush();
            message.setContent(InputStream.class, os.getInputStream());
            is.close();

            FileOutputStream fos = new FileOutputStream(f, true);
            String infoStr = (f.length() > 0 ? "\r\n\r\n\r\n" : "") + String.format("In request at %s from %s",
                    new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(System.currentTimeMillis())),
                    httpRequest.getRemoteAddr() + "\r\n");
            fos.write(infoStr.getBytes());
            fos.write(os.getBytes());
            //System.out.println("The request is: " + IOUtils.toString(os.getInputStream()));
            os.close();
            fos.flush();
            fos.close();
        } catch (Exception e) {
            logger.error(String.format("Failed to parse request from %s", httpRequest.getRemoteAddr()), e);
        }
    }
}
