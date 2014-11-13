/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.requestsandorders;

/**
 * Created with IntelliJ IDEA.
 * User: ziganshin
 * Date: 11.11.14
 * Time: 18:24
 * To change this template use File | Settings | File Templates.
 */
public enum FeedingPlanType {
    /*1*/ PAY_PLAN, /*2*/ SUBSCRIPTION_FEEDING, /*0*/ REDUCED_PRICE_PLAN;

    @Override
    public String toString() {
        return  (this == FeedingPlanType.PAY_PLAN) ?
                      "Платное питание" :
                (this == FeedingPlanType.SUBSCRIPTION_FEEDING) ?
                      "Абонементное питание"
                    : "Льготное питание";
    }
}