/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.geoplaner;

import java.util.Date;

public class JsonPurchasesInfo extends GeoplanerEventInfo {
    private Date orderTime;
    private Integer orderType;
    private Long RSum;
    private String purchasesName;
    private Long actualBalance;
    private String gender;

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Long getRSum() {
        return RSum;
    }

    public void setRSum(Long RSum) {
        this.RSum = RSum;
    }

    public String getPurchasesName() {
        return purchasesName;
    }

    public void setPurchasesName(String purchasesName) {
        this.purchasesName = purchasesName;
    }

    public Long getActualBalance() {
        return actualBalance;
    }

    public void setActualBalance(Long actualBalance) {
        this.actualBalance = actualBalance;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
