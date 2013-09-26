/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 02.06.2009
 * Time: 10:39:31
 * To change this template use File | Settings | File Templates.
 */
public class SubscriptionFee {

    public static final String[] TYPE_DESCRIPTIONS = {"Неизвестно", "Абонентская плата за сервис SMS"};

    public static final int TYPE_SMS_SERVICE = 1;

    private Long idOfSubscriptionFee;
    private Integer subscriptionYear;
    private Integer periodNo;
    private AccountTransaction transaction;
    private Long subscriptionSum;
    private Date createTime;
    private int subscriptionType;

    protected SubscriptionFee() {
    }

    public SubscriptionFee(Integer subscriptionYear, Integer periodNo, AccountTransaction transaction,
            Long subscriptionSum, Date createTime, int type) {
        this.subscriptionYear = subscriptionYear;
        this.periodNo = periodNo;
        this.transaction = transaction;
        this.subscriptionSum = subscriptionSum;
        this.createTime = createTime;
        this.subscriptionType = type;
    }

    public Long getIdOfSubscriptionFee() {
        return idOfSubscriptionFee;
    }

    public void setIdOfSubscriptionFee(Long idOfSubscriptionFee) {
        this.idOfSubscriptionFee = idOfSubscriptionFee;
    }

    public Integer getSubscriptionYear() {
        return subscriptionYear;
    }

    public void setSubscriptionYear(Integer subscriptionYear) {
        this.subscriptionYear = subscriptionYear;
    }

    public Integer getPeriodNo() {
        return periodNo;
    }

    public void setPeriodNo(Integer periodNo) {
        this.periodNo = periodNo;
    }

    public AccountTransaction getTransaction() {
        return transaction;
    }

    private void setTransaction(AccountTransaction transaction) {
        // For Hibernate only
        this.transaction = transaction;
    }

    public Long getSubscriptionSum() {
        return subscriptionSum;
    }

    private void setSubscriptionSum(Long subscriptionSum) {
        // For Hibernate only
        this.subscriptionSum = subscriptionSum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    private void setCreateTime(Date createTime) {
        // For Hibernate only
        this.createTime = createTime;
    }

    public int getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(int subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubscriptionFee)) {
            return false;
        }
        final SubscriptionFee that = (SubscriptionFee) o;
        return idOfSubscriptionFee.equals(that.idOfSubscriptionFee);
    }

    @Override
    public int hashCode() {
        return idOfSubscriptionFee.hashCode();
    }

    @Override
    public String toString() {
        return "SubscriptionFee{" + "idOfSubscriptionFee=" + idOfSubscriptionFee + ", transaction="
                + transaction + ", subscriptionSum=" + subscriptionSum + ", createTime=" + createTime + '}';
    }
}