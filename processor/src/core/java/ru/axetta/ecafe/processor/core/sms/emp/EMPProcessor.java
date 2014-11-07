/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp;

import generated.emp_events.*;
import generated.emp_storage.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sms.emp.type.EMPEventType;

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

    public static final String EMP_OUTPUT_FILE = "/emp.out.signed.xml";
    //  service instance
    protected StoragePortType storageService;
    //  jaxb contexts
    JAXBContext dataChangesRequestContext;
    JAXBContext eventsRequestContext;

    //  errors
    public static final int EMP_ERROR_CODE_NOTHING_FOUND = 504;

    public static final String ATTRIBUTE_ACCOUNT_NAME = "ACCOUNT";
    public static final String ATTRIBUTE_MOBILE_PHONE_NAME = "MSISDN";
    public static final String ATTRIBUTE_RULE_ID = "RULE_ID";
    public static final String ATTRIBUTE_SUBSCRIPTION_ID = "SUBSCRIPTION_ID";
    public static final String ATTRIBUTE_SSOID_NAME = "SSOID";
    public static final String ATTRIBUTE_EMAIL_NAME = "EMAIL";
    public static final String ATTRIBUTE_ACTIVE = "ACTIVE";
    public static final String ATTRIBUTE_SMS_SEND = "SMS_SEND";
    public static final String ATTRIBUTE_EMAIL_SEND = "EMAIL_SEND";
    public static final String ATTRIBUTE_PUSH_SEND = "PUSH_SEND";
    //
    public static final String SSOID_REGISTERED_AND_WAITING_FOR_DATA = "-1";

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EMPSmsServiceImpl.class);

    final static String CONFIG_PARAM_BASE = RuntimeContext.PROCESSOR_PARAM_BASE + ".sms.service.emp";
    
    ////config
    String token, systemId, catalogName, subscriptionServiceUrl, storageServiceUrl, syncServiceNode;
    Integer packageSize;
    Boolean logging;
    Long statsLifetime;
    
    protected String getConfigToken() {
        if (token!=null) return token;
        return token = RuntimeContext.getInstance().getConfigProperties().getProperty(CONFIG_PARAM_BASE+".token", "49aafdb8198311e48ee8416c74617269");
    }
    protected String getConfigSystemId() {
        if (systemId!=null) return systemId;
        return systemId = RuntimeContext.getInstance().getConfigProperties().getProperty(CONFIG_PARAM_BASE+".systemId", "666255");
    }
    protected String getConfigCatalogName() {
        if (catalogName!=null) return catalogName;
        return catalogName=RuntimeContext.getInstance().getConfigProperties().getProperty(CONFIG_PARAM_BASE+".catalogName", "SYS666254CAT0000000SUBSCRIPTIONS");
    }
    protected String getConfigStorageServiceUrl() {
        if (storageServiceUrl!=null) return storageServiceUrl;
        return storageServiceUrl=RuntimeContext.getInstance().getConfigProperties().getProperty(CONFIG_PARAM_BASE+".storageUrl", "http://api.uat.emp.msk.ru:8090/ws/storage/?wsdl");
    }
    protected String getConfigSyncServiceNode() {
        if (syncServiceNode!=null) return syncServiceNode;
        return syncServiceNode=RuntimeContext.getInstance().getConfigProperties().getProperty(CONFIG_PARAM_BASE+".syncServiceNode", "1");
    }
    protected int getConfigPackageSize() {
        if (packageSize!=null) return packageSize;
        return packageSize=Integer.parseInt(RuntimeContext.getInstance().getConfigProperties().getProperty(CONFIG_PARAM_BASE+".packageSize", "100"));
    }
    protected Boolean getConfigLogging() {
        if (logging!=null) return logging;
        return logging=Boolean.parseBoolean(RuntimeContext.getInstance().getConfigProperties().getProperty(CONFIG_PARAM_BASE+".logging", "false"));
    }
    protected long getConfigStatsLifetime() {
        if (statsLifetime!=null) return statsLifetime;
        return statsLifetime=Long.parseLong(RuntimeContext.getInstance().getConfigProperties().getProperty(CONFIG_PARAM_BASE+".statsLifetime", "600000"));
    }
    
    public boolean isAllowed() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        //String reqInstance = runtimeContext.getOptionValueString(Option.OPTION_EMP_PROCESSOR_INSTANCE);
        String reqInstance = getConfigSyncServiceNode();
        if (StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim()
                .equals(reqInstance.trim())) {
            return false;
        }
        return true;
    }

    public EMPStatistics recalculateEMPClientsCount() {
        EMPStatistics statistics = loadEMPStatistics();
        long notBinded = DAOService.getInstance().getNotBindedEMPClientsCount();
        long waitBind = DAOService.getInstance().getBindWaitingEMPClients();
        long binded = DAOService.getInstance().getBindedEMPClientsCount();
        long errors = DAOService.getInstance().getBindEMPErrorsCount();
        statistics.setNotBindedCount(notBinded);
        statistics.setWaitBindingCount(waitBind);
        statistics.setBindedCount(binded);
        statistics.setBindingErrors(errors);
        return statistics;
        //saveEMPStatistics(statistics);
    }

    public void runStorageMerge() throws EMPException {
        RuntimeContext.getAppContext().getBean(EMPProcessor.class).runBindClients();
        RuntimeContext.getAppContext().getBean(EMPProcessor.class).runReceiveUpdates();
    }

    public void runBindClients() throws EMPException {
        if (!isAllowed()) {
            return;
        }

        //  Загружаем статистику
        //EMPStatistics statistics = loadEMPStatistics();
        //  Вспомогательные значения
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        String synchDate = "[Привязка клиентов ИСПП к ЕМП " + date + "]: ";

        //  Загрузка клиентов для связки
        List<ru.axetta.ecafe.processor.core.persistence.Client> notBindedClients = DAOService.getInstance()
                .getNotBindedEMPClients(getConfigPackageSize());
        /*notBindedClients.clear();                                                 //!! TEST ONLY
        notBindedClients.add(DAOService.getInstance().findClientById(585664L));     //!! TEST ONLY*/
        log(synchDate + "Количество клиентов к привязке: " + notBindedClients.size());

        //  Отправка запроса на привязку
        StoragePortType storage = createStorageController();
        if (storage == null) {
            throw new EMPException("Failed to create connection with EMP web service");
        }
        for (ru.axetta.ecafe.processor.core.persistence.Client c : notBindedClients) {
            try {
                bindClient(storage, c, synchDate/*, statistics*/);
            } catch (EMPException empe) {
                logger.error(String.format("Failed to parse client: [code=%s] %s", empe.getCode(), empe.getError()),
                        empe);
            }
        }
        //  Обновляем изменившуюся статистику
        //saveEMPStatistics(statistics);
    }

    public void runReceiveUpdates() throws EMPException {
        if (!isAllowed()) {
            return;
        }

        //  Загружаем статистику
        //EMPStatistics statistics = loadEMPStatistics();
        //  Вспомогательные значения
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        String synchDate = "[Получение изменений из ЕМП " + date + "]: ";

        //  Загрузка клиентов для связки
        long changeSequence = RuntimeContext.getInstance().getOptionValueLong(Option.OPTION_EMP_CHANGE_SEQUENCE);//750
        StoragePortType storage = createStorageController();
        if (storage == null) {
            throw new EMPException("Failed to create connection with EMP web service");
        }
        ReceiveDataChangesRequest request = buildReceiveEntryParams(changeSequence);
        logRequest(request);
        ReceiveDataChangesResponse response = storage.receiveDataChanges(request);
        if (response.getErrorCode() != 0) {
            logger.error(String.format("Failed to receive updates: [code=%s] %s", response.getErrorCode(),
                    response.getErrorMessage()));
            return;
        }

        List<ReceiveDataChangesResponse.Result.Entry> entries = response.getResult().getEntry();
        if (entries.size() < 1) {
            log(synchDate + "Новых изменений по очереди " + changeSequence + " в ЕМП нет");
            return;
        }

        log(synchDate + "Поступило " + entries.size() + " изменений из ЕМП по очереди " + changeSequence);
        for (ReceiveDataChangesResponse.Result.Entry e : entries) {
            List<ReceiveDataChangesResponse.Result.Entry.Attribute> attributes = e.getAttribute();
            List<ReceiveDataChangesResponse.Result.Entry.Identifier> identifiers = e.getIdentifier();
            String ssoid = "";
            String ruleId = "";
            StringBuilder logStr = new StringBuilder();
            for (ReceiveDataChangesResponse.Result.Entry.Attribute attr : attributes) {
                if (!StringUtils.isBlank(attr.getName()) &&
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
            for (ReceiveDataChangesResponse.Result.Entry.Identifier id : identifiers) {
                if (!StringUtils.isBlank(id.getName()) &&
                        id.getName().equals(ATTRIBUTE_RULE_ID) &&
                        id.getValue() != null && id.getValue() != null && !StringUtils
                        .isBlank(id.getValue().toString())) {
                    try {
                        ruleId = ((Element) id.getValue()).getFirstChild().getTextContent();
                        break;
                    } catch (Exception e1) {
                        logger.error("Failed to parse " + ATTRIBUTE_RULE_ID + " value", e1);
                    }
                }
                addEntryToLogString(id, logStr);
            }
            if (!StringUtils.isBlank(ruleId) && !StringUtils.isBlank(ssoid) && NumberUtils.isNumber(ruleId)) {
                ru.axetta.ecafe.processor.core.persistence.Client client = DAOService.getInstance()
                        .getClientByContractId(NumberUtils.toLong(ruleId));
                if (client != null) {
                    //  Обновляем статистику
                    /*statistics.addBinded();
                    if (!StringUtils.isBlank(client.getSsoid()) && client.getSsoid()
                            .equals(SSOID_REGISTERED_AND_WAITING_FOR_DATA)) {
                        statistics.removeWaitBinding();
                    } else {
                        statistics.removeNotBinded();
                    }*/
                    List<Client> clients = DAOService.getInstance().getClientsListByMobilePhone(client.getMobile());
                    String idsList = getClientIdsAsString(clients);
                    log(synchDate + "Поступили изменения из ЕМП {SSOID: " + ssoid + "}, {№ Контракта: " + ruleId +
                        "}. Для всех " + clients.size() + " клиентов [" + idsList + "] с подпиской на телефон данного клиента [" +
                        client.getMobile() + "], изменения будут применены");
                    for(Client cl : clients) {
                        cl.setSsoid(ssoid);
                        DAOService.getInstance().saveEntity(cl);
                    }
                    /*log(synchDate + "Поступили изменения из ЕМП {SSOID: " + ssoid + "}, {№ Контракта: " + ruleId
                            + "} для клиента [" + client.getIdOfClient() + "] " + client.getMobile());
                    client.setSsoid(ssoid);
                    DAOService.getInstance().saveEntity(client);*/
                }
            } else {
                log(synchDate + "Полученное изменение из ЕМП не удалось связать: " + logStr.toString());
            }
            changeSequence = e.getChangeSequence();
        }

        log(synchDate + "Обновление очереди до " + changeSequence);
        RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_EMP_CHANGE_SEQUENCE, changeSequence + 1);
        if (response.getResult().isHasMoreEntries()) {
            log(synchDate + "Изменения в ЕМП обработаны не до конца, запрос будет выполнен повторно");
            RuntimeContext.getAppContext().getBean(EMPProcessor.class).runReceiveUpdates();
        }
        //  Обновляем изменившуюся статистику
        //saveEMPStatistics(statistics);
    }

    protected void bindClient(StoragePortType storage, ru.axetta.ecafe.processor.core.persistence.Client client,
            String synchDate/*, EMPStatistics statistics*/) throws EMPException {
        if (bindThrowSelect(storage, client, synchDate)) {
            /*statistics.addBinded();
            statistics.removeNotBinded();*/
        } else if (bindThrowAdd(storage, client, synchDate)) {
            /*statistics.addWaitBinding();
            statistics.removeNotBinded();*/
        }
    }

    protected boolean bindThrowSelect(StoragePortType storage, ru.axetta.ecafe.processor.core.persistence.Client client,
            String synchDate) throws EMPException {
        log(synchDate + "Попытка связать клиента [" + client.getIdOfClient() + "] " + client.getMobile()
                + " с использованием поиска по телефону");
        //  execute reqeuest
        SelectEntriesRequest request = buildSelectEntryParams(client.getMobile());
        SelectEntriesResponse response = storage.selectEntries(request);
        if (response==null) {
            log(synchDate + "Получен ответ: null");
            return false;
        }
        log(synchDate + "Получен ответ: " + response.getErrorCode() + ": " + response.getErrorMessage() + ", записей: "
                + ((response.getResult()==null || response.getResult().getEntry()==null)?"null":response.getResult().getEntry().size()));
        if (response.getErrorCode() == EMP_ERROR_CODE_NOTHING_FOUND || response.getResult()==null) {
            return false;
        }
        if (response.getErrorCode() != 0) {
            List<Client> clients = DAOService.getInstance().getClientsListByMobilePhone(client.getMobile());
            String idsList = getClientIdsAsString(clients);
            String newSsoid = String.format("E:[%s]", response.getErrorCode());
            log(synchDate + "Произошла ошибка при попытке поиска клиента в ЕМП с телефоном [SSOID: " + client.getMobile() +
                    "]: " + response.getErrorMessage() + ". Всем клиентам " + clients.size() + " [" + idsList +
                    "] будут обновлены следующие параметры: {SSOID: " + newSsoid + "}");
            for(Client cl : clients) {
                cl.setSsoid(newSsoid);
                DAOService.getInstance().saveEntity(cl);
            }
            throw new EMPException(response.getErrorCode(), response.getErrorMessage());
        }

        //  parse response entries
        List<Entry> entries = response.getResult().getEntry();
        if (entries.size() == 0) {
            log(synchDate + "Клиент [" + client.getIdOfClient() + "] " + client.getMobile() + " не найден по телефону");
            return false;
        }
        if (entries.size() > 1) {
            log(synchDate + "Внимание! Больше 1 записи в каталоге по клиенту с телефоном [" + client.getIdOfClient()
                    + "] " + client.getMobile());
        }
        Entry e = entries.get(0);
        boolean found = false;
        ///
        String newSsoid = SSOID_REGISTERED_AND_WAITING_FOR_DATA;
        String newEmail = null;
        List<EntryAttribute> attributes = e.getAttribute();
        for (EntryAttribute attr : attributes) {
            if (attr.getName().equals(ATTRIBUTE_SSOID_NAME) &&
                    attr.getValue() != null && attr.getValue().size() > 0 &&
                    attr.getValue().get(0) != null && ((Element) attr.getValue().get(0)).getFirstChild() != null
                    && !attr.getValue().get(0).equals(client.getSsoid())) {
                try {
                    String val = ((Element) attr.getValue().get(0)).getFirstChild().getTextContent();
                    //client.setSsoid(val);
                    newSsoid = val;
                    found = true;
                } catch (Exception e1) {
                    logger.error("Failed to process existing object", e1);
                    throw new EMPException(e1);
                }
            }
            if (attr.getName().equals(ATTRIBUTE_EMAIL_NAME) &&
                    attr.getValue() != null && attr.getValue().size() > 0 &&
                    attr.getValue().get(0) != null && ((Element) attr.getValue().get(0)).getFirstChild() != null
                    && !attr.getValue().get(0).equals(client.getEmail())) {
                try {
                    String val = ((Element) attr.getValue().get(0)).getFirstChild().getTextContent();
                    //client.setEmail(val);
                    newEmail = val;
                    found = true;
                } catch (Exception e1) {
                    logger.error("Failed to process existing object");
                    throw new EMPException(e1);
                }
            }
        }

        List<Client> clients = DAOService.getInstance().getClientsListByMobilePhone(client.getMobile());
        String idsList = getClientIdsAsString(clients);
        log(synchDate + "С телефоном [SSOID: " + client.getMobile() + "] в ИС ПП найдено " + clients.size() + " клиентов [" +
            idsList + "]. Для всех них будут обновлены следующие параметры: {Email: " + newEmail + "}, {SSOID: " + newSsoid + "}");
        for(Client cl : clients) {
            cl.setSsoid(newSsoid);
            cl.setEmail(newEmail);
            DAOService.getInstance().saveEntity(cl);
        }
        /*log(synchDate + "Клиент [" + client.getIdOfClient() + "] " + client.getMobile()
                + " найден по телефону и обновлен. {Email: " + client.getEmail() + "}, {SSOID: " + client.getSsoid()
                + "}");
        DAOService.getInstance().saveEntity(client);*/
        return true;
    }

    protected String getClientIdsAsString(List<Client> clients) {
        StringBuilder idsList = new StringBuilder();
        for(Client cl : clients) {
            if(idsList.length() > 0) {
                idsList.append(", ");
            }
            idsList.append(cl.getIdOfClient());
        }
        return idsList.toString();
    }

    protected boolean bindThrowAdd(StoragePortType storage, ru.axetta.ecafe.processor.core.persistence.Client client,
            String synchDate) throws EMPException {
        //  execute reqeuest
        log(synchDate + "Отправка запроса на регистрацию клиента [" + client.getIdOfClient() + "] " + client
                .getMobile());
        AddEntriesRequest request = buildAddEntryParams(client);
        AddEntriesResponse response = storage.addEntries(request);
        if (response.getErrorCode() != 0) {
            List<Client> clients = DAOService.getInstance().getClientsListByMobilePhone(client.getMobile());
            String idsList = getClientIdsAsString(clients);
            String newSsoid = String.format("E:[%s]", response.getErrorCode());
            log(synchDate + "Произошла ошибка при попытке добавления клиента в ЕМП с телефоном [SSOID: " + client.getMobile() +
                    "]: " + response.getErrorMessage() + ". Всем клиентам " + clients.size() + " [" + idsList +
                    "] будут обновлены следующие параметры: {SSOID: " + newSsoid + "}");
            for(Client cl : clients) {
                cl.setSsoid(newSsoid);
                DAOService.getInstance().saveEntity(cl);
            }
            throw new EMPException(response.getErrorCode(), response.getErrorMessage());
        }

        if (response.getResult().getAffected().intValue() > 0) {
            List<Client> clients = DAOService.getInstance().getClientsListByMobilePhone(client.getMobile());
            String idsList = getClientIdsAsString(clients);
            log(synchDate + "Запрос выполнен, найдено " + clients.size() + " клиентов с телефоном " + client.getMobile() +
                " [" + idsList + "] установлено {SSOID: -1}");
            for(Client cl : clients) {
                cl.setSsoid(SSOID_REGISTERED_AND_WAITING_FOR_DATA);
                DAOService.getInstance().saveEntity(cl);
            }
            /*client.setSsoid(SSOID_REGISTERED_AND_WAITING_FOR_DATA);
            DAOService.getInstance().saveEntity(client);*/
            return true;
        }
        log(synchDate + "Не удалось зарегистрировать клиента [" + client.getIdOfClient() + "] " + client.getMobile() + ", либо клиент уже зарегистрирован в ЕМП");
        //throw new EMPException("Failed to make registration request");
        return true;
    }

    protected AddEntriesRequest buildAddEntryParams(ru.axetta.ecafe.processor.core.persistence.Client client) {
        AddEntriesRequest request = new AddEntriesRequest();
        //  base
        request.setToken(getConfigToken());
        //request.setCatalogOwner("System");
        request.setCatalogName(getConfigCatalogName());

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
        String[] emptyParams = new String[]{"SURNAME", "NAME", "PATRONYMIC"};
        String[] nullParams = new String[]{
                "SMS_SEND_START", "SMS_SEND_STOP", "SMS_SEND_EXCLUDE_DAYS", "EMAIL_SEND_START", "EMAIL_SEND_STOP",
                "EMAIL_SEND_EXCLUDE_DAYS", "PUSH_SEND_START", "PUSH_SEND_STOP", "PUSH_SEND_EXCLUDE_DAYS"};
        for (String p : emptyParams) {
            EntryAttribute paramId = new EntryAttribute();
            paramId.setName(p);
            paramId.getValue().add("");
            entry.getAttribute().add(paramId);
        }
        for (String p : nullParams) {
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
        request.setToken(getConfigToken());
        //request.setCatalogOwner("System");
        request.setCatalogName(getConfigCatalogName());

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
        request.setToken(getConfigToken());
        //request.setCatalogOwner("System");
        request.setCatalogName(getConfigCatalogName());

        //  paging
        Paging paging = new Paging();
        paging.setNumber(new BigInteger("1"));
        paging.setSize(new BigInteger("100"));
        request.setPaging(paging);

        request.setChangeSequence(changeSequence);

        return request;
    }

    protected StoragePortType createStorageController() {
        if (storageService != null) {
            return storageService;
        }
        StoragePortType controller = null;
        try {
            StorageService service = new StorageService(new URL(getConfigStorageServiceUrl()),
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


    public void log(String str) {
        /*if (RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_MSK_NSI_LOG)) {
            logger.info(str);
        }*/
        if (!getConfigLogging()) {
            return;
        }

        logger.info(str);
    }

    public static void addEntryToLogString(ReceiveDataChangesResponse.Result.Entry.Identifier id, StringBuilder str) {
        addEntryToLogString(id.getName(), id.getValue(), str);
    }

    public static void addEntryToLogString(ReceiveDataChangesResponse.Result.Entry.Attribute attr, StringBuilder str) {
        Object val = null;
        if (attr.getValue() != null && attr.getValue().size() > 0 && attr.getValue().get(0) != null) {
            val = attr.getValue().get(0);
        }
        addEntryToLogString(attr.getName(), val, str);
    }

    protected static void addEntryToLogString(String name, Object val, StringBuilder str) {
        String valStr = null;
        if (val != null && val instanceof Element) {
            Element e = ((Element) val);
            if (e != null && e.getFirstChild() != null) {
                valStr = e.getFirstChild().getTextContent();
            }
        }
        if (valStr == null) {
            valStr = "-NULL-";
        }

        if (str.length() > 0) {
            str.append(",");
        }
        str.append(String.format("{%s: %s}", name, valStr));
    }

    protected void logRequest(SendSubscriptionStreamEventsRequestType request) {
        if (!getConfigLogging()) {
            return;
        }

        try {
            if(eventsRequestContext == null) {
                eventsRequestContext = JAXBContext.newInstance("generated.emp_events");
            }
            logRequest(eventsRequestContext, request);
        } catch (Exception e) {
        }
    }

    protected void logRequest(ReceiveDataChangesRequest request) {
        if (!getConfigLogging()) {
            return;
        }

        try {
            if(dataChangesRequestContext == null) {
                dataChangesRequestContext = JAXBContext.newInstance(ReceiveDataChangesRequest.class);
            }
            logRequest(dataChangesRequestContext, request);
        } catch (Exception e) {
        }
    }

    protected void logRequest(JAXBContext jaxbContext, Object obj) {
        if (!getConfigLogging()) {
            return;
        }

        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            FileWriter fw = new FileWriter(EMP_OUTPUT_FILE);
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

        runtimeContext
                .setOptionValueWithSave(Option.OPTION_EMP_NOT_BINDED_CLIENTS_COUNT, statistics.getNotBindedCount());
        runtimeContext
                .setOptionValueWithSave(Option.OPTION_EMP_BIND_WAITING_CLIENTS_COUNT, statistics.getWaitBindingCount());
        runtimeContext.setOptionValueWithSave(Option.OPTION_EMP_BINDED_CLIENTS_COUNT, statistics.getBindedCount());
    }

    public static class EMPStatistics {

        protected long notBindedCount;
        protected long waitBindingCount;
        protected long bindedCount;
        protected long bindingErrors;

        public EMPStatistics() {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            notBindedCount = runtimeContext.getOptionValueLong(Option.OPTION_EMP_NOT_BINDED_CLIENTS_COUNT);
            waitBindingCount = runtimeContext.getOptionValueLong(Option.OPTION_EMP_BIND_WAITING_CLIENTS_COUNT);
            bindedCount = runtimeContext.getOptionValueLong(Option.OPTION_EMP_BINDED_CLIENTS_COUNT);
        }

        public long getBindingErrors() {
            return bindingErrors;
        }

        public void setBindingErrors(long bindingErrors) {
            this.bindingErrors = bindingErrors;
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