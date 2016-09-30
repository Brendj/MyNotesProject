/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.groups;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.sync.ResultOperation;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * User: akmukov
 * Date: 28.07.2016
 */
public class ResProcessGroupsOrganization implements AbstractToElement {
    private List<ResProcessGroupsOrganizationItem> items;
    private ResultOperation resultOperation;

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResGroupsOrganization");
        if (resultOperation != null && !resultOperation.isSuccess()) {
            XMLUtils.setAttributeIfNotNull(element, "Error", resultOperation.getMessage());
        }
        if (items != null) {
            for (ResProcessGroupsOrganizationItem item : items) {
                element.appendChild(item.toElement(document));
            }
        }
        return element;
    }

    public List<ResProcessGroupsOrganizationItem> getItems() {
        return items;
    }

    public void setItems(List<ResProcessGroupsOrganizationItem> items) {
        this.items = items;
    }

    public void setErrorResult(ResultOperation resultOperation) {
        this.resultOperation = resultOperation;
    }
}
