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
    public static final String ATTRIBUTE_ACCOUNT_NAME      = "ACCOUNT";
    public static final String ATTRIBUTE_MOBILE_PHONE_NAME = "MSISDN";
    public static final String ATTRIBUTE_RULE_ID           = "RULE_ID";
    public static final String ATTRIBUTE_SUBSCRIPTION_ID   = "SUBSCRIPTION_ID";
    public static final String ATTRIBUTE_SSOID_NAME        = "SSOID";
    public static final String ATTRIBUTE_EMAIL_NAME        = "EMAIL";
    public static final String ATTRIBUTE_ACTIVE            = "ACTIVE";
    public static final String ATTRIBUTE_SMS_SEND          = "SMS_SEND";
    public static final String ATTRIBUTE_EMAIL_SEND        = "EMAIL_SEND";
    public static final String ATTRIBUTE_PUSH_SEND         = "PUSH_SEND";
    //  system
    public static final String ENCODING = "UTF-8";
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EMPSmsServiceImpl.class);
    //  errors
    public static final int EMP_ERROR_CODE_NOTHING_FOUND = 504;
    //  services instances
    protected SubscriptionPortType subscriptionService;
    protected StoragePortType storageService;



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
        if(!(textObject instanceof EMPEventType)) {
            throw new Exception("Text argument must be an EMPEventType");
        }

        EMPEventType empEvent = (EMPEventType) textObject;
        List<Client> client = DAOService.getInstance().findClientsByMobilePhone(phoneNumber);
        for(Client c : client) {
            RuntimeContext.getAppContext().getBean(EMPSmsServiceImpl.class).sendEvent(c, empEvent);
        }
        return new SendResponse(0, null, "");// messageId ???
    }



    /* PROCESSOR */
    @Override
    public DeliveryResponse getDeliveryStatus(String messageId) throws Exception {
        return new DeliveryResponse(DeliveryResponse.DELIVERED, null, null);
    }


    protected int getMaximumClientsPerPackage() {
        return config.getPackageSize();
    }

    public boolean isAllowed() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        //String reqInstance = runtimeContext.getOptionValueString(Option.OPTION_EMP_PROCESSOR_INSTANCE);
        String reqInstance = config.getSyncServiceNode();
        if(StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim().equals(reqInstance.trim())) {
            return false;
        }
        return true;
    }

    public void recalculateEMPClientsCount() {
        EMPStatistics statistics = loadEMPStatistics();
        long notBinded = DAOService.getInstance().getNotBindedEMPClientsCount();
        long waitBind = DAOService.getInstance().getBindWaitingEMPClients();
        long binded = DAOService.getInstance().getBindedEMPClientsCount();
        statistics.setNotBindedCount(notBinded);
        statistics.setWaitBindingCount(waitBind);
        statistics.setBindedCount(binded);
        saveEMPStatistics(statistics);
    }

    public void runStorageMerge() throws EMPException {
        RuntimeContext.getAppContext().getBean(EMPSmsServiceImpl.class).runBindClients();
        RuntimeContext.getAppContext().getBean(EMPSmsServiceImpl.class).runReceiveUpdates();
    }

    public void runBindClients() throws EMPException {
        if(!isAllowed()) {
            return;
        }

        //  Загружаем статистику
        EMPStatistics statistics = loadEMPStatistics();
        //  Вспомогательные значения
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        String synchDate = "[Привязка клиентов ИСПП к ЕМП " + date + "]: ";

        //  Загрузка клиентов для связки
        List<ru.axetta.ecafe.processor.core.persistence.Client> notBindedClients = DAOService.getInstance().getNotBindedEMPClients(
                getMaximumClientsPerPackage());
        log(synchDate + "Количество клиентов к привязке: " + notBindedClients.size(), null);

        //  Отправка запроса на привязку
        StoragePortType storage = createStorageController();
        if(storage == null) {
            throw new EMPException("Failed to create connection with EMP web service");
        }
        for(ru.axetta.ecafe.processor.core.persistence.Client c : notBindedClients) {
            try {
                bindClient(storage, c, synchDate, statistics);
            } catch (EMPException empe) {
                logger.error(String.format("Failed to parse client: [code=%s] %s", empe.getCode(), empe.getError()), empe);
            }
        }
        //  Обновляем изменившуюся статистику
        saveEMPStatistics(statistics);
    }

    public void runReceiveUpdates() throws EMPException {
        if(!isAllowed()) {
            return;
        }

        //  Загружаем статистику
        EMPStatistics statistics = loadEMPStatistics();
        //  Вспомогательные значения
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        String synchDate = "[Получение изменений из ЕМП " + date + "]: ";

        //  Загрузка клиентов для связки
        long changeSequence = RuntimeContext.getInstance().getOptionValueLong(Option.OPTION_EMP_CHANGE_SEQUENCE);//750
        StoragePortType storage = createStorageController();
        if(storage == null) {
            throw new EMPException("Failed to create connection with EMP web service");
        }
        ReceiveDataChangesRequest request = buildReceiveEntryParams(changeSequence);
        logRequest(request);
        ReceiveDataChangesResponse response = storage.receiveDataChanges(request);
        if(response.getErrorCode() != 0) {
            logger.error(String.format("Failed to receive updates: [code=%s] %s", response.getErrorCode(), response.getErrorMessage()));
            return;
        }

        List<ReceiveDataChangesResponse.Result.Entry> entries = response.getResult().getEntry();
        if(entries.size() < 1) {
            log(synchDate + "Новых изменений по очереди " + changeSequence + " в ЕМП нет", null);
            return;
        }

        log(synchDate + "Поступило " + entries.size() + " изменений из ЕМП по очереди " + changeSequence, null);
        for(ReceiveDataChangesResponse.Result.Entry e : entries) {
            List<ReceiveDataChangesResponse.Result.Entry.Attribute> attributes = e.getAttribute();
            List<ReceiveDataChangesResponse.Result.Entry.Identifier> identifiers = e.getIdentifier();
            String ssoid = "";
            String ruleId = "";
            StringBuilder logStr = new StringBuilder();
            for(ReceiveDataChangesResponse.Result.Entry.Attribute attr : attributes) {
                if(!StringUtils.isBlank(attr.getName()) &&
                        attr.getName().equals(ATTRIBUTE_SSOID_NAME) &&
                        attr.getValue() != null && attr.getValue().size() > 0 && attr.getValue().get(0) != null) {
                    try {
                        ssoid = ((Element) attr.getValue().get(0)).getFirstChild().getTextContent();
                    } catch (Exception e1) {
                        logger.error("Failed to parse " + ATTRIBUTE_SSOID_NAME + " value", e1);
                    }
                }

                //  logging
                addEntryToLogString(attr, logStr);
            }
            for(ReceiveDataChangesResponse.Result.Entry.Identifier id : identifiers) {
                if(!StringUtils.isBlank(id.getName()) &&
                        id.getName().equals(ATTRIBUTE_RULE_ID) &&
                        id.getValue() != null && id.getValue() != null && !StringUtils.isBlank(id.getValue().toString())) {
                    try {
                        ruleId = ((Element) id.getValue()).getFirstChild().getTextContent();
                        break;
                    } catch (Exception e1) {
                        logger.error("Failed to parse " + ATTRIBUTE_RULE_ID + " value", e1);
                    }
                }
                addEntryToLogString(id, logStr);
            }
            if(!StringUtils.isBlank(ruleId) && !StringUtils.isBlank(ssoid) && NumberUtils.isNumber(ruleId)) {
                ru.axetta.ecafe.processor.core.persistence.Client client = DAOService.getInstance().getClientByContractId(NumberUtils.toLong(ruleId));
                if(client != null) {
                    //  Обновляем статистику
                    statistics.addBinded();
                    if(!StringUtils.isBlank(client.getSsoid()) && client.getSsoid().equals("-1")) {
                        statistics.removeWaitBinding();
                    } else {
                        statistics.removeNotBinded();
                    }
                    log(synchDate + "Поступили изменения из ЕМП {SSOID: " + ssoid + "}, {№ Контракта: " + ruleId + "} для клиента [" + client.getIdOfClient() + "] " + client.getMobile(), null);
                    client.setSsoid(ssoid);
                    DAOService.getInstance().saveEntity(client);
                }
            } else {
                log(synchDate + "Полученное изменение из ЕМП не удалось связать: " + logStr.toString(), null);
            }
            changeSequence = e.getChangeSequence();
        }

        log(synchDate + "Обновление очереди до " + changeSequence, null);
        RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_EMP_CHANGE_SEQUENCE, changeSequence + 1);
        if(response.getResult().isHasMoreEntries()) {
            log(synchDate + "Изменения в ЕМП обработаны не до конца, запрос будет выполнен повторно", null);
            RuntimeContext.getAppContext().getBean(EMPSmsServiceImpl.class).runReceiveUpdates();
        }
        //  Обновляем изменившуюся статистику
        saveEMPStatistics(statistics);
    }

    public boolean sendEvent(ru.axetta.ecafe.processor.core.persistence.Client client, EMPEventType event) throws EMPException {
        if(!isAllowed()) {
            return false;
        }
        if(StringUtils.isBlank(client.getSsoid()) || NumberUtils.toLong(client.getSsoid()) < 0L) {
            return false;
        }

        //  Вспомогательные значения
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        String synchDate = "[Отправка события " + date + "]: ";
        log(synchDate + "Событие " + event.getType() + " для клиента [" + client.getIdOfClient() + "] " + client.getMobile(), null);

        //  Отправка запроса
        SubscriptionPortType subscription = createEventController();
        if(subscription == null) {
            throw new EMPException("Failed to create connection with EMP web service");
        }
        SendSubscriptionStreamEventsRequestType eventParam = buildEvenParam(event);
        logRequest(eventParam);
        SendSubscriptionStreamEventsResponseType response = subscription.sendSubscriptionStreamEvents(eventParam);
        if(response.getErrorCode() != 0) {
            log(synchDate + "Не удалось доставить событие " + event.getType() + " для клиента [" + client.getIdOfClient() + "] " + client.getMobile(), null);
            throw new EMPException(String.format("Failed to execute event notification: Error [%s] %s",
                    response.getErrorCode(), response.getErrorMessage()));
        }
        log(synchDate + "Событие " + event.getType() + " для клиента [" + client.getIdOfClient() + "] " + client
                .getMobile() + " доставлено", null);
        return true;
    }

    protected void bindClient(StoragePortType storage, ru.axetta.ecafe.processor.core.persistence.Client client, String synchDate, EMPStatistics statistics) throws EMPException {
        if(bindThrowSelect(storage, client, synchDate)) {
            statistics.addBinded();
            statistics.removeNotBinded();
        } else if(bindThrowAdd(storage, client, synchDate)) {
            statistics.addWaitBinding();
            statistics.removeNotBinded();
        }
    }

    protected boolean bindThrowSelect(StoragePortType storage, ru.axetta.ecafe.processor.core.persistence.Client client, String synchDate) throws EMPException {
        log(synchDate + "Попытка связать клиента [" + client.getIdOfClient() + "] " + client.getMobile() + " с использованием поиска по телефону", null);
        //  execute reqeuest
        SelectEntriesRequest request = buildSelectEntryParams(client.getMobile());
        SelectEntriesResponse response = storage.selectEntries(request);
        log(synchDate + "Получен ответ: "+response.getErrorCode()+": "+response.getErrorMessage()+", записей: "+response.getResult().getEntry().size(), null);
        if(response.getErrorCode() == EMP_ERROR_CODE_NOTHING_FOUND) {
            return false;
        }
        if(response.getErrorCode() != 0) {
            throw new EMPException(response.getErrorCode(), response.getErrorMessage());
        }

        //  parse response entries
        List<Entry> entries = response.getResult().getEntry();
        for(Entry e : entries) {
            List<EntryAttribute> attributes = e.getAttribute();
            boolean requiresUpdate = false;
            for(EntryAttribute attr : attributes) {
                if(attr.getName().equals(ATTRIBUTE_SSOID_NAME) &&
                        attr.getValue() != null && attr.getValue().size() > 0 &&
                        attr.getValue().get(0) != null && ((Element) attr.getValue().get(0)).getFirstChild() != null
                        && !attr.getValue().get(0).equals(client.getSsoid())
                        ) {
                    try {
                        String val = ((Element) attr.getValue().get(0)).getFirstChild().getTextContent();
                        client.setSsoid(val);
                        requiresUpdate = true;
                    } catch (Exception e1) {
                        logger.error("Failed to process existing object", e1);
                        throw new EMPException(e1);
                    }
                }
                if(attr.getName().equals(ATTRIBUTE_EMAIL_NAME) &&
                        attr.getValue() != null && attr.getValue().size() > 0 &&
                        attr.getValue().get(0) != null && ((Element) attr.getValue().get(0)).getFirstChild() != null
                        && !attr.getValue().get(0).equals(client.getEmail())
                        ) {
                    try {
                        String val = ((Element) attr.getValue().get(0)).getFirstChild().getTextContent();
                        client.setEmail(val);
                        requiresUpdate = true;
                    } catch (Exception e1) {
                        logger.error("Failed to process existing object");
                        throw new EMPException(e1);
                    }
                }
            }
            if(requiresUpdate) {
                log(synchDate + "Клиент [" + client.getIdOfClient() + "] " + client.getMobile() + " найден по телефону и обновлен. {Email: " + client.getEmail() + "}, {SSOID: " + client.getSsoid() + "}", null);
                DAOService.getInstance().saveEntity(client);
                return true;
            }
            else {
                log(synchDate + "Клиент [" + client.getIdOfClient() + "] " + client.getMobile() + " найден по телефону, не обновлен", null);
                return true;
            }
        }
        log(synchDate + "Клиент [" + client.getIdOfClient() + "] " + client.getMobile() + " не найден по телефону",
                null);
        return false;
    }

    protected boolean bindThrowAdd(StoragePortType storage, ru.axetta.ecafe.processor.core.persistence.Client client, String synchDate) throws EMPException {
        //  execute reqeuest
        log(synchDate + "Отправка запроса на регистрацию клиента [" + client.getIdOfClient() + "] " + client.getMobile(), null);
        AddEntriesRequest request = buildAddEntryParams(client);
        AddEntriesResponse response = storage.addEntries(request);
        if(response.getErrorCode() != 0) {
            throw new EMPException(response.getErrorCode(), response.getErrorMessage());
        }

        if(response.getResult().getAffected().intValue() > 0) {
            log(synchDate + "Запрос выполнен, клиенту [" + client.getIdOfClient() + "] " + client.getMobile() + " установлено SSOID = -1", null);
            client.setSsoid("-1");
            DAOService.getInstance().saveEntity(client);
            return true;
        }
        log(synchDate + "Не удалось зарегистрировать клиента [" + client.getIdOfClient() + "] " + client.getMobile(), null);
        throw new EMPException("Failed to make registration request");
    }

    protected SendSubscriptionStreamEventsRequestType buildEvenParam(EMPEventType eventType) {
        SendSubscriptionStreamEventsRequestType sending = new SendSubscriptionStreamEventsRequestType();
        try {
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
            String uuid = UUID.randomUUID().toString();

            //  sending spec
            sending.setToken(config.getToken());
            sending.setDatetime(xgcal);
            sending.setSystemId(config.getSystemId());
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
            if(paramsObj == null) {
                paramsObj = new EventMessageType.Parameters();
                messageParams.setParameters(paramsObj);
            }
            List<EventMessageParameterType> params = paramsObj.getParameter();
            for(String k : eventType.getParameters().keySet()) {
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
            if(filtersObj == null) {
                filtersObj = new EventType.Filters();
                event.setFilters(filtersObj);
            }
            List<EventFilterType> filters = filtersObj.getFilter();
            EventFilterType f1 = new EventFilterType();
            EventFilterType.Persons.Person personFilter = new EventFilterType.Persons.Person();
            personFilter.setSSOID(eventType.getSsoid());
            EventFilterType.Persons personsObj = f1.getPersons();
            if(personsObj == null) {
                personsObj = new EventFilterType.Persons();
                f1.setPersons(personsObj);
            }
            personsObj.getPerson().add(personFilter);
            filters.add(f1);

            //  bind event
            SendSubscriptionStreamEventsRequestType.Events eventsObj = sending.getEvents();
            if(eventsObj == null) {
                eventsObj = new SendSubscriptionStreamEventsRequestType.Events();
                sending.setEvents(eventsObj);
            }
            eventsObj.getEvent().add(event);
        } catch (Exception e) {
            logger.error("Failed to build request", e);
        }
        return sending;
    }

    protected AddEntriesRequest buildAddEntryParams(ru.axetta.ecafe.processor.core.persistence.Client client) {
        AddEntriesRequest request = new AddEntriesRequest();
        //  base
        request.setToken(config.getToken());
        //request.setCatalogOwner("System");
        request.setCatalogName(config.getCatalogName());

        List<Entry> criteries = request.getEntry();
        //  entry
        Entry entry = new Entry();
        criteries.add(entry);
        //  main
        EntryAttribute msisdn = new EntryAttribute();
        msisdn.setName(ATTRIBUTE_MOBILE_PHONE_NAME);
        msisdn.getValue().add(client.getMobile());
        entry.getAttribute().add(msisdn);
        EntryAttribute email = new EntryAttribute();
        email.setName(ATTRIBUTE_EMAIL_NAME);
        email.getValue().add(client.getEmail());
        entry.getAttribute().add(email);
        EntryAttribute ruleId = new EntryAttribute();
        ruleId.setName(ATTRIBUTE_RULE_ID);
        ruleId.getValue().add("" + client.getContractId());
        entry.getAttribute().add(ruleId);
        /*EntryAttribute subscriptionId = new EntryAttribute();
        subscriptionId.setName(ATTRIBUTE_SUBSCRIPTION_ID);
        subscriptionId.getValue().add("" + client.getContractId());
        entry.getAttribute().add(subscriptionId);*/
        //  second
        EntryAttribute active = new EntryAttribute();
        active.setName(ATTRIBUTE_ACTIVE);
        active.getValue().add(Boolean.TRUE);
        entry.getAttribute().add(active);
        EntryAttribute smsSend = new EntryAttribute();
        smsSend.setName(ATTRIBUTE_SMS_SEND);
        smsSend.getValue().add(Boolean.TRUE);
        entry.getAttribute().add(smsSend);
        EntryAttribute emailSend = new EntryAttribute();
        emailSend.setName(ATTRIBUTE_EMAIL_SEND);
        emailSend.getValue().add(Boolean.TRUE);
        entry.getAttribute().add(emailSend);
        EntryAttribute pushSend = new EntryAttribute();
        pushSend.setName(ATTRIBUTE_PUSH_SEND);
        pushSend.getValue().add(Boolean.TRUE);
        entry.getAttribute().add(pushSend);
        //  empty
        String [] emptyParams = new String [] { "SURNAME", "NAME", "PATRONYMIC" };
        String [] nullParams = new String [] { "SMS_SEND_START", "SMS_SEND_STOP", "SMS_SEND_EXCLUDE_DAYS",
                                               "EMAIL_SEND_START", "EMAIL_SEND_STOP", "EMAIL_SEND_EXCLUDE_DAYS",
                                               "PUSH_SEND_START", "PUSH_SEND_STOP", "PUSH_SEND_EXCLUDE_DAYS" };
        for(String p : emptyParams) {
            EntryAttribute paramId = new EntryAttribute();
            paramId.setName(p);
            paramId.getValue().add("");
            entry.getAttribute().add(paramId);
        }
        for(String p : nullParams) {
            EntryAttribute paramId = new EntryAttribute();
            paramId.setName(p);
            paramId.getValue().add(null);
            entry.getAttribute().add(paramId);
        }
        return request;
    }

    protected SelectEntriesRequest buildSelectEntryParams(String clientMobile) {
        SelectEntriesRequest request = new SelectEntriesRequest();
        //  base
        request.setToken(config.getToken());
        //request.setCatalogOwner("System");
        request.setCatalogName(config.getCatalogName());

        //  paging
        Paging paging = new Paging();
        paging.setNumber(new BigInteger("1"));
        paging.setSize(new BigInteger("100"));
        request.setPaging(paging);

        //  criterions
        List<EntryAttribute> criteries = request.getCriteria();
        EntryAttribute msisdn = new EntryAttribute();
        msisdn.setName(ATTRIBUTE_MOBILE_PHONE_NAME);
        msisdn.getValue().add(clientMobile);
        criteries.add(msisdn);

        return request;
    }

    protected ReceiveDataChangesRequest buildReceiveEntryParams(long changeSequence) {
        ReceiveDataChangesRequest request = new ReceiveDataChangesRequest();
        //  base
        request.setToken(config.getToken());
        //request.setCatalogOwner("System");
        request.setCatalogName(config.getCatalogName());

        //  paging
        Paging paging = new Paging();
        paging.setNumber(new BigInteger("1"));
        paging.setSize(new BigInteger("100"));
        request.setPaging(paging);

        request.setChangeSequence(changeSequence);

        return request;
    }

    protected StoragePortType createStorageController() {
        if(storageService != null) {
            return storageService;
        }
        StoragePortType controller = null;
        try {
            StorageService service = new StorageService(new URL(config.getStorageServiceUrl()),
                    new QName("http://emp.mos.ru/schemas/storage/", "StorageService"));
            controller = service.getStoragePort();

            org.apache.cxf.endpoint.Client client = ClientProxy.getClient(controller);
            HTTPConduit conduit = (HTTPConduit) client.getConduit();
            HTTPClientPolicy policy = conduit.getClient();
            policy.setReceiveTimeout(30 * 60 * 1000);
            policy.setConnectionTimeout(30 * 60 * 1000);
            storageService = controller;
            return controller;
        } catch (java.lang.Exception e) {
            logger.error("Failed to create WS controller", e);
            return null;
        }
    }

    protected SubscriptionPortType createEventController() {
        if (subscriptionService != null) {
            return subscriptionService;
        }
        SubscriptionPortType controller = null;
        try {
            SubscriptionService service = new SubscriptionService(new URL(config.getSubscriptionServiceUrl()),
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

    public static void log(String str, StringBuffer logBuffer) {
        if (logBuffer != null) {
            logBuffer.append(str).append('\n');
        }
        if (RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_MSK_NSI_LOG)) {
            logger.info(str);
        }
    }

    public static void addEntryToLogString(ReceiveDataChangesResponse.Result.Entry.Identifier id, StringBuilder str) {
        addEntryToLogString(id.getName(), id.getValue(), str);
    }

    public static void addEntryToLogString(ReceiveDataChangesResponse.Result.Entry.Attribute attr, StringBuilder str) {
        Object val = null;
        if(attr.getValue() != null && attr.getValue().size() > 0 && attr.getValue().get(0) != null) {
            val = attr.getValue().get(0);
        }
        addEntryToLogString(attr.getName(), val, str);
    }

    protected static void addEntryToLogString(String name, Object val, StringBuilder str) {
        String valStr = null;
        if(val != null && val instanceof Element) {
            Element e = ((Element) val);
            if(e != null && e.getFirstChild() != null) {
                valStr = e.getFirstChild().getTextContent();
            }
        }
        if (valStr == null) {
            valStr = "-NULL-";
        }

        if(str.length() > 0) {
            str.append(",");
        }
        str.append(String.format("{%s: %s}", name, valStr));
    }

    protected void logRequest(ReceiveDataChangesRequest request) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ReceiveDataChangesRequest.class);
            logRequest(jaxbContext, request);
        } catch (Exception e) {
        }
    }

    protected void logRequest(SendSubscriptionStreamEventsRequestType request) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance("generated.emp_events");
            logRequest(jaxbContext, request);
        } catch (Exception e) {

        }
    }

    protected void logRequest(JAXBContext jaxbContext, Object obj) {
        if(!config.getLogging()) {
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

    public static EMPStatistics loadEMPStatistics() {
        return new EMPStatistics();
    }

    public static void saveEMPStatistics(EMPStatistics statistics) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();

        runtimeContext.setOptionValueWithSave(Option.OPTION_EMP_NOT_BINDED_CLIENTS_COUNT, statistics.getNotBindedCount());
        runtimeContext.setOptionValueWithSave(Option.OPTION_EMP_BIND_WAITING_CLIENTS_COUNT, statistics.getWaitBindingCount());
        runtimeContext.setOptionValueWithSave(Option.OPTION_EMP_BINDED_CLIENTS_COUNT, statistics.getBindedCount());
    }

    public static class EMPStatistics {
        protected long notBindedCount;
        protected long waitBindingCount;
        protected long bindedCount;

        public EMPStatistics() {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            notBindedCount = runtimeContext.getOptionValueLong(Option.OPTION_EMP_NOT_BINDED_CLIENTS_COUNT);
            waitBindingCount = runtimeContext.getOptionValueLong(Option.OPTION_EMP_BIND_WAITING_CLIENTS_COUNT);
            bindedCount = runtimeContext.getOptionValueLong(Option.OPTION_EMP_BINDED_CLIENTS_COUNT);
        }

        public long getNotBindedCount() {
            return notBindedCount;
        }

        public long getWaitBindingCount() {
            return waitBindingCount;
        }

        public long getBindedCount() {
            return bindedCount;
        }

        public void addNotBinded() {
            notBindedCount++;
        }

        public void addWaitBinding() {
            waitBindingCount++;
        }

        public void addBinded() {
            bindedCount++;
        }

        public void removeNotBinded() {
            notBindedCount--;
        }

        public void removeWaitBinding() {
            waitBindingCount--;
        }

        public void setNotBindedCount(long notBindedCount) {
            this.notBindedCount = notBindedCount;
        }

        public void setWaitBindingCount(long waitBindingCount) {
            this.waitBindingCount = waitBindingCount;
        }

        public void setBindedCount(long bindedCount) {
            this.bindedCount = bindedCount;
        }
    }

}