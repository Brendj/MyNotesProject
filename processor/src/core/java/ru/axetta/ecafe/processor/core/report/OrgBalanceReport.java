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
import org.hibernate.Session;
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
public class OrgBalanceReport extends BasicReport {

    private static final String BASE_DOCUMENT_FILENAME;

    static {
        String fullName = OrgBalanceReport.class.getCanonicalName();
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
    }

    public static class OrgItem {

        private final Long idOfOrg;
        private final String shortName;
        private final String officialName;
        private final long totalClientPaymentSum;
        private final long totalOrderSumByCard;
        private final long totalOrderSumByCash;
        private final long totalBalance;
        private final List<ClientGroupItem> clientGroups;

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public String getShortName() {
            return shortName;
        }

        public String getOfficialName() {
            return officialName;
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

        public List<ClientGroupItem> getClientGroups() {
            return clientGroups;
        }

        public OrgItem(Org org, long totalClientPaymentSum, long totalOrderSumByCard, long totalOrderSumByCash,
                long totalBalance, List<ClientGroupItem> clientGroups) {
            this.idOfOrg = org.getIdOfOrg();
            this.shortName = org.getShortName();
            this.officialName = org.getOfficialName();
            this.totalClientPaymentSum = totalClientPaymentSum;
            this.totalOrderSumByCard = totalOrderSumByCard;
            this.totalOrderSumByCash = totalOrderSumByCash;
            this.totalBalance = totalBalance;
            this.clientGroups = clientGroups;
        }

        public OrgItem() {
            this.idOfOrg = null;
            this.shortName = null;
            this.officialName = null;
            this.totalClientPaymentSum = 0;
            this.totalOrderSumByCard = 0;
            this.totalOrderSumByCash = 0;
            this.totalBalance = 0;
            this.clientGroups = Collections.emptyList();
        }

        @Override
        public String toString() {
            return "OrgItem{" + "idOfOrg=" + idOfOrg + ", shortName='" + shortName + '\'' + ", officialName='"
                    + officialName + '\'' + ", totalClientPaymentSum=" + totalClientPaymentSum
                    + ", totalOrderSumByCard=" + totalOrderSumByCard + ", totalOrderSumByCash=" + totalOrderSumByCash
                    + ", totalBalance=" + totalBalance + '}';
        }
    }

    public static class Builder {

        private static class GrouppedClientsData {

            private final long totalClientPaymentSum;
            private final long totalOrderSumByCard;
            private final long totalOrderSumByCash;
            private final long totalBalance;
            private final List<ClientGroupItem> clientGroupItems;

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

            public List<ClientGroupItem> getClientGroupItems() {
                return clientGroupItems;
            }

            private GrouppedClientsData(long totalClientPaymentSum, long totalOrderSumByCard, long totalOrderSumByCash,
                    List<ClientGroupItem> clientGroupItems) {
                this.totalClientPaymentSum = totalClientPaymentSum;
                this.totalOrderSumByCard = totalOrderSumByCard;
                this.totalOrderSumByCash = totalOrderSumByCash;
                this.totalBalance = totalClientPaymentSum - totalOrderSumByCard;
                this.clientGroupItems = clientGroupItems;
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

        public OrgBalanceReport build(Session session, Date baseTime, Org org) throws Exception {
            Date generateTime = new Date();

            GrouppedClientsData grouppedClientsData = buildGrouppedClientsData(session, baseTime, org);
            ClientGroupItem unGrouppedClientsData = buildUnGrouppedClientsData(session, baseTime, org);
            List<ClientGroupItem> clientGroupItems = grouppedClientsData.getClientGroupItems();
            clientGroupItems.add(unGrouppedClientsData);

            return new OrgBalanceReport(generateTime, new Date().getTime() - generateTime.getTime(), baseTime,
                    new OrgItem(org, grouppedClientsData.getTotalClientPaymentSum() + unGrouppedClientsData
                            .getTotalClientPaymentSum(),
                            grouppedClientsData.getTotalOrderSumByCard() + unGrouppedClientsData
                                    .getTotalOrderSumByCard(),
                            grouppedClientsData.getTotalOrderSumByCash() + unGrouppedClientsData
                                    .getTotalOrderSumByCash(),
                            grouppedClientsData.getTotalBalance() + unGrouppedClientsData.getTotalBalance(),
                            clientGroupItems));
        }

        private static GrouppedClientsData buildGrouppedClientsData(Session session, Date baseTime, Org org)
                throws Exception {
            Criteria clientGroupsCriteria = session.createCriteria(ClientGroup.class);
            clientGroupsCriteria.add(Restrictions.eq("org", org));
            HibernateUtils.addAscOrder(clientGroupsCriteria, "groupName");
            List<ClientGroupItem> clientGroupItems = new LinkedList<ClientGroupItem>();
            List clientGroups = clientGroupsCriteria.list();
            long totalClientPaymentSum = 0;
            long totalOrderSumByCard = 0;
            long totalOrderSumByCash = 0;
            for (Object currObject : clientGroups) {
                ClientGroup currClientGroup = (ClientGroup) currObject;
                Criteria clientsCriteria = session.createCriteria(Client.class);
                clientsCriteria.add(Restrictions.eq("clientGroup", currClientGroup));
                clientsCriteria = clientsCriteria.createCriteria("person");
                HibernateUtils.addAscOrder(clientsCriteria, "surname");
                HibernateUtils.addAscOrder(clientsCriteria, "firstName");
                HibernateUtils.addAscOrder(clientsCriteria, "secondName");
                ClientGroupItem clientGroupItem = buildClientGroupItem(currClientGroup, session, baseTime,
                        clientsCriteria.list());
                totalClientPaymentSum += clientGroupItem.getTotalClientPaymentSum();
                totalOrderSumByCard += clientGroupItem.getTotalOrderSumByCard();
                totalOrderSumByCash += clientGroupItem.getTotalOrderSumByCash();
                clientGroupItems.add(clientGroupItem);
            }
            return new GrouppedClientsData(totalClientPaymentSum, totalOrderSumByCard, totalOrderSumByCash,
                    clientGroupItems);
        }

        private static ClientGroupItem buildUnGrouppedClientsData(Session session, Date baseTime, Org org)
                throws Exception {
            Criteria clientsCriteria = session.createCriteria(Client.class);
            clientsCriteria.add(Restrictions.eq("org", org));
            clientsCriteria.add(Restrictions.isNull("idOfClientGroup"));
            clientsCriteria = clientsCriteria.createCriteria("person");
            HibernateUtils.addAscOrder(clientsCriteria, "surname");
            HibernateUtils.addAscOrder(clientsCriteria, "firstName");
            HibernateUtils.addAscOrder(clientsCriteria, "secondName");
            return buildClientGroupItem(null, session, baseTime, clientsCriteria.list());
        }

        private static ClientGroupItem buildClientGroupItem(ClientGroup clientGroup, Session session, Date baseTime,
                Collection clients) throws Exception {
            List<ClientItem> clientItems = new LinkedList<ClientItem>();
            long totalClientPaymentSum = 0;
            long totalOrderSumByCard = 0;
            long totalOrderSumByCash = 0;
            for (Object currObject : clients) {
                Client currClient = (Client) currObject;
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
                    "select sum(clientOrder.sumByCard), sum(clientOrder.sumByCash)"+/*, sum(clientOrder.discount)*/" from Order clientOrder where clientOrder.client = ? and clientOrder.createTime < ?");
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
                writeReportDocumentTo((OrgBalanceReport) report, outputStream, dateFormat);
            } finally {
                IOUtils.closeQuietly(outputStream);
            }
        }

        private static void writeReportDocumentTo(OrgBalanceReport report, OutputStream outputStream,
                DateFormat dateFormat) throws Exception {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            try {
                String payFormUrl = runtimeContext.getPayformUrl(), payFormGroupUrl = runtimeContext.getPayformGroupUrl();
                Writer writer = new OutputStreamWriter(outputStream, "utf-8");
                writer.write("<html>");
                writer.write("<head>");
                writer.write("<title>");
                OrgItem org = report.getOrg();
                writer.write(StringEscapeUtils.escapeHtml(
                        String.format("Отчет по балансу по организации \"%s\"", org.getShortName())));
                writer.write("</title>");
                writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
                writer.write("<meta http-equiv=\"Content-Language\" content=\"ru\">");
                writer.write("</head>");
                writer.write("<body>");

                writer.write("<table>");
                writer.write("<tr>");
                writer.write("<td align=\"center\">");
                writer.write(StringEscapeUtils.escapeHtml(String.format("Отчет по балансу по организации \"%s\" на %s",
                        StringUtils.defaultString(org.getShortName()), dateFormat.format(report.getBaseTime()))));
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
                writer.write(StringEscapeUtils.escapeHtml(org.getShortName()));
                writer.write("</td>");
                writer.write("<td align=\"right\">");
                writer.write(StringEscapeUtils.escapeHtml(
                        CurrencyStringUtils.copecksToRubles(org.getTotalClientPaymentSum())));
                writer.write("</td>");
                writer.write("<td align=\"right\">");
                writer.write(StringEscapeUtils.escapeHtml(
                        CurrencyStringUtils.copecksToRubles(org.getTotalOrderSumByCard())));
                writer.write("</td>");
                writer.write("<td align=\"right\">");
                writer.write(StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(org.getTotalBalance())));
                writer.write("</td>");
                writer.write("<td/>");
                writer.write("</tr>");

                for (ClientGroupItem clientGroup : org.getClientGroups()) {
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
                            writer.write(String.format("<a href=\"%s\">%s</a>",
                                    StringEscapeUtils.escapeHtml(clientPayFormUrl),
                                    StringEscapeUtils.escapeHtml("квитанция")));
                        }
                        writer.write("</td>");
                        writer.write("</tr>");
                    }
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
            }
        }

    }

    private static class DocumentBuilderCallback implements BasicReport.DocumentBuilderCallback {

        public String getReportDistinctText(BasicReport report) {
            OrgBalanceReport orgBalanceReport = (OrgBalanceReport) report;
            return Long.toString(orgBalanceReport.getOrg().getIdOfOrg());
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
            String classPropertyValue = OrgBalanceReport.class.getCanonicalName();
            List<AutoReport> autoReports = new LinkedList<AutoReport>();
            //Builder builder = new Builder();
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
                    ReportPropertiesUtils.addProperties(properties, OrgBalanceReport.class, null);
                    ReportPropertiesUtils.addProperties(session, properties, currOrg, null);
                    OrgBalanceReport report = new OrgBalanceReport(baseTime, currOrg.getIdOfOrg(), sessionFactory);
                    //OrgBalanceReport report = builder.build(session, baseTime, currOrg);
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

    private static final Logger logger = LoggerFactory.getLogger(OrgBalanceReport.class);
    private final Date baseTime;
    private OrgItem org;
    private final Long idOfOrg;
    private final SessionFactory sessionFactory;

    public Date getBaseTime() {
        return baseTime;
    }

    public OrgItem getOrg() {
        prepare();
        return org;
    }

    public OrgBalanceReport(Date generateTime, long generateDuration, Date baseTime, OrgItem org) {
        super(generateTime, generateDuration);
        this.baseTime = baseTime;
        this.org = org;
        this.idOfOrg = org.getIdOfOrg();
        this.sessionFactory = null;
    }

    public OrgBalanceReport(Date baseTime, Long idOfOrg, SessionFactory sessionFactory) {
        super();
        this.baseTime = baseTime;
        this.org = null;
        this.idOfOrg = idOfOrg;
        this.sessionFactory = sessionFactory;
    }

    public OrgBalanceReport(Date baseTime) {
        super();
        this.baseTime = baseTime;
        this.org = new OrgItem();
        this.idOfOrg = null;
        this.sessionFactory = null;
    }

    @Override
    public String toString() {
        return "OrgBalanceReport{" + "baseTime=" + baseTime + ", org=" + org + ", idOfOrg=" + idOfOrg
                + ", sessionFactory=" + sessionFactory + "} " + super.toString();
    }

    protected void setOrg(OrgItem org) {
        this.org = org;
    }

    protected void prepare() {
        if (null == org && null != idOfOrg && null != sessionFactory) {
            Builder builder = new Builder();
            Session session = null;
            org.hibernate.Transaction transaction = null;
            try {
                session = sessionFactory.openSession();
                logger.info("Prepare. building balance report");
                transaction = BasicReport.createTransaction(session);
                transaction.begin();
                Org org = (Org) session.get(Org.class, this.idOfOrg);
                OrgBalanceReport report = builder.build(session, this.baseTime, org);
                setGenerateTime(report.getGenerateTime());
                setGenerateDuration(report.getGenerateDuration());
                setOrg(report.getOrg());
                transaction.commit();
                transaction = null;
                logger.info("Prepare. balance report has been builded");
            } catch (Exception e) {
                logger.error(String.format("Failed at report lazy-build \"%s\"", OrgBalanceReport.class), e);
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
        }
    }
}
