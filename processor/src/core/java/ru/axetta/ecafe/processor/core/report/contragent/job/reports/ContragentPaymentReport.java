/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.contragent.job.reports;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 10.09.12
 * Time: 16:21
 * To change this template use File | Settings | File Templates.
 */
public class ContragentPaymentReport extends BasicReportForContragentJob {

    public static class Builder implements BasicReportForContragentJob.Builder{

        public static class PersonItem {

            private final String firstName;
            private final String surName;
            private final String secondName;
            private final String idDocument;

            public String getFirstName() {
                return firstName;
            }

            public String getSurName() {
                return surName;
            }

            public String getSecondName() {
                return secondName;
            }

            public String getIdDocument() {
                return idDocument;
            }

            public PersonItem(Person person) {
                this.firstName = person.getFirstName();
                this.surName = person.getSurname();
                this.secondName = person.getSecondName();
                this.idDocument = person.getIdDocument();
            }
        }

        public static class OrgItem{
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

        public static class ClientItem {

            private final Long idOfClient;
            private final Long contractId;
            private final PersonItem person;
            private final OrgItem orgItem;

            public OrgItem getOrgItem() {
                return orgItem;
            }

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
                this.orgItem = new OrgItem(client.getOrg());
            }
        }

        public static class CardItem {

            private Long idOfCard;
            private Long cardNo;
            private Integer state;
            private Integer lifeState;

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
                if (card!=null) {
                    this.idOfCard = card.getIdOfCard();
                    this.cardNo = card.getCardNo();
                    this.state = card.getState();
                    this.lifeState = card.getLifeState();
                }
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

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        private long totalSum;

        @Override
        public BasicReportJob build(Session session, Contragent contragent, Date startTime, Date endTime,
                Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<Object, Object> parameterMap = new HashMap<Object, Object>();
            parameterMap.put("idOfContragent", contragent.getIdOfContragent());
            parameterMap.put("contragentName", contragent.getContragentName());
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, contragent, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            parameterMap.put("totalSum", totalSum);
            return new ContragentPaymentReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, contragent.getIdOfContragent());
        }

        private JRDataSource createDataSource(Session session, Contragent contragent, Date startTime, Date endTime,
                Calendar clone, Map<Object, Object> parameterMap) {
            //Criteria criteria = session.createCriteria(ContragentPayment.class);
            //criteria.add(Restrictions.eq("contragent",contragent));
            //criteria.add(Restrictions.between("createTime", startTime, endTime));
            //criteria.addOrder(Order.asc("createTime"));
            //List list = criteria.list();
            //for (Object  object: list){
            //    ContragentPayment contragentPayment = (ContragentPayment) object;
            //    ClientPaymentItem contragentPaymentItem = new ClientPaymentItem(contragentPayment);
            //    contragentInfoList.add(contragentPaymentItem);
            //}
            Date generateTime = new Date();
            Criteria clientPaymentCriteria = session.createCriteria(ClientPayment.class);
            clientPaymentCriteria.add(Restrictions.eq("contragent", contragent));
            clientPaymentCriteria.add(Restrictions.between("createTime",startTime,endTime));
            clientPaymentCriteria.add(Restrictions.eq("payType", ClientPayment.CLIENT_TO_ACCOUNT_PAYMENT));
            HibernateUtils.addAscOrder(clientPaymentCriteria, "createTime");
            List clientPayments = clientPaymentCriteria.list();
            totalSum = 0;
            List<ClientPaymentItem> clientPaymentItems = new LinkedList<ClientPaymentItem>();
            for (Object currObject : clientPayments) {
                ClientPayment currClientPayment = (ClientPayment) currObject;
                ClientPaymentItem newClientPaymentItem = new ClientPaymentItem(currClientPayment);
                clientPaymentItems.add(newClientPaymentItem);
                totalSum += newClientPaymentItem.getPaySum();
            }
            return new JRBeanCollectionDataSource(clientPaymentItems);
        }
    }

    @Override
    protected Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    @Override
    public BasicReportForContragentJob createInstance() {
        return new ContragentPaymentReport();
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    public ContragentPaymentReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfContragent) {
         super(generateTime,generateDuration,print,startTime,endTime, idOfContragent);
    }

    public ContragentPaymentReport() {}

    private static final Logger logger = LoggerFactory.getLogger(ContragentPaymentReport.class);
}
