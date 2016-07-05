/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.clientgroup.managers;

import ru.axetta.ecafe.processor.core.persistence.ClientGroupManager;
import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.sync.ResultOperation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.List;

/**
 * User: akmukov
 * Date: 04.04.2016
 */
public class ClientgroupManagerData implements AbstractToElement {
    private final ResultOperation resultOperation;
    private List<ResClientgroupManagerItem> items = new ArrayList<ResClientgroupManagerItem>();

    public ClientgroupManagerData(ResultOperation resultOperation) {
        this.resultOperation = resultOperation;
    }

    public List<ResClientgroupManagerItem> getItems() {
        return items;
    }
    public void setItems(List<ResClientgroupManagerItem> items) {
        this.items = items;
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("GroupManagers");
        for (ResClientgroupManagerItem item : this.getItems()) {
            element.appendChild(item.toElement(document, "GMI"));
        }
        return element;
    }

    public void addItem(ClientGroupManager clientGroupManager) {
        if (clientGroupManager == null) return;
        ResClientgroupManagerItem item = new ResClientgroupManagerItem(clientGroupManager,
                new ResultOperation(0, null));
        items.add(item);
    }
}
