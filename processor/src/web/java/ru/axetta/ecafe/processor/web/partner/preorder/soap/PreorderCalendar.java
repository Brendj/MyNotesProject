/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by i.semenov on 22.05.2018.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PreorderCalendar")
public class PreorderCalendar {
    @XmlElement(name = "item")
    private List<PreorderCalendarItem> items;

    public PreorderCalendar() {
        items = new ArrayList<PreorderCalendarItem>();
    }

    public List<PreorderCalendarItem> getItems() {
        return items;
    }

    public void setItems(List<PreorderCalendarItem> items) {
        this.items = items;
    }
}
