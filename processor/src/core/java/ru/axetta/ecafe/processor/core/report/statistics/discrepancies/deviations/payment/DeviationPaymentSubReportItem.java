/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 16.10.14
 * Time: 17:14
 */

public class DeviationPaymentSubReportItem {

    public String groupName;    // группа клиента (класс, сотрудники и т.д.)
    public String personName;
    public String condition;

    public DeviationPaymentSubReportItem() {
    }

    public DeviationPaymentSubReportItem(String groupName, String personName, String condition) {
        this.groupName = groupName;
        this.personName = personName;
        this.condition = condition;
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
}
