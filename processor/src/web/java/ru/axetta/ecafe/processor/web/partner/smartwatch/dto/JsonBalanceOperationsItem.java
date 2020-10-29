/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch.dto;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.LinkedList;
import java.util.List;

public class JsonBalanceOperationsItem {
    private Long orderDate;
    private Integer transactionType; // from SmartWatchTransactionType
    private Integer orderType; // from SmartWatchOrderType
    private Long idOfTransaction;
    private Long rSum;
    private Long date;
    private Integer state;
    private String complexName;
    private List<JsonOrderDetail> orderDetails;

    @JsonIgnore
    private String goodsNames;

    @JsonIgnore
    private String qty;

    @JsonIgnore
    private String rPrices;

    public Long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Long orderDate) {
        this.orderDate = orderDate;
    }

    public Integer getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(Integer transactionType) {
        this.transactionType = transactionType;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Long getIdOfTransaction() {
        return idOfTransaction;
    }

    public void setIdOfTransaction(Long idOfTransaction) {
        this.idOfTransaction = idOfTransaction;
    }

    public Long getrSum() {
        return rSum;
    }

    public void setrSum(Long rSum) {
        this.rSum = rSum;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public List<JsonOrderDetail> getOrderDetails() {
        if(orderDetails == null){
            orderDetails = new LinkedList<JsonOrderDetail>();
        }
        return orderDetails;
    }

    public void setOrderDetails(List<JsonOrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public String getGoodsNames() {
        return goodsNames;
    }

    public void setGoodsNames(String goodsNames) {
        this.goodsNames = goodsNames;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getrPrices() {
        return rPrices;
    }

    public void setrPrices(String rPrices) {
        this.rPrices = rPrices;
    }

    public boolean orderDetailsInfoInfoIsNotNull() {
        return goodsNames != null &&  qty != null && rPrices != null;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
