/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.nsi;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.util.Date;

@Component
@Scope("singleton")
public class ReviseLogger {
    private final Logger logger = LoggerFactory.getLogger(ReviseLogger.class);
    private final String filenameProperty = "ecafe.processor.revise.dtszn.log.file";
    private final String enableLoggerProperty = "ecafe.processor.revise.dtszn.log.enable";
    Object obj = new Object();

    private String fileName;
    private Boolean enableLogger;

    private String getFileName() {
        if (fileName == null) {
            fileName = RuntimeContext.getInstance().getConfigProperties().getProperty(filenameProperty, "");
        }
        return fileName;
    }

    private Boolean getEnableLogger() {
        if (enableLogger == null) {
            enableLogger = Boolean.parseBoolean(RuntimeContext.getInstance().getConfigProperties().getProperty(enableLoggerProperty, "false"));
        }
        return enableLogger;
    }

    public void logRequest(EntityEnclosingMethod method) {
        if (!getEnableLogger() || StringUtils.isEmpty(getFileName())) return;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(CalendarUtils.dateTimeToString(new Date()));
            sb.append(" Request ");
            fillHeaders(method.getRequestHeaders(), sb);
            sb.append(" Body={ ");
            sb.append(((StringRequestEntity)method.getRequestEntity()).getContent());
            sb.append(" }");
            sb.append("\r\n");
            writeDataToFile(sb);
        } catch (Exception e) {
            logger.error("Error writing revise 2.0 request to log: ", e);
        }
    }

    public void logResponse(EntityEnclosingMethod method, String responseBody, int status, long time) {
        if (!getEnableLogger() || StringUtils.isEmpty(getFileName())) return;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(CalendarUtils.dateTimeToString(new Date()));
            sb.append(" Response with status=");
            sb.append(status);
            sb.append(" Time taken ");
            sb.append(time);
            sb.append("msec");
            fillHeaders(method.getRequestHeaders(), sb);
            sb.append(" Body={ ");
            sb.append(responseBody);
            sb.append(" }");
            sb.append("\r\n");
            writeDataToFile(sb);
        } catch (Exception e) {
            logger.error("Error writing revise 2.0 response to log: ", e);
        }
    }

    private void fillHeaders(Header[] headers, StringBuilder sb) {
        int headersLength = headers.length;
        sb.append( " Headers={ ");
        int counter = 0;
        for (Header header : headers) {
            sb.append(header.getName());
            sb.append("=");
            sb.append(header.getValue());
            if (++counter < headersLength) {
                sb.append(", ");
            }
        }
        sb.append(" }");
    }

    private void writeDataToFile(StringBuilder sb) throws Exception {
        synchronized (obj) {
            FileWriter fw = new FileWriter(getFileName(), true);
            fw.write(sb.toString());
            fw.flush();
            fw.close();
        }
    }
}
