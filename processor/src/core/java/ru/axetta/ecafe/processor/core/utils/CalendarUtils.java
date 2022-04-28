/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SubscriberFeedingSettingSettingValue;

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
    private final static ThreadLocal<SimpleDateFormat> dateTimeFormat = new ThreadLocal<SimpleDateFormat>(){
        @Override protected SimpleDateFormat initialValue() { return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"); }
    };
    private final static ThreadLocal<SimpleDateFormat> timeFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override protected SimpleDateFormat initialValue() { return new SimpleDateFormat("HH:mm"); }
    };
    private final static ThreadLocal<SimpleDateFormat> timeUnderscoreFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override protected SimpleDateFormat initialValue() { return new SimpleDateFormat("HH_mm"); }
    };
    private final static ThreadLocal<SimpleDateFormat> timeUnderscoreExtendedFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override protected SimpleDateFormat initialValue() { return new SimpleDateFormat("HH_mm_ss"); }
    };
    private final static ThreadLocal<SimpleDateFormat> timeClassicFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override protected SimpleDateFormat initialValue() { return new SimpleDateFormat("HH:mm:ss"); }
    };
    private final static ThreadLocal<SimpleDateFormat> dateShortdd_mmFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override protected SimpleDateFormat initialValue() { return new SimpleDateFormat("dd.MM"); }
    };
    private final static ThreadLocal<SimpleDateFormat> dateShortFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override protected SimpleDateFormat initialValue() { return new SimpleDateFormat("dd.MM.yy"); }
    };
    private final static ThreadLocal<SimpleDateFormat> dateShortUnderscoreFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override protected SimpleDateFormat initialValue() { return new SimpleDateFormat("yy_MM_dd"); }
    };
    private final static ThreadLocal<SimpleDateFormat> dateShortFormatFullYear = new ThreadLocal<SimpleDateFormat>() {
        @Override protected SimpleDateFormat initialValue() { return new SimpleDateFormat("dd.MM.yyyy"); }
    };
    private final static ThreadLocal<SimpleDateFormat> MMMMYYYY = new ThreadLocal<SimpleDateFormat>() {
        @Override protected SimpleDateFormat initialValue() { return new SimpleDateFormat("MMMM yyyy"); }
    };
    private final static ThreadLocal<SimpleDateFormat> dayInWeekFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override protected SimpleDateFormat initialValue() { return new SimpleDateFormat("EE", new Locale("ru")); }
    };
    public final static Date AFTER_DATE = getAfterDate();
    public final static Long FIFTY_YEARS_MILLIS = 1577846300000L;

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
        SimpleDateFormat safeDateTimeFormat = dateTimeFormat.get();
        safeDateTimeFormat.setTimeZone(RuntimeContext.getInstance().getLocalTimeZone(null));
        if(StringUtils.isEmpty(s)){
            return null;
        } else {
            Date date = safeDateTimeFormat.parse(s);
            if(date.after(AFTER_DATE)) {
                throw new Exception("Не верно введена дата");
            }
            return date;
        }
    }

    public static XMLGregorianCalendar toXmlDateTimeWithTimezoneOffset(Date date) {
        try {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(date);
            XMLGregorianCalendar xc = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH),
                    RuntimeContext.getInstance().getDefaultLocalTimeZone(null).getRawOffset()/60000);
            xc.setTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
            return xc;
        }catch (Exception ignore) { return null;}
    }

    public static String toStringFullDateTimeWithLocalTimeZone(Date dateTime) {
        SimpleDateFormat safeDateTimeFormat = dateTimeFormat.get();
        try {safeDateTimeFormat.setTimeZone(RuntimeContext.getInstance().getLocalTimeZone(null));} catch (Exception ignore) {}
        return safeDateTimeFormat.format(dateTime);
    }

    public static boolean betweenDate(Date createDate, Date generateBeginTime, Date generateEndTime) {
        return createDate.before(generateEndTime) && createDate.after(generateBeginTime);
    }

    public static boolean betweenOrEqualDate(Date createDate, Date generateBeginTime, Date generateEndTime) {
        createDate = truncateToDayOfMonth(createDate);
        generateBeginTime = truncateToDayOfMonth(generateBeginTime);
        generateEndTime = truncateToDayOfMonth(generateEndTime);
        return ((createDate.before(generateEndTime) && createDate.after(generateBeginTime)) ||
                // сравниваем только начальную
                (createDate.getTime() == generateBeginTime.getTime()));
    }

    public static boolean betweenMoreMonth(Date startDate, Date endDate) {
        return 31 < getDifferenceInDays(startDate, endDate);
    }

    public static String toStringFullDateTimeWithUTCTimeZone(Date dateTime) throws ParseException {
        SimpleDateFormat safeDateTimeFormat = dateTimeFormat.get();
        safeDateTimeFormat.setTimeZone(utcTimeZone);
        return safeDateTimeFormat.format(dateTime);
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
        calendar.setTime(date);
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

    public static void startOfDay(Calendar calendar) {
        calendar.set(Calendar.MILLISECOND, 0);
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

    public static Date endOfDay(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        endOfDay(c);
        return c.getTime();
    }

    public static Date startOfDay(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        startOfDay(c);
        return c.getTime();
    }

    public static Date startOfDayInUTC(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTimeZone(utcTimeZone);
        c.setTime(date);
        startOfDay(c);
        return c.getTime();
    }

    public static Date dateInUTC() {
        GregorianCalendar c = new GregorianCalendar();
        int offset = c.get(Calendar.ZONE_OFFSET);
        return new Date(new Date().getTime()+offset);
    }

    public static Date convertdateInUTC(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        int offset = c.get(Calendar.ZONE_OFFSET);
        return new Date(date.getTime()-offset);
    }

    public static Date convertdateInLocal(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        int offset = c.get(Calendar.ZONE_OFFSET);
        return new Date(date.getTime()+offset);
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

    public static Date parseDateWithDayTime(String s) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
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
            SimpleDateFormat safeDateShortFormat = dateShortFormat.get();
            Date date = safeDateShortFormat.parse(s);
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
            SimpleDateFormat safeTimeFormat = timeFormat.get();
            Date date = safeTimeFormat.parse(s);
            if(date.after(AFTER_DATE)) {
                throw new Exception("Не верно введена дата");
            }
            return date;
        }
    }
    // new SimpleDateFormat("HH:mm");
    public static String timeToString(Date date) {
        SimpleDateFormat safeTimeFormat = timeFormat.get();
        return safeTimeFormat.format(date);
    }
    /**
     *   @return  new SimpleDateFormat("HH:mm");
     * */
    public static String timeToString(Long date) {
        SimpleDateFormat safeTimeFormat = timeFormat.get();
        return safeTimeFormat.format(new Date(date));
    }
    /**
     *   @return  new SimpleDateFormat("HH_mm");
     * */
    public static String formatTimeUnderscoreToString(Long date) {
        SimpleDateFormat safeTimeUnderscoreFormat = timeUnderscoreFormat.get();
        return safeTimeUnderscoreFormat.format(new Date(date));
    }

    public static String formatTimeUnderscoreExtendedToString(Long date) {
        SimpleDateFormat safeTimeUnderscoreExtendedFormat = timeUnderscoreExtendedFormat.get();
        return safeTimeUnderscoreExtendedFormat.format(new Date(date));
    }

    public static String formatTimeClassicToString(Long date) {
        SimpleDateFormat safeTimeUnderscoreExtendedFormat = timeClassicFormat.get();
        return safeTimeUnderscoreExtendedFormat.format(new Date(date));
    }

    public static Date parseDayInWeek(String validTime) throws ParseException {
        SimpleDateFormat safeTimeFormat = timeFormat.get();
        return safeTimeFormat.parse(validTime);
    }

    public static String dayInWeekToString(Long timeMillis) {
        SimpleDateFormat safeDayInWeekFormat = dayInWeekFormat.get();
        return safeDayInWeekFormat.format(timeMillis);
    }

    public static String dayInWeekToString(Date date) {
        SimpleDateFormat safeDayInWeekFormat = dayInWeekFormat.get();
        return safeDayInWeekFormat.format(date);
    }

    public static String dateShortToString(long date) {
        SimpleDateFormat safeDateShortFormat = dateShortFormat.get();
        return safeDateShortFormat.format(new Date(date));
    }
    public static String dateShortdd_mmToString(long date) {
        SimpleDateFormat safeDateShortdd_mmFormat = dateShortdd_mmFormat.get();
        return safeDateShortdd_mmFormat.format(new Date(date));
    }
    public static String dateShortToString(Date date) {
        SimpleDateFormat safeDateShortFormat = dateShortFormat.get();
        return safeDateShortFormat.format(date);
    }
    public static String dateShotFullYearToString(Date date) {
        SimpleDateFormat safeDateShortFormat = dateShortFormatFullYear.get();
        return safeDateShortFormat.format(date);
    }
    /**
    * @return new SimpleDateFormat("yy_MM_dd");
    * */
    public static String formatToDateShortUnderscoreFormat(Date date) {
        SimpleDateFormat safeDateShortUnderscoreFormat = dateShortUnderscoreFormat.get();
        return safeDateShortUnderscoreFormat.format(date);
    }

    public static String dateShortToString(Calendar date) {
        SimpleDateFormat safeDateShortFormat = dateShortFormat.get();
        return safeDateShortFormat.format(date);
    }
    public static String dateShortToStringFullYear(Date date) {
        try {
            SimpleDateFormat safeDateShortFormatFullYear = dateShortFormatFullYear.get();
            return safeDateShortFormatFullYear.format(date);
        }
        catch (Exception e)
        {
            return "";
        }
    }
    public static String dateMMMMYYYYToString(Date date) {
        SimpleDateFormat safeMMMMYYYY = MMMMYYYY.get();
        return safeMMMMYYYY.format(date);
    }

    /**
     * @return  dd.MM.yyyy HH:mm:ss
     */
    public static String dateTimeToString(Date date) {
        SimpleDateFormat safeDateTimeFormat = dateTimeFormat.get();
        return safeDateTimeFormat.format(date);
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

    public static Date subOneDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
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

    public static Date addYear(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, n);
        return calendar.getTime();
    }

    public static Date addHours(Date date, int nHours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, nHours);
        return calendar.getTime();
    }

    public static int getDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDayOfMonth(Long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
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

    public static Date getFirstDayMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        truncateToMonth(calendar);
        return calendar.getTime();
    }

    public static Date getFirstDayOfPrevMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
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

    public static Integer getCurrentYear() {
        GregorianCalendar gc = new GregorianCalendar();
        return gc.get(Calendar.YEAR);
    }

    public static Integer getCurrentMonth() {
        GregorianCalendar gc = new GregorianCalendar();
        return gc.get(Calendar.MONTH);
    }

    public static DateFormat getDateFormatLocal() {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        df.setTimeZone(RuntimeContext.getInstance().getLocalTimeZone(null));
        return df;
    }

    public static DateFormat getDateTimeFormatLocal() {
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        timeFormat.setTimeZone(RuntimeContext.getInstance().getLocalTimeZone(null));
        return timeFormat;
    }

    public static boolean isWorkDate(SubscriberFeedingSettingSettingValue parser, Date date) {
        int day = getDayOfWeek(date);
        return (parser.isSixWorkWeek() ? (day != Calendar.SUNDAY) : (day != Calendar.SUNDAY && day != Calendar.SATURDAY));
    }

    public static boolean isWorkDateWithoutParser(boolean isSixWorkWeek, Date date) {
        int day = getDayOfWeek(date);
        return (isSixWorkWeek ? (day != Calendar.SUNDAY) : (day != Calendar.SUNDAY && day != Calendar.SATURDAY));
    }

    public static boolean isWorkDateWithoutParserForPreorder(boolean isSixWorkWeek, Date date) {
        int day = getDayOfWeek(date);
        int day2 = getDayOfWeek(new Date());
        if (day2 == Calendar.THURSDAY && day == Calendar.SATURDAY) return false;
        return isWorkDateWithoutParser(isSixWorkWeek, date);
    }

    public static Date calculateNextWorkDate(SubscriberFeedingSettingSettingValue parser, Date date) {
        Date firstWorkDate = CalendarUtils.addOneDay(date);
        while (!isWorkDate(parser, firstWorkDate)) {
            firstWorkDate = CalendarUtils.addOneDay(firstWorkDate);
        }
        return firstWorkDate;
    }

    public static Date calculateNextWorkDateWithoutParser(Boolean isSixWorkWeek, Date date) {
        Date firstWorkDate = CalendarUtils.addOneDay(date);
        while (!isWorkDateWithoutParser(isSixWorkWeek, firstWorkDate)) {
            firstWorkDate = CalendarUtils.addOneDay(firstWorkDate);
        }
        return firstWorkDate;
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

    public static int getDifferenceInDays(Date data){
        Long begin = data.getTime();
        Long end = getLastDayOfMonth(data).getTime();
        Long current = new Date().getTime();
        if(end > current){
            end = current ;
        }
        return (int) (( end - begin ) / (1000 * 60 * 60 * 24)) + 1 ;
    }

    public static int getDifferenceInDays(Date start, Date end){
        long temp = end.getTime() - start.getTime();
        return  (int) (temp / (1000 * 60 * 60 * 24)) + 1;
    }

    public static List<Integer> daysBetween( Date start, Date end ){
        Date startLocal = (Date) start.clone();
        int i = getDifferenceInDays(startLocal,end);
        List<Integer> result = new ArrayList<Integer>();
        for(int j = 1; j <= i ; j++){
            result.add(getDayOfMonth(startLocal));
            startLocal = addOneDay(startLocal);
        }
        return result;
    }

    public static List<Long> daysBetweenInMillis( Date start, Date end){
        Date startLocal = (Date) start.clone();
        int i = getDifferenceInDays(startLocal,end);
        List<Long> result = new ArrayList<Long>();
        for(int j = 1; j <= i ; j++){
            result.add(startLocal.getTime());
            startLocal = addOneDay(startLocal);
        }
        return result;
    }

    public static List<String> datesBetween(Date start, Date end, int format) {
        List<String> dates = new ArrayList<String>();

        Calendar c = Calendar.getInstance();
        c.setTime(start);
        while (c.getTimeInMillis() < end.getTime() ){
            if (format == 1)
                dates.add(dateShortToString(c.getTime()));
            if (format == 2)
                dates.add(dateShotFullYearToString(c.getTime()));
            c.add(Calendar.DATE, 1);
        }

        return dates;
    }


    public static Integer getMonthNumb(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH);
    }

    public static Date roundToBeginOfDay(Date startTime) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        if(calendar.get(Calendar.HOUR_OF_DAY)> 18){
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            truncateToDayOfMonth(calendar);
        }
        return calendar.getTime();
    }

    public static Date addMinute(Date date, int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, i);
        return calendar.getTime();
    }

    public static Date addSeconds(Date date, int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, i);
        return calendar.getTime();
    }

    public static Date addMilliseconds(Date date, int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MILLISECOND, i);
        return calendar.getTime();
    }

    public static String replaceMonthNameByGenitive(String date) {
        if (date.contains("Январь")) return date.replace("Январь", "Января");
        if (date.contains("Февраль")) return date.replace("Февраль", "Февраля");
        if (date.contains("Март")) return date.replace("Март", "Марта");
        if (date.contains("Апрель")) return date.replace("Апрель", "Апреля");
        if (date.contains("Май")) return date.replace("Май", "Мая");
        if (date.contains("Июнь")) return date.replace("Июнь", "Июня");
        if (date.contains("Июль")) return date.replace("Июль", "Июля");
        if (date.contains("Август")) return date.replace("Август", "Августа");
        if (date.contains("Сентябрь")) return date.replace("Сентябрь", "Сентября");
        if (date.contains("Октябрь")) return date.replace("Октябрь", "Октября");
        if (date.contains("Ноябрь")) return date.replace("Ноябрь", "Ноября");
        if (date.contains("Декабрь")) return date.replace("Декабрь", "Декабря");
        return date;
    }

    public static Calendar setHoursAndMinutes(Calendar calendar, int hours, int minutes) {
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Calendar setHoursAndMinutesAndSeconds(Calendar calendar, int hours, int minutes, int seconds) {
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, seconds);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static boolean isDateToday(Date date) {
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        if (today.get(Calendar.DATE) == dateCalendar.get(Calendar.DATE) &&
                today.get(Calendar.MONTH) == dateCalendar.get(Calendar.MONTH) &&
                today.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR)) {
            return true;
        }
        return false;
    }

    public static int getHourFromDate(Date date) {
        Long diff = date.getTime() - startOfDay(date).getTime();
        return new Long(diff / (1000*60*60)).intValue();
    }

    public static Calendar truncateToSecond(Date date) {
        GregorianCalendar calendar = new GregorianCalendar(RuntimeContext.getInstance().getLocalTimeZone(null));
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Boolean isCurrentDay(Date targetDay, Date currentDay){
        Date beginCurrentDay = startOfDay(currentDay);
        Date endOfCurrentDay = endOfDay(currentDay);
        return beginCurrentDay.getTime() <= targetDay.getTime() && targetDay.getTime() <= endOfCurrentDay.getTime();
    }

    public static boolean isWorkDateStringByFormat(String currentDate, String format) throws Exception{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = simpleDateFormat.parse(currentDate);
        return CalendarUtils.isWorkDateWithoutParser(true, date);
    }

    public static boolean isCurrentDayStringByFormat(Date targetDay, String stringCurrentDay, String dateFormat) throws Exception{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Date currentDay = simpleDateFormat.parse(stringCurrentDay);
        return isCurrentDay(targetDay, currentDay);
    }
}