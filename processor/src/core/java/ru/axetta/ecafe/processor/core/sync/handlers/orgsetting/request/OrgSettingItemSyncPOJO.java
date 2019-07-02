/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes.OrgSettingsDataTypes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class OrgSettingItemSyncPOJO {
    private Integer globalID;
    private Integer type;
    private String value;
    private Long version;

    public Node toResElement(Document document) {
        Element element = document.createElement("OSI");
        element.setAttribute("GlobalId", globalID.toString());
        element.setAttribute("SType", type.toString());
        if(type.equals(OrgSettingsDataTypes.BOOLEAN.ordinal())){
            boolean boolVal = Boolean.valueOf(value);
            element.setAttribute("SValue", boolVal ? "1" : "0");
        } else {
            element.setAttribute("SValue", value);
        }
        element.setAttribute("V", version.toString());
        return element;
    }

    public Integer getGlobalID() {
        return globalID;
    }

    public void setGlobalID(Integer globalID) {
        this.globalID = globalID;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
