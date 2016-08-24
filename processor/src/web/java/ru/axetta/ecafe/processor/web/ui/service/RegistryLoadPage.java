/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientDao;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 17.08.16
 * Time: 10:37
 */

public class RegistryLoadPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(RegistryLoadPage.class);
    private static final long MAX_LINE_NUMBER = 600000;

    public static class LineResult {

        private final String lineNo;
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
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;

            try {
                RuntimeContext runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();

                processClients(persistenceSession, path, lastFile, firstFile, lineResults);

                persistenceTransaction.commit();
                persistenceTransaction = null;
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }

        if(parameter == 2) {

            Session persistenceSession = null;
            Transaction persistenceTransaction = null;

            try {
                RuntimeContext runtimeContext = RuntimeContext.getInstance();
                persistenceSession = runtimeContext.createPersistenceSession();

                processGuardians(persistenceSession, persistenceTransaction, path, lastFile, firstFile, lineResults);

            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }

        this.lineResults = lineResults;
        printMessage("Обработка параметров завершена");
    }

    private void processClients(Session persistenceSession, String path, Long lastFile, Long firstFile,
            List<LineResult> lineResults) {
        BufferedReader br = null;
        String line;
        for(int i = firstFile.intValue(); i <= lastFile; i++) {

            try {
                String csvFile = path + i + ".csv";
                br = new BufferedReader(new FileReader(csvFile));
                int lineNo = 1;

                while ((line = br.readLine()) != null) {

                    String currentLineNo = i + "/" + lineNo;
                    lineNo++;

                    String[] parameters = line.split(";");
                    if(parameters.length < 1){
                        continue;
                    }
                    if(parameters[0].isEmpty()){
                        continue;
                    }

                    Client client = DAOUtils.findClientByGuid(persistenceSession, parameters[2]);
                    if(client != null) {

                        String gender = parameters[6];
                        if(gender != null) {
                            if (gender.equalsIgnoreCase(Client.CLIENT_GENDER_NAMES[0])) {
                                client.setGender(0);
                            } else if (gender.equalsIgnoreCase(Client.CLIENT_GENDER_NAMES[1])) {
                                client.setGender(1);
                            } else {
                                LineResult result = new LineResult(currentLineNo, 30, "Неверный формат пола",
                                        client.getIdOfClient());
                                lineResults.add(result);
                                continue;
                            }
                        }

                        String birthDate = parameters[7];
                        if(birthDate != null) {
                            try {
                                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                                Date date = format.parse(birthDate);
                                client.setBirthDate(date);
                            } catch (ParseException e) {
                                LineResult result = new LineResult(currentLineNo, 20, "Неверный формат даты",
                                        client.getIdOfClient());
                                lineResults.add(result);
                                continue;
                            }
                        }

                        persistenceSession.update(client);
                        LineResult result = new LineResult(currentLineNo, 0, "Данные успешно изменены", client.getIdOfClient());
                        lineResults.add(result);

                    } else {
                        LineResult result = new LineResult(currentLineNo, 10, "Клиент с GUID=" + parameters[2]
                                + " не найден", null);
                        lineResults.add(result);
                    }
                    if(lineNo % 100 == 0){
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
            this.lineResultsSize = lineResults.size();
        }
    }

    private void processGuardians(Session persistenceSession, Transaction persistenceTransaction, String path, Long lastFile, Long firstFile,
            List<LineResult> lineResults) {
        BufferedReader br = null;
        String line;
        for(int i = firstFile.intValue(); i <= lastFile; i++) {

            try {
                String csvFile = path + i + ".csv";
                br = new BufferedReader(new FileReader(csvFile));
                int lineNo = 1;

                Client client = null;
                List<Client> guardians = new ArrayList<Client>();

                while ((line = br.readLine()) != null) {

                    String currentLineNo = i + "/" + lineNo;
                    lineNo++;

                    String[] parameters = line.split(";");
                    if(parameters.length < 1){
                        continue;
                    }
                    if(parameters[0] == null || parameters[0].isEmpty()){
                        if(parameters.length < 9 || parameters[8].isEmpty()){
                            continue;
                        }
                    } else {
                        if(parameters.length < 9 || parameters[8].isEmpty()){
                            continue;
                        }
                        client = DAOUtils.findClientByGuid(persistenceSession, parameters[2]);
                        guardians = new ArrayList<Client>();
                    }

                    if(client != null) {
                        guardians = getGuardians(persistenceSession, client);
                        String surname = null;
                        if(parameters.length > 8){
                            surname = parameters[8];
                        }
                        String firstName = null;
                        if(parameters.length > 9){
                            firstName = parameters[9];
                        }
                        String secondName = null;
                        if(parameters.length > 10){
                            secondName = parameters[10];
                        }

                        String relation = null;
                        if(parameters.length > 11){
                            relation = parameters[11];
                        }

                        String phone = null;
                        if(parameters.length > 12){
                            phone = Client.checkAndConvertMobile(parameters[12]);
                        }
                        String email = null;
                        if(parameters.length > 13){
                            email = parameters[13];
                        }

                        boolean c = false;
                        if(guardians.size() > 0) {
                            if (phone != null && !phone.isEmpty()) {
                                for (Client g : guardians) {
                                    if (phone.equals(g.getPhone())) {
                                        LineResult result = new LineResult(currentLineNo, 130, "Представитель найден по телефонному номеру",
                                                client.getIdOfClient());
                                        lineResults.add(result);
                                        c = true;
                                        break;
                                    }
                                }
                            }
                            if (email != null && !email.isEmpty()) {
                                for (Client g : guardians) {
                                    if (email.equalsIgnoreCase(g.getEmail())) {
                                        LineResult result = new LineResult(currentLineNo, 140, "Представитель найден по электронной почте",
                                                client.getIdOfClient());
                                        lineResults.add(result);
                                        c = true;
                                        break;
                                    }
                                }
                            }
                            if (firstName == null || surname == null) {
                                LineResult result = new LineResult(currentLineNo, 150, "Отсутствует фамилия или имя представителя",
                                        client.getIdOfClient());
                                lineResults.add(result);
                                continue;
                            }
                            for (Client g : guardians) {
                                if (firstName.equalsIgnoreCase(g.getPerson().getFirstName()) && surname
                                        .equalsIgnoreCase(g.getPerson().getSurname())) {
                                    LineResult result = new LineResult(currentLineNo, 160, "Представитель найден по фамилии и имени",
                                            client.getIdOfClient());
                                    lineResults.add(result);
                                    c = true;
                                    break;
                                }
                            }
                            if(c) {
                                continue;
                            }
                        }

                        try {
                            Long idOfGuardian = createGuardian(persistenceSession, persistenceTransaction, client, firstName, surname,
                                    secondName, phone, email, relation);
                            LineResult result = new LineResult(currentLineNo, 100, "Создан новый представитель ИД=" + idOfGuardian,
                                    client.getIdOfClient());
                            lineResults.add(result);
                            persistenceSession.flush();
                        } catch (Exception e){
                            LineResult result = new LineResult(currentLineNo, 120, "Не удалось создать представителя:" + e.getMessage(),
                                    client.getIdOfClient());
                            lineResults.add(result);
                        }

                    } else {
                        LineResult result = new LineResult(currentLineNo, 110, "Клиент с GUID=" + parameters[2]
                                + " не найден", null);
                        lineResults.add(result);
                    }
                    if(lineNo % 30 == 0){
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
            this.lineResultsSize = lineResults.size();
        }
    }

    private List<Client> getGuardians(Session session, Client client){
        List<Client> result = new ArrayList<Client>();
        List<ClientGuardian> guardians = ClientDao.getInstance().getGuardians(session, client.getIdOfClient());
        for (ClientGuardian cg : guardians) {
            Client guardian = (Client)session.load(Client.class, cg.getIdOfGuardian());
            result.add(guardian);
        }
        return result;
    }

    private Long createGuardian(Session persistenceSession, Transaction persistenceTransaction,
            Client client, String firstName, String surname,
            String secondName, String phone, String email, String relation) throws Exception{

        persistenceTransaction = persistenceSession.beginTransaction();

        RuntimeContext runtimeContext  = RuntimeContext.getInstance();
        Org org = client.getOrg();
        boolean goodConId = false;
        Long contractId = null;
        int count = 0;
        while (!goodConId) {
            if(count == 3){
                throw new Exception("Не удалось сгенерировать л/с представителя");
            }
            contractId = runtimeContext.getClientContractIdGenerator().generateTransactionFree(org.getIdOfOrg(), persistenceSession);
            Client byConId = DAOUtils.findClientByContractId(persistenceSession, contractId);
            count++;
            if(byConId == null){
                goodConId = true;
            }
        }
        long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(persistenceSession);
        Long limit = RuntimeContext.getInstance().getOptionValueLong(Option.OPTION_DEFAULT_OVERDRAFT_LIMIT);

        Person person = new Person(firstName, surname, secondName);
        person.setIdDocument("");
        persistenceSession.persist(person);
        Person contractPerson =  new Person("", "", "");
        person.setIdDocument(null);
        persistenceSession.persist(contractPerson);

        Date date = new Date();

        Client guardian = new Client(org, person, contractPerson, 0, client.isNotifyViaEmail(), client.isNotifyViaSMS(),
                client.isNotifyViaPUSH(), contractId, date, 0, "" + contractId, 0,
                clientRegistryVersion, limit, RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_DEFAULT_EXPENDITURE_LIMIT), "");

        guardian.setMobile(phone);
        guardian.setAddress("");
        guardian.setEmail(email);
        guardian.setDiscountMode(Client.DISCOUNT_MODE_NONE);
        Set<ClientNotificationSetting> set = new HashSet<ClientNotificationSetting>();
        for(ClientNotificationSetting setting : client.getNotificationSettings()){
            set.add(new ClientNotificationSetting(guardian, setting.getNotifyType()));
        }
        guardian.setNotificationSettings(set);
        persistenceSession.persist(guardian);

        ClientMigration clientMigration = new ClientMigration(guardian, org, date);
        persistenceSession.persist(clientMigration);

        ClientGuardianRelationType relationType = null;
        for(ClientGuardianRelationType type : ClientGuardianRelationType.values()){
            if(relation.equalsIgnoreCase(type.toString())){
                relationType = type;
            }
        }
        Long newGuardiansVersions = ClientManager.generateNewClientGuardianVersion(persistenceSession);
        ClientGuardian clientGuardian = new ClientGuardian(client.getIdOfClient(), guardian.getIdOfClient());
        clientGuardian.setVersion(newGuardiansVersions);
        clientGuardian.setDisabled(false);
        clientGuardian.setDeletedState(false);
        clientGuardian.setRelation(relationType);
        persistenceSession.persist(clientGuardian);

        persistenceTransaction.commit();
        persistenceTransaction = null;

        return guardian.getIdOfClient();
    }

}
