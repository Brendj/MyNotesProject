/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menus.calendar;

import ru.axetta.ecafe.processor.core.persistence.MenusCalendarDate;
import ru.axetta.ecafe.processor.core.sync.BaseItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;

/**
 * Created by i.semenov on 14.06.2018.
 */
public class MenusCalendarDateItem extends BaseItem {
    private Long idOfMenusCalendarDate;
    private Date date;
    private Boolean isWeekend;
    private String comment;

    public MenusCalendarDateItem() {

    }

    public MenusCalendarDateItem(MenusCalendarDate menusCalendarDate) {
        this.date = menusCalendarDate.getDate();
        this.isWeekend = menusCalendarDate.getIsWeekend();
        this.comment = menusCalendarDate.getComment();
    }

    public MenusCalendarDateItem(Date date, Boolean isWeekend, String comment) {
        this.date = date;
        this.isWeekend = isWeekend;
        this.comment = comment;
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "Date", CalendarUtils.dateShortToStringFullYear(date));
        XMLUtils.setAttributeIfNotNull(element, "Comment", comment);
        XMLUtils.setAttributeIfNotNull(element, "IsWeekend", isWeekend);
        return element;
    }

    public static MenusCalendarDateItem build(Node itemNode) {
        StringBuilder errorMessage = new StringBuilder();

        String comment = XMLUtils.getAttributeValue(itemNode, "Comment");
        Date date = getDateValue(itemNode, "Date", errorMessage);
        String strIsWeekend = XMLUtils.getAttributeValue(itemNode, "IsWeekend");
        Boolean isWeekend = strIsWeekend == null ? false : strIsWeekend.equals("1");
        return new MenusCalendarDateItem(date, isWeekend, comment);
    }

    public Long getIdOfMenusCalendarDate() {
        return idOfMenusCalendarDate;
    }

    public void setIdOfMenusCalendarDate(Long idOfMenusCalendarDate) {
        this.idOfMenusCalendarDate = idOfMenusCalendarDate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getWeekend() {
        return isWeekend;
    }

    public void setWeekend(Boolean weekend) {
        isWeekend = weekend;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
