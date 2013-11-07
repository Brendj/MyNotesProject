/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 07.10.13
 * Time: 15:55
 */

public class MfrRequest {

    public static final int REQUEST_TYPE_ACTIVATION = 1;
    public static final int REQUEST_TYPE_DEACTIVATION = 2;
    public static final int REQUEST_TYPE_PAYMENT = 3;
    public static final int REQUEST_TYPE_STATUS_CHECK = 4;

    public static final int ACQUIROPAY_SYSTEM = 1;

    public static final String SUBSCRIPTION_ACTIVATED = "INITIAL";
    public static final String SUBSCRIPTION_DEACTIVATED = "REBILL_CANCEL";
    public static final String PAYMENT_SUCCESSFUL = "REBILL_OK";
    public static final String ERROR = "KO";

    private Long idOfRequest;
    private BankSubscription bs;
    private int paySystem;
    private int requestType;
    private String requestUrl;
    private Date requestTime;
    private boolean success;
    private String responseStatus;
    private Client client;
    private String san;
    private String errorDescription;

    public Long getIdOfRequest() {
        return idOfRequest;
    }

    public void setIdOfRequest(Long idOfRequest) {
        this.idOfRequest = idOfRequest;
    }

    public BankSubscription getBs() {
        return bs;
    }

    public void setBs(BankSubscription bs) {
        this.bs = bs;
    }

    public int getPaySystem() {
        return paySystem;
    }

    public void setPaySystem(int paySystem) {
        this.paySystem = paySystem;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getSan() {
        return san;
    }

    public void setSan(String san) {
        this.san = san;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MfrRequest that = (MfrRequest) o;
        return idOfRequest != null && idOfRequest.equals(that.idOfRequest);
    }

    @Override
    public int hashCode() {
        return idOfRequest != null ? idOfRequest.hashCode() : 0;
    }
}
