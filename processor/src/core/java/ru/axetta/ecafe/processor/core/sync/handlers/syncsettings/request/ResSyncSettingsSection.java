/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.LinkedList;
import java.util.List;

public class ResSyncSettingsSection implements AbstractToElement {
    private List<ResSyncSettingsItem> itemList = new LinkedList<>();

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResSyncSettings");
        for(ResSyncSettingsItem item : itemList){
            element.appendChild(item.toElement(document));
        }
        return element;
    }

    public List<ResSyncSettingsItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<ResSyncSettingsItem> itemList) {
        this.itemList = itemList;
    }
}
