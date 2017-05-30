/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.spb;

import generated.spb.register.PersonWebService;
import generated.spb.register.Query;
import generated.spb.register.QueryPersonPort;
import generated.spb.register.Schools;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.ws.BindingProvider;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 10.04.17
 * Time: 10:37
 */
@Component
@Scope("singleton")
public class ClientSpbService {

    private static final Logger logger = LoggerFactory.getLogger(ClientSpbService.class);

    private QueryPersonPort createEventController(String url) {
        QueryPersonPort controller;
        try {
            PersonWebService service = new PersonWebService(url);
            controller = service.getQueryPersonPort(url);
            BindingProvider bp = (BindingProvider)controller;
            bp.getRequestContext().put("schema-validation-enabled", "false");
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://85.143.161.170:8080/webservice/food_benefits_full/execute");

            Client proxy = ClientProxy.getClient(controller);
            proxy.getOutInterceptors().add(new HeaderHandler());

            HTTPConduit conduit = (HTTPConduit) proxy.getConduit();
            HTTPClientPolicy policy = conduit.getClient();
            policy.setReceiveTimeout(30 * 60 * 1000);
            policy.setConnectionTimeout(30 * 60 * 1000);

            return controller;
        } catch (java.lang.Exception e) {
            logger.error("Failed to create WS controller", e);
            return null;
        }
    }


    public Schools sendEvent(Query query, String url) throws Exception {
        QueryPersonPort subscription = createEventController(url);
        if (subscription == null) {
            throw new Exception("Failed to create connection with spb person service");
        }
        return subscription.pushData(query);

    }

}
