/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.groups;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.*;

/**
 * User: akmukov
 * Date: 28.07.2016
 */
public class ProcessGroupsOrganizationData implements AbstractToElement {

    private List<ProcessGroupsOrganizationDataItem> items = new ArrayList<ProcessGroupsOrganizationDataItem>();

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("GroupsOrganization");

        Map<String, List<ProcessGroupsOrganizationDataItem>> middleGroupsByGroup = new HashMap<String, List<ProcessGroupsOrganizationDataItem>>();
        List<ProcessGroupsOrganizationDataItem> allItems = new ArrayList<>();
        Map<String, Boolean> is6DaysForMainGroup = new HashMap<String, Boolean>();

        for (ProcessGroupsOrganizationDataItem item : items) {
            if (item.getMiddleGroup() != null || item.getParentGroupName() != null) {
                if (item.getMiddleGroup() == true) {
                    if (middleGroupsByGroup.containsKey(item.getParentGroupName())) {
                        middleGroupsByGroup.get(item.getParentGroupName()).add(item);
                    } else {
                        List<ProcessGroupsOrganizationDataItem> array = new ArrayList<ProcessGroupsOrganizationDataItem>();
                        array.add(item);
                        middleGroupsByGroup.put(item.getParentGroupName(), array);
                    }
                }
            } else {
                is6DaysForMainGroup.put(item.getName(), item.getIsSixDaysWorkWeek());
                allItems.add(item);
            }
        }

        Set<String> groupNamesSet = middleGroupsByGroup.keySet();

        //Убираем из списка групп те, у которых есть подгруппы т.к. они вместе с подгруппами запищутся далее отдельно
        for (ProcessGroupsOrganizationDataItem processGroupsOrganizationDataItem : allItems) {
            boolean haveMainGroup = false;
            for (String mainGroup : groupNamesSet) {
                if (processGroupsOrganizationDataItem.getName().equals(mainGroup)) {
                    haveMainGroup = true;
                    break;
                }
            }
            if (!haveMainGroup) {
                element.appendChild(processGroupsOrganizationDataItem.toElement(document));
            }
        }


        for (String groupName : groupNamesSet) {
            List<ProcessGroupsOrganizationDataItem> middleGroups = middleGroupsByGroup.get(groupName);
            Boolean is6DaysValue = is6DaysForMainGroup.get(groupName);
            element.appendChild(middleGroupsToElement(groupName, middleGroups, is6DaysValue, document));
        }

        return element;
    }

    private Node middleGroupsToElement(String groupName, List<ProcessGroupsOrganizationDataItem> middleGroups,
            Boolean is6DaysValue, Document document) {
        Long maxVersion = 0L;

        for (ProcessGroupsOrganizationDataItem groupsOrganizationDataItem : middleGroups) {
            if (maxVersion < groupsOrganizationDataItem.getVersion()) {
                maxVersion = groupsOrganizationDataItem.getVersion();
            }
        }

        Element element = document.createElement("CG");
        XMLUtils.setAttributeIfNotNull(element, "Name", groupName);
        XMLUtils.setAttributeIfNotNull(element, "Is6DaysWorkWeek", is6DaysValue);
        XMLUtils.setAttributeIfNotNull(element, "V", maxVersion);

        for (ProcessGroupsOrganizationDataItem groupsOrganizationData : middleGroups) {
            Element elementCmg = document.createElement("CMG");
            XMLUtils.setAttributeIfNotNull(elementCmg, "Name", groupsOrganizationData.getName());
            XMLUtils.setAttributeIfNotNull(elementCmg, "BindingToOrg", groupsOrganizationData.getBindingToOrg());
            XMLUtils.setAttributeIfNotNull(elementCmg, "Is6DaysWorkWeek",
                    groupsOrganizationData.getIsSixDaysWorkWeek());
            element.appendChild(elementCmg);
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
