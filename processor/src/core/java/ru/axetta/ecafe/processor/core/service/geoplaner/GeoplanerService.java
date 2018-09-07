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
    private final String URL_FOR_TRANSACTIONS = getGeoplanerURLTransaction();
    private final String TEST_ENDPOINT_ADDRESS = "https://testrestcontroller.herokuapp.com/test"; // Тестовый сервер на heroku

    private boolean debug = isDebug();

    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RESET = "\u001B[0m";

    public Integer sendPost(Object event, Boolean isEnterEvents) throws Exception{
        HttpClient httpClient = new HttpClient();
        PostMethod method = new PostMethod(isEnterEvents ? URL_FOR_ENTER_EVENTS : URL_FOR_TRANSACTIONS);
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

    private String getGeoplanerURLTransaction() {
        Properties properties = RuntimeContext.getInstance().getConfigProperties();
        return properties.getProperty("ecafe.processor.geoplaner.sendevents.entereventsendpointaddress", TEST_ENDPOINT_ADDRESS);
    }

    private String getGeoplanerURLEnterEvents() {
        Properties properties = RuntimeContext.getInstance().getConfigProperties();
        return properties.getProperty("ecafe.processor.geoplaner.sendevents.paymentendpointaddress", TEST_ENDPOINT_ADDRESS);
    }

    private boolean isDebug() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String reqInstance = runtimeContext
                .getConfigProperties().getProperty("ecafe.processor.geoplaner.restcontroller.debug", "false");
        return Boolean.parseBoolean(reqInstance);
    }
}
