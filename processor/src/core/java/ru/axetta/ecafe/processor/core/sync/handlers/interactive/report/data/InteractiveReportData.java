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
 * Time: 10:56
 */

public class InteractiveReportData implements AbstractToElement {

    private List<InteractiveReportDataItem> items;

    public InteractiveReportData(List<InteractiveReportDataItem> items) {
        this.items = items;
    }

    public InteractiveReportData() {
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("InteractiveReportData");
        if (items != null && !items.isEmpty()) {
            for (InteractiveReportDataItem item : this.items) {
                element.appendChild(item.toElement(document));
            }
        }
        return element;
    }

    public List<InteractiveReportDataItem> getItems() {
        return items;
    }

    public void setItems(List<InteractiveReportDataItem> items) {
        this.items = items;
    }
}
