/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.geoplaner;

import java.util.Date;

public class JsonPurchasesInfo {
    private Long cardNo;
    private Long cardPrintedNo;
    private String cardType;
    private Long contractId;
    private Date orderTime;
    private Integer orderType;
    private Long RSum;
    private String purchasesName;

    public Long getCardNo() {
        return cardNo;
    }

    public void setCardNo(Long cardNo) {
        this.cardNo = cardNo;
    }

    public Long getCardPrintedNo() {
        return cardPrintedNo;
    }

    public void setCardPrintedNo(Long cardPrintedNo) {
        this.cardPrintedNo = cardPrintedNo;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractID(Long contractId) {
        this.contractId = contractId;
    }

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
}
