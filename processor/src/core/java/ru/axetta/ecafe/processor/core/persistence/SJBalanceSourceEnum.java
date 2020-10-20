/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 26.04.16
 * Time: 11:28
 * To change this template use File | Settings | File Templates.
 */
public enum SJBalanceSourceEnum {
    /*0*/ SJBALANCE_SOURCE_UNKNOWN("UNKNOWN"),
    /*1*/ SJBALANCE_SOURCE_STDPAY("Платеж STD-PAY"),
    /*2*/ SJBALANCE_SOURCE_RNIP("Платеж РНИП"),
    /*3*/ SJBALANCE_SOURCE_SYNC("Платеж Синхронизация"),
    /*4*/ SJBALANCE_SOURCE_ORDER("Заказ"),
    /*5*/ SJBALANCE_SOURCE_CANCEL_ORDER("Отмена заказа"),
    /*6*/ SJBALANCE_SOURCE_PAY("Платеж PAY"),
    /*7*/ SJBALANCE_SOURCE_BALANCE_TRANSFER("Перевод между счетами"),
    /*8*/ SJBALANCE_SOURCE_REFUND("Возврат средств"),
    /*9*/ SJBALANCE_SOURCE_SOAP_PAYMENT("Платеж SOAP/PAYMENT"),
    /*10*/ SJ_BALANCE_SOURCE_WAY4("Платеж PAYMENT-WAY4"),
    /*11*/ SJBALANCE_SOURCE_ORDER_PAYMENT("Платеж clientPaymentOrder");

    private final String description;
    static Map<Integer,SJBalanceSourceEnum> map = new HashMap<Integer,SJBalanceSourceEnum>();
    static {
        for (SJBalanceSourceEnum questionaryStatus : SJBalanceSourceEnum.values()) {
            map.put(questionaryStatus.ordinal(), questionaryStatus);
        }
    }
    private SJBalanceSourceEnum(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static SJBalanceSourceEnum fromInteger(Integer value){
        return map.get(value);
    }
}
