/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.geoplaner;

import java.util.Date;

public class JsonPaymentInfo {
    private Long trackerUid;
    private Long trackerId;
    private Date orderTime;
    private Integer orderType;
    private Long RSum;
    private String purchasesName;
    private String cardType;

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

    public Long getTrackerUid() {
        return trackerUid;
    }

    public void setTrackerUid(Long trackerUid) {
        this.trackerUid = trackerUid;
    }

    public Long getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(Long trackerId) {
        this.trackerId = trackerId;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}
