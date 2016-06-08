/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.ExecutorServiceWrappedJob;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 17.10.2009
 * Time: 14:09:39
 * Отчет по балансу клиентов учреждения
 */
public class OrgBalanceJasperReport extends BasicJasperReport {
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
    public static final String REPORT_NAME = "Отчет по балансу клиентов учреждения";
    public static final String[] TEMPLATE_FILE_NAMES = {"OrgBalanceReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{28, 29, 3, 4, 5, 22, 23};


    private static final String BASE_DOCUMENT_FILENAME;

    static {
        String fullName = OrgBalanceJasperReport.class.getCanonicalName();
        int i = fullName.lastIndexOf('.');
        BASE_DOCUMENT_FILENAME = fullName.substring(i + 1);
    }

    /**
     * Created by IntelliJ IDEA.
     * User: Developer
     * Date: 21.04.2010
     * Time: 17:01:29
     * To change this template use File | Settings | File Templates.
     */
    public static class Builder {

        public static class Row {

            private final long paymentSum;
            private final long sumByCard;
            private final long sumByCash;
            private final long balance;
            private final String clientGroupName;
            private final String firstName;
            private final String surname;
            private final String secondName;
            private final String contractId;
            private final String payLink;

            public Row(RuntimeContext runtimeContext, long paymentSum, long sumByCard, long sumByCash, long balance,
                    String clientGroupName, String firstName, String surname, String secondName, String contractId)
                    throws Exception {
                this.paymentSum = paymentSum;
                this.sumByCard = sumByCard;
                this.sumByCash = sumByCash;
                this.balance = balance;
                this.clientGroupName = clientGroupName;
                this.firstName = firstName;
                this.surname = surname;
                this.secondName = secondName;
                this.contractId = contractId;
                if (balance >= 0) {
                    this.payLink = null;
                } else {
                    String payFormUrl = runtimeContext.getPayformUrl();
                    this.payLink = String.format(payFormUrl, contractId);
                }
            }

            public long getPaymentSum() {
                return paymentSum;
            }

            public long getSumByCard() {
                return sumByCard;
            }

            public long getSumByCash() {
                return sumByCash;
            }

            public long getBalance() {
                return balance;
            }

            public String getClientGroupName() {
                return clientGroupName;
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

            public String getContractId() {
                return contractId;
            }

            public String getPayText() {
                if (null == payLink) {
                    return null;
                }
                return "квитанция";
            }

            public String getPayLink() {
                return payLink;
            }
        }

        private static class TotalSums {

            private final long sumByCard;
            private final long sumByCash;
            private final long discount;

            private TotalSums(long sumByCard, long sumByCash, long discount) {
                this.sumByCard = sumByCard;
                this.sumByCash = sumByCash;
                this.discount = discount;
            }

            public long getSumByCard() {
                return sumByCard;
            }

            public long getSumByCash() {
                return sumByCash;
            }

            public long getDiscount() {
                return discount;
            }
        }

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public OrgBalanceJasperReport build(RuntimeContext runtimeContext, Session session, Org org, Date baseTime)
                throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("orgOfficialName", org.getOfficialName());
            parameterMap.put("baseTime", baseTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(runtimeContext, session, org, baseTime));
            Date generateEndTime = new Date();
            return new OrgBalanceJasperReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, baseTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(RuntimeContext runtimeContext, Session session, Org org, Date baseTime)
                throws Exception {
            List<Row> rows = new LinkedList<Row>();
            rows.addAll(buildGrouppedClientData(runtimeContext, session, baseTime, org));
            rows.addAll(buildUnGrouppedClientsData(runtimeContext, session, baseTime, org));
            return new JRBeanCollectionDataSource(rows);
        }

        private static List<Row> buildGrouppedClientData(RuntimeContext runtimeContext, Session session, Date baseTime,
                Org org) throws Exception {
            Criteria clientGroupsCriteria = session.createCriteria(ClientGroup.class);
            clientGroupsCriteria.add(Restrictions.eq("org", org));
            HibernateUtils.addAscOrder(clientGroupsCriteria, "groupName");
            List<Row> rows = new LinkedList<Row>();
            List clientGroups = clientGroupsCriteria.list();
            for (Object currObject : clientGroups) {
                ClientGroup currClientGroup = (ClientGroup) currObject;
                Criteria clientsCriteria = session.createCriteria(Client.class);
                clientsCriteria.add(Restrictions.eq("clientGroup", currClientGroup));
                clientsCriteria = clientsCriteria.createCriteria("person");
                HibernateUtils.addAscOrder(clientsCriteria, "surname");
                HibernateUtils.addAscOrder(clientsCriteria, "firstName");
                HibernateUtils.addAscOrder(clientsCriteria, "secondName");
                rows.addAll(buildClientGroupItem(runtimeContext, currClientGroup, session, baseTime,
                        clientsCriteria.list()));
            }
            return rows;
        }

        private static List<Row> buildUnGrouppedClientsData(RuntimeContext runtimeContext, Session session,
                Date baseTime, Org org) throws Exception {
            Criteria clientsCriteria = session.createCriteria(Client.class);
            clientsCriteria.add(Restrictions.eq("org", org));
            clientsCriteria.add(Restrictions.isNull("idOfClientGroup"));
            clientsCriteria = clientsCriteria.createCriteria("person");
            HibernateUtils.addAscOrder(clientsCriteria, "surname");
            HibernateUtils.addAscOrder(clientsCriteria, "firstName");
            HibernateUtils.addAscOrder(clientsCriteria, "secondName");
            return buildClientGroupItem(runtimeContext, null, session, baseTime, clientsCriteria.list());
        }

        private static List<Row> buildClientGroupItem(RuntimeContext runtimeContext, ClientGroup clientGroup,
                Session session, Date baseTime, Collection clients) throws Exception {
            String clientGroupName = "";
            if (null != clientGroup) {
                clientGroupName = clientGroup.getGroupName();
            }
            List<Row> rows = new LinkedList<Row>();
            for (Object currObject : clients) {
                Client currClient = (Client) currObject;
                rows.add(buildClientItem(runtimeContext, session, baseTime, currClient, clientGroupName));
            }
            return rows;
        }

        private static Row buildClientItem(RuntimeContext runtimeContext, Session session, Date baseTime, Client client,
                String clientGroupName) throws Exception {
            TotalSums totalOrderSums = getTotalClientOrderSums(session, baseTime, client);
            TotalSums totalSmsSums = getTotalClientSmsSums(session, baseTime, client);
            TotalSums totalSubscriptionSums = getTotalSubscriptionSums(session, baseTime, client);
            long totalPaymentSum = getTotalClientPaymentSum(session, baseTime, client);
            long balance = totalPaymentSum - totalOrderSums.getSumByCard() - totalSmsSums.getSumByCard()
                    - totalSubscriptionSums.getSumByCard();
            Person person = client.getPerson();
            return new Row(runtimeContext, totalPaymentSum,
                    totalOrderSums.getSumByCard() + totalSmsSums.getSumByCard() + totalSubscriptionSums.getSumByCard(),
                    totalOrderSums.getSumByCash() + totalSmsSums.getSumByCash() + totalSubscriptionSums.getSumByCash(),
                    balance, clientGroupName, person.getFirstName(), person.getSurname(), person.getSecondName(),
                    ContractIdFormat.format(client.getContractId()));
        }

        private static long getTotalClientPaymentSum(Session session, Date baseTime, Client client) throws Exception {
            Query query = session.createQuery(
                    "select sum(clientPayment.paySum) from ClientPayment clientPayment where clientPayment.transaction.client = ? and clientPayment.payType = ? and clientPayment.createTime < ?");
            query.setParameter(0, client);
            query.setParameter(1, ClientPayment.CLIENT_TO_ACCOUNT_PAYMENT);
            query.setParameter(2, baseTime);
            return defaultValue((Long) query.uniqueResult());
        }

        private static TotalSums getTotalClientOrderSums(Session session, Date baseTime, Client client)
                throws Exception {
            Query query = session.createQuery(
                    "select sum(clientOrder.sumByCard) as SUM1, sum(clientOrder.sumByCash) as SUM2 from Order clientOrder where clientOrder.state=0 and clientOrder.client = ? and clientOrder.createTime < ?");
            query.setParameter(0, client);
            query.setParameter(1, baseTime);
            Object[] result = (Object[]) query.uniqueResult();
            if (null == result) {
                return new TotalSums(0, 0, 0);
            } else {
                return new TotalSums(defaultValue((Long) result[0]), defaultValue((Long) result[1]), 0L);
                        //defaultValue((Long) result[2]));
            }
        }

        private static TotalSums getTotalClientSmsSums(Session session, Date baseTime, Client client) throws Exception {
            Query query = session.createQuery(
                    "select sum(clientSms.price) from ClientSms clientSms where clientSms.client = ? and clientSms.serviceSendTime < ?");
            query.setParameter(0, client);
            query.setParameter(1, baseTime);
            Long result = (Long) query.uniqueResult();
            if (null == result) {
                return new TotalSums(0, 0, 0);
            } else {
                return new TotalSums(result, 0L, 0L);
            }
        }

        private static TotalSums getTotalSubscriptionSums(Session session, Date baseTime, Client client)
                throws Exception {
            Query query = session.createQuery(
                    "select sum(fee.subscriptionSum) from SubscriptionFee fee where fee.transaction.client = ? and fee.createTime < ?");
            query.setParameter(0, client);
            query.setParameter(1, baseTime);
            Long result = (Long) query.uniqueResult();
            if (null == result) {
                return new TotalSums(0, 0, 0);
            } else {
                return new TotalSums(result, 0L, 0L);
            }
        }

        private static long defaultValue(Long value) {
            if (null == value) {
                return 0L;
            }
            return value;
        }

    }

    private static class DocumentBuilderCallback implements BasicReport.DocumentBuilderCallback {

        public String getReportDistinctText(BasicReport report) {
            OrgBalanceJasperReport orgBalanceJasperReport = (OrgBalanceJasperReport) report;
            return Long.toString(orgBalanceJasperReport.getIdOfOrg());
        }
    }

    static Map<Integer, ReportDocumentBuilder> createDocumentBuilders(String reportPath, DateFormat dateFormat,
            DateFormat timeFormat) {
        DocumentBuilderCallback documentBuilderCallback = new DocumentBuilderCallback();
        Map<Integer, ReportDocumentBuilder> documentBuilders = new HashMap<Integer, ReportDocumentBuilder>();
        documentBuilders.put(ReportHandleRule.PDF_FORMAT,
                new PdfBuilder(reportPath, BASE_DOCUMENT_FILENAME, documentBuilderCallback,
                        (DateFormat) dateFormat.clone(), (DateFormat) timeFormat.clone()));
        documentBuilders.put(ReportHandleRule.XLS_FORMAT,
                new XlsBuilder(reportPath, BASE_DOCUMENT_FILENAME, documentBuilderCallback,
                        (DateFormat) dateFormat.clone(), (DateFormat) timeFormat.clone()));
        documentBuilders.put(ReportHandleRule.HTML_FORMAT,
                new HtmlBuilder(reportPath, BASE_DOCUMENT_FILENAME, documentBuilderCallback,
                        (DateFormat) dateFormat.clone(), (DateFormat) timeFormat.clone()));
        documentBuilders.put(ReportHandleRule.CSV_FORMAT,
                new CsvBuilder(reportPath, BASE_DOCUMENT_FILENAME, documentBuilderCallback,
                        (DateFormat) dateFormat.clone(), (DateFormat) timeFormat.clone()));
        return documentBuilders;
    }

    public static class BuildTask implements Runnable {

        private static final Logger logger = LoggerFactory.getLogger(BuildTask.class);
        private final ExecutorService executorService;
        private final AutoReportProcessor autoReportProcessor;
        private final SessionFactory sessionFactory;
        private final String templateFileName;
        private final Date baseTime;
        private final Map<Integer, ReportDocumentBuilder> documentBuilders;

        public BuildTask(ExecutorService executorService, AutoReportProcessor autoReportProcessor,
                SessionFactory sessionFactory, String templateFileName, Date baseTime,
                Map<Integer, ReportDocumentBuilder> documentBuilders) {
            this.executorService = executorService;
            this.autoReportProcessor = autoReportProcessor;
            this.sessionFactory = sessionFactory;
            this.templateFileName = templateFileName;
            this.baseTime = baseTime;
            this.documentBuilders = documentBuilders;
        }

        public void run() {
            if (logger.isDebugEnabled()) {
                logger.debug(
                        String.format("Building auto reports \"%s\"", OrgBalanceJasperReport.class.getCanonicalName()));
            }
            String classPropertyValue = OrgBalanceJasperReport.class.getCanonicalName();
            List<AutoReport> autoReports = new LinkedList<AutoReport>();
            //Builder builder = new Builder(templateFileName);
            Session session = null;
            org.hibernate.Transaction transaction = null;
            try {
                session = sessionFactory.openSession();
                transaction = BasicReport.createTransaction(session);
                transaction.begin();
                Criteria allOrgsCriteria = session.createCriteria(Org.class);
                List allOrgs = allOrgsCriteria.list();
                for (Object currObject : allOrgs) {
                    Org currOrg = (Org) currObject;
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Building report \"%s\" for org: %s", classPropertyValue, currOrg));
                    }
                    Properties properties = new Properties();
                    ReportPropertiesUtils.addProperties(properties, OrgBalanceJasperReport.class, null);
                    ReportPropertiesUtils.addProperties(session, properties, currOrg, null);
                    OrgBalanceJasperReport report = new OrgBalanceJasperReport(baseTime, currOrg.getIdOfOrg(),
                            templateFileName, sessionFactory);
                    //OrgBalanceJasperReport report = builder.build(session, currOrg, baseTime);
                    autoReports.add(new AutoReport(report, properties));
                }
                transaction.commit();
                transaction = null;
                executorService.execute(
                        new AutoReportProcessor.ProcessTask(this.autoReportProcessor, autoReports, documentBuilders));
            } catch (Exception e) {
                logger.error(String.format("Failed at building auto reports \"%s\"", classPropertyValue), e);
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
        }

    }

    public static class BuildJob extends ExecutorServiceWrappedJob {

        public static class ExecuteEnvironment {

            private final ExecutorService executorService;
            private final SessionFactory sessionFactory;
            private final AutoReportProcessor autoReportProcessor;
            private final String reportPath;
            private final String templateFileName;
            private final DateFormat dateFormat;
            private final DateFormat timeFormat;

            public ExecuteEnvironment(ExecutorService executorService, SessionFactory sessionFactory,
                    AutoReportProcessor autoReportProcessor, String reportPath, String templateFileName,
                    DateFormat dateFormat, DateFormat timeFormat) {
                this.executorService = executorService;
                this.sessionFactory = sessionFactory;
                this.autoReportProcessor = autoReportProcessor;
                this.reportPath = reportPath;
                this.templateFileName = templateFileName;
                this.dateFormat = dateFormat;
                this.timeFormat = timeFormat;
            }

            public ExecutorService getExecutorService() {
                return executorService;
            }

            public SessionFactory getSessionFactory() {
                return sessionFactory;
            }

            public AutoReportProcessor getAutoReportProcessor() {
                return autoReportProcessor;
            }

            public String getReportPath() {
                return reportPath;
            }

            public String getTemplateFileName() {
                return templateFileName;
            }

            public DateFormat getDateFormat() {
                synchronized (dateFormat) {
                    return (DateFormat) dateFormat.clone();
                }
            }

            public DateFormat getTimeFormat() {
                synchronized (timeFormat) {
                    return (DateFormat) timeFormat.clone();
                }
            }
        }

        public static final String ENVIRONMENT_JOB_PARAM = ExecuteEnvironment.class.getCanonicalName();

        protected ExecutorService getExecutorService(JobExecutionContext context) throws Exception {
            final ExecuteEnvironment executeEnvironment = (ExecuteEnvironment) context.getJobDetail().getJobDataMap()
                    .get(ENVIRONMENT_JOB_PARAM);
            return executeEnvironment.getExecutorService();
        }

        protected Runnable getRunnable(JobExecutionContext context) {
            final ExecuteEnvironment executeEnvironment = (ExecuteEnvironment) context.getJobDetail().getJobDataMap()
                    .get(ENVIRONMENT_JOB_PARAM);
            return new BuildTask(executeEnvironment.getExecutorService(), executeEnvironment.getAutoReportProcessor(),
                    executeEnvironment.getSessionFactory(), executeEnvironment.getTemplateFileName(),
                    context.getScheduledFireTime(),
                    createDocumentBuilders(executeEnvironment.getReportPath(), executeEnvironment.getDateFormat(),
                            executeEnvironment.getTimeFormat()));
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(OrgBalanceJasperReport.class);
    private final Date baseTime;
    private final Long idOfOrg;
    private final String templateFilename;
    private final SessionFactory sessionFactory;

    public OrgBalanceJasperReport(Date generateTime, long generateDuration, JasperPrint print, Date baseTime,
            Long idOfOrg) {
        super(generateTime, generateDuration, print);
        this.baseTime = baseTime;
        this.idOfOrg = idOfOrg;
        this.templateFilename = null;
        this.sessionFactory = null;
    }

    public OrgBalanceJasperReport(Date baseTime, Long idOfOrg, String templateFilename, SessionFactory sessionFactory) {
        super();
        this.baseTime = baseTime;
        this.idOfOrg = idOfOrg;
        this.templateFilename = templateFilename;
        this.sessionFactory = sessionFactory;
    }

    public Date getBaseTime() {
        return baseTime;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    @Override
    public String toString() {
        return "OrgBalanceJasperReport{" + "baseTime=" + baseTime + ", idOfOrg=" + idOfOrg + ", templateFilename='"
                + templateFilename + '\'' + ", sessionFactory=" + sessionFactory + "} " + super.toString();
    }

    protected void prepare() {
        if (!hasPrint() && idOfOrg != null && templateFilename != null && sessionFactory != null) {
            Builder builder = new Builder(templateFilename);
            RuntimeContext runtimeContext = null;
            Session session = null;
            org.hibernate.Transaction transaction = null;
            try {
                runtimeContext = RuntimeContext.getInstance();
                session = sessionFactory.openSession();
                transaction = BasicReport.createTransaction(session);
                transaction.begin();
                Org org = (Org) session.get(Org.class, this.idOfOrg);
                OrgBalanceJasperReport report = builder.build(runtimeContext, session, org, baseTime);
                setGenerateTime(report.getGenerateTime());
                setGenerateDuration(report.getGenerateDuration());
                setPrint(report.getPrint());
                transaction.commit();
                transaction = null;
            } catch (Exception e) {
                logger.error(String.format("Failed at report lazy-build \"%s\"", OrgBalanceJasperReport.class), e);
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
        }
    }
}