/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 03.07.2009
 * Time: 17:02:12
 * To change this template use File | Settings | File Templates.
 */
public class CalendarUtils {

    private static TimeZone localTimeZone = TimeZone.getTimeZone("Europe/Moscow");
    private static TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private static SimpleDateFormat dateShortFormat = new SimpleDateFormat("dd.MM.yy");
    private static SimpleDateFormat dayInWeekFormat = new SimpleDateFormat("EE", new Locale("ru"));

    public static Date parseFullDateTimeWithLocalTimeZone(String s) throws ParseException {
        dateTimeFormat.setTimeZone(localTimeZone);
        return (s == null || s.isEmpty()) ? null : dateTimeFormat.parse(s);
    }

    public static String toStringFullDateTimeWithLocalTimeZone(Date dateTime) {
        dateTimeFormat.setTimeZone(localTimeZone);
        return dateTimeFormat.format(dateTime);
    }

    public static String toStringFullDateTimeWithUTCTimeZone(Date dateTime) throws ParseException {
        dateTimeFormat.setTimeZone(utcTimeZone);
        return dateTimeFormat.format(dateTime);
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

    public static Date parseDate(String s) throws ParseException {
        dateFormat.setTimeZone(utcTimeZone);
        if (s == null || s.isEmpty())
            return null;
        else
            return dateFormat.parse(s);
    }

    public static String dateToString(Date date) {
        return dateFormat.format(date);
    }

    public static Date parseShortDate(String validTime) throws ParseException {
        return dateShortFormat.parse(validTime);
    }

    public static Date parseTime(String validTime) throws ParseException {
        return timeFormat.parse(validTime);
    }

    public static String timeToString(Date date) {
        return timeFormat.format(date);
    }

    public static Date parseDayInWeek(String validTime) throws ParseException {
        return timeFormat.parse(validTime);
    }

    public static String dayInWeekToString(Long timeMillis) {
        return timeFormat.format(timeMillis);
    }

    public static String dayInWeekToString(Date date) {
        return timeFormat.format(date);
    }

    public static String dateShortToString(Date date) {
        return dateShortFormat.format(date);
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

    public static Date addMonth(Date date, int nMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, nMonth);
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

    public static Boolean isDateEqLtCurrentDate(Date thisDate){
        return (thisDate!=null && System.currentTimeMillis()<=thisDate.getTime());
    }

    private CalendarUtils() {}
}