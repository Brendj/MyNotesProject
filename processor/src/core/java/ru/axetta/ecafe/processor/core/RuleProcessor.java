/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core;

import jxl.common.log.LoggerName;

import ru.axetta.ecafe.processor.core.event.BasicEvent;
import ru.axetta.ecafe.processor.core.event.EventDocumentBuilder;
import ru.axetta.ecafe.processor.core.event.EventProcessor;
import ru.axetta.ecafe.processor.core.mail.Postman;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.core.report.*;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.RuleExpressionUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.ws.security.util.StringUtil;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 15.12.2009
 * Time: 14:00:06
 * To change this template use File | Settings | File Templates.
 */
public class RuleProcessor implements AutoReportProcessor, EventProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RuleProcessor.class);

    public static final String DELIMETER = ",";

    private static Rule currRule;
    private static Properties reportProperties;

    private static interface BasicBoolExpression {

        boolean applicatable(Properties properties);

        boolean evaluate(Properties properties);

    }

    private static class TautologyExpression implements BasicBoolExpression {

        public boolean applicatable(Properties properties) {
            return true;
        }

        public boolean evaluate(Properties properties) {
            return true;
        }

    }

    private static class EqualExpression implements BasicBoolExpression {

        private final String comparatorArgument;
        private final String comparatorValue;

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

        public boolean evaluate(Properties properties) {
            boolean result = false;
            String values[] = this.comparatorValue.split(DELIMETER);
            String property[] = properties.getProperty(this.comparatorArgument).split(DELIMETER);
            for (String value : values) {
                for (String prop : property) {
                    if (RuleExpressionUtil.isPostArgument(this.comparatorArgument)) {
                        properties.put(this.comparatorArgument, String.format("%s%s%s", properties.get(this.comparatorArgument), value, DELIMETER));
                        result = true;//return true;//continue;
                    }
                    if (StringUtils.equals(prop.trim(), value.trim()))
                        return true;
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
        //private final Set<RuleCondition> ruleConditions;

        private Rule(ReportHandleRule reportHandleRule) throws Exception {
            this.documentFormat = reportHandleRule.getDocumentFormat();
            this.subject = reportHandleRule.getSubject();
            this.routeAdresses = new LinkedList<String>();
            this.routeAdresses.add(reportHandleRule.getRoute0());
            this.routeAdresses.add(reportHandleRule.getRoute1());
            this.routeAdresses.add(reportHandleRule.getRoute2());
            this.routeAdresses.add(reportHandleRule.getRoute3());
            this.routeAdresses.add(reportHandleRule.getRoute4());
            this.routeAdresses.add(reportHandleRule.getRoute5());
            this.routeAdresses.add(reportHandleRule.getRoute6());
            this.routeAdresses.add(reportHandleRule.getRoute7());
            this.routeAdresses.add(reportHandleRule.getRoute8());
            this.routeAdresses.add(reportHandleRule.getRoute9());
            this.boolExpressions = new LinkedList<BasicBoolExpression>();
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
    }

    private final SessionFactory sessionFactory;
    private final AutoReportPostman autoReportPostman;
    private final Postman eventNotificationPostman;
    private List<Rule> reportRules;
    private List<Rule> eventNotifications;
    private final Object reportRulesLock;
    private final Object eventNotificationsLock;

    public RuleProcessor(SessionFactory sessionFactory, AutoReportPostman autoReportPostman,
            Postman eventNotificationPostman) {
        this.sessionFactory = sessionFactory;
        this.autoReportPostman = autoReportPostman;
        this.eventNotificationPostman = eventNotificationPostman;
        this.reportRules = Collections.emptyList();
        this.reportRulesLock = new Object();
        this.eventNotificationsLock = new Object();
    }

    /**
     * Warning: has to be threadsafe
     *
     * @param reports
     * @param reportDocumentBuilders
     * @throws Exception
     */
    public void processAutoReports(List<AutoReport> reports, Map<Integer, ReportDocumentBuilder> reportDocumentBuilders)
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

            for (AutoReport report : reports) {
                reportProperties = report.getProperties();
                BasicReport basicReport = report.getBasicReport();
                Map<Integer, ReportDocument> readyReportDocuments = new HashMap<Integer, ReportDocument>();
                for (Rule currRule : rulesCopy) {
                    if (currRule.applicatable(reportProperties)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug(
                                    String.format("Report \"%s\" is applicatable for discountrule \"%s\"", report, currRule));
                        }
                        if (basicReport instanceof BasicReportJob && currRule.getTemplateFileName()!=null && !currRule.getTemplateFileName().isEmpty()) {
                                ((BasicReportJob)basicReport).templateFilename = currRule.getTemplateFileName();
                        }
                        ReportDocument reportDocument = readyReportDocuments.get(currRule.getDocumentFormat());
                        if (null == reportDocument) {
                            ReportDocumentBuilder documentBuilder = reportDocumentBuilders
                                    .get(currRule.getDocumentFormat());
                            if (null == documentBuilder) {
                                if (logger.isWarnEnabled()) {
                                    logger.warn(String.format(
                                            "Can't build document with format = %s for report %s - apropriate document builder not found",
                                            currRule.getDocumentFormat(), report));
                                }
                            } else {
                                reportDocument = documentBuilder.buildDocument(basicReport);
                                readyReportDocuments.put(currRule.getDocumentFormat(), reportDocument);
                            }
                        }
                        if (null != reportDocument) {
                            String subject = fillTemplate(currRule.getSubject(), reportProperties);
                            // извлекаем id органиазции
                            Long idOfOrg = Long.parseLong(report.getProperties().getProperty("idOfOrg"));
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
                                        if (address.startsWith("{") && address.endsWith("}") && mailListMap!=null) {
                                            // излекаем списик адресов рассылки
                                            // address - содержит тип рассылки: {Список рассылки отчетов по питанию}, {Список рассылки отчетов по посещению}, {Список рассылки №1}, {Список рассылки №2}
                                            String addressList = mailListMap.get(address);
                                            if (StringUtils.isNotEmpty(addressList)) {
                                                // обходим все адреса в рассылке
                                                String addresses[] = addressList.split(";");
                                                for (String addrFromList : addresses) {
                                                    try {
                                                        autoReportPostman.postReport(addrFromList, subject, reportDocument);
                                                    } catch (Exception e) {
                                                        logger.error("Failed to post report", e);
                                                    }
                                                }
                                            } else {
                                                logger.error(String.format("Failed to post report. Не определен список рассылки %s для организации с идентификатором %d", address, idOfOrg));
                                            }
                                        } else {
                                            try {
                                                autoReportPostman.postReport(address, subject, reportDocument);
                                            } catch (Exception e) {
                                                logger.error("Failed to post report", e);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Метод для загрузки адресов рассылок
    private Map<String, String> loadMailLists(Long orgId) {
        Map<String, String> mailListMap = new HashMap<String, String>();
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            transaction = session.beginTransaction();
            Criteria criteria = ReportHandleRule.createOrgByIdCriteria(session, orgId);
            Org org = (Org)criteria.uniqueResult();
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

    static String fillTemplate(String pattern, Properties properties) {
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
            RuleProcessor.currRule = currRule;
            if (currRule.applicatable(properties)) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Event \"%s\" is applicatable for discountrule \"%s\"", event, currRule));
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

    public static Rule getCurrRule() {
        return currRule;
    }

    public static Properties getReportProperties() {
        return reportProperties;
    }
}
