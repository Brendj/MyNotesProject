/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.scud;

import generated.spb.SCUD.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.nsi.SOAPLoggingHandler;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
@Scope("singleton")
public class ScudService {
    private static final Logger logger = LoggerFactory.getLogger(ScudService.class);
    private PushScudPort pushScudService;
    private ObjectFactory scudObjectFactory = new ObjectFactory();
    private final String ENDPOINT_ADDRESS = getEndPointAdressFromConfig();
    private final String TEST_ENDPOINT_ADDRESS = "http://petersburgedu.ru/service/webservice/scud";

    private String getEndPointAdressFromConfig() {
        Properties properties = RuntimeContext.getInstance().getConfigProperties();
        return properties.getProperty("ecafe.processor.scudmanager.endpointadress", "http://10.146.136.36/service/webservice/scud");
    }

    private PushScudPort createEventController() {
        if(pushScudService != null) {
            return pushScudService;
        }
        PushScudPort controller;
        try {
            ScudWebService service = new ScudWebService();
            controller = service.getPushScudPort();
            Client proxy = ClientProxy.getClient(controller);
            BindingProvider bp = (BindingProvider) controller;
            bp.getRequestContext().put("schema-validation-enabled", "false");
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, this.ENDPOINT_ADDRESS);
            proxy.getOutInterceptors().add(new HeaderHandler());

            final SOAPLoggingHandler soapLoggingHandler = new SOAPLoggingHandler();
            final ScudServiceHandler scudServiceHandler = new ScudServiceHandler();
            final List<Handler> handlerChain = new ArrayList<Handler>();
            handlerChain.add(soapLoggingHandler);
            if(this.ENDPOINT_ADDRESS.contains(this.TEST_ENDPOINT_ADDRESS)) {
                handlerChain.add(scudServiceHandler);
            }
            bp.getBinding().setHandlerChain(handlerChain);

            HTTPConduit conduit = (HTTPConduit) proxy.getConduit();
            HTTPClientPolicy policy = conduit.getClient();
            policy.setReceiveTimeout(30 * 60 * 1000);
            policy.setConnectionTimeout(30 * 60 * 1000);

            pushScudService = controller;
            return controller;
        } catch (java.lang.Exception e) {
            logger.error("Failed to create WS SCUD controller", e);
            return null;
        }
    }

    public PushResponse sendEvent(List<EventDataItem> items) throws Exception {
        PushScudPort subscription = createEventController();
        if (subscription == null) {
            throw new Exception("Failed to create connection with SCUD web service");
        }
        EventList eventList = this.scudObjectFactory.createEventList(items);
        return subscription.pushData(eventList);
    }
}
