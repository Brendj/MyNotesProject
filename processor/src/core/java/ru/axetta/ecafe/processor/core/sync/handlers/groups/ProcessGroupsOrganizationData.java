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

       // Set<String> parentGroupNameSet = new HashSet<String>();

        for (ProcessGroupsOrganizationDataItem item : items) {
            if (item.getMiddleGroup() != null || item.getParentGroupName() != null) {
                if (item.getMiddleGroup() == true) {
                    element.appendChild(item.toSubGroupElement(document));
                } else {
                    element.appendChild(item.toElement(document));
                }
            }
        }

      /*  Map<String, List<ProcessGroupsOrganizationDataItem>> parentGroupNameDataMap = new HashMap<String, List<ProcessGroupsOrganizationDataItem>>();

        for (String parentGroupName : parentGroupNameSet) {
            parentGroupNameDataMap.put(parentGroupName, new LinkedList<ProcessGroupsOrganizationDataItem>());
        }

        for (ProcessGroupsOrganizationDataItem item : items) {
            if (item.getMiddleGroup() != null || item.getParentGroupName() != null) {
                parentGroupNameDataMap.get(item.getParentGroupName()).addAll(items);
            }
        }*/

        return element;
    }

    public List<ProcessGroupsOrganizationDataItem> getItems() {
        return items;
    }

    public void setItems(List<ProcessGroupsOrganizationDataItem> items) {
        this.items = items;
    }

}
