/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.LinkedList;
import java.util.List;

public class OrgSettingSection implements AbstractToElement {
    private List<OrgSettingSyncPOJO> items;
    private Long maxVersion;

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("OrgSettings");
        element.setAttribute("V", maxVersion.toString());
        for(OrgSettingSyncPOJO item : items){
            element.appendChild(item.toElement(document));
        }
        return element;
    }

    public List<OrgSettingSyncPOJO> getItems() {
        if(items == null){
            items = new LinkedList<>();
        }
        return items;
    }

    public void setItems(List<OrgSettingSyncPOJO> items) {
        this.items = items;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(Long maxVersion) {
        this.maxVersion = maxVersion;
    }
}