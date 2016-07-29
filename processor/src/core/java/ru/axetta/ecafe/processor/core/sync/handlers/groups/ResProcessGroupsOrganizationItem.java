/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.groups;

import ru.axetta.ecafe.processor.core.sync.ResultOperation;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * User: akmukov
 * Date: 28.07.2016
 */
public class ResProcessGroupsOrganizationItem {
    private String name;
    private Long version;
    private ResultOperation resultOperation;

    public ResProcessGroupsOrganizationItem(String name, long version, ResultOperation resultOperation) {
        this.name = name;
        this.version = version;
        this.resultOperation = resultOperation;
        if (this.resultOperation == null) {
            this.resultOperation = new ResultOperation();
        }
    }

    public String getName() {
        return name;
    }

    public Long getVersion() {
        return version;
    }

    public ResultOperation getResultOperation() {
        return resultOperation;
    }

    public Node toElement(Document document) {
        Element element = document.createElement("RCG");
        XMLUtils.setAttributeIfNotNull(element,"Name", name);
        if (resultOperation.isSuccess()) {
            XMLUtils.setAttributeIfNotNull(element,"V", Long.toString(version));
        } else {
            XMLUtils.setAttributeIfNotNull(element,"Error", resultOperation.getMessage());
        }
        XMLUtils.setAttributeIfNotNull(element,"Res", Integer.toString(resultOperation.getCode()));
        return element;
    }
}
