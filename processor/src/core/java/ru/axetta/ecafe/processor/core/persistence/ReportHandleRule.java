/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.sync.SyncRequest;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import javax.persistence.metamodel.StaticMetamodel;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 27.06.2009
 * Time: 14:02:41
 * To change this template use File | Settings | File Templates.
 */
public class ReportHandleRule {
    public enum StoragePeriods {
        NO(0L, "нет"),
        WEEK(604800000L, "1 неделя"),
        MONTH(2678400000L, "1 месяц"),
        HALF_YEAR(15724800000L, "6 месяцев"),
        ONE_YEAR(31536000000L, "1 год"),
        TWO_YEARS(63072000000L, "2 года"),
        THREE_YEARS(94608000000L, "3 года"),
        FIVE_YEARS(157680000000L, "5 лет"),
        NO_REMOVE(-1L, "не удалять");

        private final long ts;
        private final String name;

        private StoragePeriods (long ts, String name) {
            this.ts = ts;
            this.name = name;
        }

        public long getMilliseconds() {
            return ts;
        }
        
        public String getName() {
            return name;
        }

        public static StoragePeriods[] getPeriods() {
            return StoragePeriods.values();
        }
    }

    public static final String UNKNOWN_FORMAT_NAME = "Неизвестный";
    public static final String[] FORMAT_NAMES = {"HTML", "XLS", "CSV", "PDF"};
    public static final String[] MAIL_LIST_NAMES = {
            "{Список рассылки отчетов по питанию}", "{Список рассылки отчетов по посещению}", "{Список рассылки №1}",
            "{Список рассылки №2}"};
    public static String DELIMETER = ";";
    public static final int HTML_FORMAT = 0;
    public static final int XLS_FORMAT  = 1;
    public static final int CSV_FORMAT  = 2;
    public static final int PDF_FORMAT  = 3;
    public static final int REPOSITORY_FORMAT = 4;  //  Должен быть последним среди всех форматов! если формат добавится, то следует обновить и это значение
    private Long IdOfReportHandleRule;
    private String ruleName;
    private String templateFileName; // имя файла шаблона отчета
    private Integer documentFormat;
    private String subject;
    private String route0;
    private String route1;
    private String route2;
    private String route3;
    private String route4;
    private String route5;
    private String route6;
    private String route7;
    private String route8;
    private String route9;
    private String remarks;
    private boolean enabled;
    private Set<RuleCondition> ruleConditions = new HashSet<RuleCondition>();
    private String tag;
    private boolean allowManualReportRun = false;
    private long storagePeriod;

    protected ReportHandleRule() {
        // For Hibernate only
    }

    public ReportHandleRule(Integer documentFormat, String subject, String route0, boolean enabled) {
        this.documentFormat = documentFormat;
        this.subject = subject;
        this.route0 = route0;
        this.enabled = enabled;
    }

    public boolean isAllowManualReportRun() {
        return allowManualReportRun;
    }

    public void setAllowManualReportRun(boolean allowManualReportRun) {
        this.allowManualReportRun = allowManualReportRun;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getIdOfReportHandleRule() {
        return IdOfReportHandleRule;
    }

    private void setIdOfReportHandleRule(Long idOfReportHandleRule) {
        // For Hibernate only
        IdOfReportHandleRule = idOfReportHandleRule;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getDocumentFormat() {
        return documentFormat;
    }

    public void setDocumentFormat(Integer documentFormat) {
        this.documentFormat = documentFormat;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRoute0() {
        return route0;
    }

    public void setRoute0(String route0) {
        this.route0 = route0;
    }

    public String getRoute1() {
        return route1;
    }

    public void setRoute1(String route1) {
        this.route1 = route1;
    }

    public String getRoute2() {
        return route2;
    }

    public void setRoute2(String route2) {
        this.route2 = route2;
    }

    public String getRoute3() {
        return route3;
    }

    public void setRoute3(String route3) {
        this.route3 = route3;
    }

    public String getRoute4() {
        return route4;
    }

    public void setRoute4(String route4) {
        this.route4 = route4;
    }

    public String getRoute5() {
        return route5;
    }

    public void setRoute5(String route5) {
        this.route5 = route5;
    }

    public String getRoute6() {
        return route6;
    }

    public void setRoute6(String route6) {
        this.route6 = route6;
    }

    public String getRoute7() {
        return route7;
    }

    public void setRoute7(String route7) {
        this.route7 = route7;
    }

    public String getRoute8() {
        return route8;
    }

    public void setRoute8(String route8) {
        this.route8 = route8;
    }

    public String getRoute9() {
        return route9;
    }

    public void setRoute9(String route9) {
        this.route9 = route9;
    }

    private Set<RuleCondition> getRuleConditionsInternal() {
        // For Hibernate only
        return this.ruleConditions;
    }

    private void setRuleConditionsInternal(Set<RuleCondition> ruleConditions) {
        // For Hibernate only
        this.ruleConditions = ruleConditions;
    }

    public Set<RuleCondition> getRuleConditions() {
        return Collections.unmodifiableSet(getRuleConditionsInternal());
    }

    public void addRuleCondition(RuleCondition ruleCondition) {
        getRuleConditionsInternal().add(ruleCondition);
    }

    public void removeRuleCondition(RuleCondition ruleCondition) {
        getRuleConditionsInternal().remove(ruleCondition);
    }

    public String getTemplateFileName() {
        return templateFileName;
    }

    public void setTemplateFileName(String templateFileName) {
        this.templateFileName = templateFileName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getStoragePeriod() {
        return storagePeriod;
    }

    public void setStoragePeriod(long storagePeriod) {
        this.storagePeriod = storagePeriod;
    }

    public String findType(Session session) throws Exception {
        List rules = session.createFilter(getRuleConditionsInternal(),
                "where this.conditionOperation = ? and this.conditionArgument = ?")
                .setString(1, RuleCondition.TYPE_CONDITION_ARG).setInteger(0, RuleCondition.EQUAL_OPERTAION).list();
        if (!rules.isEmpty()) {
            RuleCondition ruleCondition = (RuleCondition) rules.iterator().next();
            return ruleCondition.getConditionConstant();
        }
        return null;
    }

    public static Criteria createAllReportRulesCriteria(Session session) throws Exception {
        return createAllReportRulesCriteria(null, session);
    }

    public static Criteria createAllReportRulesCriteria(Boolean manualAllowed, Session session) throws Exception {
        Criteria cr = session.createCriteria(ReportHandleRule.class).addOrder(org.hibernate.criterion.Order.asc("ruleName"));
        if (manualAllowed != null) {
            cr.add(Restrictions.eq("allowManualReportRun", manualAllowed));
        }
        return cr.createCriteria("ruleConditionsInternal")
                .add(Restrictions.eq("conditionOperation", RuleCondition.EQUAL_OPERTAION))
                .add(Restrictions.eq("conditionArgument", RuleCondition.TYPE_CONDITION_ARG)).add(Restrictions
                        .like("conditionConstant", RuleCondition.REPORT_TYPE_BASE_PART + ".%", MatchMode.START));
    }

    public static Criteria createEnabledReportRulesCriteria(Session session) throws Exception {
        return session.createCriteria(ReportHandleRule.class).add(Restrictions.eq("enabled", true))
                .createCriteria("ruleConditionsInternal")
                .add(Restrictions.eq("conditionOperation", RuleCondition.EQUAL_OPERTAION))
                .add(Restrictions.eq("conditionArgument", RuleCondition.TYPE_CONDITION_ARG))
                .add(Restrictions.eq("conditionArgument", RuleCondition.TYPE_CONDITION_ARG)).add(Restrictions
                        .like("conditionConstant", RuleCondition.REPORT_TYPE_BASE_PART + ".%", MatchMode.START));
    }

    public static Criteria createAllEventNotificationsCriteria(Session session) throws Exception {
        return session.createCriteria(ReportHandleRule.class).createCriteria("ruleConditionsInternal")
                .add(Restrictions.eq("conditionOperation", RuleCondition.EQUAL_OPERTAION))
                .add(Restrictions.eq("conditionArgument", RuleCondition.TYPE_CONDITION_ARG)).add(Restrictions
                        .like("conditionConstant", RuleCondition.EVENT_TYPE_BASE_PART + ".%", MatchMode.START));
    }

    public static Criteria createEnabledEventNotificationsCriteria(Session session) throws Exception {
        return session.createCriteria(ReportHandleRule.class).add(Restrictions.eq("enabled", true))
                .createCriteria("ruleConditionsInternal")
                .add(Restrictions.eq("conditionOperation", RuleCondition.EQUAL_OPERTAION))
                .add(Restrictions.eq("conditionArgument", RuleCondition.TYPE_CONDITION_ARG)).add(Restrictions
                        .like("conditionConstant", RuleCondition.EVENT_TYPE_BASE_PART + ".%", MatchMode.START));
    }

    public static Criteria createOrgByIdCriteria(Session session, long id) {
        return session.createCriteria(Org.class).add(Restrictions.eq("idOfOrg", id));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReportHandleRule)) {
            return false;
        }
        final ReportHandleRule that = (ReportHandleRule) o;
        return IdOfReportHandleRule.equals(that.getIdOfReportHandleRule());
    }

    @Override
    public int hashCode() {
        return IdOfReportHandleRule.hashCode();
    }

    @Override
    public String toString() {
        return "ReportHandleRule{" + "IdOfReportHandleRule=" + IdOfReportHandleRule + ", reportName='" + ruleName + '\''
                + ", documentFormat=" + documentFormat + ", route0='" + route0 + '\'' + ", route1='" + route1 + '\''
                + ", route2='" + route2 + '\'' + ", route3='" + route3 + '\'' + ", route4='" + route4 + '\''
                + ", route5='" + route5 + '\'' + ", route6='" + route6 + '\'' + ", route7='" + route7 + '\''
                + ", route8='" + route8 + '\'' + ", route9='" + route9 + '\'' + '}';
    }

    public static String getMailListNames() {
        StringBuilder sb = new StringBuilder();
        for (String name : MAIL_LIST_NAMES) {
            sb.append(name).append(DELIMETER).append(" ");
        }
        return sb.toString().substring(0, sb.length() - 2);
    }


    public static String getDocumentFormatAsString(Integer documentFormat) {
        if (documentFormat >= 0 && documentFormat < FORMAT_NAMES.length) {
            return FORMAT_NAMES[documentFormat];
        }
        return "Неизвестно";
    }

}