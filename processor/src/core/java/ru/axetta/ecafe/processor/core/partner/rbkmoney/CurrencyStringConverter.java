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
public class CurrencyStringConverter {

    private static final int COPECKS_IN_RUBLE = 100;
    private static final char DEFAULT_DELIMITER = '.';

    private CurrencyStringConverter() {

    }

    public static String copecksToRubles(long sum) throws Exception {
        if (0 > sum) {
            throw new IllegalArgumentException();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Long.toString(sum / COPECKS_IN_RUBLE)).append(DEFAULT_DELIMITER)
                .append(String.format("%02d", sum % COPECKS_IN_RUBLE));
        return stringBuilder.toString();
    }

}