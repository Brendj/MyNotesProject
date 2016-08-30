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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

                            persistenceSession.update(client);
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

    private void processGuardians(String path, Long lastFile, Long firstFile, List<LineResult> lineResults) throws Exception {
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

                    Session persistenceSession = null;
                    Transaction persistenceTransaction = null;

                    try {
                        RuntimeContext runtimeContext = RuntimeContext.getInstance();
                        persistenceSession = runtimeContext.createPersistenceSession();

                        String currentLineNo = i + "/" + lineNo;
                        lineNo++;

                        String[] parameters = line.split(";");
                        if (parameters.length < 1) {
                            continue;
                        }
                        if (parameters[0] == null || parameters[0].isEmpty()) {
                            if (parameters.length < 9 || parameters[8].isEmpty()) {
                                continue;
                            }
                        } else {
                            if (parameters.length < 9 || parameters[8].isEmpty()) {
                                continue;
                            }
                            client = DAOUtils.findClientByGuid(persistenceSession, parameters[2]);
                            guardians = new ArrayList<Client>();
                        }

                        if (client != null) {
                            String[] phones = null;
                            if (parameters.length > 12) {
                                phones = parameters[12].split(",");
                                for (int j = 0; j < phones.length; j++) {
                                    String s = Client.checkAndConvertMobile(phones[j]);
                                    if (s != null && !s.isEmpty()) {
                                        if (s.charAt(1) == '9') {
                                            phones[j] = s;
                                        } else {
                                            phones[j] = null;
                                        }
                                    } else {
                                        phones[j] = null;
                                    }
                                }
                            }
                            boolean a = false;
                            if(phones == null || phones.length == 0){
                                a = true;
                            } else {
                                for (String phone : phones) {
                                    if (phone != null) {
                                        a = true;
                                    }
                                }
                            }
                            if(!a){
                                LineResult result = new LineResult(currentLineNo, 140,
                                        "Номера телефонов отсутствуют или не являются мобильными", client.getIdOfClient());
                                lineResults.add(result);
                                continue;
                            }

                            guardians = getGuardians(persistenceSession, client);

                            String surname = null;
                            if (parameters.length > 8) {
                                surname = parameters[8];
                            }

                            String firstName = null;
                            if (parameters.length > 9) {
                                firstName = parameters[9];
                            }

                            String secondName = null;
                            if (parameters.length > 10) {
                                secondName = parameters[10];
                            }

                            String relation = null;
                            if (parameters.length > 11) {
                                relation = parameters[11];
                            }

                            String email = null;
                            if (parameters.length > 13) {
                                email = parameters[13];
                            }

                            boolean c = false;
                            if (guardians.size() > 0) {
                                if (phones != null && (phones.length > 0)) {
                                    for (Client g : guardians) {
                                        for (String phone : phones) {
                                            if (phone != null && (phone.equals(g.getPhone()) || phone.equals(g.getMobile()))) {
                                                LineResult result = new LineResult(currentLineNo, 130,
                                                        "Представитель найден по телефонному номеру", client.getIdOfClient());
                                                lineResults.add(result);
                                                c = true;
                                                break;
                                            }
                                        }
                                        if (c) {
                                            break;
                                        }
                                    }
                                }
                                /*if (email != null && !email.isEmpty()) {
                                    for (Client g : guardians) {
                                        if (email.equalsIgnoreCase(g.getEmail())) {
                                            LineResult result = new LineResult(currentLineNo, 140,
                                                    "Представитель найден по электронной почте", client.getIdOfClient());
                                            lineResults.add(result);
                                            c = true;
                                            break;
                                        }
                                    }
                                }*/
                                if (firstName == null || surname == null) {
                                    LineResult result = new LineResult(currentLineNo, 150,
                                            "Отсутствует фамилия или имя представителя", client.getIdOfClient());
                                    lineResults.add(result);
                                    continue;
                                }
                                for (Client g : guardians) {
                                    if (firstName.equalsIgnoreCase(g.getPerson().getFirstName()) && surname
                                            .equalsIgnoreCase(g.getPerson().getSurname())) {
                                        persistenceTransaction = persistenceSession.beginTransaction();
                                        Long idOfGuardian = updateGuardian(persistenceSession, g, client,
                                                phones, email, relation);
                                        persistenceTransaction.commit();
                                        persistenceTransaction = null;
                                        LineResult result = new LineResult(currentLineNo, 160,
                                                "Представитель найден по фамилии и имени, данные представителя ИД=" + idOfGuardian
                                                        + " обновлены", client.getIdOfClient());
                                        lineResults.add(result);
                                        c = true;
                                        break;
                                    }
                                }
                                if (c) {
                                    continue;
                                }
                            }

                            try {
                                persistenceTransaction = persistenceSession.beginTransaction();
                                Long idOfGuardian = createGuardian(persistenceSession, client,
                                        firstName, surname, secondName, phones, email, relation);
                                persistenceTransaction.commit();
                                persistenceTransaction = null;
                                LineResult result = new LineResult(currentLineNo, 100,
                                        "Создан новый представитель ИД=" + idOfGuardian, client.getIdOfClient());
                                lineResults.add(result);
                            } catch (Exception e) {
                                LineResult result = new LineResult(currentLineNo, 120,
                                        "Не удалось создать представителя:" + e.getMessage(), client.getIdOfClient());
                                lineResults.add(result);
                            }

                        } else {
                            LineResult result = new LineResult(currentLineNo, 110, "Клиент с GUID=" + parameters[2] + " не найден", null);
                            lineResults.add(result);
                        }
                        if (lineNo % 30 == 0) {
                            this.lineResultsSize = lineResults.size();
                        }

                    } finally {
                        HibernateUtils.rollback(persistenceTransaction, logger);
                        HibernateUtils.close(persistenceSession, logger);
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

    private Long updateGuardian(Session persistenceSession, Client guardian, Client client,
            String[] phones, String email, String relation) throws Exception{
        boolean mobileSet = false;
        if(phones != null) {
            if (phones.length > 0) {
                for (String phone : phones) {
                    if (phone != null) {
                        if (!mobileSet) {
                            guardian.setMobile(phone);
                            mobileSet = true;
                        } else {
                            guardian.setPhone(phone);
                        }
                    }
                }
            }
        }

        if(email != null) {
            guardian.setEmail(email);
        }

        long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(persistenceSession);
        guardian.setClientRegistryVersion(clientRegistryVersion);

        ClientGuardianRelationType relationType = null;
        for(ClientGuardianRelationType type : ClientGuardianRelationType.values()){
            if(relation.equalsIgnoreCase(type.toString())){
                relationType = type;
            }
        }

        Long newGuardiansVersions = ClientManager.generateNewClientGuardianVersion(persistenceSession);
        List<ClientGuardian> guardians = ClientDao.getInstance().getGuardians(persistenceSession, client.getIdOfClient());
        for (ClientGuardian cg : guardians) {
            if(cg.getIdOfGuardian().equals(guardian.getIdOfClient())){
                cg.setRelation(relationType);
                cg.setVersion(newGuardiansVersions);
                persistenceSession.persist(cg);
            }
        }

        persistenceSession.update(guardian);

        return guardian.getIdOfClient();
    }

    private Long createGuardian(Session persistenceSession, Client client, String firstName, String surname,
            String secondName, String[] phones, String email, String relation) throws Exception{

        RuntimeContext runtimeContext  = RuntimeContext.getInstance();

        Org org = DAOUtils.findOrgWithPessimisticLock(persistenceSession, client.getOrg().getIdOfOrg());

        Long contractId = null;

        contractId = runtimeContext.getClientContractIdGenerator().generateTransactionFree(org, persistenceSession);
        Client byConId = DAOUtils.findClientByContractId(persistenceSession, contractId);
        if(byConId != null){
            throw new Exception("Не удалось сгенерировать л/с представителя");
        }

        long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(persistenceSession);
        Long limit = RuntimeContext.getInstance().getOptionValueLong(Option.OPTION_DEFAULT_OVERDRAFT_LIMIT);

        Person person = new Person(firstName, surname, secondName);
        person.setIdDocument("");
        persistenceSession.persist(person);
        Person contractPerson =  new Person(firstName, surname, secondName);
        contractPerson.setIdDocument("");
        persistenceSession.persist(contractPerson);

        Date date = new Date();

        Client guardian = new Client(org, person, contractPerson, 0, client.isNotifyViaEmail(), client.isNotifyViaSMS(),
                client.isNotifyViaPUSH(), contractId, date, 0, "" + contractId, 0,
                clientRegistryVersion, limit, RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_DEFAULT_EXPENDITURE_LIMIT), "");

        if(phones != null) {
            if (phones.length > 0) {
                for (String phone : phones) {
                    if (phone != null) {
                        if (guardian.getMobile() == null) {
                            guardian.setMobile(phone);
                        } else {
                            if (guardian.getPhone() == null) {
                                guardian.setPhone(phone);
                            }
                        }
                    }
                }
            }
        }

        guardian.setAddress("");

        if(email != null) {
            guardian.setEmail(email);
        }
        guardian.setDiscountMode(Client.DISCOUNT_MODE_NONE);
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

        return guardian.getIdOfClient();
    }

}
