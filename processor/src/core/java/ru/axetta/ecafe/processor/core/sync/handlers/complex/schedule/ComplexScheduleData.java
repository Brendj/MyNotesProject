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
 * Time: 12:56
 * To change this template use File | Settings | File Templates.
 */
public class ComplexScheduleData implements AbstractToElement {
    private List<ComplexScheduleItem> items;

    public ComplexScheduleData() {

    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ComplexSchedule");
        for (ComplexScheduleItem item : this.getItems()) {
            element.appendChild(item.toElement(document, "CS"));
        }
        return element;
    }

    public List<ComplexScheduleItem> getItems() {
        return items;
    }

    public void setItems(List<ComplexScheduleItem> items) {
        this.items = items;
    }
}
