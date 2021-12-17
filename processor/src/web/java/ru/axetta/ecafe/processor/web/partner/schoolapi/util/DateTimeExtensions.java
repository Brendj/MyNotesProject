/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateTimeExtensions {
    public static Date now() {
        return fromLong(System.currentTimeMillis());
    }

    public static Date today() {
        Date now = now();
        return sameDateWithOtherTime(now, 0, 0, 0);
    }

    public static String toFullDateTimeString(Date date) {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(date);
    }

    public static String toShortDateString(Date date) {
        return new SimpleDateFormat("dd.MM.yyyy").format(date);
    }

    public static String toPostgreeDateString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static Date fromString(String value, String dateTimeFormat) {
        if (value == null || value.isEmpty()) return null;
        if (dateTimeFormat == null || dateTimeFormat.isEmpty())
            dateTimeFormat = value.length() > 10 ? "dd.MM.yyyy HH:mm:ss" : "dd.MM.yyyy";
        SimpleDateFormat format = new SimpleDateFormat(dateTimeFormat);
        try {
            return format.parse(value);
        } catch (ParseException exception) {
            return null;
        }
    }

    public static Date fromLong(long dateAsLong) {
        return new Date(dateAsLong);
    }

    public static Date fromLongInString(String dateAsLongString) {
        return fromLong(Long.parseLong(dateAsLongString));
    }

    public static long toLong(Date date) {
        return date.getTime();
    }

    public static Date addMilliseconds(Date dateTime, int milliseconds) {
        return fromLong(toLong(dateTime) + milliseconds);
    }

    public static Date addSeconds(Date dateTime, int seconds) {
        return fromLong(toLong(dateTime) + (1000 * seconds));
    }

    public static Date addMinutes(Date dateTime, int minutes) {
        return fromLong(toLong(dateTime) + (60 * 1000 * minutes));
    }

    public static Date addHours(Date dateTime, int hours) {
        return fromLong(toLong(dateTime) + (60 * 60 * 1000 * hours));
    }

    public static Date addDays(Date dateTime, int days) {
        return fromLong(toLong(dateTime) + (24 * 60 * 60 * 1000 * days));
    }

    public static Date create(int year, int month, int day) {
        checkDate(year, month, day);
        return new GregorianCalendar(year, month, day).getTime();
    }

    public static Date create(int year, int month, int day, int hours, int minutes, int seconds) {
        checkDateTime(year, month, day, hours, minutes, seconds);
        return new GregorianCalendar(year, month - 1, day, hours, minutes, seconds).getTime();
    }


    public static Date sameDateWithOtherTime(Date dateTime, int hours, int minutes, int seconds) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(dateTime);
        return create(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH) + 1, gc.get(Calendar.DAY_OF_MONTH), hours, minutes, seconds);
    }


    public static boolean isSameDate(Date first, Date second) {
        if (first == null && second == null) return true;
        if (first == null) return false;
        if (second == null) return false;

        String firstDateString = toShortDateString(first);
        String secondDateString = toShortDateString(second);
        return Objects.equals(firstDateString, secondDateString);
    }

    public static boolean isSameDateTime(Date first, Date second) {
        if (first == null && second == null) return true;
        if (first == null) return false;
        if (second == null) return false;

        String firstDateTimeString = toFullDateTimeString(first);
        String secondDateTimeString = toFullDateTimeString(second);
        return Objects.equals(firstDateTimeString, secondDateTimeString);
    }

    public static int daysInMonth(int year, int month) {
        checkMonth(month);
        GregorianCalendar gregorianCalendar = new GregorianCalendar(year, month, 1);
        int daysInMonth = gregorianCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return daysInMonth;
    }

    public static Date parseDate(String dateAsString) {
        if (dateAsString == null || dateAsString.isEmpty()) return null;

        try {
            return fromString(dateAsString, "dd.MM.yyyy");
        } catch (Exception ex1) {
            return sameDateWithOtherTime(fromLongInString(dateAsString), 0, 0, 0);
        }
    }

    public static Date parseDateTime(String dateAsString) {
        if (dateAsString == null || dateAsString.isEmpty()) return null;

        try {
            return fromString(dateAsString, "dd.MM.yyyy HH:mm:ss");
        } catch (Exception ex1) {
            return fromLongInString(dateAsString);
        }
    }


    private static void checkDateTime(int year, int month, int day, int hours, int minutes, int seconds) {
        checkDate(year, month, day);
        checkTime(hours, minutes, seconds);
    }

    private static void checkDate(int year, int month, int day) {
        checkMonth(month);
        checkDay(year, month, day);
    }

    private static void checkTime(int hours, int minutes, int seconds) {
        checkHour(hours);
        checkMinutes(minutes);
        checkSeconds(seconds);
    }

    private static void checkMonth(int month) {
        if (month < 1 || month > 12) throw new IllegalArgumentException("Illegal month value (" + month + ").");
    }

    private static void checkDay(int year, int month, int day) {
        int daysInMonth = daysInMonth(year, month);
        if (day < 1 || day > daysInMonth)
            throw new IllegalArgumentException("Illegal day value (" + day + ") for year (" + year + ") month (" + month + ").");
    }

    private static void checkHour(int hours) {
        if (hours < 0 || hours > 23) throw new IllegalArgumentException("Illegal hours value (" + hours + ").");
    }

    private static void checkMinutes(int minutes) {
        if (minutes < 0 || minutes > 59) throw new IllegalArgumentException("Illegal minutes value (" + minutes + ").");
    }

    private static void checkSeconds(int seconds) {
        if (seconds < 0 || seconds > 59) throw new IllegalArgumentException("Illegal seconds value (" + seconds + ").");
    }

    public static AbstractMap.SimpleEntry<Integer, Integer> getNextMonth(int year, int month) {
        if (month < 1 || month > 12) throw new IllegalArgumentException("Illegal month value (" + month + ").");
        if (year < 1) throw new IllegalArgumentException("Illegal year value (" + year + ").");

        if (month == 12) return new AbstractMap.SimpleEntry<>(year + 1, 1);
        return new AbstractMap.SimpleEntry<>(year, month + 1);
    }
}
