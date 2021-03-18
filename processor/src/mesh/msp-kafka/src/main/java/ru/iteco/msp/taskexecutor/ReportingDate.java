/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.taskexecutor;

import java.util.Calendar;
import java.util.Date;

public class ReportingDate {
    private Date beginPeriod;
    private Date endPeriod;
    private final Calendar calendar;
    private final Calendar controlCalendar;

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
        calendar.add(Calendar.HOUR, 12);
        endPeriod = calendar.getTime();

        controlCalendar = Calendar.getInstance();
        controlCalendar.set(Calendar.DAY_OF_MONTH, 1);
        controlCalendar.set(Calendar.MILLISECOND, 0);
        controlCalendar.set(Calendar.SECOND, 0);
        controlCalendar.set(Calendar.MINUTE, 0);
        controlCalendar.set(Calendar.HOUR_OF_DAY, 0);

        controlCalendar.add(Calendar.MILLISECOND, -1);
    }

    public ReportingDate getNext() {

        beginPeriod = getBegin();
        endPeriod = getEnd();

        if(beginPeriod.after(controlCalendar.getTime())){
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
        calendar.add(Calendar.HOUR, 12);

        return calendar.getTime();
    }

    public Date getBeginPeriod() {
        return beginPeriod;
    }

    public Date getEndPeriod() {
        return endPeriod;
    }
}
