/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.complex.schedule;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 28.03.16
 * Time: 13:24
 * To change this template use File | Settings | File Templates.
 */
public class ResComplexSchedules implements AbstractToElement {
    private List<ResComplexScheduleItem> items;

    public ResComplexSchedules() {

    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResComplexSchedule");
        for (ResComplexScheduleItem item : this.getItems()) {
            element.appendChild(item.toElement(document, "RCS"));
        }
        return element;
    }

    public List<ResComplexScheduleItem> getItems() {
        return items;
    }

    public void setItems(List<ResComplexScheduleItem> items) {
        this.items = items;
    }
}
