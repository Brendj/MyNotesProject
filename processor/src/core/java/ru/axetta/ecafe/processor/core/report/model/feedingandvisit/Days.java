/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.feedingandvisit;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

/**
 * User: shamil
 * Date: 03.10.14
 * Time: 11:02
 */
public class Days {
    private long day;
    private int dayl;

    public Days(long day) {
        this.day = day;
        dayl = 2;
    }

    public long getDay() {
        return day;
    }

    public String getDayShort(){
        return CalendarUtils.dateShortdd_mmToString(day);
    }

    public void setDay(long day) {
        this.day = day;
    }

    public int getDayl() {
        return dayl;
    }

    public void setDayl(int dayl) {
        this.dayl = dayl;
    }
}
