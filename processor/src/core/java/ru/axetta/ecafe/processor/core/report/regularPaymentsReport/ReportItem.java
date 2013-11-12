/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.regularPaymentsReport;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 11.11.13
 * Time: 15:53
 */

public class ReportItem {

    private Date paymentDate;
    private Long idOfPayment;
    private String orgName;
    private Long contractId;
    private String name;
    private String surname;
    private String secondName;
    private Long paymentSum;
    private Long clientBalance;
    private String success;
    private Long rrn;
    private String status;
    private String errorMessage;

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Long getIdOfPayment() {
        return idOfPayment;
    }

    public void setIdOfPayment(Long idOfPayment) {
        this.idOfPayment = idOfPayment;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public Long getPaymentSum() {
        return paymentSum;
    }

    public void setPaymentSum(Long paymentSum) {
        this.paymentSum = paymentSum;
    }

    public Long getClientBalance() {
        return clientBalance;
    }

    public void setClientBalance(Long clientBalance) {
        this.clientBalance = clientBalance;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public Long getRrn() {
        return rrn;
    }

    public void setRrn(Long rrn) {
        this.rrn = rrn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
