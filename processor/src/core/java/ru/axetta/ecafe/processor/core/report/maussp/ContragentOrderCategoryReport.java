/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.maussp;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.report.*;
import ru.axetta.ecafe.processor.core.utils.*;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 17.10.2009
 * Time: 14:09:39
 * To change this template use File | Settings | File Templates.
 */
public class ContragentOrderCategoryReport extends BasicJasperReport {

    private static final String BASE_DOCUMENT_FILENAME;

    static {
        String fullName = ContragentOrderCategoryReport.class.getCanonicalName();
        int i = fullName.lastIndexOf('.');
        BASE_DOCUMENT_FILENAME = fullName.substring(i + 1);
    }

    public static class Builder {

        private static class OrderCategory {

            private final String orderCategoryJavaRegexpMask;
            private final String reportTitle;

            private OrderCategory(String orderCategoryJavaRegexpMask, String reportTitle) {
                this.orderCategoryJavaRegexpMask = orderCategoryJavaRegexpMask;
                this.reportTitle = reportTitle;
            }

            public String getOrderCategoryJavaRegexpMask() {
                return orderCategoryJavaRegexpMask;
            }

            public String getReportTitle() {
                return reportTitle;
            }
        }

        private static class OrderCategoryItem {

            private final OrderCategory orderCategory;
            private final Pattern pattern;
            private long sum;

            private OrderCategoryItem(OrderCategory orderCategory) {
                this.orderCategory = orderCategory;
                this.pattern = Pattern.compile(orderCategory.getOrderCategoryJavaRegexpMask());
                this.sum = 0L;
            }

            public OrderCategory getOrderCategory() {
                return orderCategory;
            }

            public long getSum() {
                return sum;
            }

            public void setSum(long sum) {
                this.sum = sum;
            }

            public void addSum(long sum) {
                this.sum += sum;
            }

            public void subSum(long sum) {
                this.sum -= sum;
            }

            public boolean matches(String orderCategory) {
                return this.pattern.matcher(orderCategory).matches();
            }
        }

        private static final OrderCategory COMPLEX_ORDER_CATEGORY = new OrderCategory("\\[.*\\]",
                "Комплексное питание");

        private static final List<OrderCategory> MAUSSP_BUFFET_ORDER_CATEGORIES = Arrays
                .asList(new OrderCategory("Буфет", "Буфет"),
                        new OrderCategory("Буфет \\(выпечка\\)", "Буфет (выпечка)"),
                        new OrderCategory("Буфет \\(кухня\\)", "Буфет (кухня)"));

        public static class Row {

            private final String orgName;
            private final String firstName;
            private final String surname;
            private final String secondName;
            private final String nameAbbreviation;
            private final Long clientAccount;
            private final String orderCategory;
            private final Long sum;

            public Row(Long clientAccount, Client client, String orderCategory, long sum) {
                this.orgName = client.getOrg().getShortName();
                Person person = client.getPerson();
                this.firstName = person.getFirstName();
                this.surname = person.getSurname();
                this.secondName = person.getSecondName();
                this.nameAbbreviation = AbbreviationUtils
                        .buildFullAbbreviation(person.getFirstName(), person.getSurname(), person.getSecondName());
                this.clientAccount = clientAccount;
                this.orderCategory = orderCategory;
                this.sum = sum;
            }

            public String getOrgName() {
                return orgName;
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

            public String getNameAbbreviation() {
                return nameAbbreviation;
            }

            public Long getClientAccount() {
                return clientAccount;
            }

            public String getOrderCategory() {
                return orderCategory;
            }

            public Long getSum() {
                return sum;
            }
        }

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public ContragentOrderCategoryReport build(Session session, Org org, Date startTime, Date endTime,
                Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone()));
            Date generateEndTime = new Date();
            return new ContragentOrderCategoryReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private List<OrderCategory> findBuffetOrderCategories() throws Exception {
            return MAUSSP_BUFFET_ORDER_CATEGORIES;
        }

        private static List<OrderCategoryItem> createOrderCategoryItems(List<OrderCategory> orderCategories) {
            List<OrderCategoryItem> orderCategoryItems = new LinkedList<OrderCategoryItem>();
            for (OrderCategory orderCategory : orderCategories) {
                orderCategoryItems.add(new OrderCategoryItem(orderCategory));
            }
            return orderCategoryItems;
        }

        private static void setZeroSum(List<OrderCategoryItem> orderCategoryItems) {
            for (OrderCategoryItem orderCategoryItem : orderCategoryItems) {
                orderCategoryItem.setSum(0L);
            }
        }

        private static void addSum(List<OrderCategoryItem> orderCategoryItems, String orderCategory, long sum)
                throws Exception {
            boolean foundOrderCategoryItem = false;
            for (OrderCategoryItem orderCategoryItem : orderCategoryItems) {
                if (orderCategoryItem.matches(orderCategory)) {
                    orderCategoryItem.addSum(sum);
                    foundOrderCategoryItem = true;
                    break;
                }
            }
            if (!foundOrderCategoryItem) {
                throw new IllegalArgumentException(String.format("Unknown order detail root menu: %s", orderCategory));
            }
        }

        private JRDataSource createDataSource(Session session, Org org, Date startTime, Date endTime,
                Calendar calendar) throws Exception {
            List<Row> rows = new LinkedList<Row>();

            List<OrderCategory> buffetOrderCategories = findBuffetOrderCategories();

            Criteria clientListCriteria = session.createCriteria(Client.class);
            clientListCriteria.add(Restrictions.eq("org", org));
            clientListCriteria.setFetchMode("person", FetchMode.JOIN);

            OrderCategoryItem complexOrderCategoryItem = new OrderCategoryItem(COMPLEX_ORDER_CATEGORY);
            List<OrderCategoryItem> cardBuffetOrderCategoryItems = createOrderCategoryItems(buffetOrderCategories);
            List<OrderCategoryItem> cashBuffetOrderCategoryItems = createOrderCategoryItems(buffetOrderCategories);

            Query ordersQuery = session.createQuery(
                    "from Order clientOrder where clientOrder.state=0 and clientOrder.client = ? and (clientOrder.createTime between ? and ?)");
            ordersQuery.setParameter(1, startTime);
            ordersQuery.setParameter(2, endTime);

            Query ccAccountQuery = session.createQuery(
                "from ContragentClientAccount ccAccount where ccAccount.client in (select c.idOfClient from Client c where c.org = ?)");

            Calendar tempCalendar = (Calendar) calendar.clone();

            List clientList = clientListCriteria.list();
            ccAccountQuery.setParameter(0, org);
            List ccAccountList = ccAccountQuery.list();
            for (Object object : clientList) {
                Client client=(Client)object;
                long accountId=client.getContractId();
                // lookup contragent account
                for (Iterator i=ccAccountList.iterator();i.hasNext();) {
                    ContragentClientAccount ccAccount=(ContragentClientAccount)i.next();
                    if (ccAccount.getClient().getIdOfClient().equals(client.getIdOfClient())) {
                        accountId=ccAccount.getIdOfAccount();
                        i.remove();
                        break;
                    }
                }
                //
                ordersQuery.setParameter(0, client);

                complexOrderCategoryItem.setSum(0L);
                setZeroSum(cardBuffetOrderCategoryItems);
                setZeroSum(cashBuffetOrderCategoryItems);
                long grantSum = 0L;
                long discountSum = 0L;

                List orderList = ordersQuery.list();
                for (Object orderObject : orderList) {
                    Order order = (Order) orderObject;

                    boolean hasSumByCard = order.getSumByCard() != 0L;
                    boolean hasSumByCash = order.getSumByCash() != 0L;
                    if (hasSumByCard && hasSumByCash) {
                        throw new IllegalArgumentException("Order has cash sum and card sum");
                    }

                    grantSum += order.getGrantSum();
                    discountSum += order.getSocDiscount();

                    boolean hasComplexDetails = false;
                    Set<OrderDetail> orderDetails = order.getOrderDetails();
                    for (OrderDetail orderDetail : orderDetails) {
                        if(orderDetail.getState()==1) continue;
                        long totalDetailSum =
                                (orderDetail.getRPrice() - orderDetail.getDiscount()) * orderDetail.getQty();
                        String orderCategory = orderDetail.getRootMenu();
                        if (complexOrderCategoryItem.matches(orderCategory)) {
                            hasComplexDetails = true;
                            complexOrderCategoryItem.addSum(totalDetailSum);
                        } else {
                            addSum(cardBuffetOrderCategoryItems, orderCategory, totalDetailSum);
                        }
                    }
                    if (hasComplexDetails) {
                        complexOrderCategoryItem.subSum(order.getGrantSum());
                    }
                }
                rows.add(new Row(accountId, client, complexOrderCategoryItem.getOrderCategory().getReportTitle(),
                        complexOrderCategoryItem.getSum()));
                rows.add(new Row(accountId, client, "Дотации", grantSum));
                rows.add(new Row(accountId, client, "Льготы", discountSum));
                for (OrderCategoryItem orderCategoryItem : cardBuffetOrderCategoryItems) {
                    rows.add(new Row(accountId, client, orderCategoryItem.getOrderCategory().getReportTitle(),
                            orderCategoryItem.getSum()));
                }
            }
            return new JRBeanCollectionDataSource(rows);
        }

    }

    private static class DocumentBuilderCallback implements BasicReport.DocumentBuilderCallback {

        public String getReportDistinctText(BasicReport report) {
            ContragentOrderCategoryReport contragentOrderCategoryReport = (ContragentOrderCategoryReport) report;
            return Long.toString(contragentOrderCategoryReport.getIdOfOrg());
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
        private final Calendar startCalendar;
        private final Date startTime;
        private final Date endTime;
        private final Map<Integer, ReportDocumentBuilder> documentBuilders;

        public BuildTask(ExecutorService executorService, AutoReportProcessor autoReportProcessor,
                SessionFactory sessionFactory, String templateFileName, Calendar startCalendar, Date startTime,
                Date endTime, Map<Integer, ReportDocumentBuilder> documentBuilders) {
            this.executorService = executorService;
            this.autoReportProcessor = autoReportProcessor;
            this.sessionFactory = sessionFactory;
            this.templateFileName = templateFileName;
            this.startCalendar = startCalendar;
            this.startTime = startTime;
            this.endTime = endTime;
            this.documentBuilders = documentBuilders;
        }

        public void run() {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Building auto reports \"%s\"",
                        ContragentOrderCategoryReport.class.getCanonicalName()));
            }
            String classPropertyValue = ContragentOrderCategoryReport.class.getCanonicalName();
            List<AutoReport> autoReports = new LinkedList<AutoReport>();
            Session session = null;
            org.hibernate.Transaction transaction = null;
            try {
                session = sessionFactory.openSession();
                transaction = createTransaction(session);
                transaction.begin();
                Criteria allOrgsCriteria = session.createCriteria(Org.class);
                List allOrgs = allOrgsCriteria.list();
                for (Object object : allOrgs) {
                    Org org = (Org) object;
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Building report \"%s\" for org: %s", classPropertyValue,
                                org));
                    }
                    Properties properties = new Properties();
                    ReportPropertiesUtils.addProperties(properties, ContragentOrderCategoryReport.class, null);
                    ReportPropertiesUtils.addProperties(session, properties, org, null);
                    ContragentOrderCategoryReport report = new ContragentOrderCategoryReport(startTime, endTime,
                            org.getIdOfOrg(), templateFileName, sessionFactory, startCalendar);
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
            private final Calendar calendar;
            private final DateFormat dateFormat;
            private final DateFormat timeFormat;

            public ExecuteEnvironment(ExecutorService executorService, SessionFactory sessionFactory,
                    AutoReportProcessor autoReportProcessor, String reportPath, String templateFileName,
                    Calendar calendar, DateFormat dateFormat, DateFormat timeFormat) {
                this.executorService = executorService;
                this.sessionFactory = sessionFactory;
                this.autoReportProcessor = autoReportProcessor;
                this.reportPath = reportPath;
                this.templateFileName = templateFileName;
                this.calendar = calendar;
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

            public Calendar getCalendar() {
                synchronized (calendar) {
                    return (Calendar) calendar.clone();
                }
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
            Calendar calendar = executeEnvironment.getCalendar();
            Date endTime = calculateEndTime(calendar, context.getScheduledFireTime());
            Date startTime = calculateStartTime(calendar, endTime);
            return new BuildTask(executeEnvironment.getExecutorService(), executeEnvironment.getAutoReportProcessor(),
                    executeEnvironment.getSessionFactory(), executeEnvironment.getTemplateFileName(), calendar,
                    startTime, endTime,
                    createDocumentBuilders(executeEnvironment.getReportPath(), executeEnvironment.getDateFormat(),
                            executeEnvironment.getTimeFormat()));
        }

        private static Date calculateEndTime(Calendar calendar, Date scheduledFireTime) {
            calendar.setTime(scheduledFireTime);
            CalendarUtils.truncateToMonth(calendar);
            return calendar.getTime();
        }

        private static Date calculateStartTime(Calendar calendar, Date endTime) {
            calendar.setTime(endTime);
            calendar.add(Calendar.MONTH, -1);
            return calendar.getTime();
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(ContragentOrderCategoryReport.class);
    private final Date startTime;
    private final Date endTime;
    private final Calendar calendar;
    private final Long idOfOrg;
    private final String templateFilename;
    private final SessionFactory sessionFactory;

    public ContragentOrderCategoryReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print);
        this.startTime = startTime;
        this.endTime = endTime;
        this.idOfOrg = idOfOrg;
        this.templateFilename = null;
        this.sessionFactory = null;
        this.calendar = null;
    }

    public ContragentOrderCategoryReport(Date startTime, Date endTime, Long idOfOrg, String templateFilename,
            SessionFactory sessionFactory, Calendar calendar) {
        super();
        this.startTime = startTime;
        this.endTime = endTime;
        this.idOfOrg = idOfOrg;
        this.templateFilename = templateFilename;
        this.sessionFactory = sessionFactory;
        this.calendar = calendar;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    @Override
    public String toString() {
        return "ContragentOrderReport{" + "startTime=" + startTime + ", endTime=" + endTime + ", idOfOrg="
                + idOfOrg + ", templateFilename='" + templateFilename + '\'' + ", sessionFactory="
                + sessionFactory + "} " + super.toString();
    }

    protected void prepare() {
        if (!hasPrint() && idOfOrg != null && templateFilename != null && sessionFactory != null) {
            Builder builder = new Builder(templateFilename);
            Session session = null;
            org.hibernate.Transaction transaction = null;
            try {
                session = sessionFactory.openSession();
                transaction = createTransaction(session);
                transaction.begin();
                Org org = (Org)session.get(Org.class, this.idOfOrg);
                ContragentOrderCategoryReport report = builder.build(session, org, startTime, endTime, calendar);
                setGenerateTime(report.getGenerateTime());
                setGenerateDuration(report.getGenerateDuration());
                setPrint(report.getPrint());
                transaction.commit();
                transaction = null;
            } catch (Exception e) {
                logger.error(String.format("Failed at report lazy-build \"%s\"", ContragentOrderCategoryReport.class),
                        e);
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
        }
    }
}