/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.clientgroup.managers;

import ru.axetta.ecafe.processor.core.persistence.ClientGroupManager;
import ru.axetta.ecafe.processor.core.sync.ResultOperation;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * User: akmukov
 * Date: 04.04.2016
 */
public class ResClientgroupManagerItem {

    private String clientGroupName;
    private Long idOfClient;
    private Long orgOwner;
    private Long version;
    private Integer deleteState;
    private ResultOperation result;

    public ResClientgroupManagerItem(ClientGroupManager item, ResultOperation resultOperation) {
        this.clientGroupName = item.getClientGroupName();
        this.idOfClient = item.getIdOfClient();
        this.orgOwner = item.getOrgOwner();
        this.version = item.getVersion();
        this.result = resultOperation;
        this.deleteState = item.isDeleted()?1:0;
    }


    public Node toElement(Document document, String elementName) {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "GroupName", clientGroupName);
        XMLUtils.setAttributeIfNotNull(element, "IdOfClient", idOfClient);
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "V", version);
        if (isDeleted()) {
            XMLUtils.setAttributeIfNotNull(element, "D", deleteState);
        }
        if (this.result != null) {
            XMLUtils.setAttributeIfNotNull(element, "ResCode", result.getCode());
            XMLUtils.setAttributeIfNotNull(element, "ResultMessage", result.getMessage());
        }
        return element;
    }

    public boolean isDeleted() {
        return deleteState>0;
    }

}
