/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.kafka;

import java.util.Calendar;
import java.util.Date;

public class ReportingDate {
    private Date beginPeriod;
    private Date endPeriod;
    private final Date finalDate;
    private final Calendar calendar;

    private static final int HOUR_PERIOD = 2;

    public ReportingDate(){
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MILLISECOND, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        beginPeriod = calendar.getTime();

        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.HOUR, HOUR_PERIOD);
        endPeriod = calendar.getTime();

        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.set(Calendar.DAY_OF_MONTH, 1);
        controlCalendar.set(Calendar.MILLISECOND, 0);
        controlCalendar.set(Calendar.SECOND, 0);
        controlCalendar.set(Calendar.MINUTE, 0);
        controlCalendar.set(Calendar.HOUR_OF_DAY, 0);

        controlCalendar.add(Calendar.MILLISECOND, -1);
        finalDate = controlCalendar.getTime();
    }

    public ReportingDate(Date start, Date end){
        calendar = Calendar.getInstance();
        calendar.setTime(start);
        calendar.set(Calendar.MILLISECOND, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        beginPeriod = calendar.getTime();

        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.HOUR, HOUR_PERIOD);
        endPeriod = calendar.getTime();

        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.setTime(end);
        controlCalendar.add(Calendar.DAY_OF_MONTH, 1);
        controlCalendar.set(Calendar.MILLISECOND, 0);
        controlCalendar.set(Calendar.SECOND, 0);
        controlCalendar.set(Calendar.MINUTE, 0);
        controlCalendar.set(Calendar.HOUR_OF_DAY, 0);

        finalDate = controlCalendar.getTime();
    }

    public ReportingDate getNext() {
        beginPeriod = getBegin();
        endPeriod = getEnd();

        if(beginPeriod.after(finalDate)){
            return null;
        }
        return this;
    }

    private Date getBegin(){
        calendar.set(Calendar.MILLISECOND, 1);
        return calendar.getTime();
    }

    private Date getEnd(){
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.HOUR, HOUR_PERIOD);

        return calendar.getTime();
    }

    public Date getBeginPeriod() {
        return beginPeriod;
    }

    public Date getEndPeriod() {
        return endPeriod;
    }
}
