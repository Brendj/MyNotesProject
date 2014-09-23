/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp;

import generated.emp_events.*;
import generated.emp_storage.*;
import ru.CryptoPro.JCP.tools.Array;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sms.DeliveryResponse;
import ru.axetta.ecafe.processor.core.sms.ISmsService;
import ru.axetta.ecafe.processor.core.sms.SendResponse;
import ru.axetta.ecafe.processor.core.sms.emp.type.EMPEventType;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.io.FileWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 08.09.14
 * Time: 22:08
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class EMPSmsServiceImpl extends ISmsService {
    @Resource
    EMPProcessor empProcessor;
    //  system
    public static final String ENCODING = "UTF-8";
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EMPSmsServiceImpl.class);
    //  services instances
    protected SubscriptionPortType subscriptionService;

    /*  BASE AND IMPL */
    public EMPSmsServiceImpl() {
    }

    public EMPSmsServiceImpl(Config config) {
        super(config);
    }

    public Config getConfig() {
        return config;
    }

    public static ISmsService getInstance(Config config) {
        EMPSmsServiceImpl impl = RuntimeContext.getAppContext().getBean(EMPSmsServiceImpl.class);
        impl.config = config;
        return impl;
    }

    @Override
    public SendResponse sendTextMessage(String sender, String phoneNumber, Object textObject) throws Exception {
        if (!(textObject instanceof EMPEventType)) {
            throw new Exception("Text argument must be an EMPEventType instead of " + textObject.getClass().toString());
        }

        EMPEventType empEvent = (EMPEventType) textObject;
        List<Client> client = DAOService.getInstance().findClientsByMobilePhone(phoneNumber);
        for (Client c : client) {
            RuntimeContext.getAppContext().getBean(EMPSmsServiceImpl.class).sendEvent(c, empEvent);
        }
        return new SendResponse(0, null, "");// messageId ???
    }


    /* PROCESSOR */
    @Override
    public DeliveryResponse getDeliveryStatus(String messageId) throws Exception {
        return new DeliveryResponse(DeliveryResponse.DELIVERED, null, null);
    }


    public static void log(String str) {
        if (RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_MSK_NSI_LOG)) {
            logger.info(str);
        }
    }

    public boolean sendEvent(ru.axetta.ecafe.processor.core.persistence.Client client, EMPEventType event)
            throws EMPException {
        if (StringUtils.isBlank(client.getSsoid()) || NumberUtils.toLong(client.getSsoid()) < 0L) {
            return false;
        }

        //  Вспомогательные значения
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        String synchDate = "[Отправка события " + date + "]: ";
        log(synchDate + "Событие " + event.getType() + " для клиента [" + client.getIdOfClient() + "] " + client
                .getMobile());

        //  Отправка запроса
        SubscriptionPortType subscription = createEventController();
        if (subscription == null) {
            throw new EMPException("Failed to create connection with EMP web service");
        }
        SendSubscriptionStreamEventsRequestType eventParam = buildEventParam(event);
        logRequest(eventParam);
        SendSubscriptionStreamEventsResponseType response = subscription.sendSubscriptionStreamEvents(eventParam);
        if (response.getErrorCode() != 0) {
            log(synchDate + "Не удалось доставить событие " + event.getType() + " для клиента [" + client
                    .getIdOfClient() + "] " + client.getMobile());
            throw new EMPException(
                    String.format("Failed to execute event notification: Error [%s] %s", response.getErrorCode(),
                            response.getErrorMessage()));
        }
        log(synchDate + "Событие " + event.getType() + " для клиента [" + client.getIdOfClient() + "] " + client
                .getMobile() + " доставлено");
        return true;
    }

    protected SubscriptionPortType createEventController() {
        if (subscriptionService != null) {
            return subscriptionService;
        }
        SubscriptionPortType controller = null;
        try {
            SubscriptionService service = new SubscriptionService(new URL(config.getServiceUrl()),
                    new QName("urn://emp.altarix.ru/subscriptions", "SubscriptionService"));
            controller = service.getServicePort();

            org.apache.cxf.endpoint.Client client = ClientProxy.getClient(controller);
            HTTPConduit conduit = (HTTPConduit) client.getConduit();
            HTTPClientPolicy policy = conduit.getClient();
            policy.setReceiveTimeout(30 * 60 * 1000);
            policy.setConnectionTimeout(30 * 60 * 1000);
            subscriptionService = controller;
            return controller;
        } catch (java.lang.Exception e) {
            logger.error("Failed to create WS controller", e);
            return null;
        }
    }

    protected SendSubscriptionStreamEventsRequestType buildEventParam(EMPEventType eventType) {
        SendSubscriptionStreamEventsRequestType sending = new SendSubscriptionStreamEventsRequestType();
        try {
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
            String uuid = UUID.randomUUID().toString();

            //  sending spec
            sending.setToken(empProcessor.getConfigToken());
            sending.setDatetime(xgcal);
            sending.setSystemId(empProcessor.getConfigSystemId());
            sending.setId(uuid);

            EventType event = new EventType();
            //  event spec
            event.setDatetime(xgcal);
            //event.setDescription(URLEncoder.encode(new String("Информирование клиента ИС ПП".getBytes(), ENCODING), ENCODING));
            event.setDescription(new String("Информирование клиента ИС ПП".getBytes(ENCODING), ENCODING));
            event.setId(uuid);
            event.setStreamId(eventType.getStream());
            event.setTypeId(eventType.getType());
            //  event message params
            EventMessageType messageParams = new EventMessageType();
            EventMessageType.Parameters paramsObj = messageParams.getParameters();
            if (paramsObj == null) {
                paramsObj = new EventMessageType.Parameters();
                messageParams.setParameters(paramsObj);
            }
            List<EventMessageParameterType> params = paramsObj.getParameter();
            for (String k : eventType.getParameters().keySet()) {
                String v = eventType.getParameters().get(k);

                EventMessageParameterType nameParam = new EventMessageParameterType();
                nameParam.setName(k);
                //nameParam.setValue(URLEncoder.encode(new String(v.getBytes(), ENCODING), ENCODING));
                nameParam.setValue(new String(v.getBytes(ENCODING), ENCODING));
                params.add(nameParam);
            }
            event.setMessage(messageParams);
            //  filters
            EventType.Filters filtersObj = event.getFilters();
            if (filtersObj == null) {
                filtersObj = new EventType.Filters();
                event.setFilters(filtersObj);
            }
            List<EventFilterType> filters = filtersObj.getFilter();
            EventFilterType f1 = new EventFilterType();
            EventFilterType.Persons.Person personFilter = new EventFilterType.Persons.Person();
            personFilter.setSSOID(eventType.getSsoid());
            EventFilterType.Persons personsObj = f1.getPersons();
            if (personsObj == null) {
                personsObj = new EventFilterType.Persons();
                f1.setPersons(personsObj);
            }
            personsObj.getPerson().add(personFilter);
            filters.add(f1);

            //  bind event
            SendSubscriptionStreamEventsRequestType.Events eventsObj = sending.getEvents();
            if (eventsObj == null) {
                eventsObj = new SendSubscriptionStreamEventsRequestType.Events();
                sending.setEvents(eventsObj);
            }
            eventsObj.getEvent().add(event);
        } catch (Exception e) {
            logger.error("Failed to build request", e);
        }
        return sending;
    }

    protected void logRequest(SendSubscriptionStreamEventsRequestType request) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance("generated.emp_events");
            logRequest(jaxbContext, request);
        } catch (Exception e) {

        }
    }

    protected void logRequest(JAXBContext jaxbContext, Object obj) {
        if (!empProcessor.getConfigLogging()) {
            return;
        }

        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            FileWriter fw = new FileWriter("C:/out.signed.xml");
            marshaller.marshal(obj, fw);
        } catch (Exception e) {
            logger.error("Failed to log", e);
        }
    }

}