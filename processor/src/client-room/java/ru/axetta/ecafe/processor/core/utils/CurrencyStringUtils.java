/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

/**
 * Утилиты для сервлетов
 */
public final class CurrencyStringUtils {

    private static final String[] DELIMITERS = {".", ",", "=", " "};
    private static final int DEFAULT_DELIMITER = 1;
    private static final int COPECKS_IN_RUBLE = 100;

    private CurrencyStringUtils() {
        // Not instantiable
    }

    public static long rublesToCopecks(String string) throws Exception {
        int delimiterPos = -1;
        for (String currDelimiter : DELIMITERS) {
            delimiterPos = string.indexOf(currDelimiter);
            if (-1 != delimiterPos) {
                break;
            }
        }
        String rublesText = string;
        String copecksText = null;
        if (-1 != delimiterPos) {
            rublesText = string.substring(0, delimiterPos);
            if (delimiterPos + 1 < string.length()) {
                copecksText = string.substring(delimiterPos + 1);
            }
        }
        long sum = Long.parseLong(rublesText) * COPECKS_IN_RUBLE;
        if (null != copecksText) {
            long copecks = Long.parseLong(copecksText);
            if (0 > copecks || COPECKS_IN_RUBLE - 1 < copecks) {
                throw new IllegalArgumentException("Invalid copecks part");
            }
            sum += copecks;
        }
        return sum;
    }

    public static String copecksToRubles(long sum) {
        Long absSum = Math.abs(sum);
        StringBuilder stringBuilder = new StringBuilder();
        if (0 > sum) {
            stringBuilder.append('-');
        }
        stringBuilder.append(Long.toString(absSum / COPECKS_IN_RUBLE)).append(DELIMITERS[DEFAULT_DELIMITER])
                .append(String.format("%02d", absSum % COPECKS_IN_RUBLE));
        return stringBuilder.toString();
    }

}