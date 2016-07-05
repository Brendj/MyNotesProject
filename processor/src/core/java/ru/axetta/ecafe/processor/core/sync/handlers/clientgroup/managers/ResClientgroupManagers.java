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
public class ResClientgroupManagers implements AbstractToElement {
    private List<ResClientgroupManagerItem> items = new ArrayList<ResClientgroupManagerItem>();

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResGroupManagers");
        for (ResClientgroupManagerItem item : items) {
            element.appendChild(item.toElement(document, "RGMI"));
        }
        return element;
    }

    public List<ResClientgroupManagerItem> getItems() {
        return items;
    }

    public void setItems(List<ResClientgroupManagerItem> items) {
        this.items = items;
    }

    public void addItem(ClientGroupManager item, int resCode, String resultMessage) {
        ResClientgroupManagerItem resItem = new ResClientgroupManagerItem(item,
                new ResultOperation(resCode, resultMessage));
        items.add(resItem);
    }

}
