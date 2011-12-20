/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 17.10.2009
 * Time: 14:09:39
 * To change this template use File | Settings | File Templates.
 */
public class ContragentClientPaymentReport extends BasicReport {

    public static class PersonItem {

        private final String firstName;
        private final String surname;
        private final String secondName;
        private final String idDocument;

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

        public PersonItem(Person person) {
            this.firstName = person.getFirstName();
            this.surname = person.getSurname();
            this.secondName = person.getSecondName();
            this.idDocument = person.getIdDocument();
        }
    }

    public static class ClientItem {

        private final Long idOfClient;
        private final Long contractId;
        private final PersonItem person;

        public Long getIdOfClient() {
            return idOfClient;
        }

        public Long getContractId() {
            return contractId;
        }

        public PersonItem getPerson() {
            return person;
        }

        public ClientItem(Client client) {
            this.idOfClient = client.getIdOfClient();
            this.contractId = client.getContractId();
            this.person = new PersonItem(client.getPerson());
        }
    }

    public static class CardItem {

        private final Long idOfCard;
        private final Long cardNo;
        private final Integer state;
        private final Integer lifeState;

        public Long getIdOfCard() {
            return idOfCard;
        }

        public Long getCardNo() {
            return cardNo;
        }

        public Integer getState() {
            return state;
        }

        public Integer getLifeState() {
            return lifeState;
        }

        public CardItem(Card card) {
            this.idOfCard = card.getIdOfCard();
            this.cardNo = card.getCardNo();
            this.state = card.getState();
            this.lifeState = card.getLifeState();
        }
    }

    public static class TransactionItem {

        private final Date transactionTime;

        public Date getTransactionTime() {
            return transactionTime;
        }

        public TransactionItem(AccountTransaction accountTransaction) {
            this.transactionTime = accountTransaction.getTransactionTime();
        }
    }

    public static class ClientPaymentItem {

        private final ClientItem client;
        private final TransactionItem transaction;
        private final CardItem card;
        private final long paySum;
        private final Date createTime;
        private final String idOfPayment;

        public ClientItem getClient() {
            return client;
        }

        public TransactionItem getTransaction() {
            return transaction;
        }

        public CardItem getCard() {
            return card;
        }

        public long getPaySum() {
            return paySum;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public String getIdOfPayment() {
            return idOfPayment;
        }

        public ClientPaymentItem(ClientPayment clientPayment) {
            AccountTransaction accountTransaction = clientPayment.getTransaction();
            this.client = new ClientItem(accountTransaction.getClient());
            this.transaction = new TransactionItem(accountTransaction);
            this.card = new CardItem(accountTransaction.getCard());
            this.paySum = clientPayment.getPaySum();
            this.createTime = clientPayment.getCreateTime();
            this.idOfPayment = clientPayment.getIdOfPayment();
        }
    }

    public static class Builder {

        public ContragentClientPaymentReport build(Session session, Date startTime, Date endTime, Contragent contragent)
                throws Exception {
            Date generateTime = new Date();
            Criteria clientPaymentCriteria = session.createCriteria(ClientPayment.class);
            clientPaymentCriteria.add(Restrictions.eq("contragent", contragent));
            clientPaymentCriteria.add(Restrictions.ge("createTime", startTime));
            clientPaymentCriteria.add(Restrictions.lt("createTime", endTime));
            clientPaymentCriteria.add(Restrictions.eq("payType", ClientPayment.CLIENT_TO_ACCOUNT_PAYMENT));
            HibernateUtils.addAscOrder(clientPaymentCriteria, "createTime");
            List clientPayments = clientPaymentCriteria.list();
            long totalSum = 0;
            List<ClientPaymentItem> clientPaymentItems = new LinkedList<ClientPaymentItem>();
            for (Object currObject : clientPayments) {
                ClientPayment currClientPayment = (ClientPayment) currObject;
                ClientPaymentItem newClientPaymentItem = new ClientPaymentItem(currClientPayment);
                clientPaymentItems.add(newClientPaymentItem);
                totalSum += newClientPaymentItem.getPaySum();
            }
            return new ContragentClientPaymentReport(generateTime, new Date().getTime() - generateTime.getTime(),
                    startTime, endTime, clientPaymentItems, totalSum);
        }

    }

    private final Date startTime;
    private final Date endTime;
    private final List<ClientPaymentItem> clientPaymentItems;
    private final long totalSum;

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public List<ClientPaymentItem> getClientPaymentItems() {
        return clientPaymentItems;
    }

    public long getTotalSum() {
        return totalSum;
    }

    public ContragentClientPaymentReport(Date startTime, Date endTime) {
        super();
        this.startTime = startTime;
        this.endTime = endTime;
        this.clientPaymentItems = Collections.emptyList();
        this.totalSum = 0;
    }

    public ContragentClientPaymentReport(Date generateTime, long generateDuration, Date startTime, Date endTime,
            List<ClientPaymentItem> clientPaymentItems, long totalSum) {
        super(generateTime, generateDuration);
        this.startTime = startTime;
        this.endTime = endTime;
        this.clientPaymentItems = clientPaymentItems;
        this.totalSum = totalSum;
    }
}