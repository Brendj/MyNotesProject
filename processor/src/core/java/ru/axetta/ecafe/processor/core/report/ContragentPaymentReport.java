/*
/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
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

    public final static String PARAM_CONTRAGENT_RECEIVER_ID = "idOfContragentReceiver";
    

    @Override
    protected Integer getContragentSelectClass() {
        return Contragent.PAY_AGENT;
    }

    public static class Builder extends BasicReportForContragentJob.Builder {

        public static class ClientPaymentRow implements Comparable<ClientPaymentRow> {

            public Long idOfClient;
            public Long contractId;
            public String firstName;
            public String surName;
            public String secondName;
            public Long idOfOrg;
            public String shortName;
            public Date transactionTime;
            public Long idOfContragentReceiver;
            public String nameOfContragentReceiver;
            public Long idOfContragentSender;
            public String nameOfContragentSender;
            public long paySum;
            public Date createTime;
            public String idOfPayment;
            public int paymentMethod;

            public ClientPaymentRow(ClientPayment clientPayment) {
                AccountTransaction accountTransaction = clientPayment.getTransaction();
                Client client = accountTransaction.getClient();
                this.idOfClient = client.getIdOfClient();
                this.contractId = client.getContractId();
                Person person = client.getPerson();
                this.firstName = person.getFirstName();
                this.surName = person.getSurname();
                this.secondName = person.getSecondName();
                Org org = client.getOrg();
                this.idOfOrg = org.getIdOfOrg();
                this.shortName = org.getShortName();
                Contragent contragentReceiver = clientPayment.getContragentReceiver();
                if (contragentReceiver == null) {
                    this.idOfContragentReceiver = -1L;
                    this.nameOfContragentReceiver = "";
                } else {
                    this.idOfContragentReceiver = contragentReceiver.getIdOfContragent();
                    this.nameOfContragentReceiver = contragentReceiver.getContragentName();
                }
                Contragent contragentSender = clientPayment.getContragent();
                this.idOfContragentSender = contragentSender.getIdOfContragent();
                this.nameOfContragentSender = contragentSender.getContragentName();
                this.paySum = clientPayment.getPaySum();
                this.createTime = clientPayment.getCreateTime();
                this.idOfPayment = clientPayment.getIdOfPayment();
                this.paymentMethod = clientPayment.getPaymentMethod();
            }

            @Override
            public int compareTo(ClientPaymentRow o) {
                return getNameOfContragentSender().compareTo(o.getNameOfContragentSender());
            }

            public Long getIdOfClient() {
                return idOfClient;
            }

            public String getContractId() {
                return ContractIdFormat.format(contractId);
            }

            public String getFirstName() {
                return firstName;
            }

            public String getSurName() {
                return surName;
            }

            public String getSecondName() {
                return secondName;
            }

            public Long getIdOfOrg() {
                return idOfOrg;
            }

            public String getOrgName() {
                return shortName;
            }

            public String getOrgNum() {
                return Org.extractOrgNumberFromName(shortName);
            }

            public Long getIdOfContragentReceiver() {
                return idOfContragentReceiver;
            }

            public String getNameOfContragentReceiver() {
                return nameOfContragentReceiver;
            }

            public Long getIdOfContragentSender() {
                return idOfContragentSender;
            }

            public String getNameOfContragentSender() {
                return nameOfContragentSender;
            }

            public Float getPaySum() {
                return (float) paySum / 100;
            }

            public Date getCreateTime() {
                return createTime;
            }

            public String getIdOfPayment() {
                return idOfPayment;
            }

            public String getPayMethod() {
                return ClientPayment.PAYMENT_METHOD_SHORT_NAMES[paymentMethod];
            }
        }

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        private long totalSum;

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);
            parameterMap.put("contragentName", contragent.getContragentName());

            String idOfContragentReceiver = getReportProperties().getProperty(PARAM_CONTRAGENT_RECEIVER_ID);
            Long lIdOfContragentReceiver=null; Contragent contragentReceiver=null;
            if (idOfContragentReceiver!=null) {
                try {
                    lIdOfContragentReceiver=Long.parseLong(idOfContragentReceiver);
                } catch (Exception e) {
                    throw new Exception("Ошибка парсинга идентификатора контрагента-получателя: "+idOfContragentReceiver, e);
                }
                contragentReceiver = (Contragent)session.get(Contragent.class, Long.parseLong(idOfContragentReceiver));
                if (contragentReceiver==null) {
                    throw new Exception("Контрагент-получатель не найден: "+idOfContragentReceiver);
                }
            }
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, contragent, contragentReceiver, startTime, endTime, (Calendar) calendar.clone(),
                            parameterMap));
            Date generateEndTime = new Date();
            return new ContragentPaymentReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, contragent.getIdOfContragent());
        }

        private JRDataSource createDataSource(Session session, Contragent contragent, Contragent contragentReceiver,
                Date startTime, Date endTime, Calendar clone, Map<String, Object> parameterMap) {
            Criteria clientPaymentCriteria = session.createCriteria(ClientPayment.class);
            if (contragentReceiver!=null) {
                clientPaymentCriteria.add(Restrictions.eq("contragentReceiver", contragentReceiver));
            }
            clientPaymentCriteria.add(Restrictions.eq("contragent", contragent));
            clientPaymentCriteria.add(Restrictions.between("createTime", startTime, endTime));
            clientPaymentCriteria.add(Restrictions.eq("payType", ClientPayment.CLIENT_TO_ACCOUNT_PAYMENT));
            HibernateUtils.addAscOrder(clientPaymentCriteria, "createTime");
            List clientPayments = clientPaymentCriteria.list();
            totalSum = 0;
            List<ClientPaymentRow> clientPaymentItems = new LinkedList<ClientPaymentRow>();
            for (Object currObject : clientPayments) {
                ClientPayment currClientPayment = (ClientPayment) currObject;
                ClientPaymentRow newClientPaymentItem = new ClientPaymentRow(currClientPayment);
                clientPaymentItems.add(newClientPaymentItem);
                totalSum += newClientPaymentItem.paySum;
            }
            parameterMap.put("totalSum", (float) totalSum / 100);
            return new JRBeanCollectionDataSource(clientPaymentItems);
        }
    }

    @Override
    public Builder createBuilder(String templateFilename) {
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
        super(generateTime, generateDuration, print, startTime, endTime, idOfContragent);
    }

    public ContragentPaymentReport() {
    }

    private static final Logger logger = LoggerFactory.getLogger(ContragentPaymentReport.class);

    @Override
    public String getReportDistinctText() {
        String caReceiver=getReportProperties().getProperty(PARAM_CONTRAGENT_RECEIVER_ID);
        return Long.toString(idOfContragent)+(caReceiver==null?"":"-"+caReceiver);
    }
}
