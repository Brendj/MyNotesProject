/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription;

import java.util.Date;

/**
 * Created by nuc on 26.09.2019.
 */
public class RegularPaymentStatus {
    private Long idOfRegularPaymentStatus;
    private RegularPayment regularPayment;
    private Integer errorCode;
    private String description;
    private Date statusDate;
    private Date createdDate;

    public RegularPaymentStatus() {

    }

    public RegularPaymentStatus(RegularPayment regularPayment, Integer errorCode, String description, Date statusDate) {
        this.regularPayment = regularPayment;
        this.errorCode = errorCode;
        this.description = description;
        this.statusDate = statusDate;
        this.createdDate = new Date();
    }

    public Long getIdOfRegularPaymentStatus() {
        return idOfRegularPaymentStatus;
    }

    public void setIdOfRegularPaymentStatus(Long idOfRegularPaymentStatus) {
        this.idOfRegularPaymentStatus = idOfRegularPaymentStatus;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public RegularPayment getRegularPayment() {
        return regularPayment;
    }

    public void setRegularPayment(RegularPayment regularPayment) {
        this.regularPayment = regularPayment;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }
}
