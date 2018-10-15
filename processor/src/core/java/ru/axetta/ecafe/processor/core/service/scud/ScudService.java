/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.scud;

import generated.spb.SCUD.*;

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

@Component
@Scope("singleton")
public class ScudService {
    private static final Logger logger = LoggerFactory.getLogger(ScudService.class);
    private ObjectFactory scudObjectFactory = new ObjectFactory();
    private final List<String> ENDPOINT_ADDRESSES = getEndPointAdressFromConfig();
    private final String TEST_ENDPOINT_ADDRESS = "http://petersburgedu.ru/service/webservice/scud";

    private List<String> getEndPointAdressFromConfig() {
        Properties properties = RuntimeContext.getInstance().getConfigProperties();
        String endPointAddresses  = properties
                .getProperty("ecafe.processor.scudmanager.endpointadress", "http://10.146.136.36/service/webservice/scud");
        return Arrays.asList(endPointAddresses.split("\\s*;\\s*"));
    }

    private PushScudPort createEventController(String endPointAddress) {
        PushScudPort controller;
        try {
            ScudWebService service = new ScudWebService();
            controller = service.getPushScudPort();
            Client proxy = ClientProxy.getClient(controller);
            BindingProvider bp = (BindingProvider) controller;
            bp.getRequestContext().put("schema-validation-enabled", "false");
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endPointAddress);
            proxy.getOutInterceptors().add(new HeaderHandler());

            //final SOAPLoggingHandler soapLoggingHandler = new SOAPLoggingHandler();
            final ScudServiceHandler scudServiceHandler = new ScudServiceHandler(endPointAddress);
            final List<Handler> handlerChain = new ArrayList<Handler>();
            //handlerChain.add(soapLoggingHandler);
            //if(this.ENDPOINT_ADDRESS.contains(this.TEST_ENDPOINT_ADDRESS)) {
                handlerChain.add(scudServiceHandler);
            //}
            bp.getBinding().setHandlerChain(handlerChain);

            HTTPConduit conduit = (HTTPConduit) proxy.getConduit();
            HTTPClientPolicy policy = conduit.getClient();
            policy.setReceiveTimeout(30 * 60 * 1000);
            policy.setConnectionTimeout(30 * 60 * 1000);

            return controller;
        } catch (java.lang.Exception e) {
            logger.error("Failed to create WS SCUD controller", e);
            return null;
        }
    }

    public HashMap<String, PushResponse> sendEvent(List<EventDataItem> items) throws Exception {
        HashMap<String, PushResponse> results = new HashMap<String, PushResponse>();
        EventList eventList = this.scudObjectFactory.createEventList(items);
        for(String endPointAddress : ENDPOINT_ADDRESSES) {
            try {
                PushScudPort subscription = createEventController(endPointAddress);
                if (subscription == null) {
                    throw new Exception(String.format("Failed to create connection with address %s a web service", endPointAddress));
                }
                PushResponse response = subscription.pushData(eventList);
                results.put(endPointAddress, response);
            } catch (Exception e){
                logger.error("Can't send packet to URL: " + endPointAddress, e);
                results.put(endPointAddress, null);
            }
        }
        return results;
    }
}
