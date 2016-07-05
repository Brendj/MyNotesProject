/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 24.03.16
 * Time: 12:15
 */

public class InteractiveReport implements AbstractToElement, SectionRequest {
    public static final String SECTION_NAME="InteractiveReportData";

    private List<InteractiveReportItem> items;

    public InteractiveReport(Node interactiveReportNode) {
        this.items = new ArrayList<InteractiveReportItem>();

        Node itemNode = interactiveReportNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("IRD")) {
                InteractiveReportItem item = InteractiveReportItem.build(itemNode);
                items.add(item);
            }
            itemNode = itemNode.getNextSibling();
        }
    }

    public InteractiveReport(List<InteractiveReportItem> items) {
        this.items = items;
    }

    public InteractiveReport() {
    }

    public List<InteractiveReportItem> getItems() {
        return items;
    }

    public void setItems(List<InteractiveReportItem> items) {
        this.items = items;
    }

    @Override
    public Element toElement(Document document) throws Exception {
        return null;
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }
}
