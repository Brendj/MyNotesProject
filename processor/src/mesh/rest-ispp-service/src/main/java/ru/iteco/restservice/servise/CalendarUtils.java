package ru.iteco.restservice.servise;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by nuc on 04.05.2021.
 */
public class CalendarUtils {
    private final static TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

    public static Date startOfDay(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        startOfDay(c);
        return c.getTime();
    }

    public static Date endOfDay(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        endOfDay(c);
        return c.getTime();
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

    public static Date getDateFromLong(Long value) {
        return CalendarUtils.startOfDayInUTC(new Date(value));
    }

    public static Date addDays(Date date, int nDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, nDays);
        return calendar.getTime();
    }

    public static String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        return dateFormat.format(date);
    }

    public static Date startOfDayInUTC(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTimeZone(utcTimeZone);
        c.setTime(date);
        startOfDay(c);
        return c.getTime();
    }

    public static int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static boolean betweenOrEqualDate(Date createDate, Date generateBeginTime, Date generateEndTime) {
        createDate = truncateToDayOfMonth(createDate);
        generateBeginTime = truncateToDayOfMonth(generateBeginTime);
        generateEndTime = truncateToDayOfMonth(generateEndTime);
        return ((createDate.before(generateEndTime) && createDate.after(generateBeginTime)) ||
                // сравниваем только начальную
                (createDate.getTime() == generateBeginTime.getTime()));
    }

    public static Date truncateToDayOfMonth(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        truncateToDayOfMonth(c);
        return c.getTime();
    }

    public static void truncateToDayOfMonth(Calendar calendar) {
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
    }

    public static Date addHours(Date date, int nHours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, nHours);
        return calendar.getTime();
    }

    public static Date parseDate(String s) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setTimeZone(utcTimeZone);
        if (StringUtils.isEmpty(s)) {
            return null;
        } else {
            return dateFormat.parse(s);
        }
    }

    public static boolean betweenDate(Date createDate, Date generateBeginTime, Date generateEndTime) {
        return createDate.before(generateEndTime) && createDate.after(generateBeginTime);
    }

    public static boolean isWorkDateWithoutParser(boolean isSixWorkWeek, Date date) {
        int day = getDayOfWeek(date);
        return (isSixWorkWeek ? (day != Calendar.SUNDAY) : (day != Calendar.SUNDAY && day != Calendar.SATURDAY));
    }
}
