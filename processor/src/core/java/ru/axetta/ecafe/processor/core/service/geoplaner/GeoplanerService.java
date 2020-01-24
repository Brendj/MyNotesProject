/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.geoplaner;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Scope("singleton")
public class GeoplanerService {
    private Logger logger = LoggerFactory.getLogger(GeoplanerService.class);

    private final String URL_FOR_ENTER_EVENTS = getGeoplanerURLEnterEvents();
    private final String URL_FOR_PURCHASES = getGeoplanerURLPurchases();
    private final String URL_FOR_PAYMENTS = getGeoplanerURLPayments();
    //private final String TEST_ENDPOINT_ADDRESS = "https://testrestcontroller.herokuapp.com/test"; // Тестовый сервер на heroku

    private boolean debug = isDebug();

    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RESET = "\u001B[0m";

    public Integer sendPost(Object event, int code) throws Exception{
        String endPointAddress = getAddress(code);
        if(endPointAddress == null){
            logger.error("The address is null when trying to send an event packet: " + event.getClass());
            return null;
        }
        HttpClient httpClient = new HttpClient();
        PostMethod method = new PostMethod(endPointAddress);
        method.addRequestHeader("Content-Type", "application/json");

        ObjectMapper mapper = new ObjectMapper();
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

    private String getAddress(int code) throws Exception{
        if(code == 1){
            return URL_FOR_ENTER_EVENTS;
        } else if(code == 2){
            return URL_FOR_PURCHASES;
        } else if(code == 3){
            return URL_FOR_PAYMENTS;
        } else {
            throw new IllegalArgumentException("Unknown code: " + code);
        }
    }

    private String getGeoplanerURLPurchases() {
        Properties properties = RuntimeContext.getInstance().getConfigProperties();
        return properties.getProperty("ecafe.processor.geoplaner.sendevents.purchasesendpointaddress");
    }

    private String getGeoplanerURLEnterEvents() {
        Properties properties = RuntimeContext.getInstance().getConfigProperties();
        return properties.getProperty("ecafe.processor.geoplaner.sendevents.entereventsendpointaddress");
    }

    private String getGeoplanerURLPayments() {
        Properties properties = RuntimeContext.getInstance().getConfigProperties();
        return properties.getProperty("ecafe.processor.geoplaner.sendevents.paymentendpointaddress");
    }

    private boolean isDebug() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String reqInstance = runtimeContext
                .getConfigProperties().getProperty("ecafe.processor.geoplaner.restcontroller.debug", "false");
        return Boolean.parseBoolean(reqInstance);
    }
}
