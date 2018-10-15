/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.meal;

import generated.spb.meal.MealData;
import generated.spb.meal.MealWebService;
import generated.spb.meal.PushMealPort;
import generated.spb.meal.PushResponse;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 10.04.17
 * Time: 10:37
 */
@Component
@Scope("singleton")
public class MealService {

    private static final Logger logger = LoggerFactory.getLogger(MealService.class);
    private final List<String> ENDPOINT_ADDRESSES = getMealEndPointAddresses();

    private List<String> getMealEndPointAddresses() {
        Properties properties = RuntimeContext.getInstance().getConfigProperties();
        String endPointAddresses  = properties
                .getProperty("ecafe.processor.mealmanager.endpointaddress", "http://10.146.136.36/service/webservice/meal/");
        return Arrays.asList(endPointAddresses.split("\\s*;\\s*"));
    }

    private PushMealPort createEventController(String endpointAddress) {
        PushMealPort controller;
        try {
            MealWebService service = new MealWebService();
            controller = service.getPushMealPort();
            Client proxy = ClientProxy.getClient(controller);
            BindingProvider bp = (BindingProvider) controller;
            bp.getRequestContext().put("schema-validation-enabled", "false");
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
            //String endpoint = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.mealmanager.service", "http://10.146.136.36/service/webservice/meal/");
            //bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
            //proxy.getInInterceptors().add(new ContentTypeHandler());
            proxy.getOutInterceptors().add(new HeaderHandler());

            final MealServiceHandler soapLoggingHandler = new MealServiceHandler(endpointAddress);
            //final MealServiceHandler soapLoggingHandler = new MealServiceHandler();
            final List<Handler> handlerChain = new ArrayList<Handler>();
            handlerChain.add(soapLoggingHandler);
            bp.getBinding().setHandlerChain(handlerChain);

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

    public HashMap<String, PushResponse> sendEvent(MealDataItem item) throws Exception {
        MealData data = MealDataItem.getMealData(item);
        HashMap<String, PushResponse> results = new HashMap<String, PushResponse>();
        for(String endPointAddress : ENDPOINT_ADDRESSES) {
            try {
                PushMealPort subscription = createEventController(endPointAddress);
                if (subscription == null) {
                    throw new Exception(String.format("Failed to create connection with %s web service ", endPointAddress));
                }
                PushResponse response = subscription.pushData(data);
                results.put(endPointAddress, response);
            }catch (Exception e){
                logger.info("Can't send packet to URL: " + endPointAddress, e);
                results.put(endPointAddress, null);
            }
        }
        return results;
    }

}
