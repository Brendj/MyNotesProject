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

    private CompositeIdOfSubscriptionFee compositeIdOfSubscriptionFee;
    private AccountTransaction transaction;
    private Long subscriptionSum;
    private Date createTime;

    protected SubscriptionFee() {
        // For Hibernate only
    }

    public SubscriptionFee(CompositeIdOfSubscriptionFee compositeIdOfSubscriptionFee, AccountTransaction transaction,
            Long subscriptionSum, Date createTime) {
        this.compositeIdOfSubscriptionFee = compositeIdOfSubscriptionFee;
        this.transaction = transaction;
        this.subscriptionSum = subscriptionSum;
        this.createTime = createTime;
    }

    public CompositeIdOfSubscriptionFee getCompositeIdOfSubscriptionFee() {
        return compositeIdOfSubscriptionFee;
    }

    private void setCompositeIdOfSubscriptionFee(CompositeIdOfSubscriptionFee compositeIdOfSubscriptionFee) {
        // For Hibernate only
        this.compositeIdOfSubscriptionFee = compositeIdOfSubscriptionFee;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubscriptionFee)) {
            return false;
        }
        final SubscriptionFee that = (SubscriptionFee) o;
        return compositeIdOfSubscriptionFee.equals(that.getCompositeIdOfSubscriptionFee());
    }

    @Override
    public int hashCode() {
        return compositeIdOfSubscriptionFee.hashCode();
    }

    @Override
    public String toString() {
        return "SubscriptionFee{" + "compositeIdOfSubscriptionFee=" + compositeIdOfSubscriptionFee + ", transaction="
                + transaction + ", subscriptionSum=" + subscriptionSum + ", createTime=" + createTime + '}';
    }
}