/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.util;

import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 30.09.2010
 * Time: 15:09:40
 * To change this template use File | Settings | File Templates.
 */
public class ParseUtils {

    private ParseUtils() {
    }


    public static int parseInt(NumberFormat format, String text) throws Exception {
        ParsePosition parsePosition = new ParsePosition(0);
        int value = format.parse(text, parsePosition).intValue();
        int stopIndex = parsePosition.getIndex();
        if (0 == stopIndex || StringUtils.length(text) != stopIndex) {
            throw new IllegalArgumentException(String.format("Invalid symbol at: %d in text \"%s\"", stopIndex, text));
        }
        return value;
    }

    public static long parseLong(NumberFormat format, String text) throws Exception {
        ParsePosition parsePosition = new ParsePosition(0);
        long value = format.parse(text, parsePosition).longValue();
        int stopIndex = parsePosition.getIndex();
        if (0 == stopIndex || StringUtils.length(text) != stopIndex) {
            throw new IllegalArgumentException(String.format("Invalid symbol at: %d in text \"%s\"", stopIndex, text));
        }
        return value;
    }

    public static Date parseDateTime(DateFormat format, String text) throws Exception {
        ParsePosition parsePosition = new ParsePosition(0);
        Date value = format.parse(text, parsePosition);
        int stopIndex = parsePosition.getIndex();
        if (0 == stopIndex || StringUtils.length(text) != stopIndex) {
            throw new ParseException(String.format("Invalid symbol at: %d in text \"%s\"", stopIndex, text), stopIndex);
        }
        return value;
    }
}
