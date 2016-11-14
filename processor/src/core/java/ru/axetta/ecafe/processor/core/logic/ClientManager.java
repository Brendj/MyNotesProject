/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.items.ClientGroupsByRegExAndOrgItem;
import ru.axetta.ecafe.processor.core.client.items.ClientGuardianItem;
import ru.axetta.ecafe.processor.core.client.items.ClientMigrationItemInfo;
import ru.axetta.ecafe.processor.core.client.items.NotificationSettingItem;
import ru.axetta.ecafe.processor.core.partner.nsi.MskNSIService;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.core.utils.FieldProcessor;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

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
        NOTIFY_BY_PUSH,
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
        CLIENT_GUID,
        FAX,
        GENDER,
        BIRTH_DATE,
        BENEFIT_ON_ADMISSION,
        GUARDIANS_COUNT,
        GUARDIANS_COUNT_LIST,
        SSOID
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
            new FieldProcessor.Def(17, false, false, "Уведомление через PUSH", "1", FieldId.NOTIFY_BY_PUSH, true),
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
            new FieldProcessor.Def(31, false, false, "Факс", null, FieldId.FAX, true),
            new FieldProcessor.Def(32, false, false, "Пол", null, FieldId.GENDER, true),
            new FieldProcessor.Def(33, false, false, "Дата рождения", null, FieldId.BIRTH_DATE, true),
            new FieldProcessor.Def(34, false, false, "Льгота при поступлении", null, FieldId.BENEFIT_ON_ADMISSION, true),
            new FieldProcessor.Def(35, false, false, "Количество представителей", null, FieldId.GUARDIANS_COUNT, false),
            new FieldProcessor.Def(36, false, false, "Коллекция представителей", null, FieldId.GUARDIANS_COUNT_LIST, false),
            new FieldProcessor.Def(37, false, false, "SSOID", null, FieldId.SSOID, true),
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
        return modifyClientTransactionFree(fieldConfig, org, registerCommentsAdds, client, persistenceSession, false);
    }

    public static long modifyClientTransactionFree(ClientFieldConfigForUpdate fieldConfig, Org org,
            String registerCommentsAdds, Client client, Session persistenceSession, boolean updateSecondNameAnyway) throws Exception {
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
            if ((secondName != null && StringUtils.isNotEmpty(secondName)) || updateSecondNameAnyway) {
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
                logger.info("class : ClientManager, method : modifyClientTransactionFree line : 344, idOfClient : " + client.getIdOfClient() + " phone : " + client.getPhone());
            }
            //tokens[14])
            String mobilePhone = fieldConfig.getValue(ClientManager.FieldId.MOBILE_PHONE);
            if (mobilePhone != null && StringUtils.isNotEmpty(mobilePhone)) {
                mobilePhone = Client.checkAndConvertMobile(mobilePhone);
                if (mobilePhone == null) {
                    throw new Exception("Неправильный формат мобильного телефона");
                }
                //  если у клиента есть мобильный и он не совпадает с новым, то сбрсываем ССОИД для ЕМП
                if(client != null && client.getMobile() != null && !client.getMobile().equals(mobilePhone)) {
                    client.setSsoid("");
                }
                client.setMobile(mobilePhone);
                logger.info("class : ClientManager, method : modifyClientTransactionFree line : 358, idOfClient : " + client.getIdOfClient() + " mobile : " + client.getMobile());
            }
            String fax = fieldConfig.getValue(FieldId.FAX);
            if (fax != null && StringUtils.isNotEmpty(fax)) {
                fax = Client.checkAndConvertMobile(fax);
                if (fax == null) {
                    throw new Exception("Неправильный формат факса");
                }
                client.setFax(fax);
            }
            //tokens[15]);
            String email = fieldConfig.getValue(ClientManager.FieldId.EMAIL);
            if (email != null && StringUtils.isNotEmpty(email) && !RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_DISABLE_EMAIL_EDIT)) {
                //  если у клиента есть емайл и он не совпадает с новым, то сбрсываем ССОИД для ЕМП
                if(client != null && client.getEmail() != null && !client.getEmail().equals(email)) {
                    client.setSsoid("");
                }
                client.setEmail(email);
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
            //tokens[20])
            if (fieldConfig.getValue(FieldId.NOTIFY_BY_PUSH) != null) {
                client.setNotifyViaPUSH(fieldConfig.getValueBool(FieldId.NOTIFY_BY_PUSH));
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
                if (comments==null) comments="";
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
                if (fieldConfig.getValue(ClientManager.FieldId.GROUP).length() > 0) {
                    String clientGroupName = fieldConfig.getValue(ClientManager.FieldId.GROUP);

                    GroupNamesToOrgs groupNamesToOrgs = DAOUtils
                            .getAllGroupnamesToOrgsByIdOfMainOrgAndGroupName(persistenceSession, client.getOrg().getIdOfOrg(),
                                    clientGroupName);

                    if (groupNamesToOrgs != null && groupNamesToOrgs.getIdOfOrg() != null) {
                        ClientGroup clientGroup = DAOUtils
                                .findClientGroupByGroupNameAndIdOfOrgNotIgnoreCase(persistenceSession,
                                        groupNamesToOrgs.getIdOfOrg(), groupNamesToOrgs.getGroupName());
                        if (clientGroup == null) {
                            clientGroup = DAOUtils.createClientGroup(persistenceSession, groupNamesToOrgs.getIdOfOrg(),
                                    groupNamesToOrgs.getGroupName());
                        }
                        client.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                        Org org1 = (Org) persistenceSession.load(Org.class, groupNamesToOrgs.getIdOfOrg());
                        client.setOrg(org1);
                    } else {
                        ClientGroup clientGroup = DAOUtils
                                .findClientGroupByGroupNameAndIdOfOrgNotIgnoreCase(persistenceSession,
                                        client.getOrg().getIdOfOrg(), clientGroupName);
                        if (clientGroup == null) {
                            clientGroup = DAOUtils.createClientGroup(persistenceSession, client.getOrg().getIdOfOrg(),
                                    clientGroupName);
                        }
                        client.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                    }
                } else {
                    client.setIdOfClientGroup(null);
                }
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
            //tokens[32])
            if (fieldConfig.getValue(FieldId.GENDER) != null) {
                client.setGender(Integer.valueOf(fieldConfig.getValue(FieldId.GENDER)));
            }

            //token[33])
            if (fieldConfig.getValue(FieldId.BIRTH_DATE) != null) {
                String birthDate = fieldConfig.getValue(ClientManager.FieldId.BIRTH_DATE);
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                Date date = format.parse(birthDate);
                client.setBirthDate(date);
            }

            //token[34])
            if (fieldConfig.getValue(FieldId.BENEFIT_ON_ADMISSION) != null) {
                client.setBenefitOnAdmission(fieldConfig.getValue(FieldId.BENEFIT_ON_ADMISSION));
            }

            //token[35])
            if (fieldConfig.getValue(FieldId.GUARDIANS_COUNT) != null) {
                client.setGuardiansCount(fieldConfig.getValue(FieldId.GUARDIANS_COUNT));
            }

            client.setUpdateTime(new Date());

            long clientRegistryVersion = DAOUtils.updateClientRegistryVersionWithPessimisticLock();
            client.setClientRegistryVersion(clientRegistryVersion);
            persistenceSession.update(client);
            return client.getIdOfClient();
        } catch (Exception e) {
            logger.error("Ошибка при обновлении данных клиента", e);
            throw new Exception(e);
        }
    }
    
    public static boolean setCategories(Session session, Client cl, List<Long> idOfCategoryList) throws Exception {
        return setCategories(session, cl, idOfCategoryList, null);
    }

    public static boolean setCategories(Session session, Client cl, List<Long> idOfCategoryList,
                                        Integer discountMode) throws Exception {
        try {
            Set<CategoryDiscount> categories = new HashSet <CategoryDiscount>();
            if (idOfCategoryList != null && idOfCategoryList.size() > 0) {
                Criteria categoryCriteria = session.createCriteria(CategoryDiscount.class);
                categoryCriteria.add(Restrictions.in("idOfCategoryDiscount", idOfCategoryList));
                for (Object object : categoryCriteria.list()) {
                    CategoryDiscount categoryDiscount = (CategoryDiscount) object;
                    categories.add(categoryDiscount);
                }
            }
            if (discountMode != null) {
                cl.setDiscountMode(discountMode);
            }
            cl.setCategories(categories);
            session.save(cl);
            return true;
        } catch (Exception e) {
            logger.info("Не удалось установить категории для клиента");
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
            logger.error("Ошибка при обновлении данных клиента", e);
            throw new Exception(e);
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
            logger.debug("exist organization");
            //Org organization = DAOUtils.findOrgWithPessimisticLock(persistenceSession, idOfOrg);
            Org organization = DAOUtils.findOrg(persistenceSession, idOfOrg);
            if (null == organization) {
                throw new Exception("Организация не найдена: " + idOfOrg);
            }

            String firstName = fieldConfig.getValue(ClientManager.FieldId.NAME); //tokens[9];
            String surname = fieldConfig.getValue(ClientManager.FieldId.SURNAME); //tokens[8];
            String secondName = fieldConfig.getValue(ClientManager.FieldId.SECONDNAME); //tokens[10];

            logger.debug("exist client");
            if (checkFullNameUnique && existClient(persistenceSession, organization, firstName, surname, secondName)) {
                throw new ClientAlreadyExistException(
                        "Клиент с данными ФИО уже зарегистрирован в организации: " + surname + " " + firstName + " "
                                + secondName, null);
            }

            logger.debug("update version");
            long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(persistenceSession);
            /*String contractIdText = fieldConfig.getValue(ClientManager.FieldId.CONTRACT_ID); //tokens[0];
            long contractId;
            if (StringUtils.equals(contractIdText, "AUTO")) {
                logger.debug("generate ContractId");
                contractId = runtimeContext.getClientContractIdGenerator().generateTransactionFree(
                        organization.getIdOfOrg(), persistenceSession);
            } else {
                contractId = Long.parseLong(contractIdText);
            }*/

            logger.debug("create contractPerson");
            Person contractPerson = new Person(fieldConfig.getValue(ClientManager.FieldId.CONTRACT_NAME),
                    fieldConfig.getValue(ClientManager.FieldId.CONTRACT_SURNAME), fieldConfig.getValue(
                    ClientManager.FieldId.CONTRACT_SECONDNAME)); //new Person(tokens[5], tokens[4], tokens[6]);
            contractPerson.setIdDocument(fieldConfig.getValue(ClientManager.FieldId.CONTRACT_DOC));
            persistenceSession.save(contractPerson);
            logger.debug("create person");
            Person person = new Person(firstName, surname, secondName);
            person.setIdDocument(fieldConfig.getValue(ClientManager.FieldId.DOC));//tokens[11]);
            persistenceSession.save(person);

            logger.debug("set OVERDRAFT LIMIT");
            long limit = organization.getCardLimit();
            if (limit == 0) {
                limit = RuntimeContext.getInstance().getOptionValueLong(Option.OPTION_DEFAULT_OVERDRAFT_LIMIT);
            }
            if (fieldConfig.getValue(ClientManager.FieldId.OVERDRAFT) != null) {
                //if (tokens.length >= 20 && StringUtils.isNotEmpty(tokens[19])) {
                limit = CurrencyStringUtils
                        .rublesToCopecks(fieldConfig.getValue(ClientManager.FieldId.OVERDRAFT));//tokens[19]);
            }
            /*String password = fieldConfig.getValue(ClientManager.FieldId.PASSWORD);//tokens[1];
            if (password.equals("X")) {
                password = "" + contractId;
            }*/

            boolean notifyByEmail = fieldConfig.getValueBool(ClientManager.FieldId.NOTIFY_BY_EMAIL);
            boolean notifyBySms = fieldConfig.getValueBool(ClientManager.FieldId.NOTIFY_BY_SMS);
            boolean notifyByPUSH = fieldConfig.getValueBool(ClientManager.FieldId.NOTIFY_BY_PUSH);
            Date contractDate = fieldConfig
                    .getValueDate(ClientManager.FieldId.CONTRACT_DATE);//dateFormat.parse(tokens[3]);
            int contractState = fieldConfig.getValueInt(ClientManager.FieldId.CONTRACT_STATE);
            if (!Client.isValidContractState(contractState)) {
                throw new Exception(
                        "Ошибочное значение поля: " + fieldConfig.getField(FieldId.CONTRACT_STATE).fieldName + ": "
                                + contractState);
            }
            int payForSms = fieldConfig.getValueInt(ClientManager.FieldId.PAY_FOR_SMS);
            logger.debug("set EXPENDITURE LIMIT");
            long expenditureLimit = RuntimeContext.getInstance()
                    .getOptionValueLong(Option.OPTION_DEFAULT_EXPENDITURE_LIMIT);
            if (fieldConfig.getValue(ClientManager.FieldId.EXPENDITURE_LIMIT) != null) {
                expenditureLimit = CurrencyStringUtils
                        .rublesToCopecks(fieldConfig.getValue(ClientManager.FieldId.EXPENDITURE_LIMIT));//tokens[19]);
            }

            String contractIdText = fieldConfig.getValue(ClientManager.FieldId.CONTRACT_ID); //tokens[0];
            long contractId;
            if (StringUtils.equals(contractIdText, "AUTO")) {
                logger.debug("generate ContractId");
                contractId = runtimeContext.getClientContractIdGenerator().generateTransactionFree(organization.getIdOfOrg(), persistenceSession);
            } else {
                contractId = Long.parseLong(contractIdText);
            }

            String password = fieldConfig.getValue(ClientManager.FieldId.PASSWORD);//tokens[1];
            if (password.equals("X")) {
                password = "" + contractId;
            }

            logger.debug("create client");
            Client client = new Client(organization, person, contractPerson, 0, notifyByEmail, notifyBySms,
                    notifyByPUSH, contractId, contractDate, contractState, password, payForSms, clientRegistryVersion,
                    limit, expenditureLimit, "");

            client.setAddress(fieldConfig.getValue(ClientManager.FieldId.ADDRESS)); //tokens[12]);
            client.setPhone(fieldConfig.getValue(ClientManager.FieldId.PHONE));//tokens[13]);
            logger.info("class : ClientManager, method : registerClientTransactionFree line : 717, idOfClient : " + client.getIdOfClient() + " phone : " + client.getPhone());
            String mobilePhone = fieldConfig.getValue(ClientManager.FieldId.MOBILE_PHONE);
            String fax = fieldConfig.getValue(FieldId.FAX);
            if (mobilePhone != null) {
                mobilePhone = Client.checkAndConvertMobile(mobilePhone);
                if (mobilePhone == null) {
                    throw new Exception("Неправильный формат мобильного телефона");
                }
            }
            if (fax != null) {
                fax = Client.checkAndConvertMobile(fax);
                if (fax == null) {
                    throw new Exception("Неправильный формат факса");
                }
            }
            client.setMobile(mobilePhone);//tokens[14]);
            logger.info("class : ClientManager, method : registerClientTransactionFree line : 734, idOfClient : " + client.getIdOfClient() + " mobile : " + client.getMobile());
            client.setFax(fax);//tokens[14]);
            client.setEmail(fieldConfig.getValue(ClientManager.FieldId.EMAIL));//tokens[15]);
            client.setRemarks(fieldConfig.getValue(ClientManager.FieldId.COMMENTS));
            client.setSanWithConvert(fieldConfig.getValue(ClientManager.FieldId.SAN));
            //if (tokens.length >= 21) {
            //    client.setRemarks(tokens[20]);
            //}

            /* проверяется есть ли в загрузочном файле параметр для группы клиента (класс для ученика)*/
            logger.debug("set client Group");
            if (fieldConfig.getValue(ClientManager.FieldId.GROUP) != null) {
                //if (tokens.length >=22){
                if (fieldConfig.getValue(ClientManager.FieldId.GROUP).length() > 0) {
                    String clientGroupName = fieldConfig.getValue(ClientManager.FieldId.GROUP);//tokens[21];

                    GroupNamesToOrgs groupNamesToOrgs = DAOUtils
                            .getAllGroupnamesToOrgsByIdOfMainOrgAndGroupName(persistenceSession, idOfOrg,
                                    clientGroupName);

                    if (groupNamesToOrgs != null && groupNamesToOrgs.getIdOfOrg() != null) {
                        ClientGroup clientGroup = DAOUtils
                                .findClientGroupByGroupNameAndIdOfOrgNotIgnoreCase(persistenceSession,
                                        groupNamesToOrgs.getIdOfOrg(), groupNamesToOrgs.getGroupName());
                        if (clientGroup == null) {
                            clientGroup = DAOUtils.createClientGroup(persistenceSession, groupNamesToOrgs.getIdOfOrg(),
                                    groupNamesToOrgs.getGroupName());
                        }
                        client.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                        Org org1 = (Org) persistenceSession.load(Org.class, groupNamesToOrgs.getIdOfOrg());
                        client.setOrg(org1);
                    } else {
                        ClientGroup clientGroup = DAOUtils
                                .findClientGroupByGroupNameAndIdOfOrg(persistenceSession, idOfOrg, clientGroupName);
                        if (clientGroup == null) {
                            clientGroup = DAOUtils.createClientGroup(persistenceSession, idOfOrg, clientGroupName);
                        }
                        client.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                    }
                } else {
                    client.setIdOfClientGroup(null);
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

            //tokens[32])
            if (fieldConfig.getValue(FieldId.GENDER) != null) {
                if (fieldConfig.getValue(FieldId.GENDER).equals("m")) {
                    client.setGender(1);
                }
                if (fieldConfig.getValue(FieldId.GENDER).equals("f")) {
                    client.setGender(0);
                }
            }

            //token[33])
            if (fieldConfig.getValue(FieldId.BIRTH_DATE) != null) {
                String birthDate = fieldConfig.getValue(ClientManager.FieldId.BIRTH_DATE);
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                Date date = format.parse(birthDate);
                client.setBirthDate(date);
            }

            //token[34])
            if (fieldConfig.getValue(FieldId.BENEFIT_ON_ADMISSION) != null) {
                client.setBenefitOnAdmission(fieldConfig.getValue(FieldId.BENEFIT_ON_ADMISSION));
            }

            //token[35])
            if (fieldConfig.getValue(FieldId.GUARDIANS_COUNT) != null) {
                client.setGuardiansCount(fieldConfig.getValue(FieldId.GUARDIANS_COUNT));
            }

            if (fieldConfig.getValue(FieldId.SSOID) != null) {
                client.setSsoid(fieldConfig.getValue(FieldId.SSOID));
            }

            logger.debug("save client");
            persistenceSession.save(client);
            Long idOfClient = client.getIdOfClient();
            ///
            logger.debug("register client card");
            if (fieldConfig.getValue(ClientManager.FieldId.CARD_ID) != null && persistenceTransaction != null) {
                registerCardForClient(runtimeContext, persistenceSession, persistenceTransaction, fieldConfig,
                        idOfClient);
            }
            ///

            logger.debug("save clientMigration");
            ClientMigration clientMigration = new ClientMigration(client, organization, contractDate);

            persistenceSession.save(clientMigration);
            logger.debug("return");
            return idOfClient;
        } catch (Exception e) {
            throw e;
        }
    }

    public static void applyClientGuardians(RegistryChangeGuardians registryChangeGuardians, Session persistenceSession,
            Long idOfOrg, Long idOfClientChild) throws Exception {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        try {
            Org organization = DAOUtils.findOrg(persistenceSession, idOfOrg);

            if (null == organization) {
                throw new Exception("Организация не найдена: " + idOfOrg);
            }

            if (registryChangeGuardians.getApplied() == false) {

                if (registryChangeGuardians.getPhoneNumber() != null && !registryChangeGuardians.getPhoneNumber().isEmpty()) {

                    String mobilePhoneGuardianAno = registryChangeGuardians.getPhoneNumber();

                    if (mobilePhoneGuardianAno != null) {
                        mobilePhoneGuardianAno = Client.checkAndConvertMobile(mobilePhoneGuardianAno);
                        if (mobilePhoneGuardianAno == null) {
                            throw new Exception("Ошибка при создании представителя: Не верный формат мобильного телефона");
                        }
                    }

                    if (mobilePhoneGuardianAno != null && !mobilePhoneGuardianAno.isEmpty()) {

                        Client clientByMobile = DAOUtils.findClientByMobile(persistenceSession, mobilePhoneGuardianAno);

                        if (clientByMobile != null) {

                            Client child = (Client) persistenceSession.load(Client.class, idOfClientChild);

                            //проверка гуидов
                            if (clientByMobile.getOrg().getGuid().equals(child.getOrg().getGuid())) {

                                String relation = registryChangeGuardians.getRelationship();

                                ClientGuardianRelationType relationType = null;
                                for (ClientGuardianRelationType type : ClientGuardianRelationType.values()) {
                                    if (relation.equalsIgnoreCase(type.toString())) {
                                        relationType = type;
                                    }
                                }

                                //сохранение связки представителя
                                Long newGuardiansVersions = ClientManager
                                        .generateNewClientGuardianVersion(persistenceSession);
                                ClientGuardian clientGuardian = new ClientGuardian(child.getIdOfClient(),
                                        clientByMobile.getIdOfClient());
                                clientGuardian.setVersion(newGuardiansVersions);
                                clientGuardian.setDisabled(true);
                                clientGuardian.setDeletedState(false);
                                clientGuardian.setRelation(relationType);
                                persistenceSession.persist(clientGuardian);
                                persistenceSession.flush();

                                setAppliedRegistryChangeGuardian(persistenceSession, registryChangeGuardians);

                            } else {
                                applyGuardians(registryChangeGuardians, persistenceSession, runtimeContext,
                                        organization, idOfClientChild);
                            }
                        } else {
                            applyGuardians(registryChangeGuardians, persistenceSession, runtimeContext, organization,
                                    idOfClientChild);
                        }
                    } else {
                        applyGuardians(registryChangeGuardians, persistenceSession, runtimeContext, organization,
                                idOfClientChild);
                    }
                } else {
                    applyGuardians(registryChangeGuardians, persistenceSession, runtimeContext, organization,
                            idOfClientChild);
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public static void applyGuardians(RegistryChangeGuardians registryChangeGuardians, Session persistenceSession, RuntimeContext runtimeContext, Org organization, Long idOfClientChild) throws Exception{
        Person personGuardian = new Person(registryChangeGuardians.getFirstName(),
                registryChangeGuardians.getFamilyName(), registryChangeGuardians.getSecondName());
        personGuardian.setIdDocument("");
        persistenceSession.persist(personGuardian);
        Person contractGuardianPerson = new Person("", "", "");
        contractGuardianPerson.setIdDocument(null);
        persistenceSession.persist(contractGuardianPerson);

        Date currentDate = new Date();

        Long contractIdGuardian = runtimeContext.getClientContractIdGenerator()
                .generateTransactionFree(organization.getIdOfOrg(), persistenceSession);

        long clientRegistryVersionGuardian = DAOUtils.updateClientRegistryVersion(persistenceSession);
        Long limitGuardian = RuntimeContext.getInstance()
                .getOptionValueLong(Option.OPTION_DEFAULT_OVERDRAFT_LIMIT);

        Client clientGuardianToSave = new Client(organization, personGuardian, contractGuardianPerson, 0, false,
                false, false, contractIdGuardian, currentDate, 0, "" + contractIdGuardian, 0,
                clientRegistryVersionGuardian, limitGuardian,
                RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_DEFAULT_EXPENDITURE_LIMIT), "");

        ClientGroup clientGroup = findGroupByIdOfOrgAndGroupName(persistenceSession, organization.getIdOfOrg(),
                "Родители");

        if (clientGroup != null) {
            clientGuardianToSave
                    .setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
        }

        String mobilePhoneGuardian = registryChangeGuardians.getPhoneNumber();

        if (mobilePhoneGuardian != null) {
            mobilePhoneGuardian = Client.checkAndConvertMobile(mobilePhoneGuardian);
            if (mobilePhoneGuardian == null) {
                throw new Exception("Ошибка при создании представителя: Не верный формат мобильного телефона");
            }
        }

        clientGuardianToSave.setMobile(mobilePhoneGuardian);
        logger.info("class : ClientManager, method : applyGuardians line : 959, idOfClient : " + clientGuardianToSave.getIdOfClient() + " mobile : " + clientGuardianToSave.getMobile());
        clientGuardianToSave.setAddress("");
        //clientGuardianToSave.setEmail(registryChangeGuardians.getEmailAddress());
        clientGuardianToSave.setDiscountMode(Client.DISCOUNT_MODE_NONE);
        String dateString = new SimpleDateFormat("dd.MM.yyyy").format(new Date(System.currentTimeMillis()));
        clientGuardianToSave.setRemarks(String.format(MskNSIService.COMMENT_AUTO_CREATE, dateString));
        persistenceSession.persist(clientGuardianToSave);

        String relation = registryChangeGuardians.getRelationship();

        ClientGuardianRelationType relationType = null;
        for (ClientGuardianRelationType type : ClientGuardianRelationType.values()) {
            if (relation.equalsIgnoreCase(type.toString())) {
                relationType = type;
            }
        }

        //сохранение связки представителя
        Long newGuardiansVersions = ClientManager.generateNewClientGuardianVersion(persistenceSession);
        ClientGuardian clientGuardian = new ClientGuardian(idOfClientChild,
                clientGuardianToSave.getIdOfClient());
        clientGuardian.setVersion(newGuardiansVersions);
        clientGuardian.setDisabled(true);
        clientGuardian.setDeletedState(false);
        clientGuardian.setRelation(relationType);
        persistenceSession.persist(clientGuardian);
        persistenceSession.flush();

        setAppliedRegistryChangeGuardian(persistenceSession, registryChangeGuardians);
    }


    public static long registerClient(long idOfOrg, ClientFieldConfig fieldConfig, boolean checkFullNameUnique)
            throws Exception {
        logger.debug("checkRequiredFields");
        fieldConfig.checkRequiredFields();

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            logger.debug("registerClientTransactionFree");
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

    /**
     * Метод возвращает список клиентов, ко/ые для данной школы являются чужими.
     * Этими клиентами могут быть ученики, администрация и т.д. из чужой школы.
     * @param session - экземпляр Session.
     * @param destinationOrg - организация (школа), в ко/ой ищем чужих клиентов.
     * @return - хэш-мап клиентов. "RegularClients" - ключ для постоянных клиентов.
     *                            "TemporaryClients" - ключ для временных клиентов.
     */

    @SuppressWarnings("unchecked")
    public static Map<String, Set<Client>> findAllocatedClients(Session session, Org destinationOrg) {
        Map<String, Set<Client>> res = new HashMap<String, Set<Client>>();
        res.put("RegularClients", new HashSet<Client>());
        res.put("TemporaryClients", new HashSet<Client>());
        Criteria cr = session.createCriteria(ClientAllocationRule.class);
        cr.add(Restrictions.eq("destinationOrg", destinationOrg));
        List<ClientAllocationRule> list = cr.list();
        for (ClientAllocationRule rule : list) {
            Set<Client> clientSet = rule.isTempClient() ? res.get("TemporaryClients") : res.get("RegularClients");
            Org clientOrg = rule.getSourceOrg();
            //if (clientOrg.getFriendlyOrg().isEmpty()) {
            //    clientSet.addAll(findMatchedAllocatedClients(clientOrg, rule.getGroupFilter()));
            //} else {
            //    for (Org friendlyOrg : clientOrg.getFriendlyOrg()) {
            //        clientSet.addAll(findMatchedAllocatedClients(friendlyOrg, rule.getGroupFilter()));
            //    }
            //}
            final Set<Org> friendlyOrg = clientOrg.getFriendlyOrg();
            List<Long> idOfOrgList = new ArrayList<Long>(friendlyOrg.size());
            for (Org org : friendlyOrg) {
                idOfOrgList.add(org.getIdOfOrg());
            }
            List<ClientGroupsByRegExAndOrgItem> idOfClientGroupsList = findMatchedClientGroupsByRegExAndOrg(session, idOfOrgList, rule.getGroupFilter());
            List<Client> clients = findClientsByInOrgAndInGroups(session, idOfClientGroupsList);
            clientSet.addAll(clients);
        }
        return res;
    }

    //public static List<Client> findMatchedAllocatedClients(Org clientOrg, String regExp) {
    //    List<Client> res = new ArrayList<Client>();
    //    for (Client client : clientOrg.getClients()) {
    //        boolean addClient =
    //                client.getClientGroup() != null && client.getClientGroup().getGroupName().matches(regExp);
    //        if (addClient) {
    //            res.add(client);
    //        }
    //    }
    //    return res;
    //}

    public static List<Client> findMatchedAllocatedClients(Session session, Long idOfOrg, String regExp) {
        List<Client> res = new ArrayList<Client>();

        String sql = "SELECT idofclientgroup FROM cf_clientgroups where groupname ~ '"+regExp+"'";
        Query query = session.createSQLQuery(sql);
        List idOfClientGroupResult = query.list();
        List<Long> idOfClientGroups = new ArrayList<Long>(idOfClientGroupResult.size());
        for (Object obj : idOfClientGroupResult){
            Long value = Long.valueOf(obj.toString());
            idOfClientGroups.add(value);
        }

        Criteria criteria = session.createCriteria(Client.class);
        criteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
        criteria.add(Restrictions.isNotNull("idOfClientGroup"));
        criteria.add(Restrictions.in("idOfClientGroup", idOfClientGroups));
        List list = criteria.list();
        for (Object obj : list) {
            Client client = (Client) obj;
            res.add(client);
        }
        return res;
    }

    public static List<ClientGroupsByRegExAndOrgItem> findMatchedClientGroupsByRegExAndOrg(Session session, List<Long> idOfOrg, String regExp) {

        List<String> regExpList = regExpParse(regExp);

        String sql = "SELECT idofclientgroup, idoforg FROM cf_clientgroups where groupname in(:regExp) and idoforg in (:idoforg)";
        Query query = session.createSQLQuery(sql);
        query.setParameterList("idoforg", idOfOrg);
        query.setParameterList("regExp", regExpList);
        List idOfClientGroupResult = query.list();

        List<ClientGroupsByRegExAndOrgItem> idOfClientGroupsList = new ArrayList<ClientGroupsByRegExAndOrgItem>();

        for (Object obj : idOfClientGroupResult){
            Object[] resultItem = (Object[]) obj;

            Long groupId = Long.valueOf(resultItem[0].toString());
            Long idOfOrgN = Long.valueOf(resultItem[1].toString());

            ClientGroupsByRegExAndOrgItem clientGroupsByRegExAndOrgItem = new ClientGroupsByRegExAndOrgItem(idOfOrgN, groupId);

            idOfClientGroupsList.add(clientGroupsByRegExAndOrgItem);
        }
        return idOfClientGroupsList;
    }

    public static List<String> regExpParse(String regExp) {
        String[] regExpList = regExp.split(",");

        List<String> regexpListTrimed = new ArrayList<String>();

        for (String str: regExpList) {
            regexpListTrimed.add(str.trim());
        }

        return regexpListTrimed;
    }

    public static List<Client> findClientsByInOrgAndInGroups(Session session, List<ClientGroupsByRegExAndOrgItem> idOfClientGroupList) {
        List<Client> res = new ArrayList<Client>();

        for (ClientGroupsByRegExAndOrgItem clientGroupsByRegExAndOrgItem : idOfClientGroupList) {

            Criteria criteria = session.createCriteria(Client.class);
            criteria.add(Restrictions.eq("org.idOfOrg", clientGroupsByRegExAndOrgItem.getIdOfOrg()));
            criteria.add(Restrictions.isNotNull("idOfClientGroup"));
            criteria.add(Restrictions.eq("idOfClientGroup", clientGroupsByRegExAndOrgItem.getIdOfClientGroup()));
            List list = criteria.list();
            for (Object obj : list) {
                Client client = (Client) obj;
                res.add(client);
            }
        }
        return res;
    }

    public static void updateClientVersionTransactional(Session session, Collection<Client> clients) {
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            updateClientVersion(session, clients);
            tr.commit();
        } catch (Exception ex) {
            HibernateUtils.rollback(tr, logger);
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static void updateClientVersion(Session session, Collection<Client> clients) throws Exception {
        for (Client client : clients) {
            Long version = DAOUtils.updateClientRegistryVersion(session);
            client.setClientRegistryVersion(version);
            session.update(client);
        }
    }

    public static void updateClientVersionBatch(Session session, Long idOfOrg) throws Exception {
        Long version = DAOUtils.updateClientRegistryVersion(session);
        Query q = session.createSQLQuery("UPDATE cf_clients SET clientregistryversion = :version where idoforg = :idoforg");
        q.setParameter("version", version);
        q.setParameter("idoforg", idOfOrg);
        q.executeUpdate();
    }

    public static List<Client> findAllAllocatedClients(Session session, Org destinationOrg) {
        Map<String, Set<Client>> map = findAllocatedClients(session, destinationOrg);
        List<Client> res = new ArrayList<Client>();
        for (Set<Client> set : map.values()) {
            res.addAll(set);
        }
        return res;
    }


    /* Загрузить список  */
    public static List<ClientGuardianItem> loadGuardiansByClient(Session session, Long idOfClient) throws Exception {
        Criteria criteria = session.createCriteria(ClientGuardian.class);
        criteria.add(Restrictions.eq("idOfChildren", idOfClient));
        criteria.add(Restrictions.eq("deletedState", false));
        List results = criteria.list();
        List<ClientGuardianItem> guardianItems = new ArrayList<ClientGuardianItem>(results.size());
        for (Object o: results){
            ClientGuardian clientGuardian = (ClientGuardian) o;
            Client cl = DAOUtils.findClient(session, clientGuardian.getIdOfGuardian());
            if(cl != null){
                List<NotificationSettingItem> notificationSettings = getNotificationSettings(clientGuardian);
                guardianItems.add(new ClientGuardianItem(cl, clientGuardian.isDisabled(), clientGuardian.getRelation(), notificationSettings));
            }
        }
        return guardianItems;
    }

    public static List<ClientGuardianItem> loadWardsByClient(Session session, Long idOfClient) throws Exception {
        Criteria criteria = session.createCriteria(ClientGuardian.class);
        criteria.add(Restrictions.eq("idOfGuardian", idOfClient));
        criteria.add(Restrictions.eq("deletedState", false));
        List results = criteria.list();
        List<ClientGuardianItem> wardItems = new ArrayList<ClientGuardianItem>(results.size());
        for (Object o: results){
            ClientGuardian clientWard = (ClientGuardian) o;
            Client cl = DAOUtils.findClient(session, clientWard.getIdOfChildren());
            if(cl != null){
                List<NotificationSettingItem> notificationSettings = getNotificationSettings(clientWard);
                wardItems.add(new ClientGuardianItem(cl, clientWard.isDisabled(), clientWard.getRelation(), notificationSettings));
            }
        }
        return wardItems;
    }

    public static List<NotificationSettingItem> getNotificationSettings(ClientGuardian clientGuardian) {
        Set<ClientGuardianNotificationSetting> settings = clientGuardian.getNotificationSettings();
        List<NotificationSettingItem> notificationSettings = new ArrayList<NotificationSettingItem>();
        for (ClientGuardianNotificationSetting.Predefined predefined : ClientGuardianNotificationSetting.Predefined.values()) {
            if (predefined.getValue().equals(ClientGuardianNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                continue;
            }
            notificationSettings.add(new NotificationSettingItem(predefined, settings));
        }
        return notificationSettings;
    }

    public static List<NotificationSettingItem> getNotificationSettings() {
        List<NotificationSettingItem> notificationSettings = new ArrayList<NotificationSettingItem>();
        for (ClientGuardianNotificationSetting.Predefined predefined : ClientGuardianNotificationSetting.Predefined.values()) {
            if (predefined.getValue().equals(ClientGuardianNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                continue;
            }
            notificationSettings.add(new NotificationSettingItem(predefined));
        }
        return notificationSettings;
    }

    @SuppressWarnings("unchecked")
    /* получить список опекунов за исключением идентифткатора опекуна idOfGuardian
    * если идентификатор опекуна пуст то выведутся все опекуны*/
    public static List<Client> findGuardiansByClient(Session session, Long idOfChildren, Long idOfGuardian) throws Exception {
        List<Client> clients = new ArrayList<Client>();
        DetachedCriteria idOfGuardianCriteria = DetachedCriteria.forClass(ClientGuardian.class);
        idOfGuardianCriteria.add(Restrictions.eq("idOfChildren", idOfChildren));
        idOfGuardianCriteria.add(Restrictions.ne("deletedState", true));
        idOfGuardianCriteria.add(Restrictions.eq("disabled", false));
        if(idOfGuardian!=null){
            idOfGuardianCriteria.add(Restrictions.ne("idOfGuardian", idOfGuardian));
        }
        idOfGuardianCriteria.setProjection(Property.forName("idOfGuardian"));

        Criteria clientCriteria = session.createCriteria(Client.class);
        clientCriteria.add(Property.forName("idOfClient").in(idOfGuardianCriteria));
        clients = clientCriteria.list();

        return clients;
    }

    /* получить список опекунов по опекаемому */
    public static List<Client> findGuardiansByClient(Session session, Long idOfChildren) throws Exception {
        List<Client> clients = new ArrayList<Client>();
        DetachedCriteria idOfGuardianCriteria = DetachedCriteria.forClass(ClientGuardian.class);
        idOfGuardianCriteria.add(Restrictions.eq("idOfChildren", idOfChildren));
        idOfGuardianCriteria.add(Restrictions.ne("deletedState", true));
        idOfGuardianCriteria.setProjection(Property.forName("idOfGuardian"));
        Criteria subCriteria = idOfGuardianCriteria.getExecutableCriteria(session);
        Integer countResult = subCriteria.list().size();
        if(countResult>0){
            Criteria clientCriteria = session.createCriteria(Client.class);
            clientCriteria.add(Property.forName("idOfClient").in(idOfGuardianCriteria));
            clients = clientCriteria.list();
        }
        return clients;
    }

    /* получить список опекаемых по опекуну */
    public static List<Client> findChildsByClient(Session session, Long idOfGuardian) throws Exception {
        List<Client> clients = new ArrayList<Client>();
        DetachedCriteria idOfGuardianCriteria = DetachedCriteria.forClass(ClientGuardian.class);
        idOfGuardianCriteria.add(Restrictions.eq("idOfGuardian", idOfGuardian));
        idOfGuardianCriteria.add(Restrictions.ne("deletedState", true));
        idOfGuardianCriteria.setProjection(Property.forName("idOfChildren"));
        Criteria subCriteria = idOfGuardianCriteria.getExecutableCriteria(session);
        Integer countResult = subCriteria.list().size();
        if(countResult>0){
            Criteria clientCriteria = session.createCriteria(Client.class);
            clientCriteria.add(Property.forName("idOfClient").in(idOfGuardianCriteria));
            clients = clientCriteria.list();
        }
        return clients;
    }

    /* Является ли опекунская связь Опекун-Клиент активной*/
    public static Boolean isGuardianshipDisabled(Session session, Long guardianId, Long clientId) {
        Criteria criteria = session.createCriteria(ClientGuardian.class);
        criteria.add(Restrictions.eq("idOfGuardian", guardianId));
        criteria.add(Restrictions.eq("idOfChildren", clientId));
        ClientGuardian cg = (ClientGuardian)criteria.uniqueResult();
        if (cg == null) return false;
        return cg.isDisabled();
    }

    /*Является ли опекунская связь Опекун-Клиент активной и включен ли в ней нужный тип оповещения*/
    public static Boolean allowedGuardianshipNotification(Long guardianId, Long clientId,
            Long notifyType) throws Exception {
        return DAOReadonlyService.getInstance().allowedGuardianshipNotification(guardianId, clientId, notifyType);
    }

    /* Удалить список опекунов клиента */
    public static void removeGuardiansByClient(Session session, Long idOfClient, List<ClientGuardianItem> clientGuardians) {
        Long version = generateNewClientGuardianVersion(session);
        for (ClientGuardianItem item: clientGuardians){
            removeGuardianByClient(session, idOfClient, item.getIdOfClient(), version);
        }
    }

    /* Удалить список опекаемых клиента */
    public static void removeWardsByClient(Session session, Long idOfClient, List<ClientGuardianItem> clientWards) {
        Long version = generateNewClientGuardianVersion(session);
        for (ClientGuardianItem item: clientWards){
            removeGuardianByClient(session, item.getIdOfClient(), idOfClient, version);
        }
    }

    /* Удалить опекуна клиента */
    public static void removeGuardianByClient(Session session, Long idOfChildren, Long idOfGuardian, Long version) {
        Criteria criteria = session.createCriteria(ClientGuardian.class);
        criteria.add(Restrictions.eq("idOfChildren", idOfChildren));
        criteria.add(Restrictions.eq("idOfGuardian", idOfGuardian));
        ClientGuardian clientGuardian = (ClientGuardian) criteria.uniqueResult();
        if(clientGuardian!=null) {
            clientGuardian.delete(version);
            session.update(clientGuardian);
        }
    }

    /* Добавить список опекунов клиента */
    public static void addGuardiansByClient(Session session, Long idOfClient, List<ClientGuardianItem> clientGuardians) {
        Long newGuardiansVersions = generateNewClientGuardianVersion(session);
        for (ClientGuardianItem item : clientGuardians) {
            addGuardianByClient(session, idOfClient, item.getIdOfClient(), newGuardiansVersions, item.getDisabled(),
                    ClientGuardianRelationType.fromInteger(item.getRelation()), item.getNotificationItems());
        }
    }

    /* Добавить опекуна клиенту */
    public static void addGuardianByClient(Session session, Long idOfChildren, Long idOfGuardian, Long version, Boolean disabled,
            ClientGuardianRelationType relation, List<NotificationSettingItem> notificationItems) {
        Criteria criteria = session.createCriteria(ClientGuardian.class);
        criteria.add(Restrictions.eq("idOfChildren", idOfChildren));
        criteria.add(Restrictions.eq("idOfGuardian", idOfGuardian));
        ClientGuardian clientGuardian = (ClientGuardian) criteria.uniqueResult();
        if (clientGuardian == null) {
            clientGuardian = new ClientGuardian(idOfChildren, idOfGuardian);
            clientGuardian.setVersion(version);
            clientGuardian.setDisabled(disabled);
            clientGuardian.setDeletedState(false);
            clientGuardian.setRelation(relation);
            attachNotifications(clientGuardian, notificationItems);
            session.persist(clientGuardian);
        } else {
            clientGuardian.setVersion(version);
            clientGuardian.setDisabled(disabled);
            clientGuardian.setDeletedState(false);
            clientGuardian.setRelation(relation);
            attachNotifications(clientGuardian, notificationItems);
            session.update(clientGuardian);
        }
    }

    public static void attachNotifications(ClientGuardian clientGuardian, List<NotificationSettingItem> notificationItems) {
        if (notificationItems == null) return;
        Set<ClientGuardianNotificationSetting> dbSettings = clientGuardian.getNotificationSettings();
        for (NotificationSettingItem item : notificationItems) {
            ClientGuardianNotificationSetting newSetting = new ClientGuardianNotificationSetting(clientGuardian, item.getNotifyType());
            createOrRemoveSetting(dbSettings, newSetting, item.isEnabled());
        }
        boolean defaultPredefineFound = false;
        for (ClientGuardianNotificationSetting dbSetting : dbSettings) {
            if (dbSetting.getNotifyType().equals(ClientGuardianNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                defaultPredefineFound = true;
                break;
            }
        }
        if (!defaultPredefineFound) {
            ClientGuardianNotificationSetting newSetting =
                    new ClientGuardianNotificationSetting(clientGuardian, ClientGuardianNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue());
            dbSettings.add(newSetting);
        }
    }

    private static void createOrRemoveSetting(Set<ClientGuardianNotificationSetting> dbSettings,
                                                                    ClientGuardianNotificationSetting newSetting, Boolean enabled) {
        for (ClientGuardianNotificationSetting dbSetting : dbSettings) {
            if (dbSetting.getNotifyType().equals(newSetting.getNotifyType())) {
                if (!enabled) {
                    dbSettings.remove(dbSetting);
                }
                return;
            }
        }
        if (enabled) {
            dbSettings.add(newSetting);
        }
    }

    public static void addWardsByClient(Session session, Long idOfClient, List<ClientGuardianItem> clientWards) {
        Long newGuardiansVersions = generateNewClientGuardianVersion(session);
        for (ClientGuardianItem item : clientWards) {
            addGuardianByClient(session, item.getIdOfClient(), idOfClient, newGuardiansVersions, item.getDisabled(),
                    ClientGuardianRelationType.fromInteger(item.getRelation()), item.getNotificationItems());
        }
    }

    public static Long generateNewClientGuardianVersion(Session session) {
        Long version = 0L;
        try {
            Criteria criteria = session.createCriteria(ClientGuardian.class);
            criteria.setProjection(Projections.max("version"));
            Object result = criteria.uniqueResult();
            if (result != null) {
                Long currentMaxVersion = (Long) result;
                version = currentMaxVersion + 1;
            }
        } catch (Exception ex) {
            logger.error("Failed get max client guardians version, ", ex);
            version = 0L;
        }
        return version;
    }

    /* История миграции клиента */
    public static List<ClientMigrationItemInfo> reloadMigrationInfoByClient(Session session, Long idOfClient){
        Criteria criteria = session.createCriteria(ClientMigration.class);
        criteria.createCriteria("client").add(Restrictions.eq("idOfClient", idOfClient));
        List<ClientMigration> clientMigrations = criteria.list();
        List<ClientMigrationItemInfo> clientMigrationItemInfoList = new ArrayList<ClientMigrationItemInfo>(clientMigrations.size());
        for (ClientMigration clientMigration: clientMigrations){
            clientMigrationItemInfoList.add(new ClientMigrationItemInfo(clientMigration));
        }
        return clientMigrationItemInfoList;

    }

    /* Разногласия по родителям после принятия applied = true */
    public static void setAppliedRegistryChangeGuardian(Session session, RegistryChangeGuardians registryChangeGuardians) {
        registryChangeGuardians.setApplied(true);
        session.update(registryChangeGuardians);
    }

    public static String firstUpperCase(String word){
        if(word == null || word.isEmpty()) return "";
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }


    private static ClientGroup findGroupByIdOfOrgAndGroupName(Session session, Long idOfOrg, String groupName) throws Exception {
        Criteria criteria = session.createCriteria(ClientGroup.class);
        criteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
        criteria.add(Restrictions.eq("groupName", groupName));
        return (ClientGroup) criteria.uniqueResult();
    }

}
