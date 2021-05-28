/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.payment.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDTO {
    private Long idOfOrder;
    private Date createdDateTime;
    private Date orderDate;
    private int orderState;
    private int orderType;
    private String guidClientBalanceHold;
    private long totalSumClientBalanceHold;
    private String comments;
    private long sum;
    private long totalSum;
    private long socialDiscount;
    private long tradeDiscount;
    private long grant;
    private long sumByCard;
    private long sumByCash;
    private Long idOfCard;
    private Long idOfCardLongFormat;
    private Long idOfOrg;
    private Long idOfClient;
    private Long idOfPayForClient;
    private Long idOfCashier;
    private Long idOfStaffConfirm;
    private Long idOfPOS;

    private List<PurchaseDTO> purchases;

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    public void setIdOfOrder(Long idOfOrder) {
        this.idOfOrder = idOfOrder;
    }

    public Date getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Date createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public int getOrderState() {
        return orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public String getGuidClientBalanceHold() {
        return guidClientBalanceHold;
    }

    public void setGuidClientBalanceHold(String guidClientBalanceHold) {
        this.guidClientBalanceHold = guidClientBalanceHold;
    }

    public long getTotalSumClientBalanceHold() {
        return totalSumClientBalanceHold;
    }

    public void setTotalSumClientBalanceHold(long totalSumClientBalanceHold) {
        this.totalSumClientBalanceHold = totalSumClientBalanceHold;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

    public long getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(long totalSum) {
        this.totalSum = totalSum;
    }

    public long getSocialDiscount() {
        return socialDiscount;
    }

    public void setSocialDiscount(long socialDiscount) {
        this.socialDiscount = socialDiscount;
    }

    public long getTradeDiscount() {
        return tradeDiscount;
    }

    public void setTradeDiscount(long tradeDiscount) {
        this.tradeDiscount = tradeDiscount;
    }

    public long getGrant() {
        return grant;
    }

    public void setGrant(long grant) {
        this.grant = grant;
    }

    public long getSumByCard() {
        return sumByCard;
    }

    public void setSumByCard(long sumByCard) {
        this.sumByCard = sumByCard;
    }

    public long getSumByCash() {
        return sumByCash;
    }

    public void setSumByCash(long sumByCash) {
        this.sumByCash = sumByCash;
    }

    public Long getIdOfCard() {
        return idOfCard;
    }

    public void setIdOfCard(Long idOfCard) {
        this.idOfCard = idOfCard;
    }

    public Long getIdOfCardLongFormat() {
        return idOfCardLongFormat;
    }

    public void setIdOfCardLongFormat(Long idOfCardLongFormat) {
        this.idOfCardLongFormat = idOfCardLongFormat;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfPayForClient() {
        return idOfPayForClient;
    }

    public void setIdOfPayForClient(Long idOfPayForClient) {
        this.idOfPayForClient = idOfPayForClient;
    }

    public Long getIdOfCashier() {
        return idOfCashier;
    }

    public void setIdOfCashier(Long idOfCashier) {
        this.idOfCashier = idOfCashier;
    }

    public Long getIdOfStaffConfirm() {
        return idOfStaffConfirm;
    }

    public void setIdOfStaffConfirm(Long idOfStaffConfirm) {
        this.idOfStaffConfirm = idOfStaffConfirm;
    }

    public Long getIdOfPOS() {
        return idOfPOS;
    }

    public void setIdOfPOS(Long idOfPOS) {
        this.idOfPOS = idOfPOS;
    }

    public List<PurchaseDTO> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<PurchaseDTO> purchases) {
        this.purchases = purchases;
    }
}
