/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.sfk.detailed;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 07.09.15
 * Time: 14:20
 */

public class LatePaymentDetailedSubReportModel implements Comparable<LatePaymentDetailedSubReportModel> {

    private Date foodDate;
    private String client;
    private String food;

    public LatePaymentDetailedSubReportModel(Date foodDate, String client, String food) {
        this.foodDate = foodDate;
        this.client = client;
        this.food = food;
    }

    public LatePaymentDetailedSubReportModel() {
    }

    public Date getFoodDate() {
        return foodDate;
    }

    public void setFoodDate(Date foodDate) {
        this.foodDate = foodDate;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    @Override
    public int compareTo(LatePaymentDetailedSubReportModel o) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
