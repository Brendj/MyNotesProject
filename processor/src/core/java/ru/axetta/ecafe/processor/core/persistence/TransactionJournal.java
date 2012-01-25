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

    public static String[] SERVICE_CODE={"SCHL_ACC","SCHL_FD"};
    public static String[] TRANSACTION_CODE={"REG","IN","OUT","FD_BEN","DEBIT","BLOCK_REQUEST","BLOCK","UNBLOCK_REQUEST","UNBLOCK"};
    /* типы карт брать с сучности Card */
    public static String[] CART_CODE={"MUID"};  //additionalDataCode
    public static String[] ADDITIONAL_DATA_CODE={"ISPP_ACCOUNT_NUMBER","ISPP_CLIENT_TYPE","ISPP_INPUT_GROUP"};

    private long idOfTransactionJournal;
    private int serviceCode;
    private int transactionCode;
    private int cartType;
    private int cardIdentityCode;
    private long contractId;

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

    public int getCartType() {
        return cartType;
    }

    public void setCartType(int cartType) {
        this.cartType = cartType;
    }

    public int getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(int transactionCode) {
        this.transactionCode = transactionCode;
    }

    public int getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(int serviceCode) {
        this.serviceCode = serviceCode;
    }

    public long getIdOfTransactionJournal() {
        return idOfTransactionJournal;
    }

    public void setIdOfTransactionJournal(long idOfTransactionJournal) {
        this.idOfTransactionJournal = idOfTransactionJournal;
    }

}
