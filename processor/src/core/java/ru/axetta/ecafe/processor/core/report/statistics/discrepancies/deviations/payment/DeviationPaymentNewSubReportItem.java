/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 03.03.15
 * Time: 16:08
 */

public class DeviationPaymentNewSubReportItem implements Comparable<DeviationPaymentNewSubReportItem> {

    public Long rowNum; //номер порядковый
    public String groupName;    // группа клиента (класс, сотрудники и т.д.)
    public String personName;
    private Condition condition;
    public String complexName;
    public Date orderDate;
    public String conditionText;

    public DeviationPaymentNewSubReportItem() {
    }

    public DeviationPaymentNewSubReportItem(Long rowNum, Condition condition, String groupName, String personName,
            Date orderDate, String complexName) {
        this.rowNum = rowNum;
        this.condition = condition;
        this.groupName = groupName;
        this.personName = personName;
        this.orderDate = orderDate;
        this.complexName = complexName;
        this.conditionText = condition.description;
    }

    public Long getRowNum() {
        return rowNum;
    }

    public void setRowNum(Long rowNum) {
        this.rowNum = rowNum;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
        this.conditionText = condition.description;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public String getOrderDateShortFormat() {
        return CalendarUtils.dateShortToStringFullYear(orderDate);
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getConditionText() {
        return conditionText;
    }

    @Override
    public int compareTo(DeviationPaymentNewSubReportItem o) {
        if (this.condition.ordinal() < o.getCondition().ordinal()) {
            return -1;
        } else if (this.condition.ordinal() > o.getCondition().ordinal()) {
            return 1;
        } else if (this.condition.ordinal() == o.getCondition().ordinal()) {
            return 0;
        }

        return this.condition.compareTo(o.getCondition());
    }

    public enum Condition {

        /*0*/ DETECTED_NOT_EAT("Проход по карте зафиксирован, питание не предоставлено"),
        /*1*/ NOT_DETECTED_EAT("Проход по карте не зафиксирован, питание предоставлено"),
        /*2*/ RESERVE("Обучающиеся из группы резерва, получившие питание. Обучающиеся, получившие питание по функционалу замены"),
        /*3*/ RECYCLE("Утилизированное питание");

        private final String description;
        static Map<Integer,Condition> map = new HashMap<Integer,Condition>();
        static {
            for (Condition questionaryStatus : Condition.values()) {
                map.put(questionaryStatus.ordinal(), questionaryStatus);
            }
        }
        private Condition(String description){
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }

        public static Condition fromInteger(Integer value){
            return map.get(value);
        }

    }
}
