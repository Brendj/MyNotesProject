/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.emias;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request.OrgSettingSyncPOJO;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.LinkedList;
import java.util.List;

public class EmiasSection implements AbstractToElement {

    private List<EMIASSyncPOJO> items;
    private Long maxVersion;

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("EMIAS");
        element.setAttribute("V", maxVersion.toString());
        for (EMIASSyncPOJO item : getItems()) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }

    public List<EMIASSyncPOJO> getItems() {
        if (items == null) {
            items = new LinkedList<>();
        }
        return items;
    }

    public void setItems(List<EMIASSyncPOJO> items) {
        this.items = items;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(Long maxVersion) {
        this.maxVersion = maxVersion;
    }
}