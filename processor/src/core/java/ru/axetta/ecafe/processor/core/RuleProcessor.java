/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core;

import ru.axetta.ecafe.processor.core.event.BasicEvent;
import ru.axetta.ecafe.processor.core.event.EventDocumentBuilder;
import ru.axetta.ecafe.processor.core.event.EventProcessor;
import ru.axetta.ecafe.processor.core.mail.Postman;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.report.*;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;
import ru.axetta.ecafe.processor.core.utils.RuleExpressionUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 15.12.2009
 * Time: 14:00:06
 * To change this template use File | Settings | File Templates.
 */
public class RuleProcessor implements AutoReportProcessor, EventProcessor {

    public static final String ORG_EXPRESSION = "org:";
    public static final String CONTRAGENT_EXPRESSION = "contragent:";
    public static final String CONTRAGENT_PAYAGENT_EXPRESSION = "contragent-payagent:"; // Использовать для контрагентов агент по платежам !!!!
    public static final String CONTRAGENT_RECEIVER_EXPRESSION = "contragent-receiver:"; // Использовать для контрагентов ТСП !!!!
    public static final String COMBOBOX_EXPRESSION = "комбобокс:";
    public static final String INPUT_EXPRESSION = "значение:";
    public static final String CHECKBOX_EXPRESSION = "чекбокс:";
    public static final String RADIO_EXPRESSION = "опции:";
    public static final String METHOD_EXPRESSION = "метод:";
    public static final String DELIMETER = ",";
    private static final Logger logger = LoggerFactory.getLogger(RuleProcessor.class);
    private final SessionFactory sessionFactory;
    private final AutoReportPostman autoReportPostman;
    private final Postman eventNotificationPostman;
    private final Object reportRulesLock;
    private final Object eventNotificationsLock;
    Pattern periodMatcher = Pattern.compile("(\\d+)-(L|\\d+)[, ]*");
    private Rule currRule;
    private Properties reportProperties;
    private List<Rule> reportRules;
    private List<Rule> eventNotifications;

    public RuleProcessor(SessionFactory sessionFactory, AutoReportPostman autoReportPostman,
            Postman eventNotificationPostman) {
        this.sessionFactory = sessionFactory;
        this.autoReportPostman = autoReportPostman;
        this.eventNotificationPostman = eventNotificationPostman;
        this.reportRules = Collections.emptyList();
        this.reportRulesLock = new Object();
        this.eventNotificationsLock = new Object();
    }

    public static Map<String, String> getParametersFromString(String parameters) {
        if (!parameters.contains(COMBOBOX_EXPRESSION) &&
                !parameters.contains(CHECKBOX_EXPRESSION) &&
                !parameters.contains(RADIO_EXPRESSION) &&
                !parameters.contains(METHOD_EXPRESSION)) {
            return Collections.emptyMap();
        }
        if (parameters.contains(METHOD_EXPRESSION)) {
            try {
                String method = parameters
                        .substring(parameters.indexOf(METHOD_EXPRESSION) + METHOD_EXPRESSION.length());
                parameters = getMethodExecutionResult(method);
            } catch (Exception e) {
                return Collections.emptyMap();
            }
        }

        Map<String, String> result = new HashMap<String, String>();
        parameters = parameters.replaceAll(COMBOBOX_EXPRESSION, "");
        parameters = parameters.replaceAll(CHECKBOX_EXPRESSION, "");
        parameters = parameters.replaceAll(RADIO_EXPRESSION, "");
        parameters = parameters.replaceAll(METHOD_EXPRESSION, "");

        String parts[] = parameters.split(DELIMETER);
        Pattern pattern = Pattern.compile("(\\{([а-яА-Яa-zA-Z0-9\\u005F]*)\\})?([-а-яА-Яa-zA-Z0-9\\s]+)");
        for (String p : parts) {
            Matcher matcher = pattern.matcher(p);
            while (matcher.find()) {
                String group1 = matcher.group(2);
                String group2 = matcher.group(3);
                if (group1 == null || group1.length() < 1) {
                    group1 = group2;
                }
                if (group1 != null && group1.length() > 0) {
                    result.put(group1, group2);
                }
            }
        }
        return result;
    }

    public static String getMethodExecutionResult(String method) throws Exception {
        //  Данный метод возвращает унифицированную строку для результата от выполнения метода в виде {ключ}значение, {ключ}значение
        if (method == null || method.length() < 1) {
            return "";
        }


        List<String[]> values = new ArrayList<String[]>();
        try {
            Class cl = Class.forName(method.substring(0, method.lastIndexOf(".")));
            java.lang.reflect.Method meth = cl
                    .getDeclaredMethod(method.substring(method.lastIndexOf(".") + 1), Session.class, Map.class,
                            List.class);
            meth.invoke(RuntimeContext.getInstance().getAutoReportProcessor(), null, Collections.EMPTY_MAP, values);
        } catch (Exception e) {
            throw e;
        }

        String result = "";
        for (String[] v : values) {
            if (v[1] == null || v[1].length() < 1) {
                continue;
            }
            if (result.length() > 0) {
                result = result + ",";
            }
            result = result + "{" + v[0] + "}" + v[1];
        }
        return result;
    }

    public static final String parseMethodExecutionResultForEquals(String methodResult) {
        //  Данный метод обрабатывает результат от метода в формате {ключ}значение, {ключ}значение в ключ, ключ
        Pattern pattern = Pattern.compile("\\{{1}([a-zA-Z0-9]*)\\}{1}");
        Matcher matcher = pattern.matcher(methodResult);
        String result = "";
        while (matcher.find()) {
            if (result.length() > 0) {
                result += DELIMETER;
            }
            result += matcher.group();
        }
        return result;
    }

    public static String fillTemplate(String pattern, Properties properties) {
        if (StringUtils.isEmpty(pattern)) {
            return StringUtils.defaultString(pattern);
        }
        StringBuilder stringBuilder = new StringBuilder(pattern);
        StringBuilder paramMatcherBuilder = new StringBuilder();
        for (Map.Entry<?, ?> entry : properties.entrySet()) {
            String param = (String) entry.getKey();
            if (StringUtils.isNotEmpty(param)) {
                String value = StringUtils.defaultString((String) entry.getValue());
                int len = paramMatcherBuilder.length();
                if (len > 0) {
                    paramMatcherBuilder.delete(0, len);
                }
                paramMatcherBuilder.append("${").append(param).append("}");
                String paramMatcher = paramMatcherBuilder.toString();
                int start = stringBuilder.indexOf(paramMatcher, 0);
                while (start >= 0) {
                    stringBuilder.replace(start, start + paramMatcher.length(), value);
                    start = stringBuilder.indexOf(paramMatcher, start + StringUtils.length(value) + 1);
                }
            }
        }
        return stringBuilder.toString();
    }

    public void testMethodCalling(Session session, Map<String, Object> parameters, List<String[]> result) {
        result.add(new String[]{"1", "один"});
        result.add(new String[]{"2", "два"});
        result.add(new String[]{"3", "три"});
    }

    public void inputValueMethodCalling(Session session, Map<String, Object> parameters, List<String[]> result) {
        result.add(new String[]{
                "", "значение будет здесь"});   //  ключ можно не заполнять, браться будет всегда только первое значение
    }

    /**
     * Warning: has to be threadsafe
     *
     * @param reports
     * @param reportDocumentBuilders
     * @throws Exception
     */
    public void processAutoReports(List<AutoReport> reports, Map<Integer, ReportDocumentBuilder> reportDocumentBuilders, List<Long> reportHandleRuleIdsList)
            throws Exception {
        if (!reports.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Handling reports \"%s\"",
                        reports.iterator().next().getBasicReport().getClass().getCanonicalName()));
            }

            List<Rule> rulesCopy;
            synchronized (this.reportRulesLock) {
                rulesCopy = this.reportRules;
            }

            Date originalReportStartTime = null, originalReportEndTime = null;
            for (AutoReport report : reports) {
                BasicReport basicReport = report.getBasicReport();
                for (Rule currRule : rulesCopy) {
                    Properties reportProperties = copyProperties(report.getProperties());
                    ReportInfo reportInfo = null;

                    boolean existFlag = getFlag(Long.valueOf(currRule.getRuleId()), reportHandleRuleIdsList);

                    if (currRule.applicatable(reportProperties) && existFlag) {
                        if (currRule.getExpressionValue(ReportPropertiesUtils.P_REPORT_PERIOD_TYPE) != null &&
                                !(reportProperties.getProperty(ReportPropertiesUtils.P_DATES_SPECIFIED_BY_USER) + "")
                                        .equals("true") && basicReport instanceof BasicReportJob) {
                            originalReportStartTime = ((BasicReportJob) basicReport).getStartTime();
                            originalReportEndTime = ((BasicReportJob) basicReport).getEndTime();
                            applyRulePeriodType(currRule, (BasicReportJob) basicReport);
                        }
                        if (currRule.getExpressionValue(ReportPropertiesUtils.P_REPORT_PERIOD) != null &&
                                !(reportProperties.getProperty(ReportPropertiesUtils.P_DATES_SPECIFIED_BY_USER) + "")
                                        .equals("true") && basicReport instanceof BasicReportJob) {
                            if (originalReportStartTime == null) {
                                originalReportStartTime = ((BasicReportJob) basicReport).getStartTime();
                                originalReportEndTime = ((BasicReportJob) basicReport).getEndTime();
                            }
                            applyRulePeriod(currRule, (BasicReportJob) basicReport);
                        }
                        ////
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("Report \"%s\" is applicatable for discountrule \"%s\"", report,
                                    currRule));
                        }
                        if (basicReport instanceof BasicReportJob && currRule.getTemplateFileName() != null && !currRule
                                .getTemplateFileName().isEmpty()) {
                            String reportPath = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
                            ((BasicReportJob) basicReport).setTemplateFilename(reportPath + currRule.getTemplateFileName());
                        }
                        String subject = "";
                        Long idOfOrg = null;
                        if (!StringUtils.isEmpty(report.getProperties().getProperty("idOfOrg"))) {
                            if (!report.getProperties().getProperty("idOfOrg").contains(",")) {
                                idOfOrg = Long.parseLong(report.getProperties().getProperty("idOfOrg"));
                            }
                        }

                        ReportDocumentBuilder documentBuilder = reportDocumentBuilders
                                .get(currRule.getDocumentFormat());
                        ReportDocument reportDocument = null;
                        if (null == documentBuilder) {
                            if (logger.isWarnEnabled()) {
                                logger.warn(String.format(
                                        "Can't build document with format = %s for report %s - apropriate document builder not found",
                                        currRule.getDocumentFormat(), report));
                            }
                        } else {
                            subject = fillTemplate(currRule.getSubject(), reportProperties);
                            basicReport.setReportProperties(reportProperties);
                            reportInfo = registerQuartzJobTriggeredStage(subject, idOfOrg, currRule);
                            try {
                                reportDocument = documentBuilder.buildDocument(currRule.getRuleId() + "", basicReport);
                            }catch(Throwable ex) {
                                String fullStackTrace = ExceptionUtils.getFullStackTrace(ex);
                                registerErrorDuringReportGeneration(reportInfo, fullStackTrace);
                                logger.error(String.format("Error during report generation with report info id=%d. Error is %s", reportInfo.getIdOfReportInfo(), ExceptionUtils.getFullStackTrace(ex)));
                                if (ex instanceof Exception) {
                                    continue;
                                }
                            }
                            if (basicReport instanceof BasicReportJob) {
                                BasicReportJob basicReportJob = (BasicReportJob) basicReport;
                                File f = new File(
                                        RuntimeContext.getInstance().getAutoReportGenerator().getReportPath());
                                String relativeReportFilePath = reportDocument.getReportFile().getAbsolutePath()
                                        .substring(f.getAbsolutePath().length());
                                Long idOfContragent = null;
                                String contragent = null;
                                if (basicReport instanceof BasicReportForContragentJob) {
                                    BasicReportForContragentJob contragentJob = (BasicReportForContragentJob) basicReport;
                                    idOfContragent = contragentJob.getIdOfContragent();
                                    contragent = DAOReadonlyService.getInstance().getContragentById(idOfContragent)
                                            .getContragentName();
                                }
                                Long idOfContragentReceiver = null;
                                String contragentReceiver = null;
                                if (reportProperties != null) {
                                    final String property = reportProperties
                                            .getProperty(ContragentPaymentReport.PARAM_CONTRAGENT_RECEIVER_ID);
                                    if (StringUtils.isNotEmpty(property)) {
                                        idOfContragentReceiver = Long.valueOf(property);
                                        contragentReceiver = DAOReadonlyService.getInstance()
                                                .getContragentById(idOfContragentReceiver).getContragentName();
                                    }
                                }
                                reportInfo = registerReportGeneratedStage(reportInfo, currRule.getRuleName(),
                                        currRule.getDocumentFormat(), subject,
                                        basicReport.getGenerateTime(), basicReport.getGenerateDuration(),
                                        basicReportJob.getStartTime(), basicReportJob.getEndTime(),
                                        relativeReportFilePath, report.getProperties()
                                        .getProperty(ReportPropertiesUtils.P_ORG_NUMBER_IN_NAME), idOfOrg,
                                        currRule.getTag(), idOfContragentReceiver, contragentReceiver,
                                        idOfContragent, contragent, ReportInfo.REPORT_GENERATED);
                            }

                            if (basicReport instanceof OrgBalanceReport) {
                                OrgBalanceReport basicReportJob = (OrgBalanceReport) basicReport;
                                File f = new File(
                                        RuntimeContext.getInstance().getAutoReportGenerator().getReportPath());
                                String relativeReportFilePath = reportDocument.getReportFile().getAbsolutePath()
                                        .substring(f.getAbsolutePath().length());
                                reportInfo = registerReportGeneratedStage(reportInfo, currRule.getRuleName(),
                                        currRule.getDocumentFormat(), subject,
                                        basicReport.getGenerateTime(), basicReport.getGenerateDuration(),
                                        basicReportJob.getBaseTime(), basicReportJob.getBaseTime(),
                                        relativeReportFilePath, report.getProperties()
                                        .getProperty(ReportPropertiesUtils.P_ORG_NUMBER_IN_NAME), idOfOrg,
                                        currRule.getTag(), null, null, null, null, ReportInfo.REPORT_GENERATED);
                            }

                        }
                        if (null != reportDocument) {
                            // загружаем списки рассылок по id
                            Map<String, String> mailListMap = null;
                            if (idOfOrg != null) {
                                mailListMap = loadMailLists(idOfOrg);
                            }
                            for (String currAddress : currRule.getRouteAdresses()) {
                                if (StringUtils.isNotEmpty(currAddress)) {
                                    String address = fillTemplate(currAddress, reportProperties).trim();
                                    if (StringUtils.isNotEmpty(address)) {
                                        // если указан не конкретный адрес, а наименование списка рассылки
                                        if (address.startsWith("{") && address.endsWith("}") && mailListMap != null) {
                                            // излекаем списик адресов рассылки
                                            // address - содержит тип рассылки: {Список рассылки отчетов по питанию}, {Список рассылки отчетов по посещению}, {Список рассылки №1}, {Список рассылки №2}
                                            String addressList = mailListMap.get(address);
                                            if (StringUtils.isNotEmpty(addressList)) {
                                                // обходим все адреса в рассылке
                                                String addresses[] = addressList.split(";");
                                                List<String> errorMailingList = new ArrayList<String>();
                                                for (String addrFromList : addresses) {
                                                    try {
                                                        autoReportPostman
                                                                .postReport(addrFromList, subject, reportDocument);
                                                    } catch (Exception e) {
                                                        logger.error("Failed to post report", e);
                                                        String fullStackTrace = ExceptionUtils.getFullStackTrace(e);
                                                        errorMailingList.add(String.format("address: %s, error: %s", addrFromList, fullStackTrace));
                                                    }
                                                }
                                                registerMailingResults(reportInfo, errorMailingList);
                                                if (!errorMailingList.isEmpty()) continue;
                                            } else {
                                                logger.error(String.format(
                                                        "Failed to post report. Не определен список рассылки %s для организации с идентификатором %d",
                                                        address, idOfOrg));
                                            }
                                        } else {
                                            List<String> errorMailingList = new ArrayList<String>();
                                            try {
                                                autoReportPostman.postReport(address, subject, reportDocument);
                                            } catch (Exception e) {
                                                logger.error("Failed to post report", e);
                                                String fullStackTrace = ExceptionUtils.getFullStackTrace(e);
                                                errorMailingList.add(String.format("address: %s, error: %s", address, fullStackTrace));
                                            }
                                            registerMailingResults(reportInfo, errorMailingList);
                                            if (!errorMailingList.isEmpty()) continue;
                                        }
                                    }
                                }
                            }
                        }
                        if (basicReport instanceof BasicReportJob) {
                            ((BasicReportJob) basicReport).setPrint(null);
                        }
                    }
                    if (originalReportStartTime != null) {
                        // если изменяли даты
                        ((BasicReportJob) basicReport).setStartTime(originalReportStartTime);
                        ((BasicReportJob) basicReport).setEndTime(originalReportEndTime);
                        originalReportStartTime = originalReportEndTime = null;
                    }
                }
            }
        }
    }

    private ReportInfo registerQuartzJobTriggeredStage(String subject, Long idOfOrg, Rule currRule) {
        Date generateDate = new Date();
        Long zeroGenerationTime = 0L;
        ReportInfo reportInfo = new ReportInfo(currRule.getRuleName(), currRule.getDocumentFormat(), subject,
                generateDate, zeroGenerationTime, generateDate, generateDate, StringUtils.EMPTY, idOfOrg, ReportInfo.QUARTZ_JOB_TRIGGERED);
        return DAOService.getInstance().saveReportInfo(reportInfo);
    }

    private ReportInfo registerReportGeneratedStage(ReportInfo reportInfo, String ruleName, int documentFormat, String reportName, Date createdDate,
            Long generationTime, Date startDate, Date endDate, String reportFile, String orgNum, Long idOfOrg,
            String tag, Long idOfContragentReceiver, String contragentReceiver, Long idOfContragent,
            String contragent, Integer createState) {

        ReportInfo.Updater updater = new ReportInfo.Updater();
        ReportInfo updatedReportInfo = updater.update(reportInfo, ruleName, documentFormat, reportName, createdDate,generationTime, startDate,
                endDate, reportFile, orgNum, idOfOrg, tag, idOfContragentReceiver, contragentReceiver, idOfContragent,
                contragent, createState);

        return DAOService.getInstance().updateReportInfo(updatedReportInfo);
    }

    private void registerErrorDuringReportGeneration(ReportInfo reportInfo, String error) {
        reportInfo.setErrorString(error);
        reportInfo.setCreateState(ReportInfo.ERROR_DURING_REPORT_GENERATION);
        DAOService.getInstance().updateReportInfo(reportInfo);
    }

    private void registerMailingResults(ReportInfo reportInfo, List<String> errorList) {
        if (errorList.isEmpty()) {
            reportInfo.setCreateState(ReportInfo.MAIL_SENT);
        }else {
            reportInfo.setCreateState(ReportInfo.ERROR_DURING_MAILING);
            reportInfo.setErrorString(StringUtils.join(errorList, '\n'));
        }
        DAOService.getInstance().updateReportInfo(reportInfo);
    }

    private Properties copyProperties(Properties properties) {
        Properties p = new Properties();
        p.putAll(properties);
        return p;
    }

    private void applyRulePeriodType(Rule currRule, BasicReportJob reportJob) throws Exception {
        String sType = currRule.getExpressionValue(ReportPropertiesUtils.P_REPORT_PERIOD_TYPE);
        int type = BasicReportJob.REPORT_PERIOD_PREV_PREV_DAY;
        if (sType.equalsIgnoreCase(intToString(BasicReportJob.REPORT_PERIOD_PREV_MONTH))) {
            type = BasicReportJob.REPORT_PERIOD_PREV_MONTH;
        } else if (sType.equalsIgnoreCase(intToString(BasicReportJob.REPORT_PERIOD_PREV_DAY))) {
            type = BasicReportJob.REPORT_PERIOD_PREV_DAY;
        } else if (sType.equalsIgnoreCase(intToString(BasicReportJob.REPORT_PERIOD_TODAY))) {
            type = BasicReportJob.REPORT_PERIOD_TODAY;
        } else if (sType.equalsIgnoreCase(intToString(BasicReportJob.REPORT_PERIOD_PREV_PREV_DAY))) {
            type = BasicReportJob.REPORT_PERIOD_PREV_PREV_DAY;
        } else if (sType.equalsIgnoreCase(intToString(BasicReportJob.REPORT_PERIOD_PREV_PREV_PREV_DAY))) {
            type = BasicReportJob.REPORT_PERIOD_PREV_PREV_PREV_DAY;
        } else if (sType.equalsIgnoreCase(intToString(BasicReportJob.REPORT_PERIOD_LAST_WEEK))) {
            type = BasicReportJob.REPORT_PERIOD_LAST_WEEK;
        } else if (sType.equalsIgnoreCase(intToString(BasicReportJob.REPORT_PERIOD_CURRENT_MONTH))) {
            type = BasicReportJob.REPORT_PERIOD_CURRENT_MONTH;
        } else if (sType.equalsIgnoreCase(intToString(BasicReportJob.REPORT_PERIOD_PREV_WEEK))) {
            type = BasicReportJob.REPORT_PERIOD_PREV_WEEK;
        }
        Date[] dates = BasicReportJob.calculateDatesForPeriodType(Calendar.getInstance(), null, new Date(), type);
        reportJob.setStartTime(dates[0]);
        reportJob.setEndTime(dates[1]);
    }

    private String intToString(int number) {
        return ((Integer) number).toString();
    }

    private Date applyRulePeriod(Rule currRule, BasicReportJob reportJob) throws Exception {
        String sPeriod = currRule.getExpressionValue(ReportPropertiesUtils.P_REPORT_PERIOD);
        int dayOfReport = CalendarUtils.getDayOfMonth(reportJob.getStartTime());
        int period;

        Date originalReportStartTime = reportJob.getStartTime();
        if (sPeriod.indexOf('-') != -1) {
            Matcher m = periodMatcher.matcher(sPeriod);
            while (m.find()) {
                int dayFrom = Integer.parseInt(m.group(1));
                int dayTo;
                Date dateTo;
                if (m.group(2).equals("L")) {
                    dateTo = CalendarUtils.getFirstDayOfNextMonth(originalReportStartTime);
                    dayTo = 31;
                } else {
                    dayTo = Integer.parseInt(m.group(2));
                    dateTo = CalendarUtils.addDays(CalendarUtils.setDayOfMonth(originalReportStartTime, dayTo), 1);
                }

                if (dayOfReport >= dayFrom && dayOfReport <= dayTo) {
                    reportJob.setStartTime(CalendarUtils.setDayOfMonth(originalReportStartTime, dayFrom));
                    reportJob.setEndTime(dateTo);
                }
            }
        } else {
            try {
                period = Integer.parseInt(sPeriod);
            } catch (Exception e) {
                throw new Exception("Ошибка парсинга периода отчета (требуется число дней): " + sPeriod);
            }
            reportJob.applyDataQueryPeriod(period);
        }
        return originalReportStartTime;
    }

    // Метод для загрузки адресов рассылок
    private Map<String, String> loadMailLists(Long orgId) {
        Map<String, String> mailListMap = new HashMap<String, String>();
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            transaction = session.beginTransaction();
            Criteria criteria = ReportHandleRule.createOrgByIdCriteria(session, orgId);
            Org org = (Org) criteria.uniqueResult();
            mailListMap.put(ReportHandleRule.MAIL_LIST_NAMES[0], org.getMailingListReportsOnNutrition());
            mailListMap.put(ReportHandleRule.MAIL_LIST_NAMES[1], org.getMailingListReportsOnVisits());
            mailListMap.put(ReportHandleRule.MAIL_LIST_NAMES[2], org.getMailingListReports1());
            mailListMap.put(ReportHandleRule.MAIL_LIST_NAMES[3], org.getMailingListReports2());
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return mailListMap;
    }

    private boolean getFlag(Long idOfRule, List<Long> reportHandleRuleIdsList) {
        boolean flag = true;
        if (!reportHandleRuleIdsList.isEmpty()) {
            flag = false;
            if (reportHandleRuleIdsList.contains(idOfRule)) {
                flag = true;
            }
        }
        return flag;
    }

    public void loadAutoReportRules() throws Exception {
        List<Rule> newRules = new LinkedList<Rule>();
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            transaction = session.beginTransaction();
            Criteria reportRulesCriteria = ReportHandleRule.createEnabledReportRulesCriteria(session);
            List rules = reportRulesCriteria.list();
            for (Object currObject : rules) {
                ReportHandleRule currRule = (ReportHandleRule) currObject;
                if (currRule.isEnabled()) {
                    newRules.add(new Rule(currRule));
                }
            }
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        synchronized (this.reportRulesLock) {
            this.reportRules = newRules;
        }
    }

    /**
     * Warning: has to be threadsafe
     *
     * @param event
     * @param properties
     * @param eventDocumentBuilders
     * @throws Exception
     */
    public void processEvent(BasicEvent event, Properties properties,
            Map<Integer, EventDocumentBuilder> eventDocumentBuilders) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Handling event \"%s\"", event.getClass().getCanonicalName()));
        }
        List<Rule> rulesCopy;
        synchronized (this.eventNotificationsLock) {
            rulesCopy = this.eventNotifications;
        }
        Map<Integer, ReportDocument> readyEventDocuments = new HashMap<Integer, ReportDocument>();
        for (Rule currRule : rulesCopy) {
            this.currRule = currRule;
            if (currRule.applicatable(properties)) {
                if (logger.isDebugEnabled()) {
                    logger.debug(
                            String.format("Event \"%s\" is applicatable for discountrule \"%s\"", event, currRule));
                }
                ReportDocument eventDocument = readyEventDocuments.get(currRule.getDocumentFormat());
                if (null == eventDocument) {
                    EventDocumentBuilder documentBuilder = eventDocumentBuilders.get(currRule.getDocumentFormat());
                    if (null == documentBuilder) {
                        if (logger.isWarnEnabled()) {
                            logger.warn(String.format(
                                    "Can't build document with format = %s for event %s - apropriate document builder not found",
                                    currRule.getDocumentFormat(), event));
                        }
                    } else {
                        eventDocument = documentBuilder.buildDocument(event);
                        readyEventDocuments.put(currRule.getDocumentFormat(), eventDocument);
                    }
                }
                if (null != eventDocument) {
                    String subject = fillTemplate(currRule.getSubject(), properties);
                    for (String currAddress : currRule.getRouteAdresses()) {
                        if (StringUtils.isNotEmpty(currAddress)) {
                            String address = fillTemplate(currAddress, properties);
                            if (StringUtils.isNotEmpty(address)) {
                                try {
                                    eventNotificationPostman.postEvent(address, subject, eventDocument);
                                } catch (Exception e) {
                                    logger.error("Failed to post event", e);
                                }
                            }
                        }
                    }
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("currRule.applicatable(properties) is false ");
                }
            }
        }
    }

    /**
     * Warning: has to be threadsafe
     *
     * @throws Exception
     */
    public void loadEventNotificationRules() throws Exception {
        List<Rule> newRules = new LinkedList<Rule>();
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            transaction = session.beginTransaction();
            Criteria reportRulesCriteria = ReportHandleRule.createEnabledEventNotificationsCriteria(session);
            List rules = reportRulesCriteria.list();
            for (Object currObject : rules) {
                ReportHandleRule currRule = (ReportHandleRule) currObject;
                if (currRule.isEnabled()) {
                    newRules.add(new Rule(currRule));
                }
            }
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        synchronized (this.eventNotificationsLock) {
            this.eventNotifications = newRules;
        }
    }

    public Rule getCurrRule() {
        return currRule;
    }

    public Properties getReportProperties() {
        return reportProperties;
    }

    private static interface BasicBoolExpression {

        public String getComparatorArgument();

        public String getComparatorValue();

        boolean applicatable(Properties properties);

        boolean evaluate(Properties properties);

    }

    private static class MethodExpression implements BasicBoolExpression {

        private final String comparatorArgument;
        private final String methodName;

        public MethodExpression(String comparatorArgument, String methodName) {
            this.comparatorArgument = comparatorArgument;
            this.methodName = methodName;
        }

        @Override
        public String getComparatorArgument() {
            return comparatorArgument;
        }

        @Override
        public String getComparatorValue() {
            return methodName;
        }

        public boolean applicatable(Properties properties) {
            return true;
        }

        public boolean evaluate(Properties properties) {
            return true;                                //  НЕОБХОДИМО ВЫЗЫВАТЬ МЕТОД И ПРОВЕРЯТЬ ОТВЕТ ОТ НЕГО!
        }
    }

    private static class TautologyExpression implements BasicBoolExpression {

        @Override
        public String getComparatorArgument() {
            return null;
        }

        @Override
        public String getComparatorValue() {
            return null;
        }

        public boolean applicatable(Properties properties) {
            return true;
        }

        public boolean evaluate(Properties properties) {
            return true;
        }
    }

    private static class EqualExpression implements BasicBoolExpression {

        private String comparatorArgument;
        private String comparatorValue;

        public EqualExpression(String comparatorArgument, String comparatorValue) {
            this.comparatorArgument = comparatorArgument;
            this.comparatorValue = comparatorValue;
        }

        public boolean applicatable(Properties properties) {
            if (RuleExpressionUtil.isPostArgument(this.comparatorArgument)) {
                properties.put(this.comparatorArgument, "");
                return true;
            }
            return StringUtils.isNotEmpty(properties.getProperty(this.comparatorArgument));
        }

        @Override
        public String getComparatorArgument() {
            return comparatorArgument;
        }

        public String getComparatorValue() {
            return comparatorValue;
        }

        public boolean evaluate(Properties properties) {
            //  Анализ строки-сигнатуры, получение дополнительных параметров для сравнения (напр., необходимость запуска процедуры, выбора из комбобокса и т.д.)
            if (comparatorValue.indexOf(METHOD_EXPRESSION) == 0) {
                String result = "";
                try {
                    result = getMethodExecutionResult(comparatorValue);
                } catch (Exception e) {
                    return false;
                }
                comparatorValue = parseMethodExecutionResultForEquals(result);
            } else if (comparatorValue.indexOf(CHECKBOX_EXPRESSION) == 0) {
                //  Если есть слово чекбокс, значит был произведен выбор из нескольких чекбоксов
                comparatorValue = comparatorValue.substring(0, CHECKBOX_EXPRESSION.length()).trim();
                //
            } else if (comparatorValue.indexOf(COMBOBOX_EXPRESSION) == 0) {
                //  Если есть слово комбобокс, значит был произведен выбор из меню
                comparatorValue = comparatorValue.substring(0, COMBOBOX_EXPRESSION.length()).trim();
            } else if (comparatorValue.indexOf(RADIO_EXPRESSION) == 0) {
                //  Если есть слово комбобокс, значит был произведен выбор из меню
                comparatorValue = comparatorValue.substring(0, RADIO_EXPRESSION.length()).trim();
            }

            return evaluateValue(properties);
        }

        public boolean evaluateValue(Properties properties) {
            boolean result = false;
            String values[];
            if (this.comparatorValue.startsWith("/"))
                values = new String[]{this.comparatorValue};
            else
                values = this.comparatorValue.split(DELIMETER);
            String property[] = properties.getProperty(this.comparatorArgument).split(DELIMETER);
            for (String value : values) {
                for (String prop : property) {
                    if (RuleExpressionUtil.isPostArgument(this.comparatorArgument)) {
                        properties.put(this.comparatorArgument, this.comparatorValue);
                        //properties.put(prop,
                        //        String.format("%s%s%s", properties.get(prop), value, DELIMETER));
                        result = true;//return true;//continue;
                    }
                    if (value.startsWith("/") && value.endsWith("/")) {
                        return prop.matches(value.substring(1, value.length() - 1));
                    }
                    if (StringUtils.equals(prop.trim(), value.trim())) {
                        return true;
                    }
                }
            }
            return result;
        }

        @Override
        public String toString() {
            return "EqualExpression{" + "comparatorArgument='" + comparatorArgument + '\'' + ", comparatorValue='"
                    + comparatorValue + '\'' + '}';
        }
    }

    public static class Rule {

        private final int documentFormat;
        private final String subject;
        private final List<String> routeAdresses;
        private final List<BasicBoolExpression> boolExpressions;
        private final String templateFileName;
        private final String ruleName;
        private String tag;
        private long ruleId;
        //private final Set<RuleCondition> ruleConditions;

        public Rule(ReportHandleRule reportHandleRule) throws Exception {
            this.ruleId = reportHandleRule.getIdOfReportHandleRule();
            this.ruleName = reportHandleRule.getRuleName();
            this.tag = reportHandleRule.getTag();
            this.documentFormat = reportHandleRule.getDocumentFormat();
            this.subject = reportHandleRule.getSubject();
            this.routeAdresses = new LinkedList<>();
            this.routeAdresses.addAll(
                    reportHandleRule.getRoutes()
                            .stream()
                            .map(ReportHandleRuleRoute::getRoute)
                            .collect(Collectors.toList())
                    );
            this.boolExpressions = new LinkedList<>();
            for (RuleCondition currRuleCondition : reportHandleRule.getRuleConditions()) {
                this.boolExpressions.add(createExpression(currRuleCondition));
            }
            this.templateFileName = reportHandleRule.getTemplateFileName();
            //this.ruleConditions = reportHandleRule.getRuleConditions();
        }

        private static BasicBoolExpression createExpression(RuleCondition ruleCondition) throws Exception {
            switch (ruleCondition.getConditionOperation()) {
                case RuleCondition.TAUTOLOGY_OPERTAION:
                    return new TautologyExpression();
                case RuleCondition.LESS_OPERATION:
                case RuleCondition.MORE_OPERATION:
                case RuleCondition.NOT_EQUAL_OPERATION:
                case RuleCondition.EQUAL_OPERTAION:
                    return new EqualExpression(ruleCondition.getConditionArgument(),
                            ruleCondition.getConditionConstant());
                default:
                    throw new IllegalArgumentException(String.format("Unknown operation: %s", ruleCondition));
            }
        }

        public int getDocumentFormat() {
            return documentFormat;
        }

        public String getSubject() {
            return subject;
        }

        public List<String> getRouteAdresses() {
            return routeAdresses;
        }

        public String getRuleName() {
            return ruleName;
        }

        public long getRuleId() {
            return ruleId;
        }

        public String getExpressionValue(String name) {
            for (BasicBoolExpression currExpression : this.boolExpressions) {
                if (currExpression.getComparatorArgument() != null && currExpression.getComparatorArgument()
                        .equals(name)) {
                    return currExpression.getComparatorValue();
                }
            }
            return null;
        }

        public boolean applicatable(Properties properties) {
            for (BasicBoolExpression currExpression : this.boolExpressions) {
                if (!currExpression.applicatable(properties)) {
                    return false;
                }
                if (!currExpression.evaluate(properties)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            return "Rule{" + "documentFormat=" + documentFormat + ", routeAdresses=" + routeAdresses
                    + ", boolExpressions=" + boolExpressions + '}';
        }

        public String getTemplateFileName() {
            return templateFileName;
        }

        public String getTag() {
            return tag;
        }
    }
}
