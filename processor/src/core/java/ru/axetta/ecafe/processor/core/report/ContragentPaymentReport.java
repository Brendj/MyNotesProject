/*
/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
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
    /*
    * Параметры отчета для добавления в правила и шаблоны
    *
    * При создании любого отчета необходимо добавить параметры:
    * REPORT_NAME - название отчета на русском
    * TEMPLATE_FILE_NAMES - названия всех jasper-файлов, созданных для отчета
    * IS_TEMPLATE_REPORT - добавлять ли отчет в шаблоны отчетов
    * PARAM_HINTS - параметры отчета (смотри ReportRuleConstants.PARAM_HINTS)
    * заполняется, если отчет добавлен в шаблоны (класс AutoReportGenerator)
    *
    * Затем КАЖДЫЙ класс отчета добавляется в массив ReportRuleConstants.ALL_REPORT_CLASSES
    */
    public static final String REPORT_NAME = "Отчет по платежам";
    public static final String[] TEMPLATE_FILE_NAMES = {"ContragentPaymentReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{31, 21, 30, 32};


    @Override
    protected Integer getContragentSelectClass() {
        return Contragent.PAY_AGENT;
    }

    public static class Builder extends BasicReportForContragentJob.Builder {

        public String error = null;

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

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
            public Long paySum;
            public Date createTime;
            public String idOfPayment;
            public int paymentMethod;
            public String addIdOfPayment;

            public ClientPaymentRow(ClientPayment clientPayment) {
                AccountTransaction accountTransaction = clientPayment.getTransaction();
                Client client = accountTransaction.getClient();
                this.idOfClient = client.getIdOfClient();
                this.contractId = client.getContractId();
                Person person = client.getPerson();
                this.firstName = person.getFirstName();
                this.surName = person.getSurname();
                this.secondName = person.getSecondName();
                Org org = accountTransaction.getOrg() != null ? accountTransaction.getOrg() : client.getOrg();
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
                this.addIdOfPayment = clientPayment.getAddIdOfPayment();
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

            public double getPaySum() {
                return paySum / 100.0;
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

            public String getAddIdOfPayment() {
                return addIdOfPayment;
            }

            public void setAddIdOfPayment(String addIdOfPayment) {
                this.addIdOfPayment = addIdOfPayment;
            }
        }

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath() + ContragentPaymentReport.class.getSimpleName() + ".jasper";
            exportToHTML = true;
        }

        private long totalSum;
        private boolean exportToHTML = false;

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

            String idOfContragentPaymentReceiver = getReportProperties().getProperty(PARAM_CONTRAGENT_PAYER_ID);

            String [] idOfContragentPaymentReceiverIds =  idOfContragentPaymentReceiver.split(",");

            List<Contragent> contragentPaymentReceiverList = new ArrayList<Contragent>();

            Long idPaymentReceiver = null;
            Contragent contragentPaymentReceiverItem;
            String contragentPaymentReceiverString = "";
            for (String contragentPaymentReceiverId: idOfContragentPaymentReceiverIds) {
                try {
                    idPaymentReceiver =Long.parseLong(contragentPaymentReceiverId);
                } catch (Exception e) {
                    throw new Exception("Ошибка парсинга идентификатора контрагента-получателя: "+idPaymentReceiver, e);
                }

                contragentPaymentReceiverItem = (Contragent)session.get(Contragent.class, idPaymentReceiver);
                contragentPaymentReceiverString = contragentPaymentReceiverString + contragentPaymentReceiverItem.getContragentName() + ", ";
                contragentPaymentReceiverList.add(contragentPaymentReceiverItem);
            }

            String idOfContragentReceiver = getReportProperties().getProperty(PARAM_CONTRAGENT_RECEIVER_ID);

            String [] idOfContragentReceiverIds =  idOfContragentReceiver.split(",");

            List<Contragent> contragentReceiverList = new ArrayList<Contragent>();

            Long idReceiver = null;
            Contragent contragentReceiverItem;
            String contragentReceiverString = "";
            for (String contragentReceiverId: idOfContragentReceiverIds) {
                try {
                    idReceiver =Long.parseLong(contragentReceiverId);
                } catch (Exception e) {
                    throw new Exception("Ошибка парсинга идентификатора контрагента-получателя: "+idReceiver, e);
                }

                contragentReceiverItem = (Contragent)session.get(Contragent.class, idReceiver);
                contragentReceiverString = contragentReceiverString + contragentReceiverItem.getContragentName() + ", ";
                contragentReceiverList.add(contragentReceiverItem);
            }

            parameterMap.put("nameOfContragentSender", contragentPaymentReceiverString);
            parameterMap.put("nameOfContragentReceiver", contragentReceiverString);

            String idOfOrgs = StringUtils.trimToEmpty(getReportProperties().getProperty("idOfOrgList"));
            List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
            List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
            for (String idOfOrg : stringOrgList) {
                idOfOrgList.add(Long.parseLong(idOfOrg));
            }

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, contragentPaymentReceiverList, contragentReceiverList, startTime, endTime, (Calendar) calendar.clone(),
                            parameterMap, idOfOrgList));
            Date generateEndTime = new Date();
            if(contragent == null){
                contragent = contragentPaymentReceiverList.get(0);
            }
            Long idOfContragent1 = contragent.getIdOfContragent();
            if (!exportToHTML) {
                ContragentPaymentReport report = new ContragentPaymentReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        jasperPrint, startTime, endTime, idOfContragent1);
                report.setReportProperties(getReportProperties());
                return report;
            }  else {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                JRHtmlExporter exporter = new JRHtmlExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);
                exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, "./images/");
                exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "/images/");
                exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
                exporter.setParameter(JRHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.FALSE);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter.exportReport();
                ContragentPaymentReport report = new ContragentPaymentReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        jasperPrint, startTime, endTime, idOfContragent1).setHtmlReport(os.toString("UTF-8"));
                report.setReportProperties(getReportProperties());
                return report;
            }
        }

        private Boolean transactionsWithoutOrgIsPresented = false;

        private JRDataSource createDataSource(Session session, List<Contragent> contragentPaymentReceiverList, List<Contragent> contragentReceiverList,
                Date startTime, Date endTime, Calendar clone, Map<String, Object> parameterMap, List<Long> idOfOrgList) {

            // терминал
            String terminal = getReportProperties().getProperty("terminal");

            // идентификатор платежа
            String paymentIdentifier = getReportProperties().getProperty("paymentIdentifier");

            String organizationTypeProperty = getReportProperties().getProperty("organizationTypeModify");
            OrganizationType orgType = null;

            OrganizationType[] organizationTypes = OrganizationType.values();
            for (OrganizationType organizationType : organizationTypes) {
                if (organizationType.toString().equals(organizationTypeProperty)) {
                    orgType = organizationType;
                    break;
                }
            }

            List<Org> orgList =  new ArrayList<Org>();
            for (Long idOfOrg : idOfOrgList) {
                Org localOrg = (Org) session.load(Org.class, idOfOrg);
                orgList.add(localOrg);
            }

            //Учитываем время до последней секунды
            endTime = CalendarUtils.addMinute(endTime, 1);

            // Принадлежность транзакции какой-либо организации берем из таблицы транзакций
            Criteria clientPaymentCriteria = session.createCriteria(ClientPayment.class);
            clientPaymentCriteria.createAlias("transaction", "t");
            clientPaymentCriteria.add(Restrictions.isNotNull("t.org"));
            if (!CollectionUtils.isEmpty(idOfOrgList))
                clientPaymentCriteria.add(Restrictions.in("t.org", orgList));
            clientPaymentCriteria.createAlias("transaction.client", "cl");
            clientPaymentCriteria.createAlias("transaction.client.org", "o");
            if (terminal != null && !terminal.equals("")) {
                clientPaymentCriteria.add(Restrictions.like("addIdOfPayment", "%" + terminal + "%").ignoreCase());
            }
            if (paymentIdentifier != null && !paymentIdentifier.equals("")) {
                clientPaymentCriteria.add(Restrictions.like("idOfPayment", "%" + paymentIdentifier + "%").ignoreCase());
            }
            if (orgType != null) {
                clientPaymentCriteria.add(Restrictions.eq("o.type", orgType));
            }
            if (contragentReceiverList != null)
                clientPaymentCriteria.add(Restrictions.in("contragentReceiver", contragentReceiverList));
            if (contragentPaymentReceiverList != null)
                clientPaymentCriteria.add(Restrictions.in("contragent", contragentPaymentReceiverList));
            clientPaymentCriteria.add(Restrictions.ge("createTime", startTime));
            clientPaymentCriteria.add(Restrictions.lt("createTime", endTime));
            Object[] types = {ClientPayment.CLIENT_TO_ACCOUNT_PAYMENT, ClientPayment.CANCELLED_PAYMENT};
            clientPaymentCriteria.add(Restrictions.in("payType", types));
            HibernateUtils.addAscOrder(clientPaymentCriteria, "createTime");
            List clientPayments = clientPaymentCriteria.list();

            // Принадлежность транзакции получаем через клиента совершившего платеж
            Criteria clientPaymentCriteriaWithTransactionOrgIsNull = session.createCriteria(ClientPayment.class);
            clientPaymentCriteriaWithTransactionOrgIsNull.createAlias("transaction", "t");
            clientPaymentCriteriaWithTransactionOrgIsNull.add(Restrictions.isNull("t.org"));
            clientPaymentCriteriaWithTransactionOrgIsNull.createAlias("transaction.client", "cl");
            clientPaymentCriteriaWithTransactionOrgIsNull.createAlias("transaction.client.org", "o");
            if (terminal != null && !terminal.equals("")) {
                clientPaymentCriteria.add(Restrictions.like("addIdOfPayment", "%" + terminal + "%"));
            }
            if (paymentIdentifier != null && !paymentIdentifier.equals("")) {
                clientPaymentCriteria.add(Restrictions.like("idOfPayment", "%" + paymentIdentifier + "%"));
            }
            if (orgType != null) {
                clientPaymentCriteriaWithTransactionOrgIsNull.add(Restrictions.eq("o.type", orgType));
            }
            if (!CollectionUtils.isEmpty(idOfOrgList))
                clientPaymentCriteriaWithTransactionOrgIsNull.add(Restrictions.in("o.idOfOrg", idOfOrgList));
            if (contragentReceiverList != null)
                clientPaymentCriteriaWithTransactionOrgIsNull.add(Restrictions.in("contragentReceiver",
                        contragentReceiverList));
            if (contragentPaymentReceiverList != null)
                clientPaymentCriteriaWithTransactionOrgIsNull.add(Restrictions.in("contragent", contragentPaymentReceiverList));
            clientPaymentCriteriaWithTransactionOrgIsNull.add(Restrictions.between("createTime", startTime, endTime));
            clientPaymentCriteriaWithTransactionOrgIsNull.add(Restrictions.eq("payType", ClientPayment.CLIENT_TO_ACCOUNT_PAYMENT));
            HibernateUtils.addAscOrder(clientPaymentCriteriaWithTransactionOrgIsNull, "createTime");
            List clientPaymentsWithTransactionOrgIsNull = clientPaymentCriteriaWithTransactionOrgIsNull.list();

            totalSum = 0;
            List<ClientPaymentRow> clientPaymentItems = new LinkedList<ClientPaymentRow>();
            for (Object currObject : clientPayments) {
                ClientPayment currClientPayment = (ClientPayment) currObject;
                ClientPaymentRow newClientPaymentItem = new ClientPaymentRow(currClientPayment);
                clientPaymentItems.add(newClientPaymentItem);
                totalSum += newClientPaymentItem.paySum;
            }
            if (clientPaymentsWithTransactionOrgIsNull.size() > 0) {
                transactionsWithoutOrgIsPresented = true;
                for (Object currObject : clientPaymentsWithTransactionOrgIsNull) {
                    ClientPayment currClientPayment = (ClientPayment) currObject;
                    ClientPaymentRow newClientPaymentItem = new ClientPaymentRow(currClientPayment);
                    clientPaymentItems.add(newClientPaymentItem);
                    totalSum += newClientPaymentItem.paySum;
                }
            }
            parameterMap.put("totalSum", (double) totalSum / 100.0);
            if(clientPaymentItems.isEmpty()) {
                error  = "Для построения отчета данных не найдено";
            }
            return new JRBeanCollectionDataSource(clientPaymentItems);
        }

        public Boolean isTransactionsWithoutOrgIsPresented() {
            return transactionsWithoutOrgIsPresented;
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
    private String htmlReport;

    public String getHtmlReport() {
        return htmlReport;
    }

    public ContragentPaymentReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }

    @Override
    public String getReportDistinctText() {
        String caReceiver=getReportProperties().getProperty(PARAM_CONTRAGENT_RECEIVER_ID);
        return Long.toString(idOfContragent)+(caReceiver==null?"":"-"+caReceiver);
    }
}
