/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.regularPaymentSubscription;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 07.10.13
 * Time: 15:55
 */

public class BankSubscription {

    private Long idOfSubscription;
    private Long paymentAmount;
    private Long thresholdAmount;
    private int monthsCount;
    private Date validToDate;
    private Date activationDate;
    private Date deactivationDate;
    private boolean active;
    private String status;
    private String paymentId;
    private Client client;
    private String san;
    private int paySystem;
    private Date lastSuccessfulPaymentDate;
    private Date lastUnsuccessfulPaymentDate;
    private String lastPaymentStatus;
    private int unsuccessfulPaymentsCount;
    private String maskedCardNumber;
    private String cardHolder;
    private Integer expMonth;
    private Integer expYear;
    private Set<MfrRequest> mfrRequests = new HashSet<MfrRequest>();
    private Set<RegularPayment> regularPayments = new HashSet<RegularPayment>();
    private Boolean notificationSent;
    private String mobile;

    public Long getIdOfSubscription() {
        return idOfSubscription;
    }

    public void setIdOfSubscription(Long idOfSubscription) {
        this.idOfSubscription = idOfSubscription;
    }

    public Long getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Long paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Long getThresholdAmount() {
        return thresholdAmount;
    }

    public void setThresholdAmount(Long thresholdAmount) {
        this.thresholdAmount = thresholdAmount;
    }

    public int getMonthsCount() {
        return monthsCount;
    }

    public void setMonthsCount(int monthsCount) {
        this.monthsCount = monthsCount;
    }

    public Date getValidToDate() {
        return validToDate;
    }

    public void setValidToDate(Date validToDate) {
        this.validToDate = validToDate;
    }

    public Date getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(Date activationDate) {
        this.activationDate = activationDate;
    }

    public Date getDeactivationDate() {
        return deactivationDate;
    }

    public void setDeactivationDate(Date deactivationDate) {
        this.deactivationDate = deactivationDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
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

    public int getPaySystem() {
        return paySystem;
    }

    public void setPaySystem(int paySystem) {
        this.paySystem = paySystem;
    }

    public Date getLastSuccessfulPaymentDate() {
        return lastSuccessfulPaymentDate;
    }

    public void setLastSuccessfulPaymentDate(Date lastSuccessfulPaymentDate) {
        this.lastSuccessfulPaymentDate = lastSuccessfulPaymentDate;
    }

    public Date getLastUnsuccessfulPaymentDate() {
        return lastUnsuccessfulPaymentDate;
    }

    public void setLastUnsuccessfulPaymentDate(Date lastUnsuccessfulPaymentDate) {
        this.lastUnsuccessfulPaymentDate = lastUnsuccessfulPaymentDate;
    }

    public String getLastPaymentStatus() {
        return lastPaymentStatus;
    }

    public void setLastPaymentStatus(String lastPaymentStatus) {
        this.lastPaymentStatus = lastPaymentStatus;
    }

    public int getUnsuccessfulPaymentsCount() {
        return unsuccessfulPaymentsCount;
    }

    public void setUnsuccessfulPaymentsCount(int unsuccessfulPaymentsCount) {
        this.unsuccessfulPaymentsCount = unsuccessfulPaymentsCount;
    }

    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }

    public void setMaskedCardNumber(String maskedCardNumber) {
        this.maskedCardNumber = maskedCardNumber;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public Integer getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(Integer expMonth) {
        this.expMonth = expMonth;
    }

    public Integer getExpYear() {
        return expYear;
    }

    public void setExpYear(Integer expYear) {
        this.expYear = expYear;
    }

    public Set<MfrRequest> getMfrRequests() {
        return mfrRequests;
    }

    protected void setMfrRequests(Set<MfrRequest> mfrRequests) {
        this.mfrRequests = mfrRequests;
    }

    public Set<RegularPayment> getRegularPayments() {
        return regularPayments;
    }

    protected void setRegularPayments(Set<RegularPayment> regularPayments) {
        this.regularPayments = regularPayments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BankSubscription that = (BankSubscription) o;
        return idOfSubscription != null && idOfSubscription.equals(that.idOfSubscription);
    }

    @Override
    public int hashCode() {
        return idOfSubscription != null ? idOfSubscription.hashCode() : 0;
    }

    public Boolean getNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(Boolean notificationSent) {
        this.notificationSent = notificationSent;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
