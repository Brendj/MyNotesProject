/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.sms.*;
import ru.axetta.ecafe.processor.core.utils.AbbreviationUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ClientSmsListPage extends BasicWorkspacePage
        implements OrgSelectPage.CompleteHandler, ClientSelectPage.CompleteHandler {

    private static final Logger logger = LoggerFactory.getLogger(ClientSmsListPage.class);

    public static class OrgItem {

        private final Long idOfOrg;
        private final String shortName;
        private final String officialName;

        public OrgItem(Org org) {
            this.idOfOrg = org.getIdOfOrg();
            this.shortName = org.getShortName();
            this.officialName = org.getOfficialName();
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public String getShortName() {
            return shortName;
        }

        public String getOfficialName() {
            return officialName;
        }
    }

    public static class PersonItem {

        private final String firstName;
        private final String surname;
        private final String secondName;
        private final String idDocument;

        public PersonItem(Person person) {
            this.firstName = person.getFirstName();
            this.surname = person.getSurname();
            this.secondName = person.getSecondName();
            this.idDocument = person.getIdDocument();
        }

        public String getFirstName() {
            return firstName;
        }

        public String getSurname() {
            return surname;
        }

        public String getSecondName() {
            return secondName;
        }

        public String getIdDocument() {
            return idDocument;
        }
    }

    public static class ClientItem {

        private Long idOfClient;
        private OrgItem org;
        private PersonItem person;
        private PersonItem contractPerson;
        private Long contractId;
        private Integer contractState;

        public Long getIdOfClient() {
            return idOfClient;
        }

        public void setIdOfClient(Long idOfClient) {
            this.idOfClient = idOfClient;
        }

        public OrgItem getOrg() {
            return org;
        }

        public void setOrg(OrgItem org) {
            this.org = org;
        }

        public PersonItem getPerson() {
            return person;
        }

        public void setPerson(PersonItem person) {
            this.person = person;
        }

        public PersonItem getContractPerson() {
            return contractPerson;
        }

        public void setContractPerson(PersonItem contractPerson) {
            this.contractPerson = contractPerson;
        }

        public Long getContractId() {
            return contractId;
        }

        public void setContractId(Long contractId) {
            this.contractId = contractId;
        }

        public Integer getContractState() {
            return contractState;
        }

        public void setContractState(Integer contractState) {
            this.contractState = contractState;
        }

        public ClientItem(Client client) {
            this.idOfClient = client.getIdOfClient();
            this.org = new OrgItem(client.getOrg());
            this.person = new PersonItem(client.getPerson());
            this.contractPerson = new PersonItem(client.getContractPerson());
            this.contractId = client.getContractId();
            this.contractState = client.getContractState();
        }

        public String getShortName() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ContractIdFormat.format(contractId)).append(" (")
                    .append(AbbreviationUtils.buildAbbreviation(contractPerson.getFirstName(),
                            contractPerson.getSurname(), contractPerson.getSecondName())).append("): ")
                    .append(AbbreviationUtils.buildAbbreviation(person.getFirstName(), person.getSurname(),
                            person.getSecondName()));
            return stringBuilder.toString();
        }
    }

    public static class ClientSmsItem {

        private final String idOfSms;
        private final ClientItem client;
        private final String phone;
        private final Integer contentsType;
        private final String textContents;
        private final Integer deliveryStatus;
        private final Date serviceSendDate;
        private final Date sendDate;
        private final Date deliveryDate;
        private final Long price;

        public ClientSmsItem(ClientSms clientSms) {
            this.idOfSms = clientSms.getIdOfSms();
            this.client = new ClientItem(clientSms.getClient());
            this.phone = clientSms.getPhone();
            this.contentsType = clientSms.getContentsType();
            this.textContents = clientSms.getTextContents();
            this.deliveryStatus = clientSms.getDeliveryStatus();
            this.serviceSendDate = clientSms.getServiceSendTime();
            this.sendDate = clientSms.getSendTime();
            this.deliveryDate = clientSms.getDeliveryTime();
            this.price = clientSms.getPrice();
        }

        public String getIdOfSms() {
            return idOfSms;
        }

        public ClientItem getClient() {
            return client;
        }

        public String getPhone() {
            return phone;
        }

        public Integer getContentsType() {
            return contentsType;
        }

        public String getTextContents() {
            return textContents;
        }

        public Integer getDeliveryStatus() {
            return deliveryStatus;
        }

        public Date getServiceSendDate() {
            return serviceSendDate;
        }

        public Date getSendDate() {
            return sendDate;
        }

        public Date getDeliveryDate() {
            return deliveryDate;
        }

        public Long getPrice() {
            return price;
        }
    }

    private List<ClientSmsItem> items = Collections.emptyList();
    private final ClientFilter clientFilter = new ClientFilter();
    private final ClientSmsFilter clientSmsFilter = new ClientSmsFilter();

    public String getPageFilename() {
        return "client/sms_list";
    }

    public List<ClientSmsItem> getItems() {
        return items;
    }

    public ClientFilter getClientFilter() {
        return clientFilter;
    }

    public ClientSmsFilter getClientSmsFilter() {
        return clientSmsFilter;
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        this.clientFilter.completeOrgSelection(session, idOfOrg);
    }

    public void completeClientSelection(Session session, Long idOfClient) throws Exception {
        this.clientSmsFilter.completeClientSelection(session, idOfClient);
    }

    public void fill(Session session) throws Exception {
        List<ClientSmsItem> items = new LinkedList<ClientSmsItem>();
        List clients = clientSmsFilter.retrieveClientSms(session);
        for (Object object : clients) {
            ClientSms clientSms = (ClientSms) object;
            items.add(new ClientSmsItem(clientSms));
        }
        this.items = items;
    }

    public void sendNegativeBalanceSms(Session session) throws Exception {
        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            SmsService smsService = runtimeContext.getSmsService();
            MessageIdGenerator messageIdGenerator = runtimeContext.getMessageIdGenerator();
            ClientSmsProcessor clientSmsProcessor = runtimeContext.getClientSmsProcessor();

            Criteria clientCriteria = session.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractState", Client.ACTIVE_CONTRACT_STATE));
            clientCriteria.add(Restrictions.lt("balance", 0L));
            clientFilter.addRestrictions(session, clientCriteria);

            List clients = clientCriteria.list();

            for (Object currObject : clients) {
                Client currClient = (Client) currObject;
                try {
                    String phoneNumber = currClient.getMobile();
                    if (StringUtils.isNotEmpty(phoneNumber)) {
                        phoneNumber = PhoneNumberCanonicalizator.canonicalize(phoneNumber);
                        if (StringUtils.length(phoneNumber) == 11) {
                            Card activeCard = currClient.findActiveCard(session, null);
                            if (null != activeCard) {
                                String sender = buildSender(currClient);
                                String text = buildSmsText(currClient, activeCard);
                                String idOfSms = messageIdGenerator.generate();
                                SendResponse sendResponse = null;
                                try {
                                    sendResponse = smsService.sendTextMessage(idOfSms, sender, phoneNumber, text);
                                } catch (Exception e) {
                                    if (logger.isWarnEnabled()) {
                                        logger.warn(String.format(
                                                "Failed to send SMS, idOfSms: %s, sender: %s, phoneNumber: %s, text: %s",
                                                idOfSms, sender, phoneNumber, text), e);
                                    }
                                }
                                if (null != sendResponse) {
                                    if (sendResponse.isSuccess()) {
                                        clientSmsProcessor
                                                .registerClientSms(currClient.getIdOfClient(), idOfSms, phoneNumber,
                                                        ClientSms.NEGATIVE_BALANCE_CONTENTS, text, new Date());
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error(String.format("Failed to send SMS to client: %s", currClient), e);
                }
            }
        } finally {
            RuntimeContext.release(runtimeContext);
        }
    }

    private static String buildSender(Client client) {
        return StringUtils.substring(StringUtils.defaultString(client.getOrg().getSmsSender()), 0, 11);
    }

    private static String buildSmsText(Client client, Card card) {
        return String.format("Просим пополнить счет карты питания. Баланс: %s р",
                CurrencyStringUtils.copecksToRubles(client.getBalance()));
    }

}