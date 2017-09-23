/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.meal;


import ru.axetta.ecafe.processor.core.persistence.Card;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 10.04.17
 * Time: 10:37
 */
public class TransactionDataItem {

    private Long idOfOrderDetail;
    private Long idOfOrg;
    private String organizationUid;
    private String studentUid;
    private String userToken;
    private String cardUid;
    private String transactionId;
    private Date transactionDate;
    private Long balance;
    private Long amount;
    private String cardName;
    private String foodName;
    private Integer foodAmount;

    public TransactionDataItem() {
    }

    public TransactionDataItem(String transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionDataItem(Long idOfOrderDetail, Long idOfOrg) {
        this.idOfOrderDetail = idOfOrderDetail;
        this.idOfOrg = idOfOrg;
    }

    public TransactionDataItem(Long idOfOrderDetail, Long idOfOrg, String organizationUid, String studentUid, Long cardPrintedNo,
            Integer cardType, Long transactionId, Date transactionDate, Long balance, String foodName,
            Long qty, Long rprice) {
        this.idOfOrderDetail = idOfOrderDetail;
        this.idOfOrg = idOfOrg;
        this.organizationUid = organizationUid;
        this.studentUid = studentUid;
        this.userToken = "";
        this.cardUid = cardPrintedNo == null ? null : cardPrintedNo.toString();
        this.transactionId = transactionId == null ? null : transactionId.toString();
        this.transactionDate = transactionDate == null ? null : transactionDate;
        this.balance = balance == null ? null : balance;
        this.cardName = cardType == null ? "" : Card.TYPE_NAMES[cardType];
        this.foodName = foodName;
        this.foodAmount = qty.intValue();
        this.amount = qty * rprice;
    }

    public TransactionDataItem(Long idOfOrg, String organizationUid, String studentUid, Long cardPrintedNo,
            Integer cardType, Long transactionId, Date transactionDate, Long balance, Long amount, Integer sourceType) {
        this.idOfOrg = idOfOrg;
        this.organizationUid = organizationUid;
        this.studentUid = studentUid;
        this.cardUid = cardPrintedNo == null ? "" : cardPrintedNo.toString();
        this.cardName = cardType == null ? "" : Card.TYPE_NAMES[cardType];
        this.transactionId = transactionId.toString();
        this.transactionDate = transactionDate;
        this.balance = balance;
        this.amount = amount;
        this.foodName = "Движение денег";
        this.foodAmount = 1;
    }

    public Long getIdOfOrderDetail() {
        return idOfOrderDetail;
    }

    public void setIdOfOrderDetail(Long idOfOrderDetail) {
        this.idOfOrderDetail = idOfOrderDetail;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getOrganizationUid() {
        return organizationUid;
    }

    public void setOrganizationUid(String organizationUid) {
        this.organizationUid = organizationUid;
    }

    public String getStudentUid() {
        return studentUid;
    }

    public void setStudentUid(String studentUid) {
        this.studentUid = studentUid;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getCardUid() {
        return cardUid;
    }

    public void setCardUid(String cardUid) {
        this.cardUid = cardUid;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Integer getFoodAmount() {
        return foodAmount;
    }

    public void setFoodAmount(Integer foodAmount) {
        this.foodAmount = foodAmount;
    }

}
