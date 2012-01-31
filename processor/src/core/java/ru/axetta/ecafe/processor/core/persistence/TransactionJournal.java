/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 25.01.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class TransactionJournal {

    public static String[] CART_CODE={"MUID"};  //additionalDataCode
    public static String[] ADDITIONAL_DATA_CODE={"ISPP_ACCOUNT_NUMBER","ISPP_CLIENT_TYPE","ISPP_INPUT_GROUP"};

    private static long key= 1;

    private long idOfTransactionJournal;
    private String serviceCode;
    private String transactionCode;
    private long contractId;

    private String enterName;
    private String clientType;
    private String OGRN;
    private Date sycroDate;

    private String cardIdentityName;
    private String cardIdentityCode;
    private String cardTypeName;
    private String cardTypeCode;
    private String clientSnilsSan;

    private long orderRSum;
    private Date accountingDate;

    public  Date getAccountingDate() {
        return accountingDate;
    }

    public void setAccountingDate( Date accountingDate) {
        this.accountingDate = accountingDate;
    }

    public String getCardTypeCode() {
        return cardTypeCode;
    }

    public void setCardTypeCode(String cardTypeCode) {
        this.cardTypeCode = cardTypeCode;
    }

    public String getCardTypeName() {
        return cardTypeName;
    }

    public void setCardTypeName(String cardTypeName) {
        this.cardTypeName = cardTypeName;
    }

    public String getCardIdentityCode() {
        return cardIdentityCode;
    }

    public void setCardIdentityCode(String cardIdentityCode) {
        this.cardIdentityCode = cardIdentityCode;
    }

    public String getCardIdentityName() {
        return cardIdentityName;
    }

    public void setCardIdentityName(String cardIdentityName) {
        this.cardIdentityName = cardIdentityName;
    }

    public Date getSycroDate() {
        return sycroDate;
    }

    public void setSycroDate(Date sycroDate) {
        this.sycroDate = sycroDate;
    }

    public String getOGRN() {
        return OGRN;
    }

    public void setOGRN(String OGRN) {
        this.OGRN = OGRN;
    }


    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public long getOrderRSum() {
        return orderRSum;
    }

    public void setOrderRSum(long orderRSum) {
        this.orderRSum = orderRSum;
    }

    public String getEnterName() {
        return enterName;
    }

    public void setEnterName(String enterName) {
        this.enterName = enterName;
    }

    public String getClientSnilsSan() {
        return clientSnilsSan;
    }

    public void setClientSnilsSan(String clientSnilsSan) {
        this.clientSnilsSan = clientSnilsSan;
    }

    public long getContractId() {
        return contractId;
    }

    public void setContractId(long contractId) {
        this.contractId = contractId;
    }


    public long getIdOfTransactionJournal() {
        return idOfTransactionJournal;
    }

    public void setIdOfTransactionJournal(long idOfTransactionJournal) {
        this.idOfTransactionJournal = idOfTransactionJournal;
    }

    public TransactionJournal() {
        this.idOfTransactionJournal=key;
        key++;
    }

}
