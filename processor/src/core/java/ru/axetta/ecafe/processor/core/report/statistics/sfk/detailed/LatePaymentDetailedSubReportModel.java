/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.sfk.detailed;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 07.09.15
 * Time: 14:20
 */

public class LatePaymentDetailedSubReportModel implements Comparable<LatePaymentDetailedSubReportModel> {

    private String foodDate;
    private String client;
    private String groupName;

    public LatePaymentDetailedSubReportModel(String foodDate, String client, String groupName) {
        this.foodDate = foodDate;
        this.client = client;
        this.groupName = groupName;
    }

    public LatePaymentDetailedSubReportModel() {
    }

    public String getFoodDate() {
        return foodDate;
    }

    public void setFoodDate(String foodDate) {
        this.foodDate = foodDate;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    @Override
    public int compareTo(LatePaymentDetailedSubReportModel o) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
