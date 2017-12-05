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
    private Long idOfClient;
    private Integer menuType;

    public LatePaymentDetailedSubReportModel(Long idOfClient, String foodDate, String client, String groupName, Integer menuType) {
        this.foodDate = foodDate;
        this.client = client;
        this.groupName = groupName;
        this.menuType = menuType;
        this.idOfClient = idOfClient;
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
        // -1 - less than, 1 - greater than, 0 - equal
        return this.getClient().compareTo(o.getClient());
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Integer getMenuType() {
        return menuType;
    }

    public void setMenuType(Integer menuType) {
        this.menuType = menuType;
    }
}
