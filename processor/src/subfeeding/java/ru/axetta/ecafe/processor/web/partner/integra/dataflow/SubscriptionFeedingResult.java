/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.04.14
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
public class SubscriptionFeedingResult extends Result {

    private SubscriptionFeedingExt subscriptionFeedingExt;

    public SubscriptionFeedingExt getSubscriptionFeedingExt() {
        return subscriptionFeedingExt;
    }

    public void setSubscriptionFeedingExt(SubscriptionFeedingExt subscriptionFeedingExt) {
        this.subscriptionFeedingExt = subscriptionFeedingExt;
    }
}
