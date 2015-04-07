/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.event.BasicEvent;
import ru.axetta.ecafe.processor.core.report.BasicReport;

import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 27.06.2009
 * Time: 14:02:41
 * To change this template use File | Settings | File Templates.
 */
public class RuleCondition {

    public static final int SIZE_OF_CONFITIONCONSTANT_FIELD = 1099;

    public static final String TYPE_CONDITION_ARG = "class";
    public static final int TAUTOLOGY_OPERTAION = 0;

    public static final String EVENT_TYPE_BASE_PART;
    public static final String REPORT_TYPE_BASE_PART;

    public static final int EQUAL_OPERTAION = 1;
    public static final int LESS_OPERATION = 2;
    public static final int MORE_OPERATION = 3;
    public static final int NOT_EQUAL_OPERATION = 4;
    public static final int[] BINARY_OPERTAIONS = {EQUAL_OPERTAION};            //  Добавлять только бинарные операции для обработки их в ReportConditionItem.ReportConditionItem. Используется для анализа той операции, которую необходимо производить
    public static final String[] OPERATION_TEXT = {"всегда", "=", "<", ">", "!="};
    public static final String[] ENTEREVENT_TYPE_TEXT = {"все", "учащиеся", "все_без_учащихся"};
    public static final int ENTEREVENT_TYPE_ALL = 0, ENTEREVENT_TYPE_STUDS = 1, ENTEREVENT_TYPE_WITHOUTSTUDS = 2;

    private Long idOfRuleCondition;
    private ReportHandleRule reportHandleRule;
    private Integer conditionOperation;
    private String conditionArgument;
    private String conditionConstant;

    static {
        String basicReportType = BasicReport.class.getCanonicalName();
        int i = basicReportType.lastIndexOf('.');
        if (i > 0) {
            REPORT_TYPE_BASE_PART = basicReportType.substring(0, i);
        } else {
            REPORT_TYPE_BASE_PART = basicReportType;
        }

        String basicNotificationType = BasicEvent.class.getCanonicalName();
        i = basicNotificationType.lastIndexOf('.');
        if (i > 0) {
            EVENT_TYPE_BASE_PART = basicNotificationType.substring(0, i);
        } else {
            EVENT_TYPE_BASE_PART = basicNotificationType;
        }
    }

    protected RuleCondition() {
        // For Hibernate only
    }

    public RuleCondition(ReportHandleRule reportHandleRule, Integer conditionOperation) {
        this.reportHandleRule = reportHandleRule;
        this.conditionOperation = conditionOperation;
    }

    public RuleCondition(ReportHandleRule reportHandleRule, Integer conditionOperation, String conditionArgument) {
        this.reportHandleRule = reportHandleRule;
        this.conditionOperation = conditionOperation;
        this.conditionArgument = conditionArgument;
    }

    public RuleCondition(ReportHandleRule reportHandleRule, Integer conditionOperation, String conditionArgument,
            String conditionConstant) {
        this.reportHandleRule = reportHandleRule;
        this.conditionOperation = conditionOperation;
        this.conditionArgument = conditionArgument;
        this.conditionConstant = conditionConstant;
    }

    public Long getIdOfRuleCondition() {
        return idOfRuleCondition;
    }

    private void setIdOfRuleCondition(Long idOfRuleCondition) {
        // For Hibernate only
        this.idOfRuleCondition = idOfRuleCondition;
    }

    public ReportHandleRule getReportHandleRule() {
        return reportHandleRule;
    }

    private void setReportHandleRule(ReportHandleRule reportHandleRule) {
        // For Hibernate only
        this.reportHandleRule = reportHandleRule;
    }

    public Integer getConditionOperation() {
        return conditionOperation;
    }

    public void setConditionOperation(Integer conditionOperation) {
        this.conditionOperation = conditionOperation;
    }

    public String getConditionArgument() {
        return conditionArgument;
    }

    public void setConditionArgument(String conditionArgument) {
        this.conditionArgument = conditionArgument;
    }

    public String getConditionConstant() {
        return conditionConstant;
    }

    public void setConditionConstant(String conditionConstant) {
        this.conditionConstant = conditionConstant;
    }

    public boolean isTypeCondition() {
        if (RuleCondition.EQUAL_OPERTAION == getConditionOperation()) {
            if (StringUtils.equals(getConditionArgument(), TYPE_CONDITION_ARG)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RuleCondition)) {
            return false;
        }
        final RuleCondition that = (RuleCondition) o;
        return conditionOperation.equals(that.getIdOfRuleCondition());
    }

    @Override
    public int hashCode() {
        int result = conditionOperation.hashCode();
        result = 31 * result + (conditionArgument != null ? conditionArgument.hashCode() : 0);
        result = 31 * result + (conditionConstant != null ? conditionConstant.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RuleCondition{" + "idOfRuleCondition=" + idOfRuleCondition + ", reportHandleRule=" + reportHandleRule
                + ", conditionOperation=" + conditionOperation + ", conditionArgument='" + conditionArgument + '\''
                + ", conditionConstant='" + conditionConstant + '\'' + '}';
    }
}