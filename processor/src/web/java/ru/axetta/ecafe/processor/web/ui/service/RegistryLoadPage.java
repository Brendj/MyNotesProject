/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils.findClientGroupByGroupNameAndIdOfOrg;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 17.08.16
 * Time: 10:37
 */

public class RegistryLoadPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(RegistryLoadPage.class);
    private static final int MAX_BATCH_SIZE = 50;

    public static class LineResult {

        private String lineNo;
        private final int resultCode;
        private final String message;
        private final Long idOfClient;

        public LineResult(String lineNo, int resultCode, String message, Long idOfClient) {
            this.lineNo = lineNo;
            this.resultCode = resultCode;
            this.message = message;
            this.idOfClient = idOfClient;
        }

        public String getLineNo() {
            return lineNo;
        }

        public void setLineNo(String lineNo) {
            this.lineNo = lineNo;
        }

        public int getResultCode() {
            return resultCode;
        }

        public String getMessage() {
            return message;
        }

        public Long getIdOfClient() {
            return idOfClient;
        }
    }

    private String path;
    private String firstFile;
    private String lastFile;
    private int parameters;
    private List<LineResult> lineResults = Collections.emptyList();
    private int lineResultsSize;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFirstFile() {
        return firstFile;
    }

    public void setFirstFile(String firstFile) {
        this.firstFile = firstFile;
    }

    public String getLastFile() {
        return lastFile;
    }

    public void setLastFile(String lastFile) {
        this.lastFile = lastFile;
    }

    public int getParameters() {
        return parameters;
    }

    public void setParameters(int parameters) {
        this.parameters = parameters;
    }

    public String getPageFilename() {
        return "service/load_registry";
    }

    public List<LineResult> getLineResults() {
        return lineResults;
    }

    public int getLineResultsSize() {
        return lineResultsSize;
    }

    public void setLineResultsSize(int lineResultsSize) {
        this.lineResultsSize = lineResultsSize;
    }

    public void fill(Session persistenceSession) throws Exception {
        // Nothing to do here
    }

    public void process() throws Exception {
        if(parameters == 1){
            process(1);
        } else if(parameters == 2){
            process(2);
        } else {
            printError("Выберите действие");
        }

    }

    public void process(int parameter) throws Exception {
        String path = this.path;
        File f = new File(path);
        if (!f.isDirectory()) {
            printError("Указанная директория не существует");
            return;
        }
        Long firstFile = 0L;
        Long lastFile = 0L;
        try{
            firstFile = Long.parseLong(this.firstFile);
            lastFile = Long.parseLong(this.lastFile);
        } catch (NumberFormatException e){
            printError("В названии файлов допускаются только числа");
            return;
        }

        List<LineResult> lineResults = new ArrayList<LineResult>();

        if(parameter == 1) {
            processClients(path, lastFile, firstFile, lineResults);
        }

        if(parameter == 2) {
            processGuardians(path, lastFile, firstFile, lineResults);
        }

        this.lineResults = lineResults;
        printMessage("Обработка параметров завершена");
    }

    private void processClients(String path, Long lastFile, Long firstFile,
            List<LineResult> lineResults) throws Exception {
        BufferedReader br = null;
        String line;
        for(int i = firstFile.intValue(); i <= lastFile; i++) {

            Session persistenceSession = null;
            Transaction persistenceTransaction = null;

            try {
                RuntimeContext runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();

                Long clientRegistryVersion = DAOUtils.updateClientRegistryVersionWithPessimisticLock(MAX_BATCH_SIZE);
                int count = 0;
                try {
                    String csvFile = path + i + ".csv";
                    br = new BufferedReader(new FileReader(csvFile));
                    int lineNo = 1;

                    while ((line = br.readLine()) != null) {

                        String currentLineNo = i + "/" + lineNo;
                        lineNo++;

                        String[] parameters = line.split(";");
                        if (parameters.length < 1) {
                            continue;
                        }
                        if (parameters[0].isEmpty()) {
                            continue;
                        }

                        Client client = DAOUtils.findClientByGuid(persistenceSession, parameters[2]);

                        if (client != null) {

                            String gender = parameters[6];
                            if (gender != null) {
                                if (gender.equalsIgnoreCase(Client.CLIENT_GENDER_NAMES[0])) {
                                    client.setGender(0);
                                } else if (gender.equalsIgnoreCase(Client.CLIENT_GENDER_NAMES[1])) {
                                    client.setGender(1);
                                } else {
                                    LineResult result = new LineResult(currentLineNo, 30, "Неверный формат пола", client.getIdOfClient());
                                    lineResults.add(result);
                                    continue;
                                }
                            }

                            String birthDate = parameters[7];
                            if (birthDate != null) {
                                try {
                                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                                    Date date = format.parse(birthDate);
                                    client.setBirthDate(date);
                                } catch (ParseException e) {
                                    LineResult result = new LineResult(currentLineNo, 20, "Неверный формат даты", client.getIdOfClient());
                                    lineResults.add(result);
                                    continue;
                                }
                            }

                            client.setClientRegistryVersion(clientRegistryVersion + count);
                            persistenceSession.update(client);
                            count++;
                            if(count == (MAX_BATCH_SIZE - 1)){
                                clientRegistryVersion = DAOUtils.updateClientRegistryVersionWithPessimisticLock(MAX_BATCH_SIZE);
                            }
                            LineResult result = new LineResult(currentLineNo, 0, "Данные успешно изменены", client.getIdOfClient());
                            lineResults.add(result);

                        } else {
                            LineResult result = new LineResult(currentLineNo, 10, "Клиент с GUID=" + parameters[2] + " не найден", null);
                            lineResults.add(result);
                        }
                        if (lineNo % 100 == 0) {
                            this.lineResultsSize = lineResults.size();
                        }
                    }
                } catch (FileNotFoundException ignore) {
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }

            this.lineResultsSize = lineResults.size();
        }
    }

    private synchronized void processGuardians(String path, Long lastFile, Long firstFile, List<LineResult> lineResults) throws Exception {
        BufferedReader br = null;
        String line;
        for(int i = firstFile.intValue(); i <= lastFile; i++) {

            try {
                String csvFile = path + i + ".csv";
                br = new BufferedReader(new FileReader(csvFile));
                int lineNo = 1;

                List<ClientItem> clientItems = new ArrayList<ClientItem>();

                while ((line = br.readLine()) != null) {
                    parseItem(line, clientItems, lineNo);
                    lineNo++;
                }

                formClientItems(clientItems);

                List<LineResult> lineResultsByFile = processClientItems(clientItems, i);
                lineResults.addAll(lineResultsByFile);

            } catch (FileNotFoundException ignore) {
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
            this.lineResultsSize = lineResults.size();
        }
    }

    private void formClientItems(List<ClientItem> clientItems) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Map<String, ClientItem> clientItemGuidMap = new HashMap<String, ClientItem>();
            for(ClientItem clientItem : clientItems){
                clientItemGuidMap.put(clientItem.getGuid(), clientItem);
            }

            Map<Long, ClientItem> clientItemIdMap = new HashMap<Long, ClientItem>();
            List<Object[]> clientDatas = findClients(persistenceSession, clientItemGuidMap.keySet());
            for(Object[] cd : clientDatas) {
                Long idOfClient = (Long)cd[0];
                String guid = (String)cd[1];
                boolean notifyViaEmail = (Boolean)cd[2];
                boolean notifyViaSMS = (Boolean)cd[3];
                boolean notifyViaPUSH = (Boolean)cd[4];
                Long idOfOrg = (Long)cd[5];
                ClientItem clientItem = clientItemGuidMap.get(guid);
                clientItem.setClientData(new ClientData(idOfClient, guid, notifyViaEmail, notifyViaSMS, notifyViaPUSH, idOfOrg));
                clientItemIdMap.put(idOfClient, clientItem);
            }

            Map<Long, List<ClientItem>> clientItemGuarIdMap = new HashMap<Long, List<ClientItem>>();
            List<Object[]> clientGuardianDatas = findClientGuardians(persistenceSession, clientItemIdMap.keySet());
            for(Object[] cgd : clientGuardianDatas) {
                Long idOfClientGuardian = (Long)cgd[0];
                Long idOfClient = (Long)cgd[1];
                Long idOfGuardian = (Long)cgd[2];
                ClientGuardianRelationType relation = (ClientGuardianRelationType)cgd[3];
                ClientItem clientItem = clientItemIdMap.get(idOfClient);
                clientItem.getGuardianDatas().add(new GuardianData(idOfClient, idOfClientGuardian,
                        idOfGuardian, relation));
                if(clientItemGuarIdMap.containsKey(idOfGuardian)){
                    clientItemGuarIdMap.get(idOfGuardian).add(clientItem);
                } else {
                    clientItemGuarIdMap.put(idOfGuardian, new ArrayList<ClientItem>());
                    clientItemGuarIdMap.get(idOfGuardian).add(clientItem);
                }
            }

            List<Object[]> guardianDatas = findGuardians(persistenceSession, clientItemGuarIdMap.keySet());
            for(Object[] gd : guardianDatas) {
                Long idOfGuardian = (Long)gd[0];
                String phone = (String)gd[1];
                String mobile = (String)gd[2];
                String email = (String)gd[3];
                String firstName = (String)gd[4];
                String surName = (String)gd[5];
                String secondName = (String)gd[6];
                List<ClientItem> clientItemList = clientItemGuarIdMap.get(idOfGuardian);
                for(ClientItem clientItem : clientItemList) {
                    GuardianData guardianData = clientItem.getGuardianData(idOfGuardian);
                    guardianData.setParameters(firstName, surName, secondName, phone, mobile, email);
                }
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

    }

    private List<LineResult> processClientItems(List<ClientItem> clientItems, int orgInt) throws Exception {

        Map<Integer, LineResult> lineResults = new TreeMap<Integer, LineResult>();
        Map<GuardianItem, GuardianData> updateMap = new HashMap<GuardianItem, GuardianData>();
        Map<GuardianItem, ClientItem> createMap = new HashMap<GuardianItem, ClientItem>();

        for(ClientItem clientItem : clientItems){
            if(clientItem.getClientData() == null){
                LineResult result = new LineResult(orgInt + "/" + clientItem.getCount(), 110, "Клиент с GUID=" +
                        clientItem.getGuid() + " не найден или находится в группе выбывших", null);
                lineResults.put(clientItem.getCount(), result);
                continue;
            }
            for(GuardianItem guardianItem : clientItem.getGuardianItems()){
                if(guardianItem.getPhones().isEmpty()){
                    LineResult result = new LineResult(orgInt + "/" + guardianItem.getCount(), 140,
                            "Номера телефонов отсутствуют или не являются мобильными", clientItem.getClientData().getIdOfClient());
                    lineResults.put(guardianItem.getCount(), result);
                    continue;
                }
                if(guardianItem.getFirstName() == null || guardianItem.getFirstName().isEmpty() ||
                        guardianItem.getSurName() == null || guardianItem.getSurName().isEmpty()){
                    LineResult result = new LineResult(orgInt + "/" + guardianItem.getCount(), 150,
                            "Отсутствует фамилия или имя представителя", clientItem.getClientData().getIdOfClient());
                    lineResults.put(guardianItem.getCount(), result);
                    continue;
                }
                if(findGuardianByMobile(clientItem.getGuardianDatas(), guardianItem)){
                    LineResult result = new LineResult(orgInt + "/" + guardianItem.getCount(), 130,
                            "Представитель найден по телефонному номеру", clientItem.getClientData().getIdOfClient());
                    lineResults.put(guardianItem.getCount(), result);
                    continue;
                }
                GuardianData guardianDataForUpdate = findGuardianByName(clientItem.getGuardianDatas(), guardianItem);
                if(guardianDataForUpdate != null){
                    updateMap.put(guardianItem, guardianDataForUpdate);
                } else {
                    createMap.put(guardianItem, clientItem);
                }
            }
        }

        updateAndCreateGuardians(orgInt, lineResults, updateMap, createMap);

        return new ArrayList<LineResult>(lineResults.values());
    }

    private void updateAndCreateGuardians(int orgInt, Map<Integer, LineResult> lineResults,
            Map<GuardianItem, GuardianData> updateMap, Map<GuardianItem, ClientItem> createMap) throws Exception {
        updateGuardians(orgInt, lineResults, updateMap);
        createGuardians(orgInt, lineResults, createMap);
    }

    private void createGuardians(int orgInt, Map<Integer, LineResult> lineResults,
            Map<GuardianItem, ClientItem> createMap) throws Exception {

        Map<Long, Map<GuardianItem, ClientItem>> orgMap = new HashMap<Long, Map<GuardianItem, ClientItem>>();
        for(Map.Entry<GuardianItem, ClientItem> entry : createMap.entrySet()) {
            Long idOfOrg = entry.getValue().getClientData().getIdOfOrg();
            if(!orgMap.containsKey(idOfOrg)){
                Map<GuardianItem, ClientItem> map = new HashMap<GuardianItem, ClientItem>();
                map.put(entry.getKey(), entry.getValue());
                orgMap.put(idOfOrg, map);
            } else {
                orgMap.get(idOfOrg).put(entry.getKey(), entry.getValue());
            }
        }

        Map<GuardianItem, ClientItem> batchMap = new HashMap<GuardianItem, ClientItem>();
        for(Map.Entry<Long, Map<GuardianItem, ClientItem>> entry : orgMap.entrySet()) {
            for(Map.Entry<GuardianItem, ClientItem> entry1 : entry.getValue().entrySet()) {
                batchMap.put(entry1.getKey(), entry1.getValue());
                if(batchMap.size() % MAX_BATCH_SIZE == 0){
                    createGuardiansBatch(orgInt, lineResults, batchMap, entry.getKey());
                    batchMap = new HashMap<GuardianItem, ClientItem>();
                }
            }
            createGuardiansBatch(orgInt, lineResults, batchMap, entry.getKey());
            batchMap = new HashMap<GuardianItem, ClientItem>();
        }
    }

    private void createGuardiansBatch(int orgInt, Map<Integer, LineResult> lineResults,
            Map<GuardianItem, ClientItem> createMap, Long idOfOrg) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org org = (Org) persistenceSession.load(Org.class, idOfOrg);
            ClientGroup clientGroup = findClientGroupByGroupNameAndIdOfOrg(persistenceSession,
                    idOfOrg, ClientGroup.Predefined.CLIENT_PARENTS.getNameOfGroup());
            List<Long> contractIds = RuntimeContext.getInstance().getClientContractIdGenerator()
                    .generateTransactionFree(idOfOrg, createMap.size());
            int i = 0;
            Long newGuardiansVersions = ClientManager.generateNewClientGuardianVersion(persistenceSession);
            Long clientRegistryVersion = DAOUtils.updateClientRegistryVersionWithPessimisticLock(createMap.size());

            for(Map.Entry<GuardianItem, ClientItem> entry : createMap.entrySet()) {
                try {
                    Long idOfGuardian = createGuardian(persistenceSession, entry.getKey(), entry.getValue(), org,
                            contractIds.get(i), newGuardiansVersions, clientRegistryVersion, clientGroup);
                    i++;
                    newGuardiansVersions++;
                    clientRegistryVersion++;
                    LineResult result = new LineResult(orgInt + "/" + entry.getKey().getCount(), 100,
                            "Создан новый представитель ИД=" + idOfGuardian, entry.getValue().getClientData().getIdOfClient());
                    lineResults.put(entry.getKey().getCount(), result);
                } catch (Exception e) {
                    LineResult result = new LineResult(orgInt + "/" + entry.getKey().getCount(), 120,
                            "Не удалось создать представителя:" + e.getMessage(), entry.getValue().getClientData().getIdOfClient());
                    lineResults.put(entry.getKey().getCount(), result);
                }
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private void updateGuardians(int orgInt, Map<Integer, LineResult> lineResults,
            Map<GuardianItem, GuardianData> updateMap) throws Exception {
        Map<GuardianItem, GuardianData> batchMap = new HashMap<GuardianItem, GuardianData>();
        for(Map.Entry<GuardianItem, GuardianData> entry : updateMap.entrySet()) {
            batchMap.put(entry.getKey(), entry.getValue());
            if(batchMap.size() % MAX_BATCH_SIZE == 0){
                updateGuardiansBatch(orgInt, lineResults, batchMap);
                batchMap = new HashMap<GuardianItem, GuardianData>();
            }
        }
        updateGuardiansBatch(orgInt, lineResults, batchMap);
    }

    private void updateGuardiansBatch(int orgInt, Map<Integer, LineResult> lineResults,
            Map<GuardianItem, GuardianData> updateMap) throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Long newGuardiansVersions = ClientManager.generateNewClientGuardianVersion(persistenceSession);
            Long clientRegistryVersion = DAOUtils.updateClientRegistryVersionWithPessimisticLock(updateMap.size());

            for(Map.Entry<GuardianItem, GuardianData> entry : updateMap.entrySet()) {
                Long idOfGuardian = updateGuardian(persistenceSession, entry.getKey(), entry.getValue(),
                        newGuardiansVersions, clientRegistryVersion);
                newGuardiansVersions++;
                clientRegistryVersion++;
                LineResult result = new LineResult(orgInt + "/" + entry.getKey().getCount(), 160,
                        "Представитель найден по фамилии и имени, данные представителя ИД="
                                + idOfGuardian + " обновлены", null);
                lineResults.put(entry.getKey().getCount(), result);
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private Long updateGuardian(Session persistenceSession, GuardianItem guardianItem, GuardianData guardianData,
            Long newGuardiansVersions, Long clientRegistryVersion) throws Exception {
        if(guardianItem.getRelationType() != null){
            if(!guardianItem.getRelationType().equals(guardianData.getRelationType())) {
                Query query = persistenceSession.createQuery(
                        "UPDATE ClientGuardian SET version = :version, relation = :relation " + "WHERE idOfClientGuardian = :idOfClientGuardian");
                query.setParameter("version", newGuardiansVersions);
                query.setParameter("relation", guardianItem.getRelationType());
                query.setParameter("idOfClientGuardian", guardianData.getIdOfClientGuardian());
                query.executeUpdate();
            }
        }

        Query query = persistenceSession.createQuery("UPDATE Client SET clientRegistryVersion = :clientRegistryVersion, "
                + "mobile = :mobile, phone = :phone, email = :email WHERE idOfClient = :idOfClient");
        query.setParameter("clientRegistryVersion", clientRegistryVersion);
        query.setParameter("mobile", guardianItem.getPhones().get(0));
        query.setParameter("phone", guardianItem.getPhones().size() > 1 ? guardianItem.getPhones().get(1) : null);
        query.setParameter("email", guardianItem.getEmail());
        query.setParameter("idOfClient", guardianData.getIdOfGuardian());
        query.executeUpdate();

        return guardianData.getIdOfGuardian();
    }

    private Long createGuardian(Session persistenceSession, GuardianItem guardianItem, ClientItem clientItem, Org org,
            Long contractId, Long newGuardiansVersions, Long clientRegistryVersion, ClientGroup clientGroup) throws Exception {
        Long limit = RuntimeContext.getInstance().getOptionValueLong(Option.OPTION_DEFAULT_OVERDRAFT_LIMIT);

        Person person = new Person(guardianItem.getFirstName(), guardianItem.getSurName(), guardianItem.getSecondName());
        person.setIdDocument("");
        persistenceSession.persist(person);
        Person contractPerson = new Person(guardianItem.getFirstName(), guardianItem.getSurName(), guardianItem.getSecondName());
        contractPerson.setIdDocument("");
        persistenceSession.persist(contractPerson);

        Date date = new Date();

        Client guardian = new Client(org, person, contractPerson, 0, clientItem.getClientData().isNotifyViaEmail(),
                clientItem.getClientData().isNotifyViaSMS(), clientItem.getClientData().isNotifyViaPUSH(), contractId,
                date, 0, "" + contractId, 0, clientRegistryVersion, limit,
                RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_DEFAULT_EXPENDITURE_LIMIT));

        if (clientGroup != null) {
            guardian.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
        }
        guardian.setMobile(guardianItem.getPhones().get(0));
        logger.info("class : RegistryLoadPage, method : createGuardian line : 605, idOfClient : " + guardian.getIdOfClient() + " mobile : " + guardian.getMobile());
        if(guardianItem.getPhones().size() > 1){
            guardian.setPhone(guardianItem.getPhones().get(1));
            logger.info("class : RegistryLoadPage, method : createGuardian line : 608, idOfClient : " + guardian.getIdOfClient() + " phone : " + guardian.getPhone());
        }
        guardian.setAddress("");
        guardian.setEmail(guardianItem.getEmail());
        guardian.setDiscountMode(Client.DISCOUNT_MODE_NONE);
        persistenceSession.persist(guardian);

        RuntimeContext.getInstance().getClientContractIdGenerator().updateUsedContractId(persistenceSession, contractId, org.getIdOfOrg());

        ClientMigration clientMigration = new ClientMigration(guardian, org, date);
        persistenceSession.persist(clientMigration);

        ClientGuardian clientGuardian = new ClientGuardian(clientItem.getClientData().getIdOfClient(), guardian.getIdOfClient());
        clientGuardian.setVersion(newGuardiansVersions);
        clientGuardian.setDisabled(false);
        clientGuardian.setDeletedState(false);
        clientGuardian.setRelation(guardianItem.getRelationType());
        persistenceSession.persist(clientGuardian);
        return guardian.getIdOfClient();
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> findClients(Session persistenceSession, Set<String> guids){
        if(guids.size() < 1){
            return new ArrayList<Object[]>();
        }
        Criteria criteria = persistenceSession.createCriteria(Client.class);
        criteria.add(Restrictions.in("clientGUID", guids));
        criteria.add(Restrictions.lt("idOfClientGroup", ClientGroup.Predefined.CLIENT_LEAVING.getValue()));
        criteria.createAlias("org", "org");
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("idOfClient"))
                .add(Projections.property("clientGUID"))
                .add(Projections.property("notifyViaEmail"))
                .add(Projections.property("notifyViaSMS"))
                .add(Projections.property("notifyViaPUSH"))
                .add(Projections.property("org.idOfOrg")));
        return (List<Object[]>) criteria.list();
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> findClientGuardians(Session persistenceSession, Set<Long> clientsIds){
        if(clientsIds.size() < 1){
            return new ArrayList<Object[]>();
        }
        Criteria criteria = persistenceSession.createCriteria(ClientGuardian.class);
        criteria.add(Restrictions.in("idOfChildren", clientsIds));
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("idOfClientGuardian"))
                .add(Projections.property("idOfChildren"))
                .add(Projections.property("idOfGuardian"))
                .add(Projections.property("relation")));
        return (List<Object[]>) criteria.list();
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> findGuardians(Session persistenceSession, Set<Long> guardiansIds){
        if(guardiansIds.size() < 1){
            return new ArrayList<Object[]>();
        }
        Criteria criteria = persistenceSession.createCriteria(Client.class);
        criteria.add(Restrictions.in("idOfClient", guardiansIds));
        criteria.createAlias("person", "p");
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("idOfClient"))
                .add(Projections.property("phone"))
                .add(Projections.property("mobile"))
                .add(Projections.property("email"))
                .add(Projections.property("p.firstName"))
                .add(Projections.property("p.surname"))
                .add(Projections.property("p.secondName")));
        return (List<Object[]>) criteria.list();
    }

    private boolean findGuardianByMobile(List<GuardianData> guardianDatas, GuardianItem guardianItem) {
        for(GuardianData guardianData : guardianDatas){
            if(guardianItem.getPhones().contains(guardianData.getMobile())) {
                return true;
            }
        }
        return false;
    }

    private GuardianData findGuardianByName(List<GuardianData> guardianDatas, GuardianItem guardianItem) {
        for(GuardianData guardianData : guardianDatas){
            if(guardianItem.getFirstName().equalsIgnoreCase(guardianData.getFirstName()) &&
                    guardianItem.getSurName().equalsIgnoreCase(guardianData.getSurName())) {
                return guardianData;
            }
        }
        return null;
    }

    private void parseItem(String line, List<ClientItem> clientItems, int lineNo) {
        String[] parameters = line.split(";");

        if (parameters.length < 1) {
            return;
        }
        if (parameters[0] == null || parameters[0].isEmpty()) {
            if (parameters.length < 9 || parameters[8].isEmpty()) {
                return;
            }
            GuardianItem guardianItem = new GuardianItem(parameters, lineNo);
            List<GuardianItem> guardianItems = clientItems.get(clientItems.size() - 1).getGuardianItems();
            for(GuardianItem gi : guardianItems) {
                if(gi.getFirstName() != null && gi.getFirstName().equalsIgnoreCase(guardianItem.getFirstName())
                        && gi.getSurName() != null && gi.getSurName().equalsIgnoreCase(guardianItem.getSurName())) {
                    return;
                }
                if(guardianItem.getPhones().size() > 0 && gi.getPhones().contains(guardianItem.getPhones().get(0))){
                    guardianItem.getPhones().remove(0);
                }
            }
            guardianItems.add(guardianItem);
        } else {
            if (parameters.length < 9 || parameters[8].isEmpty()) {
                return;
            }
            ClientItem clientItem = new ClientItem(parameters, lineNo);
            clientItems.add(clientItem);
            GuardianItem guardianItem = new GuardianItem(parameters, lineNo);
            clientItems.get(clientItems.size() - 1).getGuardianItems().add(guardianItem);
        }

    }


    private class ClientItem {
        private int count;
        private String guid;
        private List<GuardianItem> guardianItems;
        private ClientData clientData;
        private List<GuardianData> guardianDatas;

        public ClientItem(String[] parameters, int lineNo) {
            this.count = lineNo;
            this.guid = parameters[2];
            this.guardianItems = new ArrayList<GuardianItem>();
            this.guardianDatas = new ArrayList<GuardianData>();
        }

        public GuardianData getGuardianData(Long idOfGuardian) {
            for(GuardianData gd : guardianDatas){
                if(gd.getIdOfGuardian().equals(idOfGuardian)){
                    return gd;
                }
            }
            return null;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getGuid() {
            return guid;
        }

        public void setGuid(String guid) {
            this.guid = guid;
        }

        public List<GuardianItem> getGuardianItems() {
            return guardianItems;
        }

        public void setGuardianItems(List<GuardianItem> guardianItems) {
            this.guardianItems = guardianItems;
        }

        public ClientData getClientData() {
            return clientData;
        }

        public void setClientData(ClientData clientData) {
            this.clientData = clientData;
        }

        public List<GuardianData> getGuardianDatas() {
            return guardianDatas;
        }

        public void setGuardianDatas(List<GuardianData> guardianDatas) {
            this.guardianDatas = guardianDatas;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ClientItem that = (ClientItem) o;

            return count == that.count;

        }

        @Override
        public int hashCode() {
            return count;
        }
    }

    private class GuardianItem {
        private int count;
        private String firstName;
        private String surName;
        private String secondName;
        private ClientGuardianRelationType relationType;
        private List<String> phones;
        private String email;

        public GuardianItem(String[] parameters, int lineNo) {
            this.count = lineNo;
            this.phones = new ArrayList<String>();
            String[] phones = null;
            if (parameters.length > 12) {
                phones = parameters[12].split(",");
                for (int j = 0; j < phones.length; j++) {
                    String s = Client.checkAndConvertMobile(phones[j]);
                    if (s != null && !s.isEmpty()) {
                        if (s.charAt(1) == '9') {
                            phones[j] = s;
                            this.phones.add(s);
                        }
                    }
                }
            }

            String surname = null;
            if (parameters.length > 8) {
                surname = parameters[8];
            }
            this.surName = surname;

            String firstName = null;
            if (parameters.length > 9) {
                firstName = parameters[9];
            }
            this.firstName = firstName;

            String secondName = null;
            if (parameters.length > 10) {
                secondName = parameters[10];
            }
            this.secondName = secondName;

            String relation = null;
            if (parameters.length > 11) {
                relation = parameters[11];
            }
            ClientGuardianRelationType relationType = null;
            for(ClientGuardianRelationType type : ClientGuardianRelationType.values()){
                if(type.toString().equalsIgnoreCase(relation)){
                    relationType = type;
                }
            }
            this.relationType = relationType;

            String email = null;
            if (parameters.length > 13) {
                email = parameters[13];
            }
            this.email = email;

        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getSurName() {
            return surName;
        }

        public void setSurName(String surName) {
            this.surName = surName;
        }

        public String getSecondName() {
            return secondName;
        }

        public void setSecondName(String secondName) {
            this.secondName = secondName;
        }

        public ClientGuardianRelationType getRelationType() {
            return relationType;
        }

        public void setRelationType(ClientGuardianRelationType relationType) {
            this.relationType = relationType;
        }

        public List<String> getPhones() {
            return phones;
        }

        public void setPhones(List<String> phones) {
            this.phones = phones;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            GuardianItem that = (GuardianItem) o;

            return count == that.count;

        }

        @Override
        public int hashCode() {
            return count;
        }
    }

    private class ClientData {
        private Long idOfClient;
        private String guid;
        private boolean notifyViaEmail;
        private boolean notifyViaSMS;
        private boolean notifyViaPUSH;
        private Long idOfOrg;

        public ClientData(Long idOfClient, String guid, boolean notifyViaEmail, boolean notifyViaSMS,
                boolean notifyViaPUSH, Long idOfOrg) {
            this.idOfClient = idOfClient;
            this.guid = guid;
            this.notifyViaEmail = notifyViaEmail;
            this.notifyViaSMS = notifyViaSMS;
            this.notifyViaPUSH = notifyViaPUSH;
            this.idOfOrg = idOfOrg;
        }

        public Long getIdOfClient() {
            return idOfClient;
        }

        public void setIdOfClient(Long idOfClient) {
            this.idOfClient = idOfClient;
        }

        public String getGuid() {
            return guid;
        }

        public void setGuid(String guid) {
            this.guid = guid;
        }

        public boolean isNotifyViaEmail() {
            return notifyViaEmail;
        }

        public void setNotifyViaEmail(boolean notifyViaEmail) {
            this.notifyViaEmail = notifyViaEmail;
        }

        public boolean isNotifyViaSMS() {
            return notifyViaSMS;
        }

        public void setNotifyViaSMS(boolean notifyViaSMS) {
            this.notifyViaSMS = notifyViaSMS;
        }

        public boolean isNotifyViaPUSH() {
            return notifyViaPUSH;
        }

        public void setNotifyViaPUSH(boolean notifyViaPUSH) {
            this.notifyViaPUSH = notifyViaPUSH;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }
    }

    private class GuardianData {
        private Long idOfClient;
        private Long idOfClientGuardian;
        private Long idOfGuardian;
        private String firstName;
        private String surName;
        private String secondName;
        private ClientGuardianRelationType relationType;
        private String phone;
        private String mobile;
        private String email;

        public GuardianData(Long idOfClient, Long idOfClientGuardian, Long idOfGuardian, ClientGuardianRelationType relationType) {
            this.idOfClient = idOfClient;
            this.idOfClientGuardian = idOfClientGuardian;
            this.idOfGuardian = idOfGuardian;
            this.relationType = relationType;
        }


        public void setParameters(String firstName, String surName, String secondName,
                String phone, String mobile, String email) {
            this.firstName = firstName;
            this.surName = surName;
            this.secondName = secondName;
            this.phone = phone;
            this.mobile = mobile;
            this.email = email;
        }

        public Long getIdOfClient() {
            return idOfClient;
        }

        public void setIdOfClient(Long idOfClient) {
            this.idOfClient = idOfClient;
        }

        public Long getIdOfClientGuardian() {
            return idOfClientGuardian;
        }

        public void setIdOfClientGuardian(Long idOfClientGuardian) {
            this.idOfClientGuardian = idOfClientGuardian;
        }

        public Long getIdOfGuardian() {
            return idOfGuardian;
        }

        public void setIdOfGuardian(Long idOfGuardian) {
            this.idOfGuardian = idOfGuardian;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getSurName() {
            return surName;
        }

        public void setSurName(String surName) {
            this.surName = surName;
        }

        public String getSecondName() {
            return secondName;
        }

        public void setSecondName(String secondName) {
            this.secondName = secondName;
        }

        public ClientGuardianRelationType getRelationType() {
            return relationType;
        }

        public void setRelationType(ClientGuardianRelationType relationType) {
            this.relationType = relationType;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

}
