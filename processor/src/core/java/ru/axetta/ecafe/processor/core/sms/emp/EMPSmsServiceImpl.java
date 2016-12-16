/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp;

import generated.emp_events.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sms.DeliveryResponse;
import ru.axetta.ecafe.processor.core.sms.ISmsService;
import ru.axetta.ecafe.processor.core.sms.SendResponse;
import ru.axetta.ecafe.processor.core.sms.emp.type.EMPEventType;
import ru.axetta.ecafe.processor.core.sms.emp.type.EMPInfoMailingEventType;
import ru.axetta.ecafe.processor.core.utils.ExternalSystemStats;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.net.URL;
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
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EMPProcessor.class);
    //  services instances
    protected SubscriptionPortType subscriptionService;

    private static CircularFifoBuffer buffer = new CircularFifoBuffer(100);

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

    public SendResponse sendTextMessage(String sender, Client client, Object textObject) throws Exception {
        if (!(textObject instanceof EMPEventType)) {
            throw new Exception("Text argument must be an EMPEventType instead of " + textObject.getClass().toString());
        }

        EMPEventType empEvent = (EMPEventType) textObject;
        try {
            String messageId = RuntimeContext.getAppContext().getBean(EMPSmsServiceImpl.class).sendEvent(client, empEvent);
            if(messageId == null || StringUtils.isBlank(messageId)) {
                throw new Exception(String.format("Failed tot send EMP event for client [%s] - EMP system failed to send", client.getIdOfClient()));
            }
            return new SendResponse(1, null, messageId);// messageId ???
        } catch(EMPException empe) {
            if(empe.getMessageId() != null && !StringUtils.isBlank(empe.getMessageId())) {
                return new SendResponse(empe.getCode() < SendResponse.MIN_SUCCESS_STATUS ? empe.getCode() : -empe.getCode(),
                                        String.format("E: [%s] %s", "" + empe.getCode(), empe.getError()), empe.getMessageId());
            }
        } catch (Exception e) {
            throw e;
        }
        throw new Exception("Nor error neither success while sending EMP event");
    }

    @Override
    public SendResponse sendTextMessage(String sender, String phoneNumber, Object textObject) throws Exception {
        throw new UnsupportedOperationException();
    }


    /* PROCESSOR */
    @Override
    public DeliveryResponse getDeliveryStatus(String messageId) throws Exception {
        return new DeliveryResponse(DeliveryResponse.DELIVERED, null, null);
    }

    /*public void updateIncome(int incomeIncrease, int outcomeIncrease, int failedIncrease) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();

        Integer in = 0;
        Integer out = 0;
        Integer fail = 0;

        String optionValue = runtimeContext.getOptionValueString(Option.OPTION_EMP_COUNTER);//"1/100/200/300;2/400/500/600";
        if(!StringUtils.isBlank(optionValue)) {
            String nodes [] = optionValue.split(";");
            for(String n : nodes) {
                String [] oValues = n.split("/");
                if(oValues.length < 3) {
                    continue;
                }
                String oInstance  = oValues[0];
                String oIncome    = oValues[1];
                String oOutcome   = oValues[2];
                String oFailed    = oValues[3];

                if(oInstance.equals(instance)) {
                    optionValue = optionValue.replaceAll(";" + n, "");
                    optionValue = optionValue.replaceAll(n, "");
                    in = NumberUtils.toInt(oIncome);
                    out = NumberUtils.toInt(oOutcome);
                    fail = NumberUtils.toInt(oFailed);
                    break;
                }
            }
        }

        in += incomeIncrease;
        out += outcomeIncrease;
        fail += failedIncrease;
        if(optionValue.length() > 0) {
            optionValue = optionValue + ";";
        }
        optionValue = optionValue + String.format("%s/%s/%s/%s", instance, in, out, fail);
        runtimeContext.setOptionValueWithSave(Option.OPTION_EMP_COUNTER, optionValue);
    }*/

    protected ExternalSystemStats stats;
    public static final int INCOME_STATS_ID = 1;
    public static final int OUTCOME_STATS_ID = 2;
    public static final int FAILED_STATS_ID = 3;


    public void updateStats(int type, int inc) {
        String instance = RuntimeContext.getInstance().getNodeName();
        if(stats == null) {
            stats = DAOService.getInstance().getAllPreviousStatsForExternalSystem("emp_event", instance);
        }

        stats.setValue(type, stats.getValue(type) + inc);

        if(System.currentTimeMillis() >= stats.getCreateDate().getTime() + empProcessor.getConfigStatsLifetime()) {
            stats.setCreateDate(new Date(System.currentTimeMillis()));
            stats = DAOService.getInstance().saveStatsForExtermalSystem(stats);
        }
    }

    private boolean ignoreMobileTest(EMPEventType event) {
        return event instanceof EMPInfoMailingEventType;
    }

    public String sendEvent(ru.axetta.ecafe.processor.core.persistence.Client client, EMPEventType event)
            throws EMPException {
        /*if(1 == 1) {
            updateStats(INCOME_STATS_ID, 10000);       //  TEST ONLY!!!!!!
            return null;                //  TEST ONLY!!!!!!
        }*/
        if(!ignoreMobileTest(event) && (event.getMsisdn() == null || StringUtils.isBlank("" + event.getMsisdn()))) {
            throw new EMPException(String.format("Failed to send EMP event for client [%s] - msisdn (mobile) is required", client.getIdOfClient()));
        }
        /*if (StringUtils.isBlank(client.getSsoid())/* || NumberUtils.toLong(client.getSsoid()) < 0L/) {
            //return null;
            throw new EMPException(String.format("Failed to send EMP event for client [%s] - ssoid is required", client.getIdOfClient()));
        }*/


        updateStats(INCOME_STATS_ID, 1);

        //  Вспомогательные значения
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        String synchDate = "[Отправка события " + date + "]: ";
        empProcessor.log(synchDate + "Событие " + event.getType() + " для клиента [" +
                client.getIdOfClient() + "] " + client.getMobile());

        //  Отправка запроса
        SubscriptionPortType subscription = createEventController();
        if (subscription == null) {
            throw new EMPException("Failed to create connection with EMP web service");
        }
        SendSubscriptionStreamEventsRequestType eventParam = buildEventParam(event);
        empProcessor.logRequest(eventParam);
        Date timeBefore = new Date();
        //Здесь можно включить логирование
        //final SOAPLoggingHandler soapLoggingHandler = new SOAPLoggingHandler();
        //final List<Handler> handlerChain = new ArrayList<Handler>();
        //handlerChain.add(soapLoggingHandler);
        //((BindingProvider) subscription).getBinding().setHandlerChain(handlerChain);
        SendSubscriptionStreamEventsResponseType response = subscription.sendSubscriptionStreamEvents(eventParam);
        Date timeAfter = new Date();
        addResponseTime(timeAfter.getTime() - timeBefore.getTime());
        if (response.getErrorCode() != 0) {
            empProcessor.log(synchDate + "Не удалось доставить событие " + event.getType() + " для клиента [" + client
                    .getIdOfClient() + "] " + client.getMobile());
            updateStats(FAILED_STATS_ID, 1);
            throw new EMPException(response.getErrorCode(),
                    String.format("Failed to execute event notification: Error [%s] %s", response.getErrorCode(),
                            response.getErrorMessage())).setMessageId(eventParam.getId());
        }
        empProcessor.log(synchDate + "Событие " + event.getType() + " для клиента [" + client.getIdOfClient() + "] " + client
                .getMobile() + " доставлено");
        updateStats(OUTCOME_STATS_ID, 1);
        return eventParam.getId();
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
            gcal.setTimeInMillis(eventType.getTime());
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
            //personFilter.setSSOID(eventType.getSsoid());
            personFilter.setMSISDN(eventType.getMsisdn());
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

    private synchronized void addResponseTime(long responseTime) {
        buffer.add(responseTime);
    }

    @Override
    public Boolean ignoreNotifyFlags() {
        return true;
    }

    @Override
    public Boolean emailDisabled() {
        return true;
    }

    public static synchronized CircularFifoBuffer getBuffer() {
        return buffer;
    }
}