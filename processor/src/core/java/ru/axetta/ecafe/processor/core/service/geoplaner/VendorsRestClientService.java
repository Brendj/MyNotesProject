/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.geoplaner;

import org.apache.commons.httpclient.params.HttpClientParams;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.SmartWatchVendor;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VendorsRestClientService {
    private final Logger logger = LoggerFactory.getLogger(VendorsRestClientService.class);
    private final ObjectMapper mapper = new ObjectMapper();

    private final boolean debug = isDebug();

    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final int CONNECTION_TIMEOUT = 5000;

    public Integer sendPost(GeoplanerEventInfo event, EventType type, SmartWatchVendor vendor) throws Exception {
        String endPointAddress = getAddress(type, vendor);
        if(endPointAddress == null){
            logger.error("The address is null when trying to send an event packet: " + event.getClass());
            return null;
        }
        HttpClientParams httpParams = new HttpClientParams();
        httpParams.setParameter("http.connection.timeout", CONNECTION_TIMEOUT);
        HttpClient httpClient = new HttpClient(httpParams);
        PostMethod method = new PostMethod(endPointAddress);
        method.addRequestHeader("Content-Type", "application/json");

        String JSONString = mapper.writeValueAsString(event);

        StringRequestEntity requestEntity = new StringRequestEntity(
                JSONString,
                "application/json",
                "UTF-8");
        method.setRequestEntity(requestEntity);

        if(debug){
            String outputMessage = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(event);
            logger.info("\n\n" + ANSI_YELLOW + outputMessage + ANSI_RESET + "\n\n");
        }

        return httpClient.executeMethod(method);
    }

    private String getAddress(EventType type, SmartWatchVendor vendor) throws Exception {
        switch (type) {
            case ENTER_EVENTS:
                return vendor.getEnterEventsEndPoint();
            case PURCHASES:
                return vendor.getPurchasesEndPoint();
            case PAYMENTS:
                return vendor.getPaymentEndPoint();
            default:
                throw new IllegalArgumentException("Unknown code: " + type);
        }
    }

    private boolean isDebug() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String reqInstance = runtimeContext
                .getConfigProperties().getProperty("ecafe.processor.geoplaner.restcontroller.debug", "false");
        return Boolean.parseBoolean(reqInstance);
    }
}
