/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch.dto;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class JsonPurchaseDetailItem {
    private Date orderDate;
    private String orderType;
    private Long rSum;
    private Long socDiscount;
    private Long trdDiscount;
    private Long grantSum;
    private Long sumByCard;
    private Long sumByCash;
    private List<JsonOrderDetail> orderDetails = new LinkedList<JsonOrderDetail>();

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Long getrSum() {
        return rSum;
    }

    public void setrSum(Long rSum) {
        this.rSum = rSum;
    }

    public Long getSocDiscount() {
        return socDiscount;
    }

    public void setSocDiscount(Long socDiscount) {
        this.socDiscount = socDiscount;
    }

    public Long getTrdDiscount() {
        return trdDiscount;
    }

    public void setTrdDiscount(Long trdDiscount) {
        this.trdDiscount = trdDiscount;
    }

    public Long getGrantSum() {
        return grantSum;
    }

    public void setGrantSum(Long grantSum) {
        this.grantSum = grantSum;
    }

    public Long getSumByCard() {
        return sumByCard;
    }

    public void setSumByCard(Long sumByCard) {
        this.sumByCard = sumByCard;
    }

    public Long getSumByCash() {
        return sumByCash;
    }

    public void setSumByCash(Long sumByCash) {
        this.sumByCash = sumByCash;
    }

    public List<JsonOrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<JsonOrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
