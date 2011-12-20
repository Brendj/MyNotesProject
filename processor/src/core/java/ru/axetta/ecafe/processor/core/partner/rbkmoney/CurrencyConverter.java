/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.rbkmoney;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 01.12.2009
 * Time: 14:36:37
 * To change this template use File | Settings | File Templates.
 */
public class CurrencyConverter {

    private static final int COPECKS_IN_RUBLE = 100;

    private CurrencyConverter() {

    }

    public static long rublesToCopecks(double rubles) throws Exception {
        return (long) (rubles * COPECKS_IN_RUBLE);
    }

}