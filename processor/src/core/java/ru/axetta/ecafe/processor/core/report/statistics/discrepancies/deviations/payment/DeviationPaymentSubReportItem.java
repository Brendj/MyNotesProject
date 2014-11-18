/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 16.10.14
 * Time: 17:14
 */

public class DeviationPaymentSubReportItem implements Comparable<DeviationPaymentSubReportItem> {

    public String groupName;    // группа клиента (класс, сотрудники и т.д.)
    public String personName;
    public String condition;
    public String complexName;
    public Date orderDate;

    public DeviationPaymentSubReportItem() {
    }

    public DeviationPaymentSubReportItem(String condition, String groupName, String personName, Date orderDate,
            String complexName) {
        this.condition = condition;
        this.groupName = groupName;
        this.personName = personName;
        this.orderDate = orderDate;
        this.complexName = complexName;
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
    public int compareTo(DeviationPaymentSubReportItem o) {
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
