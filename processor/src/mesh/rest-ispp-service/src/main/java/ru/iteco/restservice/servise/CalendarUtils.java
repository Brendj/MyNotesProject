package ru.iteco.restservice.servise;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by nuc on 04.05.2021.
 */
public class CalendarUtils {
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
}
