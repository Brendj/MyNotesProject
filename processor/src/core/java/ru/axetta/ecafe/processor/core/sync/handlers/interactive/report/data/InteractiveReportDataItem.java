/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 21.03.16
 * Time: 10:57
 */

public class InteractiveReportDataItem extends AbstractToElement {

    private List<InteractiveReportDataItem> items;

    private final long idOfRecord;
    private final String value;

    public InteractiveReportDataItem(String value, long idOfRecord) {
        this.value = value;
        this.idOfRecord = idOfRecord;
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("IRD");
        element.setAttribute("Id", Long.toString(idOfRecord));
        element.setAttribute("Value", value);
        return element;
    }

    public List<InteractiveReportDataItem> getItems() {
        return items;
    }

    public void setItems(List<InteractiveReportDataItem> items) {
        this.items = items;
    }

    public long getIdOfRecord() {
        return idOfRecord;
    }

    public String getValue() {
        return value;
    }
}
