/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by i.semenov on 14.06.2018.
 */
public class MenusCalendarDate {
    private Long idOfMenusCalendarDate;
    private MenusCalendar menusCalendar;
    private Date date;
    private Boolean isWeekend;
    private String comment;

    public MenusCalendarDate() {

    }

    public Long getIdOfMenusCalendarDate() {
        return idOfMenusCalendarDate;
    }

    public void setIdOfMenusCalendarDate(Long idOfMenusCalendarDate) {
        this.idOfMenusCalendarDate = idOfMenusCalendarDate;
    }

    public MenusCalendar getMenusCalendar() {
        return menusCalendar;
    }

    public void setMenusCalendar(MenusCalendar menusCalendar) {
        this.menusCalendar = menusCalendar;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getIsWeekend() {
        return isWeekend;
    }

    public void setIsWeekend(Boolean weekend) {
        isWeekend = weekend;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
