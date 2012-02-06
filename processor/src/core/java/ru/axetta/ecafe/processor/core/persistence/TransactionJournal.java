/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 25.01.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class TransactionJournal {
    public static final String CARD_TYPE_CODE_UEC = "UEC";
    public static final String CARD_TYPE_ID_CODE_MUID = "MUID";
    public static final String SERVICE_CODE_SCHL_FD = "SCHL_FD";
    public static final String SERVICE_CODE_SCHL_ACC = "SCHL_ACC";
    public static final String TRANS_CODE_FD_BEN = "FD_BEN";
    public static final String TRANS_CODE_DEBIT = "DEBIT";

    public static String[] ADDITIONAL_DATA_CODE={"ISPP_ACCOUNT_NUMBER","ISPP_CLIENT_TYPE","ISPP_INPUT_GROUP"};

    private long idOfOrg;
    private long idOfInternalOperation;

    // transactionIdDescription.transactionId
    private long idOfTransactionJournal;
    // transactionIdDescription.transactionDate
    private Date transDate;

    // transactionSourceId.idCode
    private String OGRN;

    // transactionTypeDescription.serviceCode (тип услуги)
    private String serviceCode;
    // transactionTypeDescription.transactionCode (тип транзакции)
    private String transactionCode;

    // holderDescription.cardTypeCode (тип карты: UEC)
    private String cardTypeCode;
    // holderDescription.cardIdentityCode (тип идентификаторы карты: MUID)
    private String cardIdentityCode;
    // holderDescription.cardIdentityName (номер чипа в hex для типа MUID)
    private String cardIdentityName;
    // holderDescription.snils
    private String clientSan;

    // additionalInfo.additionalData.additionalDataValue (ISPP_ACCOUNT_NUMBER/Идентификатор лицевого счета)
    private long contractId;

    // additionalInfo.additionalData.additionalDataValue (ISPP_CLIENT_TYPE/Тип клиента - ученик, сотрудник, другое)
    private String clientType;

    // additionalInfo.additionalData.additionalDataValue (ISPP_INPUT_GROUP/Наименование входной группы)
    private String enterName;


    // accountingDescription.accountingDescriptionItem.financialDescription.financialDescriptionItem.financialAmount
    private long financialAmount;
    // accountingDescription.accountingDescriptionItem.financialDescription.financialDescriptionItem.accountingDate
    private Date accountingDate;

    public long getIdOfInternalOperation() {
        return idOfInternalOperation;
    }

    public void setIdOfInternalOperation(long idOfInternalOperation) {
        this.idOfInternalOperation = idOfInternalOperation;
    }

    public long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

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
        if (cardIdentityCode.equals(CARD_TYPE_CODE_UEC)) return "Универсальная Электронная Карта";
        return "Неизвестно";
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

    public Date getTransDate() {
        return transDate;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
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

    public long getFinancialAmount() {
        return financialAmount;
    }

    public void setFinancialAmount(long financialAmount) {
        this.financialAmount = financialAmount;
    }

    public String getEnterName() {
        return enterName;
    }

    public void setEnterName(String enterName) {
        this.enterName = enterName;
    }

    public String getClientSan() {
        return clientSan;
    }

    public void setClientSan(String clientSan) {
        this.clientSan = clientSan;
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

    protected TransactionJournal() {
    }

    public TransactionJournal(long idOfOrg, long idOfInternalOperation, Date transDate,
            String OGRN, String serviceCode, String transactionCode, String cardTypeCode,
            String cardIdentityCode, String cardIdentityName, String clientSan, long contractId, String clientType,
            String enterName) {
        this.idOfOrg = idOfOrg;
        this.idOfInternalOperation = idOfInternalOperation;
        this.transDate = transDate;
        this.OGRN = OGRN;
        this.serviceCode = serviceCode;
        this.transactionCode = transactionCode;
        this.cardTypeCode = cardTypeCode;
        this.cardIdentityCode = cardIdentityCode;
        this.cardIdentityName = cardIdentityName;
        this.clientSan = clientSan;
        this.contractId = contractId;
        this.clientType = clientType;
        this.enterName = enterName;
    }

    public TransactionJournal(long idOfOrg, long idOfInternalOperation, Date transDate,
            String OGRN, String serviceCode, String transactionCode, String cardTypeCode,
            String cardIdentityCode, String cardIdentityName, String clientSan, long contractId, String clientType,
            long financialAmount, Date accountingDate) {
        this.idOfOrg = idOfOrg;
        this.idOfInternalOperation = idOfInternalOperation;
        this.transDate = transDate;
        this.OGRN = OGRN;
        this.serviceCode = serviceCode;
        this.transactionCode = transactionCode;
        this.cardTypeCode = cardTypeCode;
        this.cardIdentityCode = cardIdentityCode;
        this.cardIdentityName = cardIdentityName;
        this.clientSan = clientSan;
        this.contractId = contractId;
        this.clientType = clientType;
        this.financialAmount = financialAmount;
        this.accountingDate = accountingDate;
    }

}
