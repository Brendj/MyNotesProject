/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 27.07.2009
 * Time: 13:50:22
 * To change this template use File | Settings | File Templates.
 */
public class CompositeIdOfSubscriptionFee implements Serializable {

    private Integer subscriptionYear;
    private Integer periodNo;

    CompositeIdOfSubscriptionFee() {
        // For Hibernate only
    }

    public CompositeIdOfSubscriptionFee(Integer subscriptionYear, Integer periodNo) {
        this.subscriptionYear = subscriptionYear;
        this.periodNo = periodNo;
    }

    public Integer getSubscriptionYear() {
        return subscriptionYear;
    }

    private void setSubscriptionYear(Integer subscriptionYear) {
        // For Hibernate only
        this.subscriptionYear = subscriptionYear;
    }

    public Integer getPeriodNo() {
        return periodNo;
    }

    private void setPeriodNo(Integer periodNo) {
        // For Hibernate only
        this.periodNo = periodNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeIdOfSubscriptionFee)) {
            return false;
        }
        final CompositeIdOfSubscriptionFee that = (CompositeIdOfSubscriptionFee) o;
        return periodNo.equals(that.getPeriodNo()) && subscriptionYear.equals(that.getSubscriptionYear());
    }

    @Override
    public int hashCode() {
        int result = subscriptionYear != null ? subscriptionYear.hashCode() : 0;
        result = 31 * result + (periodNo != null ? periodNo.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CompositeIdOfSubscriptionFee{" + "subscriptionYear=" + subscriptionYear + ", periodNo=" + periodNo
                + '}';
    }
}