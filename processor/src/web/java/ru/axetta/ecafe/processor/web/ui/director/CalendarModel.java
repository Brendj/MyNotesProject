/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.director;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.richfaces.model.CalendarDataModel;
import org.richfaces.model.CalendarDataModelItem;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

public class CalendarModel implements CalendarDataModel {
    private static final String OUT_OF_DATE_CLASS = "out-of-date";
    private Date startDate;
    private Date endDate;

    public CalendarModel() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Calendar localCalendar = RuntimeContext.getInstance()
                .getDefaultLocalCalendar((HttpSession) facesContext.getExternalContext().getSession(false));

        localCalendar.setTime(new Date());
        CalendarUtils.endOfDay(localCalendar);
        this.endDate = localCalendar.getTime();
        localCalendar.add(Calendar.DAY_OF_YEAR, -31);
        CalendarUtils.truncateToDayOfMonth(localCalendar);
        this.startDate = localCalendar.getTime();
    }

    public CalendarDataModelItem[] getData(Date[] dateArray) {
        CalendarDataModelItem[] modelItems = new CalendarModelItem[dateArray.length];
        Calendar current = GregorianCalendar.getInstance();
        Calendar startCalendar = GregorianCalendar.getInstance();
        Calendar endCalendar = GregorianCalendar.getInstance();
        startCalendar.setTime(startDate);
        endCalendar.setTime(endDate);
        for (int i = 0; i < dateArray.length; i++) {
            current.setTime(dateArray[i]);
            CalendarModelItem modelItem = new CalendarModelItem();
            if (current.compareTo(startCalendar) < 0) {
                modelItem.setEnabled(false);
                modelItem.setStyleClass(OUT_OF_DATE_CLASS);
            } else if (current.compareTo(endCalendar) >= 0) {
                modelItem.setEnabled(false);
                modelItem.setStyleClass(OUT_OF_DATE_CLASS);
            } else {
                modelItem.setEnabled(true);
                modelItem.setStyleClass("");
            }
            modelItems[i] = modelItem;
        }

        return modelItems;
    }

    public Object getToolTip(Date date) {
        return null;
    }

    public void updateStartDate(Date startDate) {
        this.startDate = CalendarUtils.truncateToDayOfMonth(startDate);
    }

    public void updateEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
