/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

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

    private static long key=20000;

    private long idOfTransactionJournal;
    private String serviceCode;
    private String transactionCode;
    private int cardIdentityCode;
    private long contractId;
    private String clientSnilsSan;
    private String enterName;
    private long orderRSum;
    private String clientType;
    private String cartTypeName;

    public String getCartTypeName() {
        return cartTypeName;
    }

    public void setCartTypeName(String cartTypeName) {
        this.cartTypeName = cartTypeName;
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


    public int getCardIdentityCode() {
        return cardIdentityCode;
    }

    public void setCardIdentityCode(int cardIdentityCode) {
        this.cardIdentityCode = cardIdentityCode;
    }

    public long getIdOfTransactionJournal() {
        return idOfTransactionJournal;
    }

    public void setIdOfTransactionJournal(long idOfTransactionJournal) {
        this.idOfTransactionJournal = idOfTransactionJournal;
    }

    public TransactionJournal() {
        key++;
        this.idOfTransactionJournal=key;
        this.serviceCode = "1";
        this.transactionCode = "8";
        this.cardIdentityCode = 1;
        this.contractId = 12;
        this.clientSnilsSan = "jjj";
        this.enterName = "enterName";
        this.orderRSum = 1212;
        this.clientType = "clientType";
        this.cartTypeName = "cartT";
    }

    @Override
    public String toString(){
        return this.idOfTransactionJournal+" "+this.transactionCode+" "+this.cartTypeName;
    }
}
