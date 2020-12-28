/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp;

import generated.emp_events.SendSubscriptionStreamEventsRequestType;
import generated.emp_storage.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientsMobileHistory;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.SecurityJournalProcess;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.FileWriter;
import java.math.BigInteger;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    public static final String SSOID_FAILED_TO_REGISTER = "-2";
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
        return catalogName=RuntimeContext.getInstance().getConfigProperties().getProperty(
                CONFIG_PARAM_BASE + ".catalogName", "SYS666254CAT0000000SUBSCRIPTIONS");
    }
    protected String getConfigStorageServiceUrl() {
        if (storageServiceUrl!=null) return storageServiceUrl;
        return storageServiceUrl=RuntimeContext.getInstance().getConfigProperties().getProperty(
                CONFIG_PARAM_BASE + ".storageUrl", "http://api.uat.emp.msk.ru:8090/ws/storage/?wsdl");
    }
    protected String getConfigSyncServiceNode() {
        if (syncServiceNode!=null) return syncServiceNode;
        return syncServiceNode=RuntimeContext.getInstance().getConfigProperties().getProperty(
                CONFIG_PARAM_BASE + ".syncServiceNode", "1");
    }
    protected int getConfigPackageSize() {
        if (packageSize!=null) return packageSize;
        return packageSize=Integer.parseInt(RuntimeContext.getInstance().getConfigProperties()
                .getProperty(CONFIG_PARAM_BASE + ".packageSize", "100"));
    }
    protected Boolean getConfigLogging() {
        if (logging!=null) return logging;
        return logging=Boolean.parseBoolean(
                RuntimeContext.getInstance().getConfigProperties().getProperty(CONFIG_PARAM_BASE + ".logging", "false"));
    }
    protected long getConfigStatsLifetime() {
        if (statsLifetime!=null) return statsLifetime;
        return statsLifetime=Long.parseLong(RuntimeContext.getInstance().getConfigProperties().getProperty(
                CONFIG_PARAM_BASE + ".statsLifetime", "600000"));
    }
    
    public boolean isAllowed() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
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
        long l = System.currentTimeMillis();
        ClientsMobileHistory clientsMobileHistory =
                new ClientsMobileHistory("Получение данных из ЕМП по расписанию");
        clientsMobileHistory.setShowing("ЕМП");
        RuntimeContext.getAppContext().getBean(EMPProcessor.class).runBindClients();
        RuntimeContext.getAppContext().getBean(EMPProcessor.class).runReceiveUpdates(clientsMobileHistory);
        l = System.currentTimeMillis() - l;
        if(l > 50000){
            logger.warn("EMPProcessor time:" +  l);
        }
    }

    public void runBindClients() throws EMPException {
        if (!isAllowed()) {
            return;
        }
        SecurityJournalProcess process = SecurityJournalProcess.createJournalRecordStart(
                SecurityJournalProcess.EventType.EMP_BIND_CLIENTS, new Date());
        process.saveWithSuccess(true);
        boolean isSuccessEnd = true;
        boolean resultProcess;
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
            SecurityJournalProcess processEnd = SecurityJournalProcess.createJournalRecordEnd(SecurityJournalProcess.EventType.EMP_BIND_CLIENTS, new Date());
            processEnd.saveWithSuccess(false);
            throw new EMPException("Failed to create connection with EMP web service");
        }
        for (ru.axetta.ecafe.processor.core.persistence.Client c : notBindedClients) {
            try {
                resultProcess = bindClient(storage, c, synchDate/*, statistics*/);
                isSuccessEnd = isSuccessEnd && resultProcess;
            } catch (EMPException empe) {
                isSuccessEnd = false;
                logger.error(String.format("Failed to parse client: [code=%s] %s", empe.getCode(), empe.getError()),
                        empe);
            }
        }
        SecurityJournalProcess processEnd = SecurityJournalProcess.createJournalRecordEnd(SecurityJournalProcess.EventType.EMP_BIND_CLIENTS, new Date());
        processEnd.saveWithSuccess(isSuccessEnd); //todo дописать по примеру рнипа дальше
        //  Обновляем изменившуюся статистику
        //saveEMPStatistics(statistics);
    }

    final String[] UPDATE_ATTRS=new String[]{ATTRIBUTE_SSOID_NAME, ATTRIBUTE_EMAIL_NAME, ATTRIBUTE_EMAIL_SEND, ATTRIBUTE_SMS_SEND, ATTRIBUTE_PUSH_SEND};
    public void runReceiveUpdates(ClientsMobileHistory clientsMobileHistory) throws EMPException {
        if (!isAllowed()) {
            return;
        }
        SecurityJournalProcess process = SecurityJournalProcess.createJournalRecordStart(
                SecurityJournalProcess.EventType.EMP_RECEIVE_UPDATES, new Date());
        process.saveWithSuccess(true);
        //  Загружаем статистику
        //EMPStatistics statistics = loadEMPStatistics();
        //  Вспомогательные значения
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        String synchDate = "[Получение изменений из ЕМП " + date + "]: ";

        //  Загрузка клиентов для связки
        long changeSequence = RuntimeContext.getInstance().getOptionValueLong(Option.OPTION_EMP_CHANGE_SEQUENCE);//750
        StoragePortType storage = createStorageController();
        if (storage == null) {
            SecurityJournalProcess processEnd = SecurityJournalProcess.createJournalRecordEnd(SecurityJournalProcess.EventType.EMP_RECEIVE_UPDATES, new Date());
            processEnd.saveWithSuccess(false);
            throw new EMPException("Failed to create connection with EMP web service");
        }
        ReceiveDataChangesRequest request = buildReceiveEntryParams(changeSequence);
        logRequest(request);
        ReceiveDataChangesResponse response = storage.receiveDataChanges(request);
        if (response.getErrorCode() != 0) {
            SecurityJournalProcess processEnd = SecurityJournalProcess.createJournalRecordEnd(SecurityJournalProcess.EventType.EMP_RECEIVE_UPDATES, new Date());
            processEnd.saveWithSuccess(false);
            logger.error(String.format("Failed to receive updates: [code=%s] %s", response.getErrorCode(),
                    response.getErrorMessage()));
            return;
        }

        List<ReceiveDataChangesResponse.Result.Entry> entries = response.getResult().getEntry();
        if (entries.size() < 1) {
            SecurityJournalProcess processEnd = SecurityJournalProcess.createJournalRecordEnd(SecurityJournalProcess.EventType.EMP_RECEIVE_UPDATES, new Date());
            processEnd.saveWithSuccess(true);
            log(synchDate + "Новых изменений по очереди " + changeSequence + " в ЕМП нет");
            return;
        }

        log(synchDate + "Поступило " + entries.size() + " изменений из ЕМП по очереди " + changeSequence);
        for (ReceiveDataChangesResponse.Result.Entry e : entries) {
            List<ReceiveDataChangesResponse.Result.Entry.Attribute> attributes = e.getAttribute();
            List<ReceiveDataChangesResponse.Result.Entry.Identifier> identifiers = e.getIdentifier();
            String ruleId = "";
            StringBuilder logStr = new StringBuilder();
            ////
            String newSsoid = "";
            String newEmail = null;
            String newNotifyViaEmail = null;
            String newNotifyViaSMS = null;
            String newNotifyViaPUSH = null;
            String newMsisdn = null;
            String oldMsisdn = null;
            ////
            for (ReceiveDataChangesResponse.Result.Entry.Attribute attr : attributes) {
                for (String attrName : UPDATE_ATTRS) {
                    if (!StringUtils.isBlank(attr.getName()) &&
                            attr.getName().equals(attrName) &&
                            attr.getValue() != null && attr.getValue().size() > 0 && attr.getValue().get(0) != null) {
                        try {
                            String v = ((Element) attr.getValue().get(0)).getFirstChild().getTextContent();
                            if (attrName.equals(ATTRIBUTE_SSOID_NAME)) newSsoid = v;
                            else if (attrName.equals(ATTRIBUTE_EMAIL_NAME)) newEmail = v;
                            else if (attrName.equals(ATTRIBUTE_EMAIL_SEND)) newNotifyViaEmail = v;
                            else if (attrName.equals(ATTRIBUTE_SMS_SEND)) newNotifyViaSMS = v;
                            else if (attrName.equals(ATTRIBUTE_PUSH_SEND)) newNotifyViaPUSH = v;
                        } catch (Exception e1) {
                            logger.error("Failed to parse " + attrName + " value");
                        }
                    }
                }
                oldMsisdn = getOldMsisdn(oldMsisdn, attr);
                newMsisdn = getNewMsisdn(newMsisdn, attr);
                addEntryToLogString(attr, logStr);
            }
            for (ReceiveDataChangesResponse.Result.Entry.Identifier id : identifiers) {
                addEntryToLogString(id, logStr);
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
            }
            if (!StringUtils.isBlank(ruleId) && NumberUtils.isNumber(ruleId) && StringUtils.isBlank(newMsisdn) && StringUtils.isBlank(oldMsisdn)) {
                ru.axetta.ecafe.processor.core.persistence.Client client = DAOService.getInstance()
                        .getClientByContractId(NumberUtils.toLong(ruleId));
                if (client != null) {
                    if(client.getMobile() == null || StringUtils.isBlank(client.getMobile())) {
                        client.setSsoid("E: no mobile");
                        DAOService.getInstance().saveEntity(client);
                        changeSequence = e.getChangeSequence();
                        continue;
                    }
                    updateByMobile(client.getMobile(), ruleId, synchDate, logStr, newSsoid, newEmail,
                            newNotifyViaEmail, newNotifyViaSMS, newNotifyViaPUSH, null, clientsMobileHistory);
                } else {
                    log(synchDate + "Полученное изменение из ЕМП не удалось связать (не найден клиент с л/c: "+ruleId+"): " + logStr.toString());
                }
            } else if (!StringUtils.isBlank(newMsisdn) || !StringUtils.isBlank(oldMsisdn)) {
                updateByMobile(newMsisdn, ruleId, synchDate, logStr, newSsoid, newEmail, newNotifyViaEmail,
                        newNotifyViaSMS, newNotifyViaPUSH, oldMsisdn, clientsMobileHistory);
            } else {
                log(synchDate + "Полученное изменение из ЕМП не удалось связать: " + logStr.toString());
            }
            changeSequence = e.getChangeSequence();
        }

        log(synchDate + "Обновление очереди до " + changeSequence);
        RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_EMP_CHANGE_SEQUENCE, changeSequence + 1);
        if (response.getResult().isHasMoreEntries()) {
            log(synchDate + "Изменения в ЕМП обработаны не до конца, запрос будет выполнен повторно");
            RuntimeContext.getAppContext().getBean(EMPProcessor.class).runReceiveUpdates(clientsMobileHistory);
        }
        SecurityJournalProcess processEnd = SecurityJournalProcess.createJournalRecordEnd(SecurityJournalProcess.EventType.EMP_RECEIVE_UPDATES, new Date());
        processEnd.saveWithSuccess(true);
        //  Обновляем изменившуюся статистику
        //saveEMPStatistics(statistics);
    }

    private void updateByMobile(String mobile, String ruleId, String synchDate, StringBuilder logStr, String newSsoid,
            String newEmail, String newNotifyViaEmail, String newNotifyViaSMS, String newNotifyViaPUSH, String oldMsisdn,
            ClientsMobileHistory clientsMobileHistory) {
        List<Client> clients = DAOService.getInstance().getClientsListByMobilePhone(oldMsisdn != null ? oldMsisdn : mobile);
        if (clients.size() > 0) {
            String idsList = getClientIdsAsString(clients);
            log(synchDate + "Поступили изменения из ЕМП {SSOID: " + newSsoid + "}, {№ Контракта: " + ruleId +
                    "}. Для всех " + clients.size() + " клиентов [" + idsList + "] с подпиской на телефон данного клиента [" +
                    mobile + "] применяются изменения: "+logStr.toString());
            for (Client cl : clients) {
                if (newSsoid!=null && !newSsoid.equals("")) cl.setSsoid(newSsoid);
                if (newEmail!=null) cl.setEmail(newEmail);
                if (newNotifyViaEmail!=null) cl.setNotifyViaEmail(newNotifyViaEmail.equalsIgnoreCase("true"));
                if (newNotifyViaSMS!=null) cl.setNotifyViaSMS(newNotifyViaSMS.equalsIgnoreCase("true"));
                if (newNotifyViaPUSH!=null) cl.setNotifyViaPUSH(newNotifyViaPUSH.equalsIgnoreCase("true"));
                if ((oldMsisdn != null) && (mobile != null)) {
                    cl.initClientMobileHistory(clientsMobileHistory);
                    cl.setMobile(mobile);
                }
                else if ((oldMsisdn != null) && (mobile == null)) {
                    cl.initClientMobileHistory(clientsMobileHistory);
                    cl.setMobile("");
                    //cl.setSsoid("");
                }
                DAOService.getInstance().saveEntity(cl);
            }
        } else {
            log(synchDate + "Полученное изменение из ЕМП не удалось связать (не найден клиент с телефоном: " + mobile + "): " + logStr.toString());
        }
    }

    private String getOldMsisdn(String oldMsisdn, ReceiveDataChangesResponse.Result.Entry.Attribute attr) {
        if (oldMsisdn == null) {
            return getPreviousValue(attr);
        } else {
            return oldMsisdn;
        }
    }

    private String getNewMsisdn(String newMsisdn, ReceiveDataChangesResponse.Result.Entry.Attribute attr) {
        if (newMsisdn == null) {
            return getValue(attr);
        } else {
            return newMsisdn;
        }
    }

    private String getPreviousValue(ReceiveDataChangesResponse.Result.Entry.Attribute attr) {
        try {
            if (!StringUtils.isBlank(attr.getName()) &&
                    attr.getName().equals(ATTRIBUTE_MOBILE_PHONE_NAME) &&
                    attr.getPrevious() != null && attr.getPrevious().size() > 0 && attr.getPrevious().get(0) != null) {
                return ((Element) attr.getPrevious().get(0)).getFirstChild().getTextContent();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private String getValue(ReceiveDataChangesResponse.Result.Entry.Attribute attr) {
        try {
            if (!StringUtils.isBlank(attr.getName()) &&
                    attr.getName().equals(ATTRIBUTE_MOBILE_PHONE_NAME) &&
                    attr.getValue() != null && attr.getValue().size() > 0 && attr.getValue().get(0) != null) {
                return ((Element) attr.getValue().get(0)).getFirstChild().getTextContent();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    protected Boolean bindClient(StoragePortType storage, ru.axetta.ecafe.processor.core.persistence.Client client,
            String synchDate/*, EMPStatistics statistics*/) throws EMPException {
        /*if (bindThrowSelect(storage, client, synchDate)) {
            / *statistics.addBinded();
            statistics.removeNotBinded();* /
        } else if (bindThrowAdd(storage, client, synchDate)) {
            / *statistics.addWaitBinding();
            statistics.removeNotBinded();* /
        }*/

        if (bindThrowSelectByPhone(storage, client, synchDate)) {
            return true;
        }

        if (!findAndDeleteThrowSelectByContractId(storage, client, synchDate)) {
            log(synchDate + "Попытка удаления клиента [" + client.getIdOfClient() + "] " + client.getMobile()
                    + " с использованием поиска по контракту завершилась неудачей (см. причину выше)");
        }
        return bindThrowAdd(storage, client, synchDate);
    }

    protected boolean bindThrowSelectByPhone(StoragePortType storage, ru.axetta.ecafe.processor.core.persistence.Client client,
            String synchDate) throws EMPException {
        log(synchDate + "Попытка связать клиента [" + client.getIdOfClient() + "] " + client.getMobile()
                + " с использованием поиска по телефону");
        //  execute reqeuest
        SelectEntriesRequest request = buildSelectByPhoneEntryParams(client);
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
        String newSsoid = null;
        String newEmail = null;
        String newNotifyViaEmail = null;
        String newNotifyViaSMS = null;
        String newNotifyViaPUSH = null;
        List<EntryAttribute> attributes = e.getAttribute();
        for (EntryAttribute attr : attributes) {
            if (attr.getName().equals(ATTRIBUTE_SSOID_NAME)) {
                newSsoid = getString(client, attr);
            }
            if (attr.getName().equals(ATTRIBUTE_EMAIL_NAME)) {
                newEmail = getString(client, attr);
            }
            if (attr.getName().equals(ATTRIBUTE_EMAIL_SEND)) {
                newNotifyViaEmail = getString(client, attr);
            }
            if (attr.getName().equals(ATTRIBUTE_SMS_SEND)) {
                newNotifyViaSMS = getString(client, attr);
            }
            if (attr.getName().equals(ATTRIBUTE_PUSH_SEND)) {
                newNotifyViaPUSH = getString(client, attr);
            }
        }
        if (newSsoid==null) newSsoid = SSOID_REGISTERED_AND_WAITING_FOR_DATA; // из ЕМП может прийти SSOID = null

        List<Client> clients = DAOService.getInstance().getClientsListByMobilePhone(client.getMobile());
        String idsList = getClientIdsAsString(clients);
        log(synchDate + "С телефоном [" + client.getMobile() + "] в ИС ПП найдено " + clients.size()
                + " клиентов [" + idsList + "]. Для полученного списка клиентов будут обновлены параметры: {Email: " + newEmail
                + "}, {SSOID: " + newSsoid + "}, {notifyViaEmail: " + newNotifyViaEmail + "}, {notifyViaSMS: "
                + newNotifyViaSMS + "}, {notifyViaPUSH: " + newNotifyViaPUSH + "}");
        for(Client cl : clients) {
            cl.setSsoid(newSsoid);
            cl.setEmail(newEmail);
            if (newNotifyViaEmail!=null) cl.setNotifyViaEmail(newNotifyViaEmail.equalsIgnoreCase("true"));
            if (newNotifyViaSMS!=null) cl.setNotifyViaSMS(newNotifyViaSMS.equalsIgnoreCase("true"));
            if (newNotifyViaPUSH!=null) cl.setNotifyViaPUSH(newNotifyViaPUSH.equalsIgnoreCase("true"));
            DAOService.getInstance().saveEntity(cl);
        }
        /*log(synchDate + "Клиент [" + client.getIdOfClient() + "] " + client.getMobile()
                + " найден по телефону и обновлен. {Email: " + client.getEmail() + "}, {SSOID: " + client.getSsoid()
                + "}");
        DAOService.getInstance().saveEntity(client);*/
        return true;
    }

    protected boolean findAndDeleteThrowSelectByContractId
                                (StoragePortType storage, ru.axetta.ecafe.processor.core.persistence.Client client,
                                String synchDate) throws EMPException {
        log(synchDate + "Попытка найти клиента [" + client.getIdOfClient() + "] " + client.getMobile()
                + " с использованием поиска по номеру контракта [" + client.getContractId() + "]");
        //  execute reqeuest
        SelectEntriesRequest request = buildSelectByContractEntryParams(client);
        SelectEntriesResponse response = storage.selectEntries(request);
        if (response==null) {
            log(synchDate + "Получен ответ: null");
            return false;
        }
        log(synchDate + "Получен ответ: " + response.getErrorCode() + ": " + response.getErrorMessage() + ", записей: "
                + ((response.getResult()==null || response.getResult().getEntry()==null)?"null":response.getResult().getEntry().size()));
        if (response.getErrorCode() == EMP_ERROR_CODE_NOTHING_FOUND || response.getResult()==null) {
            return true;
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
            log(synchDate + "Клиент [" + client.getIdOfClient() + "] " + client.getMobile() + " не найден по номеру контракта [" + client.getContractId() + "]");
            return true;
        }
        if (entries.size() > 1) {
            log(synchDate + "Внимание! Больше 1 записи в каталоге по клиенту с телефоном [" + client.getIdOfClient()
                    + "] " + client.getMobile() + " найден по номеру контракта [" + client.getContractId() + "] ");
        }


        log(synchDate + "Внимание! Осуществляем удаление клиента  с телефоном [" + client.getIdOfClient() + "] "
                + client.getMobile() + ", найденный по номеру контракта [" + client.getContractId() + "] ");
        DeleteEntriesRequest delRequest = buildDeleteByContractEntryParams(client);
        DeleteEntriesResponse delResponse = storage.deleteEntries(delRequest);
        if (delResponse==null) {
            log(synchDate + "Получен ответ: null");
            return false;
        }
        log(synchDate + "Получен ответ: " + delResponse.getErrorCode() + ": " + delResponse.getErrorMessage() + ", записей: " +
                (delResponse.getResult() == null ||
                 delResponse.getResult().getAffected() == null ? "-unknown-" : delResponse.getResult().getAffected()));
        if(delResponse.getResult()==null) {
            log(synchDate + "Получен ответ при удалении: null");
            return false;
        }
        BigInteger affected = delResponse.getResult().getAffected();
        if(affected == null) {
            log(synchDate + "Получено пустое количество при удалении: null");
            return false;
        }
        if(affected.longValue() > 1) {
            log(synchDate + "Внимание! При попытке удаления записи клиента с телефоном [" + client.getIdOfClient() + "] "
                + client.getMobile() + ", найденный по номеру контракта [" + client.getContractId() + "] было удалено БОЛЕЕ 1 записи!");
        }
        log(synchDate + "Удаление записи клиента с телефоном [" + client.getIdOfClient() + "] "
                + client.getMobile() + ", найденный по номеру контракта [" + client.getContractId() + "] выполнено успешно.");
        return true;
    }

    private String getString(Client client, EntryAttribute attr) throws EMPException {
        String newValue = null;
        if (attr.getValue() != null &&
                attr.getValue().size() > 0 &&
                attr.getValue().get(0) != null &&
                ((Element) attr.getValue().get(0)).getFirstChild() != null &&
                !attr.getValue().get(0).equals(Boolean.toString(client.isNotifyViaSMS()))) {
            try {
                newValue = ((Element) attr.getValue().get(0)).getFirstChild().getTextContent();
            } catch (Exception e1) {
                logger.error("Failed to process existing object");
                throw new EMPException(e1);
            }
        }
        return newValue;
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
            log(synchDate + "Произошла ошибка при попытке добавления клиента в ЕМП с телефоном [" + client.getMobile() +
                    "]: " + response.getErrorMessage() + ". Всем клиентам " + clients.size() + " [" + idsList +
                    "] будут обновлены следующие параметры: {SSOID: " + newSsoid + "}");
            updateClientsSsoid(clients, newSsoid);
            throw new EMPException(response.getErrorCode(), response.getErrorMessage());
        }

        if (response.getResult().getAffected().intValue() > 0) {
            List<Client> clients = DAOService.getInstance().getClientsListByMobilePhone(client.getMobile());
            String idsList = getClientIdsAsString(clients);
            log(synchDate + "Запрос выполнен, найдено " + clients.size() + " клиентов с телефоном " + client.getMobile() +
                " [" + idsList + "] установлено {SSOID: " + SSOID_REGISTERED_AND_WAITING_FOR_DATA + "}");
            updateClientsSsoid(clients, SSOID_REGISTERED_AND_WAITING_FOR_DATA);
            /*client.setSsoid(SSOID_REGISTERED_AND_WAITING_FOR_DATA);
            DAOService.getInstance().saveEntity(client);*/
            return true;
        }

        log(synchDate + "Не удалось зарегистрировать клиента [" + client.getIdOfClient() + "] " + client.getMobile() + ", либо клиент уже зарегистрирован в ЕМП");
        List<Client> clients = DAOService.getInstance().getClientsListByMobilePhone(client.getMobile());
        String idsList = getClientIdsAsString(clients);
        log(synchDate + "Всем клиентам (" + clients.size() + ") с телефоном [" + client.getMobile() + "] [" + idsList +
                "] будут обновлены следующие параметры: {SSOID: " + SSOID_FAILED_TO_REGISTER + "}");
        updateClientsSsoid(clients, SSOID_FAILED_TO_REGISTER);
        return false;
    }

    protected void updateClientsSsoid(List<Client> clients, String newSsoid) {
        for(Client cl : clients) {
            cl.setSsoid(newSsoid);
            DAOService.getInstance().saveEntity(cl);
        }
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
        //email.getValue().add(client.getEmail());  TODO: is it correct??
        email.getValue().add("");
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
        smsSend.getValue().add(client.isNotifyViaSMS());
        entry.getAttribute().add(smsSend);
        EntryAttribute pushSend = new EntryAttribute();
        pushSend.setName(ATTRIBUTE_PUSH_SEND);
        pushSend.getValue().add(client.isNotifyViaPUSH());
        entry.getAttribute().add(pushSend);
        EntryAttribute emailSend = new EntryAttribute();
        emailSend.setName(ATTRIBUTE_EMAIL_SEND);
        emailSend.getValue().add(client.isNotifyViaEmail());
        entry.getAttribute().add(emailSend);
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

    protected SelectEntriesRequest buildSelectByPhoneEntryParams(Client client) {
        return buildSelectEntryParams(client.getMobile(), null);
    }

    protected SelectEntriesRequest buildSelectByContractEntryParams(Client client) {
        return buildSelectEntryParams(null, client.getContractId());
    }

    protected SelectEntriesRequest buildSelectEntryParams(String clientMobile, Long clientContractId) {
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
        addCriteries(criteries, clientMobile, clientContractId);

        return request;
    }

    protected DeleteEntriesRequest buildDeleteByPhoneEntryParams(Client client) {
        return buildDeleteEntryParams(client.getMobile(), null);
    }

    protected DeleteEntriesRequest buildDeleteByContractEntryParams(Client client) {
        return buildDeleteEntryParams(null, client.getContractId());
    }

    protected DeleteEntriesRequest buildDeleteEntryParams(String clientMobile, Long clientContractId) {
        DeleteEntriesRequest request = new DeleteEntriesRequest();
        //  base
        request.setToken(getConfigToken());
        //request.setCatalogOwner("System");
        request.setCatalogName(getConfigCatalogName());

        //  criterions
        List<EntryAttribute> criteries = request.getCriteria();
        addCriteries(criteries, clientMobile, clientContractId);

        return request;
    }

    private void addCriteries(List<EntryAttribute> criteries, String clientMobile, Long clientContractId) {
        if(criteries == null) {
            return;
        }
        if(clientMobile != null && clientMobile.length() > 0) {
            EntryAttribute msisdn = new EntryAttribute();
            msisdn.setName(ATTRIBUTE_MOBILE_PHONE_NAME);
            msisdn.getValue().add(clientMobile);
            criteries.add(msisdn);
        }
        if (clientContractId != null) {
            EntryAttribute msisdn = new EntryAttribute();
            msisdn.setName(ATTRIBUTE_RULE_ID);
            msisdn.getValue().add(clientContractId);
            criteries.add(msisdn);
        }
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

    @Async
    public void updateNotificationParams(Long contractId) {
        log("Получен запрос на изменение способа уведомления клиента с № контракта " + contractId);
        Client client = DAOService.getInstance().getClientByContractId(contractId);
        //Client client = ClientReadOnlyRepository.getInstance().findByContractId(contractId);
        if (client != null){
            updateNotificationParams(client);
        }
    }

    @Async
    public void updateNotificationParams(Client client) {

        StoragePortType storage = createStorageController();
        UpdateEntriesRequest request = new UpdateEntriesRequest();

        request.setToken(getConfigToken());

        request.setCatalogName(getConfigCatalogName());

        List<EntryAttribute> criteria = request.getCriteria();

        if (client.getSsoid() != null && !client.getSsoid().equals("") &&
            !client.getSsoid().equals(SSOID_REGISTERED_AND_WAITING_FOR_DATA) && !client.getSsoid().equals(SSOID_FAILED_TO_REGISTER)) {
            log("Клиент уже привязан к ЕМП, обновление через SSOID " + client.getSsoid());
            EntryAttribute ssoid = new EntryAttribute();
            ssoid.setName(ATTRIBUTE_SSOID_NAME);
            ssoid.getValue().add(client.getSsoid());
            criteria.add(ssoid);
        } else {
            log("Клиент не привязан к ЕМП, обновление через мобильный телефон " + client.getMobile());
            EntryAttribute msisdn = new EntryAttribute();
            msisdn.setName(ATTRIBUTE_MOBILE_PHONE_NAME);
            msisdn.getValue().add(client.getMobile());
            criteria.add(msisdn);
        }

        List<EntryAttribute> attribute = request.getAttribute();

        EntryAttribute smsSend = new EntryAttribute();
        smsSend.setName(ATTRIBUTE_SMS_SEND);
        smsSend.getValue().add(client.isNotifyViaSMS());
        attribute.add(smsSend);

        EntryAttribute pushSend = new EntryAttribute();
        pushSend.setName(ATTRIBUTE_PUSH_SEND);
        pushSend.getValue().add(client.isNotifyViaPUSH());
        attribute.add(pushSend);

        EntryAttribute emailSend = new EntryAttribute();
        emailSend.setName(ATTRIBUTE_EMAIL_SEND);
        emailSend.getValue().add(client.isNotifyViaEmail());
        attribute.add(emailSend);

        String flags = String.format("notifyViaSms=%s, notifyViaPush=%s, notifyViaEmail=%s",
                client.isNotifyViaSMS(), client.isNotifyViaPUSH(), client.isNotifyViaEmail());
        log(String.format("Отправка запроса на изменение информирования клиента...Телефон: %s. Флаги к отправке: %s",
                client.getMobile() == null ? "" : client.getMobile(), flags));
        UpdateEntriesResponse response = storage.updateEntries(request);
        if (response.getErrorCode() != 0) {
            logger.error(String.format("Failed to proceed updates: [code=%s] %s", response.getErrorCode(),
                    response.getErrorMessage()));
            return;
        }

        log("на изменение информирования: " + response.getErrorCode() + ": " + response.getErrorMessage()
                + ", записей: " +
                (response.getResult() == null || response.getResult().getAffected() == null ? "-unknown-"
                        : response.getResult().getAffected()));
    }

    public HashMap<String, List<String>> getEntryAttributesByMobile(String clientMobileString) {

        HashMap<String, List<String>> resultMap = new HashMap<String, List<String>>();

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession;
        Client client = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            Criteria criteria = persistenceSession.createCriteria(Client.class);
            criteria.add(Restrictions.eq("mobile", clientMobileString));
            List resultList = criteria.list();
            client = (Client) resultList.get(0);
        } catch (Exception e) {
            logger.warn("Failed to get Client from persistence for {contractId :" + clientMobileString + "\"} : ", e);
            return resultMap;
        }

        StoragePortType storage = createStorageController();
        SelectEntriesRequest request = new SelectEntriesRequest();

        request.setToken(getConfigToken());

        request.setCatalogName(getConfigCatalogName());

        List<EntryAttribute> criteria = request.getCriteria();

        String ssoidString = null;
        try {
            ssoidString = client.getSsoid();
        } catch (Exception e) {}

        if (client != null && ssoidString != null && !ssoidString.equals("") &&
            !ssoidString.equals(SSOID_REGISTERED_AND_WAITING_FOR_DATA) && !ssoidString.equals(SSOID_REGISTERED_AND_WAITING_FOR_DATA)) {
            EntryAttribute ssoid = new EntryAttribute();
            ssoid.setName(ATTRIBUTE_SSOID_NAME);
            ssoid.getValue().add(client.getSsoid());
            criteria.add(ssoid);
        } else {
            EntryAttribute msisdn = new EntryAttribute();
            msisdn.setName(ATTRIBUTE_MOBILE_PHONE_NAME);
            msisdn.getValue().add(clientMobileString);
            criteria.add(msisdn);
        }

        if (storage != null) {
            SelectEntriesResponse response = storage.selectEntries(request);
            if (response.getResult() != null) {
                List<Entry> entries = response.getResult().getEntry();
                for (Entry entry : entries) {
                    List<EntryAttribute> entryAttributeList = entry.getAttribute();
                    for (EntryAttribute entryAttribute : entryAttributeList) {
                        List<String> attributeList = resultMap.get(entryAttribute.getName());
                        if (attributeList == null) {
                            attributeList = new ArrayList<String>();
                        }
                        if (entryAttribute.getValue().get(0) != null && ((Element) entryAttribute.getValue().get(0)).getFirstChild() != null) {
                            attributeList.add(((Element) entryAttribute.getValue().get(0)).getFirstChild().getTextContent());
                        }
                        resultMap.put(entryAttribute.getName(), attributeList);
                    }
                }
            }
        }

        return resultMap;
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