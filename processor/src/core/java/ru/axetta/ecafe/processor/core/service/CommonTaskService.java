/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.cxf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Created by nuc on 08.11.2018.
 */
@Component
@Scope("singleton")
public class CommonTaskService {
    public static final String INFO_PARAM = "info";
    public static final String ERROR_PARAM = "error";
    public static final String NODE_PARAM = "node";
    public static final String OPERATION_PARAM = "operation";
    public static final String OPERATION_LOGGING = "logging";
    private static final Logger logger = LoggerFactory.getLogger(CommonTaskService.class);

    @Async
    public void writeToCommonLog(String node, String level, String payload) {
        String url = RuntimeContext.getInstance().getPropertiesValue("ecafe.processor.commonTaskService", "");
        if (StringUtils.isEmpty(url)) return;
        PostMethod httpMethod = new PostMethod(url);
        httpMethod.addParameter(OPERATION_PARAM, OPERATION_LOGGING);
        httpMethod.addParameter(NODE_PARAM, node);
        httpMethod.addParameter(level, payload);

        try {
            HttpClient httpClient = new HttpClient();
            httpClient.getParams().setContentCharset("UTF-8");
            httpClient.getParams().setSoTimeout(5000);
            int statusCode = httpClient.executeMethod(httpMethod);
            if (HttpStatus.SC_OK != statusCode) {
                logger.error("CommonTaskService bad response code");
            }
        } catch(Exception e) {
            logger.error("Error in sending log message to CommonTaskService: ", e);
        } finally {
            httpMethod.releaseConnection();
        }
    }
}
