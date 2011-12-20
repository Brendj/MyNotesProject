/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.core.utils.ExecutorServiceWrappedJob;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 17.10.2009
 * Time: 14:09:39
 * To change this template use File | Settings | File Templates.
 */
public class ClientGroupBalanceReport extends BasicReport {

    private static final String BASE_DOCUMENT_FILENAME;

    static {
        String fullName = ClientGroupBalanceReport.class.getCanonicalName();
        int i = fullName.lastIndexOf('.');
        BASE_DOCUMENT_FILENAME = fullName.substring(i + 1);
    }

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

        private final Long contractId;
        private final PersonItem person;
        private final long totalClientPaymentSum;
        private final long totalOrderSumByCard;
        private final long totalOrderSumByCash;
        private final long totalBalance;

        public Long getContractId() {
            return contractId;
        }

        public PersonItem getPerson() {
            return person;
        }

        public long getTotalClientPaymentSum() {
            return totalClientPaymentSum;
        }

        public long getTotalOrderSumByCard() {
            return totalOrderSumByCard;
        }

        public long getTotalOrderSumByCash() {
            return totalOrderSumByCash;
        }

        public long getTotalBalance() {
            return totalBalance;
        }

        public ClientItem(Client client, long totalClientPaymentSum, long totalOrderSumByCard,
                long totalOrderSumByCash) {
            this.contractId = client.getContractId();
            this.person = new PersonItem(client.getPerson());
            this.totalClientPaymentSum = totalClientPaymentSum;
            this.totalOrderSumByCard = totalOrderSumByCard;
            this.totalOrderSumByCash = totalOrderSumByCash;
            this.totalBalance = totalClientPaymentSum - totalOrderSumByCard;
        }
    }

    public static class ClientGroupItem {

        private final Long idOfClientGroup;
        private final String groupName;
        private final long totalClientPaymentSum;
        private final long totalOrderSumByCard;
        private final long totalOrderSumByCash;
        private final long totalBalance;
        private final List<ClientItem> clients;

        public Long getIdOfClientGroup() {
            return idOfClientGroup;
        }

        public String getGroupName() {
            return groupName;
        }

        public long getTotalClientPaymentSum() {
            return totalClientPaymentSum;
        }

        public long getTotalOrderSumByCard() {
            return totalOrderSumByCard;
        }

        public long getTotalOrderSumByCash() {
            return totalOrderSumByCash;
        }

        public long getTotalBalance() {
            return totalBalance;
        }

        public List<ClientItem> getClients() {
            return clients;
        }

        public int getClientsWithDebtCount() {
            int n=0;
            for (ClientItem ci : clients) {
                if (ci.getTotalBalance()<0) n++;
            }
            return n;
        }

        public ClientGroupItem(ClientGroup clientGroup, long totalClientPaymentSum, long totalOrderSumByCard,
                long totalOrderSumByCash, List<ClientItem> clients) {
            if (null == clientGroup) {
                this.idOfClientGroup = null;
                this.groupName = null;
            } else {
                this.idOfClientGroup = clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup();
                this.groupName = clientGroup.getGroupName();
            }
            this.totalClientPaymentSum = totalClientPaymentSum;
            this.totalOrderSumByCard = totalOrderSumByCard;
            this.totalOrderSumByCash = totalOrderSumByCash;
            this.totalBalance = totalClientPaymentSum - totalOrderSumByCard;
            this.clients = clients;
        }

        public ClientGroupItem() {
            this.idOfClientGroup = null;
            this.groupName = null;
            this.totalClientPaymentSum = 0;
            this.totalOrderSumByCard = 0;
            this.totalOrderSumByCash = 0;
            this.totalBalance = 0;
            this.clients = Collections.emptyList();
        }

        @Override
        public String toString() {
            return "ClientGroupItem{" + "idOfClientGroup=" + idOfClientGroup + ", groupName='" + groupName + '\''
                    + ", totalClientPaymentSum=" + totalClientPaymentSum + ", totalOrderSumByCard="
                    + totalOrderSumByCard + ", totalOrderSumByCash=" + totalOrderSumByCash + ", totalBalance="
                    + totalBalance + '}';
        }

    }

    public static class OrgItem {

        private final Long idOfOrg;
        private final String shortName;
        private final String officialName;

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public String getShortName() {
            return shortName;
        }

        public String getOfficialName() {
            return officialName;
        }

        public OrgItem(Org org) {
            this.idOfOrg = org.getIdOfOrg();
            this.shortName = org.getShortName();
            this.officialName = org.getOfficialName();
        }

        public OrgItem() {
            this.idOfOrg = null;
            this.shortName = null;
            this.officialName = null;
        }

        @Override
        public String toString() {
            return "OrgItem{" + "idOfOrg=" + idOfOrg + ", shortName='" + shortName + '\'' + ", officialName='"
                    + officialName + '\'' + '}';
        }
    }

    public static class Builder {

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

        public ClientGroupBalanceReport build(Session session, Date baseTime, ClientGroup clientGroup)
                throws Exception {
            Date generateTime = new Date();
            ClientGroupItem clientGroupItem = buildClientGroupItem(clientGroup, session, baseTime);
            return new ClientGroupBalanceReport(generateTime, new Date().getTime() - generateTime.getTime(), baseTime,
                    new OrgItem(clientGroup.getOrg()), clientGroupItem);
        }

        private static ClientGroupItem buildClientGroupItem(ClientGroup clientGroup, Session session, Date baseTime)
                throws Exception {
            List<ClientItem> clientItems = new LinkedList<ClientItem>();
            long totalClientPaymentSum = 0;
            long totalOrderSumByCard = 0;
            long totalOrderSumByCash = 0;
            Criteria clientsCriteria = session.createCriteria(Client.class);
            clientsCriteria.add(Restrictions.eq("clientGroup", clientGroup));
            clientsCriteria = clientsCriteria.createCriteria("person");
            HibernateUtils.addAscOrder(clientsCriteria, "surname");
            HibernateUtils.addAscOrder(clientsCriteria, "firstName");
            HibernateUtils.addAscOrder(clientsCriteria, "secondName");
            List clients = clientsCriteria.list();
            for (Object object : clients) {
                Client currClient = (Client) object;
                ClientItem clientItem = buildClientItem(session, baseTime, currClient);
                clientItems.add(clientItem);
                totalClientPaymentSum += clientItem.getTotalClientPaymentSum();
                totalOrderSumByCard += clientItem.getTotalOrderSumByCard();
                totalOrderSumByCash += clientItem.totalOrderSumByCash;
            }
            return new ClientGroupItem(clientGroup, totalClientPaymentSum, totalOrderSumByCard, totalOrderSumByCash,
                    clientItems);
        }

        private static ClientItem buildClientItem(Session session, Date baseTime, Client client) throws Exception {
            TotalSums totalOrderSums = getTotalClientOrderSums(session, baseTime, client);
            TotalSums totalSmsSums = getTotalClientSmsSums(session, baseTime, client);
            TotalSums totalSubscriptionSums = getTotalSubscriptionSums(session, baseTime, client);
            return new ClientItem(client, getTotalClientPaymentSum(session, baseTime, client),
                    totalOrderSums.getSumByCard() + totalSmsSums.getSumByCard() + totalSubscriptionSums.getSumByCard(),
                    totalOrderSums.getSumByCash() + totalSmsSums.getSumByCash() + totalSubscriptionSums.getSumByCash());
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
                    "select sum(clientOrder.sumByCard), sum(clientOrder.sumByCash), sum(clientOrder.discount) from Order clientOrder where clientOrder.client = ? and clientOrder.createTime < ?");
            query.setParameter(0, client);
            query.setParameter(1, baseTime);
            Object[] result = (Object[]) query.uniqueResult();
            if (null == result) {
                return new TotalSums(0, 0, 0);
            } else {
                return new TotalSums(defaultValue((Long) result[0]), defaultValue((Long) result[1]),
                        defaultValue((Long) result[2]));
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

    public static class HtmlReportBuilder extends BasicDocumentBuilder {

        private final DateFormat dateFormat;

        public HtmlReportBuilder(String basePath, String baseFileName, DocumentBuilderCallback documentBuilderCallback,
                DateFormat dateFormat, DateFormat timeFormat) {
            super(basePath, baseFileName, "html", documentBuilderCallback, dateFormat, timeFormat);
            this.dateFormat = dateFormat;
        }

        protected void writeReportDocumentTo(BasicReport report, File file) throws Exception {
            FileOutputStream outputStream = new FileOutputStream(file);
            try {
                writeReportDocumentTo((ClientGroupBalanceReport) report, outputStream, dateFormat);
            } finally {
                IOUtils.closeQuietly(outputStream);
            }
        }

        private static void writeReportDocumentTo(ClientGroupBalanceReport report, OutputStream outputStream,
                DateFormat dateFormat) throws Exception {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            try {
                String payFormUrl = runtimeContext.getPayformUrl(), payFormGroupUrl = runtimeContext.getPayformGroupUrl();
                Writer writer = new OutputStreamWriter(outputStream, "utf-8");
                writer.write("<html>");
                writer.write("<head>");
                writer.write("<title>");
                OrgItem org = report.getOrg();
                ClientGroupItem clientGroup = report.getClientGroup();
                writer.write(StringEscapeUtils.escapeHtml(String.format("Отчет по организации \"%s\" по классу \"%s\"",
                        StringUtils.defaultString(org.getShortName()),
                        StringUtils.defaultString(clientGroup.getGroupName()))));
                writer.write("</title>");
                writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
                writer.write("<meta http-equiv=\"Content-Language\" content=\"ru\">");
                writer.write("</head>");
                writer.write("<body>");

                writer.write("<table>");
                writer.write("<tr>");
                writer.write("<td align=\"center\">");
                writer.write(StringEscapeUtils.escapeHtml(
                        String.format("Отчет по организации \"%s\" по классу \"%s\" на %s",
                                StringUtils.defaultString(org.getShortName()),
                                StringUtils.defaultString(clientGroup.getGroupName()),
                                dateFormat.format(report.getBaseTime()))));
                writer.write("</td>");
                writer.write("</tr>");

                writer.write("<tr>");
                writer.write("<td>");
                writer.write("<table>");

                writer.write("<tr>");
                writer.write("<td colspan=\"4\"/>");
                writer.write("<td>");
                writer.write(StringEscapeUtils.escapeHtml("Платежи"));
                writer.write("</td>");
                writer.write("<td>");
                writer.write(StringEscapeUtils.escapeHtml("Покупки по картам"));
                writer.write("</td>");
                writer.write("<td>");
                writer.write(StringEscapeUtils.escapeHtml("Баланс по картам"));
                writer.write("</td>");
                writer.write("<td>");
                writer.write(StringEscapeUtils.escapeHtml("Квитанция на пополнение счета"));
                writer.write("</td>");
                writer.write("</tr>");

                writer.write("<tr>");
                writer.write("<td colspan=\"4\">");
                writer.write(StringEscapeUtils.escapeHtml(StringUtils.defaultString(clientGroup.getGroupName())));
                writer.write("</td>");
                writer.write("<td align=\"right\">");
                writer.write(StringEscapeUtils.escapeHtml(
                        CurrencyStringUtils.copecksToRubles(clientGroup.getTotalClientPaymentSum())));
                writer.write("</td>");
                writer.write("<td align=\"right\">");
                writer.write(StringEscapeUtils.escapeHtml(
                        CurrencyStringUtils.copecksToRubles(clientGroup.getTotalOrderSumByCard())));
                writer.write("</td>");
                writer.write("<td align=\"right\">");
                writer.write(StringEscapeUtils.escapeHtml(
                        CurrencyStringUtils.copecksToRubles(clientGroup.getTotalBalance())));
                writer.write("</td>");
                writer.write("<td>");
                int nClientsWithDebt = clientGroup.getClientsWithDebtCount();
                if (nClientsWithDebt!=0) {
                    String clientGroupDebtPayFormUrl = String.format(payFormGroupUrl, org.getIdOfOrg(), clientGroup.getIdOfClientGroup(), "true");
                    writer.write(
                            String.format("<a href=\"%s\">%s</a>", StringEscapeUtils.escapeHtml(clientGroupDebtPayFormUrl),
                                    StringEscapeUtils.escapeHtml(" [квитанции по должникам ("+nClientsWithDebt+")] ")));
                }
                String clientGroupDebtPayFormUrl = String.format(payFormGroupUrl, org.getIdOfOrg(), clientGroup.getIdOfClientGroup(), "false");
                writer.write(
                        String.format("<a href=\"%s\">%s</a>", StringEscapeUtils.escapeHtml(clientGroupDebtPayFormUrl),
                                StringEscapeUtils.escapeHtml(" [квитанции ("+clientGroup.getClients().size()+")] ")));
                writer.write("</td>");
                writer.write("</tr>");

                for (ClientItem client : clientGroup.getClients()) {
                    writer.write("<tr>");
                    writer.write("<td align=\"right\">");
                    writer.write(StringEscapeUtils.escapeHtml(ContractIdFormat.format(client.getContractId())));
                    writer.write("</td>");
                    PersonItem person = client.getPerson();
                    writer.write("<td>");
                    writer.write(StringEscapeUtils.escapeHtml(person.getSurname()));
                    writer.write("</td>");
                    writer.write("<td>");
                    writer.write(StringEscapeUtils.escapeHtml(person.getFirstName()));
                    writer.write("</td>");
                    writer.write("<td>");
                    writer.write(StringEscapeUtils.escapeHtml(person.getSecondName()));
                    writer.write("</td>");
                    writer.write("<td align=\"right\">");
                    writer.write(StringEscapeUtils.escapeHtml(
                            CurrencyStringUtils.copecksToRubles(client.getTotalClientPaymentSum())));
                    writer.write("</td>");
                    writer.write("<td align=\"right\">");
                    writer.write(StringEscapeUtils.escapeHtml(
                            CurrencyStringUtils.copecksToRubles(client.getTotalOrderSumByCard())));
                    writer.write("</td>");
                    writer.write("<td align=\"right\">");
                    writer.write(StringEscapeUtils.escapeHtml(
                            CurrencyStringUtils.copecksToRubles(client.getTotalBalance())));
                    writer.write("</td>");
                    writer.write("<td>");
                    if (client.getTotalBalance() < 0) {
                        String clientPayFormUrl = String.format(payFormUrl, client.getContractId().toString());
                        writer.write(
                                String.format("<a href=\"%s\">%s</a>", StringEscapeUtils.escapeHtml(clientPayFormUrl),
                                        StringEscapeUtils.escapeHtml("квитанция")));
                    }
                    writer.write("</td>");
                    writer.write("</tr>");
                }

                writer.write("</table>");
                writer.write("</td>");
                writer.write("</tr>");

                writer.write("</table>");

                writer.write("<tr>");
                writer.write("<td>");
                writer.write(StringEscapeUtils.escapeHtml(
                        String.format("Продолжительность формирования отчета %d мс", report.getGenerateDuration())));
                writer.write("</td>");
                writer.write("</tr>");

                writer.write("</body>");
                writer.write("</html>");
                writer.flush();
            } finally {
                runtimeContext.release();
            }
        }

    }

    private static class DocumentBuilderCallback implements BasicReport.DocumentBuilderCallback {

        public String getReportDistinctText(BasicReport report) {
            ClientGroupBalanceReport clientGroupBalanceReport = (ClientGroupBalanceReport) report;
            return String.format("%s-%s", Long.toString(clientGroupBalanceReport.getOrg().getIdOfOrg()),
                    clientGroupBalanceReport.getClientGroup().getGroupName());
        }
    }

    static Map<Integer, ReportDocumentBuilder> createDocumentBuilders(String reportPath, DateFormat dateFormat,
            DateFormat timeFormat) {
        DocumentBuilderCallback documentBuilderCallback = new DocumentBuilderCallback();
        Map<Integer, ReportDocumentBuilder> documentBuilders = new HashMap<Integer, ReportDocumentBuilder>();
        documentBuilders.put(ReportHandleRule.HTML_FORMAT,
                new HtmlReportBuilder(reportPath, BASE_DOCUMENT_FILENAME, documentBuilderCallback,
                        (DateFormat) dateFormat.clone(), (DateFormat) timeFormat.clone()));
        return documentBuilders;
    }

    public static class BuildTask implements Runnable {

        private static final Logger logger = LoggerFactory.getLogger(BuildTask.class);
        private final ExecutorService executorService;
        private final AutoReportProcessor autoReportProcessor;
        private final SessionFactory sessionFactory;
        private final Date baseTime;
        private final Map<Integer, ReportDocumentBuilder> documentBuilders;

        public BuildTask(ExecutorService executorService, AutoReportProcessor autoReportProcessor,
                SessionFactory sessionFactory, Date baseTime, Map<Integer, ReportDocumentBuilder> documentBuilders) {
            this.executorService = executorService;
            this.autoReportProcessor = autoReportProcessor;
            this.sessionFactory = sessionFactory;
            this.baseTime = baseTime;
            this.documentBuilders = documentBuilders;
        }

        public void run() {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Building auto reports \"%s\"", OrgBalanceReport.class.getCanonicalName()));
            }
            String classPropertyValue = ClientGroupBalanceReport.class.getCanonicalName();
            List<AutoReport> autoReports = new LinkedList<AutoReport>();
            //Builder builder = new Builder();
            Session session = null;
            org.hibernate.Transaction transaction = null;
            try {
                session = sessionFactory.openSession();
                transaction = BasicReport.createTransaction(session);
                transaction.begin();
                Criteria allClientGroupsCriteria = session.createCriteria(ClientGroup.class);
                List allClientGroups = allClientGroupsCriteria.list();
                for (Object currObject : allClientGroups) {
                    ClientGroup currClientGroup = (ClientGroup) currObject;
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Building report \"%s\" for client group: %s", classPropertyValue,
                                currClientGroup));
                    }
                    Properties properties = new Properties();
                    ReportPropertiesUtils.addProperties(properties, ClientGroupBalanceReport.class);
                    ReportPropertiesUtils.addProperties(properties, currClientGroup.getOrg(), null);
                    ReportPropertiesUtils.addProperties(properties, currClientGroup, null);
                    ClientGroupBalanceReport report = new ClientGroupBalanceReport(baseTime,
                            currClientGroup.getCompositeIdOfClientGroup(), sessionFactory);
                    //ClientGroupBalanceReport report = builder.build(session, baseTime, currClientGroup);
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
            private final DateFormat dateFormat;
            private final DateFormat timeFormat;

            public ExecuteEnvironment(ExecutorService executorService, SessionFactory sessionFactory,
                    AutoReportProcessor autoReportProcessor, String reportPath, DateFormat dateFormat,
                    DateFormat timeFormat) {
                this.executorService = executorService;
                this.sessionFactory = sessionFactory;
                this.autoReportProcessor = autoReportProcessor;
                this.reportPath = reportPath;
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
                    executeEnvironment.getSessionFactory(), context.getScheduledFireTime(),
                    createDocumentBuilders(executeEnvironment.getReportPath(), executeEnvironment.getDateFormat(),
                            executeEnvironment.getTimeFormat()));
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(ClientGroupBalanceReport.class);
    private final Date baseTime;
    private OrgItem orgItem;
    private ClientGroupItem clientGroup;
    private final CompositeIdOfClientGroup compositeIdOfClientGroup;
    private final SessionFactory sessionFactory;

    public Date getBaseTime() {
        return baseTime;
    }

    public OrgItem getOrg() {
        prepare();
        return orgItem;
    }

    public ClientGroupItem getClientGroup() {
        prepare();
        return clientGroup;
    }

    public ClientGroupBalanceReport(Date generateTime, long generateDuration, Date baseTime, OrgItem orgItem,
            ClientGroupItem clientGroup) {
        super(generateTime, generateDuration);
        this.baseTime = baseTime;
        this.orgItem = orgItem;
        this.clientGroup = clientGroup;
        this.compositeIdOfClientGroup = new CompositeIdOfClientGroup(orgItem.getIdOfOrg(),
                clientGroup.getIdOfClientGroup());
        this.sessionFactory = null;
    }

    public ClientGroupBalanceReport(Date baseTime, CompositeIdOfClientGroup compositeIdOfClientGroup,
            SessionFactory sessionFactory) {
        super();
        this.baseTime = baseTime;
        this.orgItem = null;
        this.clientGroup = null;
        this.compositeIdOfClientGroup = compositeIdOfClientGroup;
        this.sessionFactory = sessionFactory;
    }

    public ClientGroupBalanceReport(Date baseTime) {
        super();
        this.baseTime = baseTime;
        this.orgItem = new OrgItem();
        this.clientGroup = new ClientGroupItem();
        this.compositeIdOfClientGroup = null;
        this.sessionFactory = null;
    }

    @Override
    public String toString() {
        return "ClientGroupBalanceReport{" + "baseTime=" + baseTime + ", orgItem=" + orgItem + ", clientGroup="
                + clientGroup + ", compositeIdOfClientGroup=" + compositeIdOfClientGroup + ", sessionFactory="
                + sessionFactory + "} " + super.toString();
    }

    protected void setOrgItem(OrgItem orgItem) {
        this.orgItem = orgItem;
    }

    protected void setClientGroup(ClientGroupItem clientGroup) {
        this.clientGroup = clientGroup;
    }

    protected void prepare() {
        if ((null == orgItem || null == clientGroup) && null != compositeIdOfClientGroup && null != sessionFactory) {
            Builder builder = new Builder();
            Session session = null;
            org.hibernate.Transaction transaction = null;
            try {
                session = sessionFactory.openSession();
                transaction = BasicReport.createTransaction(session);
                transaction.begin();
                ClientGroup clientGroup = (ClientGroup) session.get(ClientGroup.class, this.compositeIdOfClientGroup);
                ClientGroupBalanceReport report = builder.build(session, this.baseTime, clientGroup);
                setGenerateTime(report.getGenerateTime());
                setGenerateDuration(report.getGenerateDuration());
                setOrgItem(report.getOrg());
                setClientGroup(report.getClientGroup());
                transaction.commit();
                transaction = null;
            } catch (Exception e) {
                logger.error(String.format("Failed at report lazy-build \"%s\"", ClientGroupBalanceReport.class), e);
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
        }
    }
}