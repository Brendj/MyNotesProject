/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 27.02.17
 * Time: 10:58
 * To change this template use File | Settings | File Templates.
 */
public enum SubscriptionFeedingType {
    ABON_TYPE("Абонементное питание"),
    VARIABLE_TYPE("Вариативное льготное питание");

    private String description;

    static HashMap<Integer, SubscriptionFeedingType> subscriptionFeedingHashMap = new HashMap<Integer, SubscriptionFeedingType>();
    static {
        for (SubscriptionFeedingType subscriptionFeeding: SubscriptionFeedingType.values()){
            subscriptionFeedingHashMap.put(subscriptionFeeding.ordinal(), subscriptionFeeding);
        }
    }

    public static SubscriptionFeedingType fromInteger(int value){
        return subscriptionFeedingHashMap.get(value);
    }

    private SubscriptionFeedingType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return  description;
    }
}
