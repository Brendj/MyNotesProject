/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.nsi.SOAPLoggingHandler;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.cxf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by i.semenov on 24.09.2018.
 */
@Component
@Scope("singleton")
public class EMPMessageLogger extends SOAPLoggingHandler implements SOAPHandler<SOAPMessageContext>  {
    private final Logger logger = LoggerFactory.getLogger(EMPMessageLogger.class);
    Object obj = new Object();
    private String fileName;
    private final String filenameProperty = "ecafe.processor.sms.service.emp.log.file";
    private final String nodeProperty = "ecafe.processor.sms.service.emp.log.node";
    private boolean initComplete = false;
    private boolean isOn = false;

    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext smc) {
        logRequest(smc, true);
        return true;
    }

    public boolean handleFault(SOAPMessageContext smc) {
        logRequest(smc, false);
        return true;
    }

    // nothing to clean up
    public void close(MessageContext messageContext) {
    }

    private void logRequest(SOAPMessageContext smc, boolean success) {
        if (!isOn() || StringUtils.isEmpty(getFileName())) return;
        try {
            final SOAPPart soapPart = smc.getMessage().getSOAPPart();
            final Document doc = soapPart.getEnvelope().getOwnerDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(CalendarUtils.dateTimeToString(new Date()));
            sb.append(success ? " Success " : " Fault ");
            sb.append((Boolean)smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY) ? "out: " : "in: ");
            sb.append(String.format("(%s) ", Thread.currentThread().getId()));
            sb.append(toString(doc));
            sb.append("\r\n");
            synchronized (obj) {
                FileWriter fw = new FileWriter(getFileName(), true);
                fw.write(sb.toString());
                fw.flush();
                fw.close();
            }
        } catch (Exception e) {
            logger.error("Error writing EMP message to log: ", e);
        }
    }

    private boolean isOn() {
        if (initComplete) return isOn;
        initComplete = true;
        String nodes = RuntimeContext.getInstance().getPropertiesValue(nodeProperty, "");
        if (nodes.equals("ALL")) {
            isOn = true;
            return isOn;
        } else if (nodes.equals("")) {
            isOn = false;
            return isOn;
        }
        String[] strs = nodes.split(",");
        List<String> nodesList = new ArrayList<String>(Arrays.asList(strs));
        if (nodesList.contains(RuntimeContext.getInstance().getNodeName()))
            isOn = true;
        else
            isOn = false;
        return  isOn;
    }

    private String getFileName() {
        if (fileName == null) {
            fileName = RuntimeContext.getInstance().getConfigProperties().getProperty(filenameProperty, "");
        }
        return fileName;
    }
}
