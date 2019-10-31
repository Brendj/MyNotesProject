/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.LinkedList;
import java.util.List;

public class SyncSettingsSection implements AbstractToElement {

    private List<SyncSettingsSectionItem> itemList = new LinkedList<>();

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("SyncSettings");
        for(SyncSettingsSectionItem item : itemList){
            element.appendChild(item.toElement(document));
        }

        return element;
    }

    public List<SyncSettingsSectionItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<SyncSettingsSectionItem> itemList) {
        this.itemList = itemList;
    }
}
