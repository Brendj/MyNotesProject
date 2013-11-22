/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.RuleCondition;

import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 31.12.2009
 * Time: 12:29:00
 * To change this template use File | Settings | File Templates.
 */
public class RuleConditionItem {

    private int conditionOperation;
    private String conditionArgument;
    private String conditionConstant;
    private String conditionOperationText;

    public RuleConditionItem(RuleCondition ruleCondition) {
        this.conditionOperation = ruleCondition.getConditionOperation();
        this.conditionArgument = ruleCondition.getConditionArgument();
        this.conditionConstant = ruleCondition.getConditionConstant();
        if (this.conditionOperation < RuleCondition.OPERATION_TEXT.length) {
            this.conditionOperationText = RuleCondition.OPERATION_TEXT[this.conditionOperation];
        } else {
            this.conditionOperationText = "";
        }
    }

    public RuleConditionItem(String text) throws Exception {
        /* Производится процедура сверки текста с тем, что указано в шаблонах. Выполняется с целью понять, какой класс для сравнения использовать */
        if (StringUtils.equals(text, RuleCondition.OPERATION_TEXT[RuleCondition.TAUTOLOGY_OPERTAION])) {
            this.conditionOperation = RuleCondition.TAUTOLOGY_OPERTAION;
            this.conditionOperationText = RuleCondition.OPERATION_TEXT[RuleCondition.TAUTOLOGY_OPERTAION];
        } else {
            for (int operation : RuleCondition.BINARY_OPERTAIONS) {
                int operationPos = StringUtils.indexOf(text, RuleCondition.OPERATION_TEXT[operation]);
                if (-1 < operationPos) {
                    this.conditionOperation = operation;
                    this.conditionOperationText = RuleCondition.OPERATION_TEXT[operation];
                    this.conditionArgument = StringUtils.trim(StringUtils.substring(text, 0, operationPos));
                    this.conditionConstant = StringUtils.trim(StringUtils.substring(text, operationPos + 1));
                    return;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    public String getConditionOperationText() {
        return conditionOperationText;
    }

    public void setConditionOperationText(String conditionOperationText) {
        this.conditionOperationText = conditionOperationText;
    }

    public int getConditionOperation() {
        return conditionOperation;
    }

    public void setConditionOperation(int conditionOperation) {
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

    public String buildText() {
        StringBuilder stringBuilder = new StringBuilder();
        appendNotEmpty(stringBuilder, conditionArgument);
        appendNotEmpty(stringBuilder, conditionOperationText);
        appendNotEmpty(stringBuilder, conditionConstant);
        return stringBuilder.toString();
    }

    private void appendNotEmpty(StringBuilder stringBuilder, String value) {
        if (StringUtils.isNotEmpty(value)) {
            if (0 != stringBuilder.length()) {
                stringBuilder.append(' ');
            }
            stringBuilder.append(value);
        }
    }
}
