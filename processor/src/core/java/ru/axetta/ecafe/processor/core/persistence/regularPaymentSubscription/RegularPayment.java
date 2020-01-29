/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 07.10.13
 * Time: 15:56
 */

public class RegularPayment {

    private Long idOfPayment;
    private BankSubscription bankSubscription;
    private MfrRequest mfrRequest;
    private ClientPayment clientPayment;
    private Long paymentAmount;
    private Date paymentDate;
    private Client client;
    private Long clientBalance;
    private Long thresholdAmount;
    private boolean success;
    private String status;
    private String authCode;
    private Long rrn;
    private Integer errorCode;
    private String errorDesc;

    public Long getIdOfPayment() {
        return idOfPayment;
    }

    public void setIdOfPayment(Long idOfPayment) {
        this.idOfPayment = idOfPayment;
    }

    public BankSubscription getBankSubscription() {
        return bankSubscription;
    }

    public void setStatusFields(Integer errorCode, String errorDesc) {
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    public void setBankSubscription(BankSubscription bankSubscription) {
        this.bankSubscription = bankSubscription;
    }

    public MfrRequest getMfrRequest() {
        return mfrRequest;
    }

    public void setMfrRequest(MfrRequest mfrRequest) {
        this.mfrRequest = mfrRequest;
    }

    public Long getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Long paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getClientBalance() {
        return clientBalance;
    }

    public void setClientBalance(Long clientBalance) {
        this.clientBalance = clientBalance;
    }

    public Long getThresholdAmount() {
        return thresholdAmount;
    }

    public void setThresholdAmount(Long thresholdAmount) {
        this.thresholdAmount = thresholdAmount;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public Long getRrn() {
        return rrn;
    }

    public void setRrn(Long rrn) {
        this.rrn = rrn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegularPayment payment = (RegularPayment) o;
        return idOfPayment != null && idOfPayment.equals(payment.idOfPayment);
    }

    @Override
    public int hashCode() {
        return idOfPayment != null ? idOfPayment.hashCode() : 0;
    }

    public ClientPayment getClientPayment() {
        return clientPayment;
    }

    public void setClientPayment(ClientPayment clientPayment) {
        this.clientPayment = clientPayment;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }
}
