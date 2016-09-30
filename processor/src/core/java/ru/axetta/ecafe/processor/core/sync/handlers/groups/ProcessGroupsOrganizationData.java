/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.groups;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * User: akmukov
 * Date: 28.07.2016
 */
public class ProcessGroupsOrganizationData implements AbstractToElement {

    private List<ProcessGroupsOrganizationDataItem> items = new ArrayList<ProcessGroupsOrganizationDataItem>();

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("GroupsOrganization");
        for (ProcessGroupsOrganizationDataItem item : items) {
            if (item.getMiddleGroup() != null || item.getParentGroupName() != null) {
                if (item.getMiddleGroup() == true) {
                    element.appendChild(item.toSubGroupElement(document));
                } else {
                    element.appendChild(item.toElement(document));
                }
            }
        }
        return element;
    }

    public List<ProcessGroupsOrganizationDataItem> getItems() {
        return items;
    }

    public void setItems(List<ProcessGroupsOrganizationDataItem> items) {
        this.items = items;
    }

}
