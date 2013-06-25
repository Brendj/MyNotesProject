/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.nsi.MskNSIService;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.core.utils.FieldProcessor;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public class ClientManager {

    private static final Logger logger = LoggerFactory.getLogger(ClientManager.class);

    public enum FieldId {
        CONTRACT_ID, PASSWORD,
        CONTRACT_DATE,
        CONTRACT_SURNAME,
        CONTRACT_NAME,
        CONTRACT_SECONDNAME,
        CONTRACT_DOC,
        SURNAME,
        NAME,
        SECONDNAME,
        DOC,
        ADDRESS,
        PHONE,
        MOBILE_PHONE,
        EMAIL,
        PAY_FOR_SMS,
        NOTIFY_BY_SMS,
        NOTIFY_BY_EMAIL,
        OVERDRAFT,
        COMMENTS,
        CARD_ID,
        CARD_PRINTED_NUM,
        CARD_TYPE,
        CARD_EXPIRY,
        CARD_ISSUED,
        EXPENDITURE_LIMIT,
        CONTRACT_STATE,
        GROUP,
        SAN,
        EXTERNAL_ID,
        CLIENT_GUID
    }

    static FieldProcessor.Def[] fieldInfo = {
            new FieldProcessor.Def(0, false, true, "Номер договора", "AUTO", FieldId.CONTRACT_ID, true),
            new FieldProcessor.Def(1, false, false, "Пароль", "X", FieldId.PASSWORD, true),
            new FieldProcessor.Def(2, false, false, "Статус", "1", FieldId.CONTRACT_STATE, true),
            new FieldProcessor.Def(3, false, false, "Дата заключения", "#CURRENT_DATE", FieldId.CONTRACT_DATE, true),
            new FieldProcessor.Def(4, false, false, "Договор-фамилия", "", FieldId.CONTRACT_SURNAME, true),
            new FieldProcessor.Def(5, false, false, "Договор-имя", "", FieldId.CONTRACT_NAME, true),
            new FieldProcessor.Def(6, false, false, "Договор-отчество", "", FieldId.CONTRACT_SECONDNAME, true),
            new FieldProcessor.Def(7, false, false, "Договор-документ", null, FieldId.CONTRACT_DOC, true),
            new FieldProcessor.Def(8, true, false, "Фамилия", null, FieldId.SURNAME, true),
            new FieldProcessor.Def(9, true, false, "Имя", null, FieldId.NAME, true),
            new FieldProcessor.Def(10, true, false, "Отчество", null, FieldId.SECONDNAME, true),
            new FieldProcessor.Def(11, false, false, "Документ", "", FieldId.DOC, true),
            new FieldProcessor.Def(12, false, false, "Адрес", "", FieldId.ADDRESS, true),
            new FieldProcessor.Def(13, false, false, "Телефон", null, FieldId.PHONE, true),
            new FieldProcessor.Def(14, false, false, "Мобильный", null, FieldId.MOBILE_PHONE, true),
            new FieldProcessor.Def(15, false, false, "E-mail", null, FieldId.EMAIL, true),
            new FieldProcessor.Def(16, false, false, "Платный SMS", "0", FieldId.PAY_FOR_SMS, true),
            new FieldProcessor.Def(17, false, false, "Уведомление по SMS", "1", FieldId.NOTIFY_BY_SMS, true),
            new FieldProcessor.Def(18, false, false, "Уведомление по e-mail", "0", FieldId.NOTIFY_BY_EMAIL, true),
            new FieldProcessor.Def(19, false, false, "Овердрафт", null, FieldId.OVERDRAFT, true),
            new FieldProcessor.Def(20, false, false, "Комментарии", null, FieldId.COMMENTS, true),
            new FieldProcessor.Def(21, false, false, "Группа", null, FieldId.GROUP, true),
            new FieldProcessor.Def(22, false, false, "СНИЛС", null, FieldId.SAN, true),
            new FieldProcessor.Def(23, false, false, "Дневной лимит", null, FieldId.EXPENDITURE_LIMIT, true),
            new FieldProcessor.Def(24, false, false, "Карта-ид", null, FieldId.CARD_ID, false),
            new FieldProcessor.Def(25, false, false, "Карта-номер", null, FieldId.CARD_PRINTED_NUM, false),
            new FieldProcessor.Def(26, false, false, "Карта-тип", null, FieldId.CARD_TYPE, false),
            new FieldProcessor.Def(27, false, false, "Карта-выдана", "#CURRENT_DATE", FieldId.CARD_ISSUED, false),
            new FieldProcessor.Def(28, false, false, "Карта-срок", "#5", FieldId.CARD_EXPIRY, false),
            new FieldProcessor.Def(29, false, false, "Внешний идентификатор", null, FieldId.EXTERNAL_ID, true),
            new FieldProcessor.Def(30, false, false, "GUID", null, FieldId.CLIENT_GUID, true),
            new FieldProcessor.Def(-1, false, false, "#", null, -1, false) // поля которые стоит пропустить в файле
    };

    public static class ClientFieldConfig extends FieldProcessor.Config {

        public ClientFieldConfig() {
            super(fieldInfo, true);
        }

        @Override
        public void checkRequiredFields() throws Exception {
            if (nFields > 0) {
                for (FieldProcessor.Def fd : currentConfig) {
                    if (fd.requiredForInsert && fd.realPos == -1) {
                        throw new Exception("В списке полей отсутствует обязательное поле: " + fd.fieldName);
                    }
                }
                if (getField(FieldId.CARD_ID).realPos != -1) {
                    if (getField(FieldId.CARD_TYPE).realPos == -1) {
                        throw new Exception("В списке полей отсутствует обязательное поле: " + getField(
                                FieldId.CARD_TYPE).fieldName);
                    }
                }
            }
        }
    }

    public static class ClientFieldConfigForUpdate extends FieldProcessor.Config {

        public ClientFieldConfigForUpdate() {
            super(fieldInfo, false);
        }

        @Override
        public void checkRequiredFields() throws Exception {
            if (nFields > 0) {
                for (FieldProcessor.Def fd : currentConfig) {
                    if (!fd.isUpdatable() && fd.realPos != -1) {
                        throw new Exception("Поле нельзя обновить: " + fd.fieldName);
                    }
                    if (fd.requiredForUpdate && fd.realPos == -1) {
                        throw new Exception("В списке полей отсутствует обязательное поле: " + fd.fieldName);
                    }
                }
            }
        }
    }


    public static boolean deleteClient(Org org, ClientFieldConfigForUpdate fieldConfig) throws Exception {
        fieldConfig.checkRequiredFields();

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;

        try {
            persistenceSession = runtimeContext.createPersistenceSession();

            String surname = fieldConfig.getValue(ClientManager.FieldId.SURNAME);
            String firstName = fieldConfig.getValue(ClientManager.FieldId.NAME);
            String secondName = fieldConfig.getValue(ClientManager.FieldId.SECONDNAME);

            long idOfClient = findClientByFullName(persistenceSession, org, surname, firstName, secondName);
            Client client = DAOUtils.findClient(persistenceSession, idOfClient);
            if (client == null) {
                throw new Exception("Клиент не найден: " + idOfClient);
            }

            removeClient(persistenceSession, client);
            return true;
        } catch (Exception e) {
            throw e;
        }
    }


    @Transactional
    public static void removeClient(Session persistenceSession, Client client) throws Exception {
        if (!client.getOrders().isEmpty()) {
            throw new Exception("Имеются зарегистрированные заказы");
        }
        if (!client.getClientPaymentOrders().isEmpty()) {
            throw new Exception("Имеются зарегистрированные пополнения счета");
        }
        if (!client.getCards().isEmpty()) {
            throw new Exception("Имеются зарегистрированные карты");
        }
        if (!client.getCategories().isEmpty()) {
            for (CategoryDiscount categoryDiscount : client.getCategories()) {
                client.getCategories().remove(categoryDiscount);
            }
        }
        persistenceSession.delete(client);
    }


    public static Long findClientByFullName(Org organization, ClientManager.ClientFieldConfigForUpdate fieldConfig)
            throws Exception {
        String surname = fieldConfig.getValue(ClientManager.FieldId.SURNAME);
        String firstName = fieldConfig.getValue(ClientManager.FieldId.NAME);
        String secondName = fieldConfig.getValue(ClientManager.FieldId.SECONDNAME);
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = runtimeContext.createPersistenceSession();
        return findClientByFullName(persistenceSession, organization, surname, firstName, secondName);
    }


    public static Long findClientByFullName(Session session, Org organization, String surname, String firstName,
            String secondName) throws Exception {
        String q =
                "select idOfClient from Client client where (client.org = :org) and (upper(client.person.surname) = :surname) and"
                        + "(upper(client.person.firstName) = :firstName)";
        if (StringUtils.isEmpty(secondName)) {
            q += " and (upper(client.person.secondName) = :secondName) ";
        }
        org.hibernate.Query query = session.createQuery(q);
        query.setParameter("org", organization);
        query.setParameter("surname", StringUtils.upperCase(surname));
        query.setParameter("firstName", StringUtils.upperCase(firstName));
        if (StringUtils.isEmpty(secondName)) {
            query.setParameter("secondName", StringUtils.upperCase(secondName));
        }
        query.setMaxResults(2);
        if (query.list().isEmpty()) {
            return null;
        }
        if (query.list().size() == 2) {
            return -1L;
        }
        return (Long) query.list().get(0);
    }

    public static long modifyClientTransactionFree(ClientFieldConfigForUpdate fieldConfig, Org org,
            String registerCommentsAdds, Client client, Session persistenceSession) throws Exception {
        try {
            //tokens[2];
            if (fieldConfig.getValue(ClientManager.FieldId.CONTRACT_STATE) != null) {
                int contractState = fieldConfig.getValueInt(ClientManager.FieldId.CONTRACT_STATE);
                if (!Client.isValidContractState(contractState)) {
                    throw new Exception(
                            "Ошибочное значение поля: " + fieldConfig.getField(FieldId.CONTRACT_STATE).fieldName + ": "
                                    + contractState);
                }
                client.setContractState(contractState);
            }

            //dateFormat.parse(tokens[3]);
            if (fieldConfig.getValue(ClientManager.FieldId.CONTRACT_DATE) != null) {
                client.setContractTime(fieldConfig.getValueDate(ClientManager.FieldId.CONTRACT_DATE));
            }
            //tokens[4];
            String contractSurname = fieldConfig.getValue(ClientManager.FieldId.CONTRACT_SURNAME);
            //tokens[5];
            String contractFirstName = fieldConfig.getValue(ClientManager.FieldId.CONTRACT_NAME);
            //tokens[6];
            String contractSecondName = fieldConfig.getValue(ClientManager.FieldId.CONTRACT_SECONDNAME);
            //tokens[7];
            String contractDoc = fieldConfig.getValue(ClientManager.FieldId.CONTRACT_DOC);
            Person contractPerson = client.getContractPerson();
            boolean changed = false;
            if (contractFirstName != null && StringUtils.isNotEmpty(contractFirstName)) {
                contractPerson.setFirstName(contractFirstName);
                changed = true;
            }
            if (contractSurname != null && StringUtils.isNotEmpty(contractSurname)) {
                contractPerson.setSurname(contractSurname);
                changed = true;
            }
            if (contractSecondName != null && StringUtils.isNotEmpty(contractSecondName)) {
                contractPerson.setSecondName(contractSecondName);
                changed = true;
            }
            if (contractDoc != null && StringUtils.isNotEmpty(contractDoc)) {
                contractPerson.setIdDocument(contractDoc);
                changed = true;
            }
            if (changed) {
                persistenceSession.save(contractPerson);
                client.setContractPerson(contractPerson);
            }

            //tokens[8];
            changed = false;
            String surname = fieldConfig.getValue(ClientManager.FieldId.SURNAME);
            //tokens[9];
            String firstName = fieldConfig.getValue(ClientManager.FieldId.NAME);
            //tokens[10];
            String secondName = fieldConfig.getValue(ClientManager.FieldId.SECONDNAME);
            Person person = client.getPerson();
            if (firstName != null && StringUtils.isNotEmpty(firstName)) {
                person.setFirstName(firstName);
                changed = true;
            }
            if (secondName != null && StringUtils.isNotEmpty(secondName)) {
                person.setSecondName(secondName);
                changed = true;
            }
            if (surname != null && StringUtils.isNotEmpty(surname)) {
                person.setSurname(surname);
                changed = true;
            }
            //tokens[11])
            if (fieldConfig.getValue(ClientManager.FieldId.DOC) != null) {
                person.setIdDocument(fieldConfig.getValue(ClientManager.FieldId.DOC));//tokens[11]);
                changed = true;
            }
            if (changed) {
                persistenceSession.save(person);
                client.setPerson(person);
            }

            //tokens[12])
            if (fieldConfig.getValue(FieldId.ADDRESS) != null) {
                client.setAddress(fieldConfig.getValue(ClientManager.FieldId.ADDRESS));
            }
            //tokens[13])
            if (fieldConfig.getValue(FieldId.PHONE) != null) {
                client.setPhone(fieldConfig.getValue(ClientManager.FieldId.PHONE));
            }
            //tokens[14])
            String mobilePhone = fieldConfig.getValue(ClientManager.FieldId.MOBILE_PHONE);
            if (mobilePhone != null && StringUtils.isNotEmpty(mobilePhone)) {
                mobilePhone = Client.checkAndConvertMobile(mobilePhone);
                if (mobilePhone == null) {
                    throw new Exception("Неправильный формат мобильного телефона");
                }
                client.setMobile(mobilePhone);
            }
            //tokens[15]);
            if (fieldConfig.getValue(FieldId.EMAIL) != null) {
                client.setEmail(fieldConfig.getValue(ClientManager.FieldId.EMAIL));
            }
            //tokens[16])
            if (fieldConfig.getValue(FieldId.PAY_FOR_SMS) != null) {
                client.setPayForSMS(fieldConfig.getValueInt(ClientManager.FieldId.PAY_FOR_SMS));
            }
            //tokens[17])
            if (fieldConfig.getValue(ClientManager.FieldId.NOTIFY_BY_EMAIL) != null) {
                client.setNotifyViaEmail(fieldConfig.getValueBool(ClientManager.FieldId.NOTIFY_BY_EMAIL));
            }
            //tokens[18])
            if (fieldConfig.getValue(ClientManager.FieldId.NOTIFY_BY_SMS) != null) {
                client.setNotifyViaSMS(fieldConfig.getValueBool(ClientManager.FieldId.NOTIFY_BY_SMS));
            }
            //tokens[19]);
            if (fieldConfig.getValue(ClientManager.FieldId.OVERDRAFT) != null) {
                long limit = CurrencyStringUtils.rublesToCopecks(fieldConfig.getValue(ClientManager.FieldId.OVERDRAFT));
                client.setLimit(limit);
            }
            //tokens[20])
            if (fieldConfig.getValue(FieldId.COMMENTS) != null) {
                client.setRemarks(fieldConfig.getValue(ClientManager.FieldId.COMMENTS));
            }
            if (registerCommentsAdds != null && registerCommentsAdds.length() > 0) {
                String comments = client.getRemarks();
                if (comments.indexOf("{%") > -1) {
                    comments = comments.substring(0, comments.indexOf("{%")) + comments
                            .substring(comments.indexOf("%}") + 1);
                }
                comments += registerCommentsAdds;
                if (comments.length() >= 1024) {
                    comments = comments.replaceAll(MskNSIService.REPLACEMENT_REGEXP, "");
                }

                client.setRemarks(comments);
            }

            /* проверяется есть ли в загрузочном файле параметр для группы клиента (класс для ученика)*/
            if (fieldConfig.getValue(ClientManager.FieldId.GROUP) != null) {
                //tokens[21];
                String clientGroupName = fieldConfig.getValue(ClientManager.FieldId.GROUP);
                ClientGroup clientGroup = DAOUtils
                        .findClientGroupByGroupNameAndIdOfOrg(persistenceSession, client.getOrg().getIdOfOrg(),
                                clientGroupName);
                if (clientGroup == null) {
                    clientGroup = DAOUtils
                            .createNewClientGroup(persistenceSession, client.getOrg().getIdOfOrg(), clientGroupName);
                }
                client.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
            }
            //tokens[22])
            if (fieldConfig.getValue(FieldId.SAN) != null) {
                client.setSanWithConvert(fieldConfig.getValue(ClientManager.FieldId.SAN));
            }
            //tokens[23])
            long expenditureLimit = 0;
            if (fieldConfig.getValue(ClientManager.FieldId.EXPENDITURE_LIMIT) != null) {
                expenditureLimit = CurrencyStringUtils.rublesToCopecks(
                        fieldConfig.getValue(ClientManager.FieldId.EXPENDITURE_LIMIT));//old value tokens[19]);
                client.setExpenditureLimit(expenditureLimit);
            }
            //
            if (fieldConfig.getValue(ClientManager.FieldId.EXTERNAL_ID) != null) {
                if (fieldConfig.getValue(ClientManager.FieldId.EXTERNAL_ID).isEmpty()) {
                    client.setExternalId(null);
                } else {
                    client.setExternalId(fieldConfig.getValueLong(ClientManager.FieldId.EXTERNAL_ID));
                }
            }
            if (fieldConfig.getValue(ClientManager.FieldId.CLIENT_GUID) != null) {
                String clientGUID = fieldConfig.getValue(ClientManager.FieldId.CLIENT_GUID);
                if (clientGUID.isEmpty()) {
                    client.setClientGUID(null);
                } else {
                    client.setClientGUID(clientGUID);
                }
            }

            long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(persistenceSession);
            client.setClientRegistryVersion(clientRegistryVersion);

            return client.getIdOfClient();
        } catch (Exception e) {
            logger.info("Ошибка при обновлении данных клиента", e);
            throw new Exception(e.getMessage());
        }
    }


    public static long modifyClient(ClientFieldConfigForUpdate fieldConfig) throws Exception {
        return modifyClient(fieldConfig, null, null);
    }


    public static long modifyClient(ClientFieldConfigForUpdate fieldConfig, Org org, String registerComentsAdds)
            throws Exception {
        fieldConfig.checkRequiredFields();

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Client client;
            long contractId = 0L;
            if (fieldConfig.getValue(ClientManager.FieldId.CONTRACT_ID) != null
                    && fieldConfig.getValue(ClientManager.FieldId.CONTRACT_ID).length() > 0) {
                String contractIdText = fieldConfig.getValue(ClientManager.FieldId.CONTRACT_ID);
                contractId = Long.parseLong(contractIdText);
                client = DAOUtils.findClientByContractId(persistenceSession, contractId);

                if (client == null) {
                    throw new Exception("Клиент не найден: " + contractId);
                }
            } else {
                String surname = fieldConfig.getValue(ClientManager.FieldId.SURNAME);
                String firstName = fieldConfig.getValue(ClientManager.FieldId.NAME);
                String secondName = fieldConfig.getValue(ClientManager.FieldId.SECONDNAME);

                Long idOfClient = findClientByFullName(persistenceSession, org, surname, firstName, secondName);
                if (idOfClient < 1 || idOfClient == null) {
                    return -1L;
                }
                client = DAOUtils.findClient(persistenceSession, idOfClient);

                if (client == null) {
                    throw new Exception("Клиент не найден: " + surname + ", " + firstName + ", " + secondName);
                }
            }

            //tokens[1];
            if (fieldConfig.getValue(ClientManager.FieldId.PASSWORD) != null) {
                String password = fieldConfig.getValue(ClientManager.FieldId.PASSWORD);
                if (password.equals("X")) {
                    password = "" + contractId;
                }
                client.setPassword(password);
            }

            long idOfClient = modifyClientTransactionFree(fieldConfig, org, registerComentsAdds, client,
                    persistenceSession);

            persistenceTransaction.commit();
            persistenceTransaction = null;
            return idOfClient;
        } catch (Exception e) {
            logger.info("Ошибка при обновлении данных клиента", e);
            throw new Exception(e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public static long registerClientTransactionFree (long idOfOrg, ClientFieldConfig fieldConfig,
            boolean checkFullNameUnique, Session persistenceSession) throws Exception {
        return registerClientTransactionFree (idOfOrg, fieldConfig, checkFullNameUnique, persistenceSession, null);
    }

    public static long registerClientTransactionFree (long idOfOrg, ClientFieldConfig fieldConfig,
                                                      boolean checkFullNameUnique, Session persistenceSession,
                                                    Transaction persistenceTransaction) throws Exception {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();

        try {
            Org organization = DAOUtils.findOrg(persistenceSession, idOfOrg);
            if (null == organization) {
                throw new Exception("Организация не найдена: " + idOfOrg);
            }

            String firstName = fieldConfig.getValue(ClientManager.FieldId.NAME); //tokens[9];
            String surname = fieldConfig.getValue(ClientManager.FieldId.SURNAME); //tokens[8];
            String secondName = fieldConfig.getValue(ClientManager.FieldId.SECONDNAME); //tokens[10];

            if (checkFullNameUnique && existClient(persistenceSession, organization, firstName, surname, secondName)) {
                throw new Exception(
                        "Клиент с данными ФИО уже зарегистрирован в организации: " + surname + " " + firstName + " "
                                + secondName, null);
            }

            long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(persistenceSession);
            String contractIdText = fieldConfig.getValue(ClientManager.FieldId.CONTRACT_ID); //tokens[0];
            long contractId;
            if (StringUtils.equals(contractIdText, "AUTO")) {
                contractId = runtimeContext.getClientContractIdGenerator().generateTransactionFree(
                        organization.getIdOfOrg(), persistenceSession);
            } else {
                contractId = Long.parseLong(contractIdText);
            }

            Person contractPerson = new Person(fieldConfig.getValue(ClientManager.FieldId.CONTRACT_NAME),
                    fieldConfig.getValue(ClientManager.FieldId.CONTRACT_SURNAME), fieldConfig.getValue(
                    ClientManager.FieldId.CONTRACT_SECONDNAME)); //new Person(tokens[5], tokens[4], tokens[6]);
            contractPerson.setIdDocument(fieldConfig.getValue(ClientManager.FieldId.CONTRACT_DOC));
            persistenceSession.save(contractPerson);
            Person person = new Person(firstName, surname, secondName);
            person.setIdDocument(fieldConfig.getValue(ClientManager.FieldId.DOC));//tokens[11]);
            persistenceSession.save(person);

            long limit = organization.getCardLimit();
            if (limit == 0) {
                limit = RuntimeContext.getInstance().getOptionValueLong(Option.OPTION_DEFAULT_OVERDRAFT_LIMIT);
            }
            if (fieldConfig.getValue(ClientManager.FieldId.OVERDRAFT) != null) {
                //if (tokens.length >= 20 && StringUtils.isNotEmpty(tokens[19])) {
                limit = CurrencyStringUtils
                        .rublesToCopecks(fieldConfig.getValue(ClientManager.FieldId.OVERDRAFT));//tokens[19]);
            }
            String password = fieldConfig.getValue(ClientManager.FieldId.PASSWORD);//tokens[1];
            if (password.equals("X")) {
                password = "" + contractId;
            }

            boolean notifyByEmail = fieldConfig.getValueBool(ClientManager.FieldId.NOTIFY_BY_EMAIL);
            boolean notifyBySms = fieldConfig.getValueBool(ClientManager.FieldId.NOTIFY_BY_SMS);
            Date contractDate = fieldConfig
                    .getValueDate(ClientManager.FieldId.CONTRACT_DATE);//dateFormat.parse(tokens[3]);
            int contractState = fieldConfig.getValueInt(ClientManager.FieldId.CONTRACT_STATE);
            if (!Client.isValidContractState(contractState)) {
                throw new Exception(
                        "Ошибочное значение поля: " + fieldConfig.getField(FieldId.CONTRACT_STATE).fieldName + ": "
                                + contractState);
            }
            int payForSms = fieldConfig.getValueInt(ClientManager.FieldId.PAY_FOR_SMS);
            long expenditureLimit = RuntimeContext.getInstance()
                    .getOptionValueLong(Option.OPTION_DEFAULT_EXPENDITURE_LIMIT);
            if (fieldConfig.getValue(ClientManager.FieldId.EXPENDITURE_LIMIT) != null) {
                expenditureLimit = CurrencyStringUtils
                        .rublesToCopecks(fieldConfig.getValue(ClientManager.FieldId.EXPENDITURE_LIMIT));//tokens[19]);
            }
            Client client = new Client(organization, person, contractPerson, 0, notifyByEmail, notifyBySms, contractId,
                    contractDate, contractState, password, payForSms, clientRegistryVersion, limit, expenditureLimit,
                    "");

            client.setAddress(fieldConfig.getValue(ClientManager.FieldId.ADDRESS)); //tokens[12]);
            client.setPhone(fieldConfig.getValue(ClientManager.FieldId.PHONE));//tokens[13]);
            String mobilePhone = fieldConfig.getValue(ClientManager.FieldId.MOBILE_PHONE);
            if (mobilePhone != null) {
                mobilePhone = Client.checkAndConvertMobile(mobilePhone);
                if (mobilePhone == null) {
                    throw new Exception("Неправильный формат мобильного телефона");
                }
            }
            client.setMobile(mobilePhone);//tokens[14]);
            client.setEmail(fieldConfig.getValue(ClientManager.FieldId.EMAIL));//tokens[15]);
            client.setRemarks(fieldConfig.getValue(ClientManager.FieldId.COMMENTS));
            client.setSanWithConvert(fieldConfig.getValue(ClientManager.FieldId.SAN));
            //if (tokens.length >= 21) {
            //    client.setRemarks(tokens[20]);
            //}

            /* проверяется есть ли в загрузочном файле параметр для группы клиента (класс для ученика)*/
            if (fieldConfig.getValue(ClientManager.FieldId.GROUP) != null) {
                //if (tokens.length >=22){
                String clientGroupName = fieldConfig.getValue(ClientManager.FieldId.GROUP);//tokens[21];
                ClientGroup clientGroup = DAOUtils
                        .findClientGroupByGroupNameAndIdOfOrg(persistenceSession, idOfOrg, clientGroupName);
                if (clientGroup == null) {
                    clientGroup = DAOUtils.createNewClientGroup(persistenceSession, idOfOrg, clientGroupName);
                }
                client.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
            }
            if (fieldConfig.getValue(ClientManager.FieldId.CLIENT_GUID) != null) {
                String clientGUID = fieldConfig.getValue(ClientManager.FieldId.CLIENT_GUID);
                if (clientGUID.isEmpty()) {
                    client.setClientGUID(null);
                } else {
                    client.setClientGUID(clientGUID);
                }
            }

            persistenceSession.save(client);
            Long idOfClient = client.getIdOfClient();
            ///
            if (fieldConfig.getValue(ClientManager.FieldId.CARD_ID) != null && persistenceTransaction != null) {
                registerCardForClient(runtimeContext, persistenceSession, persistenceTransaction, fieldConfig,
                        idOfClient);
            }
            ///

            ClientMigration clientMigration = new ClientMigration(client, organization, contractDate);
            persistenceSession.save(clientMigration);
            return idOfClient;
        } catch (Exception e) {
            throw e;
        }
    }


    public static long registerClient(long idOfOrg, ClientFieldConfig fieldConfig, boolean checkFullNameUnique)
            throws Exception {
        fieldConfig.checkRequiredFields();

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            long idOfClient = registerClientTransactionFree(idOfOrg, fieldConfig, checkFullNameUnique,
                    persistenceSession, persistenceTransaction);

            persistenceTransaction.commit();
            persistenceTransaction = null;
            return idOfClient;
        } catch (Exception e) {
            logger.error("Ошибка при создании клиента", e);
            throw new Exception("Ошибка: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }


    public static boolean existClient(Session persistenceSession, Org organization, String firstName, String surname,
            String secondName) throws Exception {
        if (StringUtils.isEmpty(secondName)) {
            return DAOUtils.existClient(persistenceSession, organization, firstName, surname);
        }
        return DAOUtils.existClient(persistenceSession, organization, firstName, surname, secondName);
    }


    public static Client getClient(Session persistenceSession, Org organization, String firstName, String surname,
            String secondName) throws Exception {
        Long idOfClient = ClientManager
                .findClientByFullName(persistenceSession, organization, firstName, surname, secondName);
        if (idOfClient < 1 || idOfClient == null) {
            return null;
        }
        return DAOUtils.findClient(persistenceSession, idOfClient);
    }

    private static Long registerCardForClient(RuntimeContext runtimeContext, Session persistenceSession,
            Transaction persistenceTransaction, ClientManager.ClientFieldConfig fieldConfig, Long idOfClient)
            throws Exception {
        String sCardType = fieldConfig.getValue(ClientManager.FieldId.CARD_TYPE);
        int cardType;
        if (sCardType.length() > 0 && (sCardType.charAt(0) >= '0' && sCardType.charAt(0) <= '9')) {
            cardType = Integer.parseInt(sCardType);
        } else {
            cardType = Card.parseCardType(sCardType);
        }
        int state = Card.ACTIVE_STATE;
        int lifeState = Card.ISSUED_LIFE_STATE;
        Date validTime = fieldConfig.getValueDate(ClientManager.FieldId.CARD_EXPIRY);
        Date issueTime = fieldConfig.getValueDate(ClientManager.FieldId.CARD_ISSUED);
        String lockReason = null;
        long cardNo = fieldConfig.getValueLong(ClientManager.FieldId.CARD_ID);
        Long cardPrintedNo;
        if (fieldConfig.getValue(ClientManager.FieldId.CARD_PRINTED_NUM) != null) {
            cardPrintedNo = fieldConfig.getValueLong(ClientManager.FieldId.CARD_PRINTED_NUM);
        } else {
            cardPrintedNo = cardNo;
        }
        try {
            return runtimeContext.getCardManager()
                    .createCard(persistenceSession, persistenceTransaction, idOfClient, cardNo, cardType, state,
                            validTime, lifeState, lockReason, issueTime, cardPrintedNo);
        } catch (Exception e) {
            throw new Exception("Ошибка при создании карты: " + e);
        }

    }
}
