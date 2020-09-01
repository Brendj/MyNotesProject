/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.nsi;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.revise.ReviseDAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.Parameter;
import javax.persistence.Query;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
@Scope("singleton")
public class ReviseLogger {
    private final Logger logger = LoggerFactory.getLogger(ReviseLogger.class);
    private final String filenameProperty = "ecafe.processor.revise.dtszn.log.file";
    private final String enableLoggerProperty = "ecafe.processor.revise.dtszn.log.enable";
    Object obj = new Object();

    private String fileName;
    private Boolean enableLogger;
    private Date currentDate;
    private String currentDateString;

    @PostConstruct
    private void init() {
        currentDate = new Date();
    }

    private String getFileName() {
        if (fileName == null) {
            fileName = RuntimeContext.getInstance().getConfigProperties().getProperty(filenameProperty, "");
        }
        Date date = new Date();
        if (!DateUtils.isSameDay(date, currentDate) || StringUtils.isEmpty(currentDateString)) {
            currentDate = date;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            currentDateString = format.format(currentDate);
        }
        return fileName + "." + currentDateString;
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

    public void logRequestDB(Query query, String queryString) throws Exception {
        if (!getEnableLogger() || StringUtils.isEmpty(getFileName())) return;
        StringBuilder sb = new StringBuilder();
        sb.append(new Date());
        sb.append(" Query { ");
        sb.append(queryString);
        sb.append(" }");
        sb.append("\r\n");
        sb.append("Parameters={ ");
        int size = query.getParameters().size();
        int counter = 0;
        for (Parameter parameter : query.getParameters()) {
            sb.append(parameter.getName());
            sb.append("=");
            sb.append(query.getParameterValue(parameter.getName()));
            if (++counter < size) {
                sb.append(", ");
            }
        }
        sb.append(" }");
        sb.append("\r\n");
        writeDataToFile(sb);
    }

    public void logResponseDB(List<ReviseDAOService.DiscountItem> discountItemList) throws Exception {
        if (!getEnableLogger() || StringUtils.isEmpty(getFileName())) return;
        StringBuilder sb = new StringBuilder();
        sb.append(new Date());
        sb.append(" Response { size=");
        sb.append(discountItemList.size());
        sb.append("\r\n");
        int size = discountItemList.size();
        int counter = 0;
        for (ReviseDAOService.DiscountItem item : discountItemList) {
            sb.append("item={");
            sb.append("registry_guid=\"");
            sb.append(item.getRegistryGUID());
            sb.append("\",dszn_code=\"");
            sb.append(item.getDsznCode());
            sb.append("\",title=\"");
            sb.append(item.getTitle());
            sb.append("\",sd=\"");
            sb.append(item.getSd());
            sb.append("\",sd_dszn=\"");
            sb.append(item.getSdDszn());
            sb.append("\",fd=\"");
            sb.append(item.getFd());
            sb.append("\",fd_dszn=\"");
            sb.append(item.getFdDszn());
            sb.append("\",is_benefit_confirm=\"");
            sb.append(item.getBenefitConfirm());
            sb.append("\",updated_at=\"");
            sb.append(item.getUpdatedAt());
            sb.append("\",is_del=\"");
            sb.append(item.getDeleted());
            sb.append("\",mesh_guid=\"");
            sb.append(item.getMeshGUID() == null ? "null" : item.getMeshGUID());
            sb.append("\"}");
            if (++counter < size) {
                sb.append(", ");
            }
        }
        sb.append(" }\r\n");
        writeDataToFile(sb);
    }
}
