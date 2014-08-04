/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.emp;

import generated.emp_events.*;
import generated.emp_storage.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.apache.commons.lang.StringUtils;
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
import java.util.GregorianCalendar;
import java.util.List;

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
    public static final String ATTRIBUTE_ACCOUNT_NAME      = "MSISDN";
    public static final String ATTRIBUTE_MOBILE_PHONE_NAME = "MSISDN";
    public static final String ATTRIBUTE_SSOID_NAME        = "SSOID";
    public static final String ATTRIBUTE_EMAIL_NAME        = "EMAIL";
    public static final String ATTRIBUTE_TOKEN_VALUE       = "TOKEN";
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EMPProcessor.class);


    protected SendSubscriptionStreamEventsRequestType buildEvenParam(ru.axetta.ecafe.processor.core.persistence.Client client) {
        SendSubscriptionStreamEventsRequestType sending = new SendSubscriptionStreamEventsRequestType();
        try {
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);

            //  sending spec
            sending.setToken(ATTRIBUTE_TOKEN_VALUE);
            sending.setDatetime(xgcal);
            sending.setSystemId("100999");
            sending.setId("c758b1e0-deef-4eea-bac7-21cda8531d9f");

            EventType event = new EventType();
            //  event spec
            event.setDatetime(xgcal);
            event.setDescription("Пробное информирование");
            event.setId("e8356612-3a60-4922-8ece-5a8244cd6794");
            event.setStreamId(4);
            event.setTypeId(1);
            //  event message params
            EventMessageType messageParams = new EventMessageType();
            List<EventMessageParameterType> params = messageParams.getParameters().getParameter();
            //  name param
            EventMessageParameterType nameParam = new EventMessageParameterType();
            nameParam.setName("NAME");
            nameParam.setValue("Здесь будет имя пользователя");
            params.add(nameParam);
            //  txt
            EventMessageParameterType textParam = new EventMessageParameterType();
            textParam.setName("парам1");
            textParam.setValue("Тестирование");
            params.add(textParam);
            event.setMessage(messageParams);
            //  filters
            List<EventFilterType> filters = event.getFilters().getFilter();
            EventFilterType f1 = new EventFilterType();
            EventFilterType.Persons.Person personFilter = new EventFilterType.Persons.Person();
            personFilter.setSSOID(client.getSsoid());
            f1.getPersons().getPerson().add(personFilter);
            filters.add(f1);

            //  bind event
            sending.getEvents().getEvent().add(event);
        } catch (Exception e) {

        }
        return sending;
    }

    public void sendEvent() {
        ru.axetta.ecafe.processor.core.persistence.Client client =
                DAOService.getInstance().getClientByGuid("eb759664-b6be-1ec2-e043-a2997e0a261d");
        SubscriptionPortType subscription = createEventController();
        SendSubscriptionStreamEventsRequestType eventParam = buildEvenParam(client);
        subscription.sendSubscriptionStreamEvents(eventParam);
    }

    public void runBindClients() {
        List<ru.axetta.ecafe.processor.core.persistence.Client> notBindedClients = DAOService.getInstance().getNotBindedEMPClients();
        /*ru.axetta.ecafe.processor.core.persistence.Client client =
                DAOService.getInstance().getClientByGuid("eb759664-b6be-1ec2-e043-a2997e0a261d");*/

        StoragePortType storage = createStorageController();
        for(ru.axetta.ecafe.processor.core.persistence.Client c : notBindedClients) {
            try {
                bindClient(storage, c);
            } catch (EMPException empe) {
                logger.error(String.format("Failed to parse client: [code=%s] %s", empe.getCode(), empe.getError()), empe);
            }
        }
    }

    public void runReceiveUpdates() {
        long changeSequence = RuntimeContext.getInstance().getOptionValueLong(Option.OPTION_EMP_CHANGE_SEQUENCE);
        StoragePortType storage = createStorageController();
        ReceiveDataChangesRequest request = buildReceiveEntryParams(1L);
        ReceiveDataChangesResponse response = storage.receiveDataChanges(request);
        if(response.getErrorCode() != 0) {
            logger.error(String.format("Failed to receive updates: [code=%s] %s", response.getErrorCode(), response.getErrorMessage()));
            return;
        }

        List<ReceiveDataChangesResponse.Result.Entry> entries = response.getResult().getEntry();
        for(ReceiveDataChangesResponse.Result.Entry e : entries) {
            List<EntryAttribute> attributes = e.getAttribute();
            String ssoid = "";
            String msisdn = "";
            for(EntryAttribute attr : attributes) {
                if(attr.getName().equals(ATTRIBUTE_SSOID_NAME)) {
                    ssoid = attr.getValue();
                }
                if(attr.getName().equals(ATTRIBUTE_MOBILE_PHONE_NAME)) {
                    msisdn = attr.getValue();
                }
            }
            if(StringUtils.isBlank(msisdn)) {
                ru.axetta.ecafe.processor.core.persistence.Client client = DAOService.getInstance().getClientByMobilePhone(msisdn);
                client.setSsoid(ssoid);
                DAOService.getInstance().saveEntity(client);
            }
            changeSequence = e.getChangeSequence();
        }

        RuntimeContext.getInstance().setOptionValue(Option.OPTION_EMP_CHANGE_SEQUENCE, changeSequence);
        if(response.getResult().isHasMoreEntries()) {
            RuntimeContext.getAppContext().getBean(EMPProcessor.class).runReceiveUpdates();
        }
    }

    protected void bindClient(StoragePortType storage, ru.axetta.ecafe.processor.core.persistence.Client client) throws EMPException {
        if(bindThrowSelect(storage, client)) {
            logger.debug("Client is binded");
        } else if(bindThrowAdd(storage, client)) {
            logger.debug("Client is binded");
        }
    }

    protected boolean bindThrowAdd(StoragePortType storage, ru.axetta.ecafe.processor.core.persistence.Client client) throws EMPException {
        //  execute reqeuest
        AddEntriesRequest request = buildAddEntryParams(client);
        AddEntriesResponse response = storage.addEntries(request);
        if(response.getErrorCode() != 0) {
            throw new EMPException(response.getErrorCode(), response.getErrorMessage());
        }

        if(response.getResult().getAffected().intValue() > 0) {
            client.setSsoid("-1");
            DAOService.getInstance().saveEntity(client);
            return true;
        }
        return false;
    }

    protected boolean bindThrowSelect(StoragePortType storage, ru.axetta.ecafe.processor.core.persistence.Client client) throws EMPException {
        //  execute reqeuest
        SelectEntriesRequest request = buildSelectEntryParams(client);
        SelectEntriesResponse response = storage.selectEntries(request);
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
                   !attr.getValue().equals(client.getMobile())) {
                    client.setSsoid(attr.getValue());
                    requiresUpdate = true;
                }
                if(attr.getName().equals(ATTRIBUTE_EMAIL_NAME) &&
                   !attr.getValue().equals(client.getEmail())) {
                    client.setEmail(attr.getValue());
                    requiresUpdate = true;
                }
            }
            if(requiresUpdate) {
                DAOService.getInstance().saveEntity(client);
                return true;
            }
        }
        return false;
    }

    protected AddEntriesRequest buildAddEntryParams(ru.axetta.ecafe.processor.core.persistence.Client client) {
        AddEntriesRequest request = new AddEntriesRequest();
        //  base
        request.setToken(ATTRIBUTE_TOKEN_VALUE);
        request.setCatalogOwner("System");
        request.setCatalogName("Citizens");

        List<Entry> criteries = request.getEntry();
        //  entry
        Entry entry = new Entry();
        EntryAttribute msisdn = new EntryAttribute();
        msisdn.setName(ATTRIBUTE_MOBILE_PHONE_NAME);
        msisdn.setValue(client.getMobile());
        entry.getAttribute().add(msisdn);
        EntryAttribute email = new EntryAttribute();
        email.setName(ATTRIBUTE_EMAIL_NAME);
        email.setValue(client.getEmail());
        entry.getAttribute().add(email);
        EntryAttribute account = new EntryAttribute();
        account.setName(ATTRIBUTE_ACCOUNT_NAME);
        account.setValue("" + client.getContractId());
        entry.getAttribute().add(account);

        return request;
    }

    protected SelectEntriesRequest buildSelectEntryParams(ru.axetta.ecafe.processor.core.persistence.Client client) {
        SelectEntriesRequest request = new SelectEntriesRequest();
        //  base
        request.setToken(ATTRIBUTE_TOKEN_VALUE);
        request.setCatalogOwner("System");
        request.setCatalogName("Citizens");

        //  paging
        Paging paging = new Paging();
        paging.setNumber(new BigInteger("1"));
        paging.setSize(new BigInteger("100"));
        request.setPaging(paging);

        //  criteries
        List<EntryAttribute> criteries = request.getCriteria();
        EntryAttribute msisdn = new EntryAttribute();
        msisdn.setName(ATTRIBUTE_MOBILE_PHONE_NAME);
        msisdn.setValue(client.getMobile());
        criteries.add(msisdn);

        return request;
    }

    protected ReceiveDataChangesRequest buildReceiveEntryParams(long changeSequence) {
        ReceiveDataChangesRequest request = new ReceiveDataChangesRequest();
        //  base
        request.setToken(ATTRIBUTE_TOKEN_VALUE);
        request.setCatalogOwner("System");
        request.setCatalogName("Citizens");

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
            StorageService service = new StorageService(new URL("http://Inv5379-NB:8088/mockStorageBinding?wsdl"),
                    new QName("http://emp.mos.ru/schemas/storage/", "StorageService"));
            controller = service.getStoragePort();

            Client client = ClientProxy.getClient(controller);
            HTTPConduit conduit = (HTTPConduit) client.getConduit();
            HTTPClientPolicy policy = conduit.getClient();
            policy.setReceiveTimeout(30 * 60 * 1000);
            policy.setConnectionTimeout(30 * 60 * 1000);
            return controller;
        } catch (java.lang.Exception e) {
            return null;
        }
    }

    protected SubscriptionPortType createEventController() {
        SubscriptionPortType controller = null;
        try {
            SubscriptionService service = new SubscriptionService(new URL("http://Inv5379-NB:8088/mockSubscriptionBinding?wsdl"),
                    new QName("http://emp.mos.ru/schemas/storage/", "SubscriptionService"));
            controller = service.getServicePort();

            Client client = ClientProxy.getClient(controller);
            HTTPConduit conduit = (HTTPConduit) client.getConduit();
            HTTPClientPolicy policy = conduit.getClient();
            policy.setReceiveTimeout(30 * 60 * 1000);
            policy.setConnectionTimeout(30 * 60 * 1000);
            return controller;
        } catch (java.lang.Exception e) {
            return null;
        }
    }
}