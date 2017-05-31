/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.meal;

import generated.spb.meal.MealWebService;
import generated.spb.meal.PushMealPort;
import generated.spb.meal.PushResponse;

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
public class MealService {

    private static final Logger logger = LoggerFactory.getLogger(MealService.class);
    private PushMealPort pushMealService;

    private PushMealPort createEventController() {
        if(pushMealService != null) {
            return pushMealService;
        }
        PushMealPort controller;
        try {
            MealWebService service = new MealWebService();
            controller = service.getPushMealPort();
            Client proxy = ClientProxy.getClient(controller);
            BindingProvider bp = (BindingProvider) controller;
            bp.getRequestContext().put("schema-validation-enabled", "false");
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://svc.edu.n3demo.ru/service/webservice/meal");
            proxy.getOutInterceptors().add(new HeaderHandler());

            HTTPConduit conduit = (HTTPConduit) proxy.getConduit();
            HTTPClientPolicy policy = conduit.getClient();
            policy.setReceiveTimeout(30 * 60 * 1000);
            policy.setConnectionTimeout(30 * 60 * 1000);

            pushMealService = controller;
            return controller;
        } catch (java.lang.Exception e) {
            logger.error("Failed to create WS controller", e);
            return null;
        }
    }

    public PushResponse sendEvent(MealDataItem item) throws Exception {
        PushMealPort subscription = createEventController();
        if (subscription == null) {
            throw new Exception("Failed to create connection with meal web service");
        }

        PushResponse response = subscription.pushData(MealDataItem.getMealData(item));

        if(!response.isResult()) {
            logger.error(String.format(
                    "Не удалось доставить данные о покупке для клиента studentUid = %s, transactionId = %s",
                    item.getStudentUid(), item.getTransactionItems().get(0)));
        }
        return response;
    }

}
