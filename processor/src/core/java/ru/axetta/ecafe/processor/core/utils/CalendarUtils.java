/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 03.07.2009
 * Time: 17:02:12
 * To change this template use File | Settings | File Templates.
 */
public class CalendarUtils {

    private CalendarUtils() {

    }

    public static void truncateToMonth(Calendar calendar) {
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
    }

    public static void truncateToDayOfMonth(Calendar calendar) {
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
    }
    public static Date truncateToDayOfMonth(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        truncateToDayOfMonth(c);
        return c.getTime();
    }
    public static Date truncateToDayOfMonthAndAddDay(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        c.add(Calendar.HOUR, 24);
        truncateToDayOfMonth(c);
        return c.getTime();
    }

    static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    static DateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    public static Date parseDate(String validTime) throws ParseException {
        return dateFormat.parse(validTime);
    }

    public static String dateToString(Date date) {
        return dateFormat.format(date);
    }

    public static String dateTimeToString(Date date) {
        return dateTimeFormat.format(date);
    }

    public static long getTimeFirstDayOfMonth(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        truncateToMonth(calendar);
        return calendar.getTimeInMillis();
    }

    public static long getTimeLastDayOfMonth(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.MONTH, 1);
        truncateToMonth(calendar);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTimeInMillis();
    }

    public static Date subTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        return calendar.getTime();
    }

    public static Date addOneDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }

    public static Date addDays(Date date, int nDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, nDays);
        return calendar.getTime();
    }

    public static int getDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static Date setDayOfMonth(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }
    public static Date getFirstDayOfNextMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        truncateToMonth(calendar);
        return calendar.getTime();
    }
}