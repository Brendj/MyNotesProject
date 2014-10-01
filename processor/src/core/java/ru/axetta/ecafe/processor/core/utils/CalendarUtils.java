/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.apache.commons.lang.StringUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
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

    //private final static TimeZone localTimeZone = RuntimeContext.getInstance().getLocalTimeZone(null);
    private final static TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
    private final static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private final static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private final static SimpleDateFormat dateShortFormat = new SimpleDateFormat("dd.MM.yy");
    private final static SimpleDateFormat dayInWeekFormat = new SimpleDateFormat("EE", new Locale("ru"));
    public final static Date AFTER_DATE = getAfterDate();

    public static XMLGregorianCalendar getXMLGregorianCalendarByDate(Date date) throws DatatypeConfigurationException {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    }

    public static XMLGregorianCalendar getXMLGregorianCalendar() throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar();
    }

    private static Date getAfterDate(){
        Calendar date = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        date.set(Calendar.YEAR, 2200);
        // next day
        return date.getTime();
    }

    public static Date parseFullDateTimeWithLocalTimeZone(String s) throws Exception {
        dateTimeFormat.setTimeZone(RuntimeContext.getInstance().getLocalTimeZone(null));
        if(StringUtils.isEmpty(s)){
            return null;
        } else {
            Date date = dateTimeFormat.parse(s);
            if(date.after(AFTER_DATE)) {
                throw new Exception("Не верно введена дата");
            }
            return date;
        }
    }

    public static String toStringFullDateTimeWithLocalTimeZone(Date dateTime) {
        try {dateTimeFormat.setTimeZone(RuntimeContext.getInstance().getLocalTimeZone(null));} catch (Exception ignore) {}
        return dateTimeFormat.format(dateTime);
    }

    public static boolean betweenDate(Date createDate, Date generateBeginTime, Date generateEndTime) {
        return createDate.before(generateEndTime) && createDate.after(generateBeginTime);
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

    public static Calendar truncateToMonth(Date date) {
        GregorianCalendar calendar = new GregorianCalendar(RuntimeContext.getInstance().getLocalTimeZone(null));
        calendar.setTime (date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar;
    }

    public static void truncateToDayOfMonth(Calendar calendar) {
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
    }

    public static void endOfDay(Calendar calendar) {
        calendar.set(Calendar.MILLISECOND, 999);
        calendar.set(Calendar.MILLISECOND, 999);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
    }

    public static Date truncateToDayOfMonth(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        truncateToDayOfMonth(c);
        return c.getTime();
    }

    public static Date endOfDay(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        endOfDay(c);
        return c.getTime();
    }
    public static Date truncateToDayOfMonthAndAddDay(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        c.add(Calendar.HOUR, 24);
        truncateToDayOfMonth(c);
        return c.getTime();
    }

    public static Date parseDate(String s) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setTimeZone(utcTimeZone);
        if (StringUtils.isEmpty(s)) {
            return null;
        } else {
            Date date = dateFormat.parse(s);
            if (date.after(AFTER_DATE)) {
                throw new Exception("Не верно введена дата");
            }
            return date;
        }
    }

    public static String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setTimeZone(RuntimeContext.getInstance().getLocalTimeZone(null));
        return dateFormat.format(date);
    }

    public static Date parseShortDate(String s) throws Exception {
        if(StringUtils.isEmpty(s)){
            return null;
        } else {
            Date date = dateShortFormat.parse(s);
            if(date.after(AFTER_DATE)) {
                throw new Exception("Не верно введена дата");
            }
            return date;
        }
    }

    public static Date parseTime(String s) throws Exception {
        if(StringUtils.isEmpty(s)){
            return null;
        } else {
            Date date = timeFormat.parse(s);
            if(date.after(AFTER_DATE)) {
                throw new Exception("Не верно введена дата");
            }
            return date;
        }
    }

    public static String timeToString(Date date) {
        return timeFormat.format(date);
    }

    public static Date parseDayInWeek(String validTime) throws ParseException {
        return timeFormat.parse(validTime);
    }

    public static String dayInWeekToString(Long timeMillis) {
        return dayInWeekFormat.format(timeMillis);
    }

    public static String dayInWeekToString(Date date) {
        return dayInWeekFormat.format(date);
    }

    public static String dateShortToString(Date date) {
        return dateShortFormat.format(date);
    }


    public static String dateTimeToString(Date date) {
        return dateTimeFormat.format(date);
    }

    public static Date getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        truncateToMonth(calendar);
        return calendar.getTime();
    }

    public static long getTimeFirstDayOfMonth(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        truncateToMonth(calendar);
        return calendar.getTimeInMillis();
    }

    public static Date getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        truncateToMonth(calendar);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
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

    // Возвращает дату последнего дня месяца для заданных года и месяца.
    // Например, для декабря 2012 года вернет дату от "31.12.2012".
    public static Date getDateOfLastDay(int year, int month) {
        Calendar calendar = new GregorianCalendar(year, month - 1, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, daysInMonth);
        return calendar.getTime();
    }

    public static Boolean isDateEqLtCurrentDate(Date thisDate){
        return (thisDate!=null && System.currentTimeMillis()<=thisDate.getTime());
    }

    public static Date[] getCurrentWeekBeginAndEnd(Date date) {
        Date[] res = new Date[2];
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        res[0] = c.getTime();
        c = (Calendar) c.clone();
        c.add(Calendar.DAY_OF_YEAR, 6);
        res[1] = c.getTime();
        return res;
    }

    public static DateFormat getDateFormatLocal() {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        df.setTimeZone(RuntimeContext.getInstance().getLocalTimeZone(null));
        return df;
    }

    public static boolean isWorkingDate(Date date) {
        int day = getDayOfMonth(date);
        return day != Calendar.SUNDAY;
    }

    public static Date calculateYesterdayStart(Calendar calendar, Date scheduledFireTime) {
        calendar.setTime(scheduledFireTime);
        CalendarUtils.truncateToDayOfMonth(calendar);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

    public static Date calculateTodayStart(Calendar calendar, Date scheduledFireTime) {
        calendar.setTime(scheduledFireTime);
        CalendarUtils.truncateToDayOfMonth(calendar);
        return calendar.getTime();
    }

    public static Date calculateMinusOneDay(Calendar calendar, Date endTime) {
        calendar.setTime(endTime);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

    public static Date calculatePlusOneDay(Calendar calendar, Date endTime) {
        calendar.setTime(endTime);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static Date calculateLastMonthFirstDay(Calendar calendar, Date scheduledFireTime) {
        calendar.setTime(scheduledFireTime);
        calendar.add(Calendar.MONTH, -1);
        CalendarUtils.truncateToMonth(calendar);
        return calendar.getTime();
    }

    public static Date calculateCurrentMonthFirstDay(Calendar calendar, Date scheduledFireTime) {
        calendar.setTime(scheduledFireTime);
        CalendarUtils.truncateToMonth(calendar);
        return calendar.getTime();
    }

    public static Date calculatePlusOneMonth(Calendar calendar, Date endTime) {
        calendar.setTime(endTime);
        calendar.add(Calendar.MONTH, 1);
        return calendar.getTime();
    }

    private CalendarUtils() {}

    //Сравнивает часы
    public static boolean timeEquals(String one, String two) {
        int oneTime,twoTime;
        oneTime = Integer.valueOf(one.replace(":",""));
        twoTime = Integer.valueOf(two.replace(":",""));
        return oneTime > twoTime;
    }
}