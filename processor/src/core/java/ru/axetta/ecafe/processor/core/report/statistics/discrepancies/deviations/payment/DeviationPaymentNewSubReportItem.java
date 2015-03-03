/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 03.03.15
 * Time: 16:08
 */

public class DeviationPaymentNewSubReportItem implements Comparable<DeviationPaymentNewSubReportItem>  {

    public Long rowNum; //номер порядковый
    public String groupName;    // группа клиента (класс, сотрудники и т.д.)
    public String personName;
    public String condition;
    public String complexName;
    public Date orderDate;

    public DeviationPaymentNewSubReportItem() {
    }

    public DeviationPaymentNewSubReportItem(Long rowNum, String condition, String groupName, String personName, Date orderDate,
            String complexName) {
        this.rowNum = rowNum;
        this.condition = condition;
        this.groupName = groupName;
        this.personName = personName;
        this.orderDate = orderDate;
        this.complexName = complexName;
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

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
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

    @Override
    public int compareTo(DeviationPaymentNewSubReportItem o) {
        int retCode = this.condition.compareTo(o.getCondition());
        if (retCode == 0) {
            retCode = this.groupName.compareTo(o.getGroupName());
        }
        if (retCode == 0) {
            retCode = this.orderDate.compareTo(o.getOrderDate());
        }
        return retCode;
    }
}
