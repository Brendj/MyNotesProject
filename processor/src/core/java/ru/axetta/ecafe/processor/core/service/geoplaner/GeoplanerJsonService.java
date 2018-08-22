/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.geoplaner;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Component
@Scope("singleton")
public class GeoplanerJsonService {
    private HttpClient httpClient = new HttpClient();
    private final String URL_FOR_ENTER_EVENTS = getGeoplanerURLEnterEvents();
    private final String URL_FOR_TRANSACTIONS = getGeoplanerURLTransaction();
    private final String TEST_ENDPOINT_ADDRESS = "https://testrestcontroller.herokuapp.com/test"; // Тестовый сервер на heroku

    public Integer sendPost(List eventsList, Boolean isEnterEvents) throws Exception{
        PostMethod method = new PostMethod(isEnterEvents ? URL_FOR_ENTER_EVENTS : URL_FOR_TRANSACTIONS);
        method.addRequestHeader("Content-Type", "application/json");

        ObjectMapper mapper = new ObjectMapper();
        String JSONString = mapper.writeValueAsString(eventsList);

        StringRequestEntity requestEntity = new StringRequestEntity(
                JSONString,
                "application/json",
                "UTF-8");
        method.setRequestEntity(requestEntity);

        return this.httpClient.executeMethod(method);
    }

    private String getGeoplanerURLTransaction() {
        Properties properties = RuntimeContext.getInstance().getConfigProperties();
        return properties.getProperty("ecafe.processor.geoplaner.sendevents.enterEventsEndPointAddress", TEST_ENDPOINT_ADDRESS);
    }

    private String getGeoplanerURLEnterEvents() {
        Properties properties = RuntimeContext.getInstance().getConfigProperties();
        return properties.getProperty("ecafe.processor.geoplaner.sendevents.transactionEndPointAddress", TEST_ENDPOINT_ADDRESS);
    }
}
