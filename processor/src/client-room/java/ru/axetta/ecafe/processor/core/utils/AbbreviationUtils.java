/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 03.07.2009
 * Time: 17:02:12
 * To change this template use File | Settings | File Templates.
 */
public class AbbreviationUtils {

    private AbbreviationUtils() {

    }

    public static String buildAbbreviation(String firstName, String surname, String secondName) {
        StringBuilder stringBuilder = new StringBuilder(StringUtils.length(surname) + 5);
        stringBuilder.append(surname);
        if (StringUtils.isNotEmpty(firstName)) {
            stringBuilder.append(' ').append(firstName.charAt(0)).append('.');
            if (StringUtils.isNotEmpty(secondName)) {
                stringBuilder.append(secondName.charAt(0)).append('.');
            }
        }
        return stringBuilder.toString();
    }

    public static String buildFullAbbreviation(String firstName, String surname, String secondName) {
        StringBuilder stringBuilder = new StringBuilder(
                StringUtils.length(firstName) + StringUtils.length(surname) + StringUtils.length(secondName) + 2);
        stringBuilder.append(surname);
        if (StringUtils.isNotEmpty(firstName)) {
            if (0 != stringBuilder.length()) {
                stringBuilder.append(' ');
            }
            stringBuilder.append(firstName);
        }
        if (StringUtils.isNotEmpty(secondName)) {
            if (0 != stringBuilder.length()) {
                stringBuilder.append(' ');
            }
            stringBuilder.append(secondName);
        }
        return stringBuilder.toString();
    }
}
