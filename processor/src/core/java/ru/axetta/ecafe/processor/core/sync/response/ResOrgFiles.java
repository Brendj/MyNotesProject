/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.sync.request.OrgFilesRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class ResOrgFiles implements AbstractToElement {
    private List<ResOrgFilesItem> items;
    final private OrgFilesRequest.Operation operation;

    public ResOrgFiles(OrgFilesRequest.Operation operation) {
        this.operation = operation;
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResOrgFile");
        XMLUtils.setAttributeIfNotNull(element, "operation", operation.getDescription());
        for (ResOrgFilesItem item : this.getItems()) {
            element.appendChild(item.toElement(document, "ROF"));
        }
        return element;
    }

    public List<ResOrgFilesItem> getItems() {
        return items;
    }

    public void setItems(List<ResOrgFilesItem> items) {
        this.items = items;
    }

    public OrgFilesRequest.Operation getOperation() {
        return operation;
    }
}
