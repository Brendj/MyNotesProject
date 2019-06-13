/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.LinkedList;
import java.util.List;

public class OrgSettingSyncPOJO {
    private Integer groupID;
    private Integer idOfOrg;
    private List<OrgSettingItemSyncPOJO> items;

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("OS");
        element.setAttribute("SGroup", groupID.toString());
        element.setAttribute("IdOfOrg",idOfOrg.toString());
        for (OrgSettingItemSyncPOJO item : this.getItems()) {
            element.appendChild(item.toResElement(document));
        }
        return element;
    }

    public List<OrgSettingItemSyncPOJO> getItems() {
        if(items == null){
            items = new LinkedList<>();
        }
        return items;
    }

    public void setItems(List<OrgSettingItemSyncPOJO> items) {
        this.items = items;
    }

    public Integer getGroupID() {
        return groupID;
    }

    public void setGroupID(Integer groupID) {
        this.groupID = groupID;
    }

    public Integer getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Integer idOfOrg) {
        idOfOrg = idOfOrg;
    }
}
