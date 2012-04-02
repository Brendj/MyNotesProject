/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.RuntimeContext;
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
        SAN
    }

    static FieldProcessor.Def[] fieldInfo={
            new FieldProcessor.Def(0, false, "Номер договора", "AUTO", FieldId.CONTRACT_ID),
            new FieldProcessor.Def(1, false, "Пароль", "X", FieldId.PASSWORD),
            new FieldProcessor.Def(2, false, "Статус", "1", FieldId.CONTRACT_STATE),
            new FieldProcessor.Def(3, false, "Дата заключения", "#CURRENT_DATE", FieldId.CONTRACT_DATE),
            new FieldProcessor.Def(4, false, "Договор-фамилия", "", FieldId.CONTRACT_SURNAME),
            new FieldProcessor.Def(5, false, "Договор-имя", "", FieldId.CONTRACT_NAME),
            new FieldProcessor.Def(6, false, "Договор-отчество", "", FieldId.CONTRACT_SECONDNAME),
            new FieldProcessor.Def(7, false, "Договор-документ", null, FieldId.CONTRACT_DOC),
            new FieldProcessor.Def(8, true,  "Фамилия", null, FieldId.SURNAME),
            new FieldProcessor.Def(9, true,  "Имя", null, FieldId.NAME),
            new FieldProcessor.Def(10, true, "Отчество", null, FieldId.SECONDNAME),
            new FieldProcessor.Def(11, false, "Документ", "", FieldId.DOC),
            new FieldProcessor.Def(12, false, "Адрес", "", FieldId.ADDRESS),
            new FieldProcessor.Def(13, false, "Телефон", null, FieldId.PHONE),
            new FieldProcessor.Def(14, false, "Мобильный", null, FieldId.MOBILE_PHONE),
            new FieldProcessor.Def(15, false, "E-mail", null, FieldId.EMAIL),
            new FieldProcessor.Def(16, false, "Платный SMS", "0", FieldId.PAY_FOR_SMS),
            new FieldProcessor.Def(17, false, "Уведомление по SMS", "0", FieldId.NOTIFY_BY_SMS),
            new FieldProcessor.Def(18, false, "Уведомление по e-mail", "0", FieldId.NOTIFY_BY_EMAIL),
            new FieldProcessor.Def(19, false, "Овердрафт", "0", FieldId.OVERDRAFT),
            new FieldProcessor.Def(20, false, "Комментарии", null, FieldId.COMMENTS),
            new FieldProcessor.Def(21, false, "Группа", null, FieldId.GROUP),
            new FieldProcessor.Def(22, false, "СНИЛС", null, FieldId.SAN),
            new FieldProcessor.Def(23, false, "Дневной лимит", "0", FieldId.EXPENDITURE_LIMIT),
            new FieldProcessor.Def(24, false, "Карта-ид", null, FieldId.CARD_ID),
            new FieldProcessor.Def(25, false, "Карта-номер", null, FieldId.CARD_PRINTED_NUM),
            new FieldProcessor.Def(26, false, "Карта-тип", null, FieldId.CARD_TYPE),
            new FieldProcessor.Def(27, false, "Карта-выдана", "#CURRENT_DATE", FieldId.CARD_ISSUED),
            new FieldProcessor.Def(28, false, "Карта-срок", "#5", FieldId.CARD_EXPIRY)
    };

    public static class ClientFieldConfig extends FieldProcessor.Config {

        public ClientFieldConfig() {
            super(fieldInfo);
        }

        @Override
        public void checkRequiredFields() throws Exception {
            if (nFields>0) {
                for (FieldProcessor.Def fd : currentConfig) {
                    if (fd.required && fd.realPos==-1) throw new Exception("В списке полей отсутствует обязательное поле: "+fd.fieldName);
                }
                if (getField(FieldId.CARD_ID).realPos!=-1) {
                    if (getField(FieldId.CARD_TYPE).realPos==-1) throw new Exception("В списке полей отсутствует обязательное поле: "+getField(FieldId.CARD_TYPE).fieldName);
                }
            }
        }

        public void setValue(Object id, Object value) throws Exception {
            if (value instanceof Date) getField(id).currentValue = dateFormat.format((Date)value);
            else getField(id).currentValue = value.toString();
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

            Org organization = DAOUtils.findOrg(persistenceSession, idOfOrg);
            if (null == organization) {
                throw new Exception("Организация не найдена: "+idOfOrg);
            }

            String firstName = fieldConfig.getValue(ClientManager.FieldId.NAME); //tokens[9];
            String surname = fieldConfig.getValue(ClientManager.FieldId.SURNAME); //tokens[8];
            String secondName = fieldConfig.getValue(ClientManager.FieldId.SECONDNAME); //tokens[10];

            if (checkFullNameUnique && existClient(persistenceSession, organization, firstName, surname, secondName)) {
                throw new Exception("Клиент с данными ФИО уже зарегистрирован в организации", null);
            }

            long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(persistenceSession);
            String contractIdText = fieldConfig.getValue(ClientManager.FieldId.CONTRACT_ID); //tokens[0];
            long contractId;
            if (StringUtils.equals(contractIdText, "AUTO")) {
                contractId = runtimeContext.getClientContractIdGenerator().generate(organization.getIdOfOrg());
            } else {
                contractId = Long.parseLong(contractIdText);
            }

            Person contractPerson = new Person(fieldConfig.getValue(ClientManager.FieldId.CONTRACT_NAME), fieldConfig.getValue(
                    ClientManager.FieldId.CONTRACT_SURNAME), fieldConfig.getValue(
                    ClientManager.FieldId.CONTRACT_SECONDNAME)); //new Person(tokens[5], tokens[4], tokens[6]);
            contractPerson.setIdDocument(fieldConfig.getValue(ClientManager.FieldId.CONTRACT_DOC));
            persistenceSession.save(contractPerson);
            Person person = new Person(firstName, surname, secondName);
            person.setIdDocument(fieldConfig.getValue(ClientManager.FieldId.DOC));//tokens[11]);
            persistenceSession.save(person);

            long limit = organization.getCardLimit();
            if (fieldConfig.getValue(ClientManager.FieldId.OVERDRAFT)!=null) {
            //if (tokens.length >= 20 && StringUtils.isNotEmpty(tokens[19])) {
                limit = CurrencyStringUtils.rublesToCopecks(fieldConfig.getValue(ClientManager.FieldId.OVERDRAFT));//tokens[19]);
            }
            String password = fieldConfig.getValue(ClientManager.FieldId.PASSWORD);//tokens[1];
            if (password.equals("X")) password = ""+contractId;

            boolean notifyByEmail = fieldConfig.getValueBool(ClientManager.FieldId.NOTIFY_BY_EMAIL);
            boolean notifyBySms = fieldConfig.getValueBool(ClientManager.FieldId.NOTIFY_BY_SMS);
            Date contractDate = fieldConfig.getValueDate(ClientManager.FieldId.CONTRACT_DATE);//dateFormat.parse(tokens[3]);
            int contractState = fieldConfig.getValueInt(ClientManager.FieldId.CONTRACT_STATE);
            int payForSms = fieldConfig.getValueInt(ClientManager.FieldId.PAY_FOR_SMS);
            long expenditureLimit = 0;
            if (fieldConfig.getValue(ClientManager.FieldId.EXPENDITURE_LIMIT)!=null) {
                expenditureLimit = CurrencyStringUtils.rublesToCopecks(fieldConfig.getValue(ClientManager.FieldId.EXPENDITURE_LIMIT));//tokens[19]);
            }
            Client client = new Client(organization, person, contractPerson, 0, notifyByEmail,
                    notifyBySms, contractId, contractDate,
                    contractState, password, payForSms, clientRegistryVersion, limit,
                    expenditureLimit, "");

            client.setAddress(fieldConfig.getValue(ClientManager.FieldId.ADDRESS)); //tokens[12]);
            client.setPhone(fieldConfig.getValue(ClientManager.FieldId.PHONE));//tokens[13]);
            String mobilePhone = fieldConfig.getValue(ClientManager.FieldId.MOBILE_PHONE);
            if (mobilePhone!=null) {
                mobilePhone = Client.checkAndConvertMobile(mobilePhone);
                if (mobilePhone==null) throw new Exception("Неправильный формат мобильного телефона");
            }
            client.setMobile(mobilePhone);//tokens[14]);
            client.setEmail(fieldConfig.getValue(ClientManager.FieldId.EMAIL));//tokens[15]);
            client.setRemarks(fieldConfig.getValue(ClientManager.FieldId.COMMENTS));
            client.setSanWithConvert(fieldConfig.getValue(ClientManager.FieldId.SAN));
            //if (tokens.length >= 21) {
            //    client.setRemarks(tokens[20]);
            //}

            /* проверяется есть ли в загрузочном файле параметр для группы клиента (класс для ученика)*/
            if (fieldConfig.getValue(ClientManager.FieldId.GROUP)!=null) {
            //if (tokens.length >=22){
                String clientGroupName = fieldConfig.getValue(ClientManager.FieldId.GROUP);//tokens[21];
                ClientGroup clientGroup = DAOUtils.findClientGroupByGroupNameAndIdOfOrg(persistenceSession, idOfOrg,
                        clientGroupName);
                if (clientGroup == null) {
                    clientGroup = DAOUtils.createNewClientGroup(persistenceSession, idOfOrg, clientGroupName);
                }
                client.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
            }
            persistenceSession.save(client);
            Long idOfClient = client.getIdOfClient();
            ///
            if (fieldConfig.getValue(ClientManager.FieldId.CARD_ID)!=null) {
                registerCardForClient(runtimeContext, persistenceSession, persistenceTransaction, fieldConfig, idOfClient);
            }
            ///

            persistenceTransaction.commit();
            persistenceTransaction = null;

            return idOfClient;
        } catch (Exception e) {
            logger.info("Ошибка при создании клиента", e);
            throw new Exception("Ошибка: "+e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }


    private static boolean existClient(Session persistenceSession, Org organization, String firstName, String surname,
            String secondName) throws Exception {
        if (StringUtils.isEmpty(secondName)) {
            return DAOUtils.existClient(persistenceSession, organization, firstName, surname);
        }
        return DAOUtils.existClient(persistenceSession, organization, firstName, surname, secondName);
    }

    private static Long registerCardForClient(RuntimeContext runtimeContext, Session persistenceSession, Transaction persistenceTransaction,
            ClientManager.ClientFieldConfig fieldConfig, Long idOfClient)
            throws Exception {
        String sCardType = fieldConfig.getValue(ClientManager.FieldId.CARD_TYPE);
        int cardType;
        if (sCardType.length()>0 && (sCardType.charAt(0)>='0' && sCardType.charAt(0)<='9')) cardType = Integer.parseInt(sCardType);
        else cardType = Card.parseCardType(sCardType);
        int state = Card.ACTIVE_STATE;
        int lifeState = Card.ISSUED_LIFE_STATE;
        Date validTime = fieldConfig.getValueDate(ClientManager.FieldId.CARD_EXPIRY);
        Date issueTime = fieldConfig.getValueDate(ClientManager.FieldId.CARD_ISSUED);
        String lockReason = null;
        long cardNo = fieldConfig.getValueLong(ClientManager.FieldId.CARD_ID);
        Long cardPrintedNo;
        if (fieldConfig.getValue(ClientManager.FieldId.CARD_PRINTED_NUM)!=null) cardPrintedNo = fieldConfig.getValueLong(
                ClientManager.FieldId.CARD_PRINTED_NUM);
        else cardPrintedNo = cardNo;
        try {
            return runtimeContext.getCardManager()
                    .createCard(persistenceSession, persistenceTransaction, idOfClient, cardNo, cardType, state, validTime, lifeState, lockReason, issueTime,
                            cardPrintedNo);
        } catch (Exception e) {
            throw new Exception("Ошибка при создании карты: " + e);
        }

    }

}
