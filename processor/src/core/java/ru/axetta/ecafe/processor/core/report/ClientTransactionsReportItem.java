/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

/**
 * Created by anvarov on 14.06.2017.
 */
public class ClientTransactionsReportItem {

    private Long idOfOrg;
    private String contragent;
    private String operationType;
    private String summ;
    private String transactionDescription;
    private String transactionTime;
    private Long orderNumber;
    private Long personalAccount;

    public ClientTransactionsReportItem() {
    }

    public ClientTransactionsReportItem(Long idOfOrg, String contragent, String operationType, String summ,
            String transactionDescription, String transactionTime, Long orderNumber, Long personalAccount) {
        this.idOfOrg = idOfOrg;
        this.contragent = contragent;
        this.operationType = operationType;
        this.summ = summ;
        this.transactionDescription = transactionDescription;
        this.transactionTime = transactionTime;
        this.orderNumber = orderNumber;
        this.personalAccount = personalAccount;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getContragent() {
        return contragent;
    }

    public void setContragent(String contragent) {
        this.contragent = contragent;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getSumm() {
        return summ;
    }

    public void setSumm(String summ) {
        this.summ = summ;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getPersonalAccount() {
        return personalAccount;
    }

    public void setPersonalAccount(Long personalAccount) {
        this.personalAccount = personalAccount;
    }
}
