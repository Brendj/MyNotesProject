/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp;

import generated.emp_events.*;
import generated.emp_storage.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sms.emp.type.EMPEventType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.math.BigInteger;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 18.07.14
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class EMPProcessor {
    public static final String ATTRIBUTE_ACCOUNT_NAME      = "ACCOUNT";
    public static final String ATTRIBUTE_MOBILE_PHONE_NAME = "MSISDN";
    public static final String ATTRIBUTE_RULE_ID           = "RULE_ID";
    public static final String ATTRIBUTE_SSOID_NAME        = "SSOID";
    public static final String ATTRIBUTE_EMAIL_NAME        = "EMAIL";
    public static final String ATTRIBUTE_ACTIVE            = "ACTIVE";
    public static final String ATTRIBUTE_SMS_SEND          = "SMS_SEND";
    public static final String ATTRIBUTE_EMAIL_SEND        = "EMAIL_SEND";
    public static final String ATTRIBUTE_PUSH_SEND         = "PUSH_SEND";
    //  system
    public static final String ATTRIBUTE_TOKEN_VALUE       = "49aafdb8198311e48ee8416c74617269";
    public static final String ATTRIBUTE_SYSTEM_ID         = "666255";
    public static final String ATTRIBUTE_CATALOG_VALUE     = "SYS666254CAT0000000SUBSCRIPTIONS";
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EMPProcessor.class);
    //  errors
    public static final int EMP_ERROR_CODE_NOTHING_FOUND = 504;


    protected int getMaximumClientsPerPackage() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        return runtimeContext.getOptionValueInt(Option.OPTION_EMP_CLIENTS_PER_PACKAGE);
    }

    protected boolean isAllowed() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getInstanceNameDecorated();
        String reqInstance = runtimeContext.getOptionValueString(Option.OPTION_EMP_PROCESSOR_INSTANCE);
        if(StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim().equals(reqInstance.trim())) {
            return false;
        }
        return true;
    }

    public void runBindClients() throws EMPException {
        if(!isAllowed()) {
            return;
        }

        //  Вспомогательные значения
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        String synchDate = "[Привязка клиентов ИСПП к ЕМП " + date + "]: ";

        //  Загрузка клиентов для связки
        List<ru.axetta.ecafe.processor.core.persistence.Client> notBindedClients = DAOService.getInstance().getNotBindedEMPClients(getMaximumClientsPerPackage());
        log(synchDate + "Количество клиентов к привязке: " + notBindedClients.size(), null);

        //  Отправка запроса на привязку
        StoragePortType storage = createStorageController();
        if(storage == null) {
            throw new EMPException("Failed to create connection with EMP web service");
        }
        for(ru.axetta.ecafe.processor.core.persistence.Client c : notBindedClients) {
            try {
                bindClient(storage, c, synchDate);
            } catch (EMPException empe) {
                logger.error(String.format("Failed to parse client: [code=%s] %s", empe.getCode(), empe.getError()), empe);
            }
        }
    }

    public void runReceiveUpdates() throws EMPException {
        if(!isAllowed()) {
            return;
        }

        //  Вспомогательные значения
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        String synchDate = "[Получение изменений из ЕМП " + date + "]: ";

        //  Загрузка клиентов для связки
        long changeSequence = RuntimeContext.getInstance().getOptionValueLong(Option.OPTION_EMP_CHANGE_SEQUENCE);
        StoragePortType storage = createStorageController();
        if(storage == null) {
            throw new EMPException("Failed to create connection with EMP web service");
        }
        ReceiveDataChangesRequest request = buildReceiveEntryParams(changeSequence);
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
            String ssoid = "";
            String msisdn = "";
            for(ReceiveDataChangesResponse.Result.Entry.Attribute attr : attributes) {
                if(!StringUtils.isBlank(attr.getName()) &&
                   attr.getName().equals(ATTRIBUTE_SSOID_NAME) &&
                   attr.getValue() != null && attr.getValue().size() > 0 && attr.getValue().get(0) != null) {
                    ssoid = attr.getValue().get(0).toString();
                }
                if(!StringUtils.isBlank(attr.getName()) &&
                   attr.getName().equals(ATTRIBUTE_MOBILE_PHONE_NAME) &&
                   attr.getValue() != null && attr.getValue().size() > 0 && attr.getValue().get(0) != null) {
                    msisdn = attr.getValue().get(0).toString();
                }
            }
            if(!StringUtils.isBlank(msisdn) && !StringUtils.isBlank(ssoid)) {
                ru.axetta.ecafe.processor.core.persistence.Client client = DAOService.getInstance().getClientByMobilePhone(msisdn);
                if(client != null) {
                    log(synchDate + "Поступили изменения {SSOID: " + ssoid + "}, {Моб. тел: " + msisdn + "} для клиента [" + client.getIdOfClient() + "] " + client.getMobile(), null);
                    client.setSsoid(ssoid);
                    DAOService.getInstance().saveEntity(client);
                }
            }
            changeSequence = e.getChangeSequence();
        }

        log(synchDate + "Обновление очереди до " + changeSequence, null);
        RuntimeContext.getInstance().setOptionValue(Option.OPTION_EMP_CHANGE_SEQUENCE, changeSequence + 1);
        if(response.getResult().isHasMoreEntries()) {
            log(synchDate + "Изменения в ЕМП обработаны не до конца, запрос будет выполнен повторно", null);
            RuntimeContext.getAppContext().getBean(EMPProcessor.class).runReceiveUpdates();
        }
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
        SendSubscriptionStreamEventsResponseType response = subscription.sendSubscriptionStreamEvents(eventParam);
        if(response.getErrorCode() != 0) {
            log(synchDate + "Не удалось доставить событие " + event.getType() + " для клиента [" + client.getIdOfClient() + "] " + client.getMobile(), null);
            throw new EMPException(String.format("Failed to execute event notification: Error [%s] %s",
                    response.getErrorCode(), response.getErrorMessage()));
        }
        log(synchDate + "Событие " + event.getType() + " для клиента [" + client.getIdOfClient() + "] " + client.getMobile() + " доставлено", null);
        return true;
    }

    protected void bindClient(StoragePortType storage, ru.axetta.ecafe.processor.core.persistence.Client client, String synchDate) throws EMPException {
        if(bindThrowSelect(storage, client, synchDate)) {
            logger.debug("Client is binded");
        } else if(bindThrowAdd(storage, client, synchDate)) {
            logger.debug("Client is binded");
        }
    }

    protected boolean bindThrowSelect(StoragePortType storage, ru.axetta.ecafe.processor.core.persistence.Client client, String synchDate) throws EMPException {
        log(synchDate + "Попытка сзязать клиента [" + client.getIdOfClient() + "] " + client.getMobile() + " с использованием поиска по телефону", null);
        //  execute reqeuest
        SelectEntriesRequest request = buildSelectEntryParams(client);
        SelectEntriesResponse response = storage.selectEntries(request);
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
                        !attr.getValue().equals(client.getMobile()) &&
                        attr.getValue() != null && attr.getValue().size() > 0) {
                    client.setSsoid(attr.getValue().get(0).toString());
                    requiresUpdate = true;
                }
                if(attr.getName().equals(ATTRIBUTE_EMAIL_NAME) &&
                        !attr.getValue().equals(client.getEmail()) &&
                        attr.getValue() != null && attr.getValue().size() > 0) {
                    client.setEmail(attr.getValue().get(0).toString());
                    requiresUpdate = true;
                }
            }
            if(requiresUpdate) {
                log(synchDate + "Клиент [" + client.getIdOfClient() + "] " + client.getMobile() + " найден по телефону. {Email: " + client.getEmail() + "}, {SSOID: " + client.getSsoid() + "}", null);
                DAOService.getInstance().saveEntity(client);
                return true;
            }
        }
        log(synchDate + "Клиент [" + client.getIdOfClient() + "] " + client.getMobile() + " не найден по телефону", null);
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

            //  sending spec
            sending.setToken(ATTRIBUTE_TOKEN_VALUE);
            sending.setDatetime(xgcal);
            sending.setSystemId(ATTRIBUTE_SYSTEM_ID);
            sending.setId(UUID.fromString("" + System.currentTimeMillis()).toString());

            EventType event = new EventType();
            //  event spec
            event.setDatetime(xgcal);
            event.setDescription("Пробное информирование");
            event.setId(UUID.fromString("" + System.currentTimeMillis()).toString());
            event.setStreamId(eventType.getStream());
            event.setTypeId(eventType.getType());
            //  event message params
            EventMessageType messageParams = new EventMessageType();
            List<EventMessageParameterType> params = messageParams.getParameters().getParameter();
            for(String k : eventType.getParameters().keySet()) {
                String v = eventType.getParameters().get(k);

                EventMessageParameterType nameParam = new EventMessageParameterType();
                nameParam.setName(k);
                nameParam.setValue(v);
                params.add(nameParam);
            }
            //  filters
            List<EventFilterType> filters = event.getFilters().getFilter();
            EventFilterType f1 = new EventFilterType();
            EventFilterType.Persons.Person personFilter = new EventFilterType.Persons.Person();
            personFilter.setSSOID(eventType.getSsoid());
            f1.getPersons().getPerson().add(personFilter);
            filters.add(f1);

            //  bind event
            sending.getEvents().getEvent().add(event);
        } catch (Exception e) {

        }
        return sending;
    }

    protected AddEntriesRequest buildAddEntryParams(ru.axetta.ecafe.processor.core.persistence.Client client) {
        AddEntriesRequest request = new AddEntriesRequest();
        //  base
        request.setToken(ATTRIBUTE_TOKEN_VALUE);
        //request.setCatalogOwner("System");
        request.setCatalogName(ATTRIBUTE_CATALOG_VALUE);

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
        /*EntryAttribute account = new EntryAttribute();
        account.setName(ATTRIBUTE_ACCOUNT_NAME);
        account.getValue().add("" + client.getContractId());
        entry.getAttribute().add(account);*/
        EntryAttribute ruleId = new EntryAttribute();
        ruleId.setName(ATTRIBUTE_RULE_ID);
        ruleId.getValue().add("" + client.getContractId());
        entry.getAttribute().add(ruleId);
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

        return request;
    }

    protected SelectEntriesRequest buildSelectEntryParams(ru.axetta.ecafe.processor.core.persistence.Client client) {
        SelectEntriesRequest request = new SelectEntriesRequest();
        //  base
        request.setToken(ATTRIBUTE_TOKEN_VALUE);
        //request.setCatalogOwner("System");
        request.setCatalogName(ATTRIBUTE_CATALOG_VALUE);

        //  paging
        Paging paging = new Paging();
        paging.setNumber(new BigInteger("1"));
        paging.setSize(new BigInteger("100"));
        request.setPaging(paging);

        //  criterions
        List<EntryAttribute> criteries = request.getCriteria();
        EntryAttribute msisdn = new EntryAttribute();
        msisdn.setName(ATTRIBUTE_MOBILE_PHONE_NAME);
        msisdn.getValue().add(client.getMobile());
        criteries.add(msisdn);

        return request;
    }

    protected ReceiveDataChangesRequest buildReceiveEntryParams(long changeSequence) {
        ReceiveDataChangesRequest request = new ReceiveDataChangesRequest();
        //  base
        request.setToken(ATTRIBUTE_TOKEN_VALUE);
        //request.setCatalogOwner("System");
        request.setCatalogName(ATTRIBUTE_CATALOG_VALUE);

        //  paging
        Paging paging = new Paging();
        paging.setNumber(new BigInteger("1"));
        paging.setSize(new BigInteger("100"));
        request.setPaging(paging);

        request.setChangeSequence(changeSequence);

        return request;
    }

    protected StoragePortType createStorageController() {
        StoragePortType controller = null;
        try {
            StorageService service = new StorageService(new URL("http://api.uat.emp.msk.ru:8090/ws/storage/?wsdl"),
                    new QName("http://emp.mos.ru/schemas/storage/", "StorageService"));
            controller = service.getStoragePort();

            Client client = ClientProxy.getClient(controller);
            HTTPConduit conduit = (HTTPConduit) client.getConduit();
            HTTPClientPolicy policy = conduit.getClient();
            policy.setReceiveTimeout(30 * 60 * 1000);
            policy.setConnectionTimeout(30 * 60 * 1000);
            return controller;
        } catch (java.lang.Exception e) {
            logger.error("Failed to create WS controller", e);
            return null;
        }
    }

    protected SubscriptionPortType createEventController() {
        SubscriptionPortType controller = null;
        try {
            SubscriptionService service = new SubscriptionService(new URL("http://api.uat.emp.msk.ru:8090/ws/subscriptions/?wsdl"),
                    new QName("urn://emp.altarix.ru/subscriptions", "SubscriptionService"));
            controller = service.getServicePort();

            Client client = ClientProxy.getClient(controller);
            HTTPConduit conduit = (HTTPConduit) client.getConduit();
            HTTPClientPolicy policy = conduit.getClient();
            policy.setReceiveTimeout(30 * 60 * 1000);
            policy.setConnectionTimeout(30 * 60 * 1000);
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
}