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

import java.util.ArrayList;
import java.util.List;

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
    public static final String OPERATION_INVALIDATE_CACHE = "invcache";
    public static final String OPERATION_IDOFORG = "idoforg";
    private static final Logger logger = LoggerFactory.getLogger(CommonTaskService.class);
    private List<String> cacheMulticastList;
    private static final String CACHE_MULTICAST_ADDRESSES_PROPERTY = "ecafe.processor.cache.multicast.addresses";
    private static final String PORT_PROPERTY = "ecafe.processor.commonTaskService.port";

    @Async
    public void writeToCommonLog(String node, String level, String payload) {
        String url = RuntimeContext.getInstance().getPropertiesValue("ecafe.processor.commonTaskService", "");
        if (StringUtils.isEmpty(url)) return;
        PostMethod httpMethod = new PostMethod(url);
        httpMethod.addParameter(OPERATION_PARAM, OPERATION_LOGGING);
        httpMethod.addParameter(NODE_PARAM, node);
        httpMethod.addParameter(level, payload);

        int statusCode = getStatus(httpMethod);
        if (HttpStatus.SC_OK != statusCode) {
            logger.error("CommonTaskService bad response code");
        }
    }

    private int getStatus(PostMethod httpMethod) {
        try {
            HttpClient httpClient = new HttpClient();
            httpClient.getParams().setContentCharset("UTF-8");
            httpClient.getParams().setSoTimeout(5000);
            return httpClient.executeMethod(httpMethod);
        } catch(Exception e) {
            logger.error("Error in getStatus CommonTaskService: ", e);
            return -1;
        } finally {
            httpMethod.releaseConnection();
        }
    }

    @Async
    public void invalidateOrgMulticast(Long idOfOrg) {
        return;
        /*if (cacheMulticastList == null) {
            cacheMulticastList = getCacheMulticastAddresses();
        }
        String port = RuntimeContext.getInstance().getPropertiesValue(PORT_PROPERTY, "8080");
        for (String ipAddress : cacheMulticastList) {
            String url = String.format("http://%s:%s/processor/commontask", ipAddress, port);
            //logger.info(String.format("Doing http request to %s", url));
            PostMethod httpMethod = new PostMethod(url);
            httpMethod.addParameter(OPERATION_PARAM, OPERATION_INVALIDATE_CACHE);
            httpMethod.addParameter(OPERATION_IDOFORG, idOfOrg.toString());

            int statusCode = getStatus(httpMethod);
        }*/
    }

    private List<String> getCacheMulticastAddresses() {
        List<String> result = new ArrayList<>();
        String str = RuntimeContext.getInstance().getConfigProperties().getProperty(CACHE_MULTICAST_ADDRESSES_PROPERTY);
        String[] arr = str.split(",");
        for (String s : arr) {
            result.add(s);
        }
        return result;
    }
}
