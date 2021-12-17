/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.MeshUnprocessableEntityException;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.DefaultProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.Date;

@Component
@Scope("session")
@DependsOn("runtimeContext")
public class ServiceControlPage extends BasicWorkspacePage {
    private static final Logger log = LoggerFactory.getLogger(ServiceControlPage.class);

    private static final String URL_ENDPOINT_PROP = "ecafe.processing.service.mspkafka.rest.url";
    private static final String API_KEY_PROP = "ecafe.processing.service.mspkafka.rest.apikey";
    private static final String DEFAULT_SAMPLE_SIZE = "50000";
    private static final Long DELTA_MONTH = 2595600000L;

    private String endpointUrl;
    private String apiKey;
    private Date startDate = new Date();
    private Date endDate = new Date();

    @PostConstruct
    public void init(){
        endpointUrl = RuntimeContext.getInstance()
                .getConfigProperties()
                .getProperty(URL_ENDPOINT_PROP);

        apiKey = RuntimeContext.getInstance()
                .getConfigProperties()
                .getProperty(API_KEY_PROP);
    }

    public void sendTask(){
        try{
            if(startDate.after(endDate)){
                throw new IllegalArgumentException("Begin date after end date");
            }
            if((endDate.getTime() - startDate.getTime()) > DELTA_MONTH){
                throw new IllegalArgumentException("Too long period");
            }

            URL url = new URL(endpointUrl);
            log.info("Execute GET request to " + url);
            GetMethod httpMethod = new GetMethod(url.getPath());
            httpMethod.setRequestHeader("API-KEY", apiKey);

            NameValuePair[] nameValuePairs = {
                    new NameValuePair("beginPeriod", String.valueOf(startDate.getTime())),
                    new NameValuePair("endPeriod", String.valueOf(endDate.getTime())),
                    new NameValuePair("sampleSize", DEFAULT_SAMPLE_SIZE)
            };
            httpMethod.setQueryString(nameValuePairs);

            executeRequest(httpMethod, url);

            printMessage("Запрос отправлен");
        } catch (Exception e){
            log.error("Failed to send task ", e);
            printError("Не удалось отправить задачу: " + e.getMessage());
        }
    }

    private void executeRequest(HttpMethodBase httpMethod, URL url) throws Exception {
        try {
            HttpClient httpClient = getHttpClient(url);
            int statusCode = httpClient.executeMethod(httpMethod);

            if (statusCode != HttpStatus.SC_OK) {
                String errorMessage = "Service request has status " + statusCode;
                if (statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY) {
                    throw new MeshUnprocessableEntityException(errorMessage);
                } else {
                    throw new Exception(errorMessage);
                }
            }
        } finally {
            httpMethod.releaseConnection();
        }
    }

    private HttpClient getHttpClient(URL url) {
        HttpClient httpClient = new HttpClient();
        httpClient.getHostConfiguration().setHost(url.getHost(), url.getPort(),
                new Protocol(url.getProtocol(), new DefaultProtocolSocketFactory() , url.getPort()));
        return httpClient;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String getPageFilename() {
        return "service/service_control_page";
    }
}
