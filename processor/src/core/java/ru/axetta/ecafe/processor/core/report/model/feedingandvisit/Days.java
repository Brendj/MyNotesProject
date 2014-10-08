/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.feedingandvisit;

/**
 * User: shamil
 * Date: 03.10.14
 * Time: 11:02
 */
public class Days {
    private int day;
    private int dayl;

    public Days(int day) {
        this.day = day;
        dayl = 2;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getDayl() {
        return dayl;
    }

    public void setDayl(int dayl) {
        this.dayl = dayl;
    }
}
