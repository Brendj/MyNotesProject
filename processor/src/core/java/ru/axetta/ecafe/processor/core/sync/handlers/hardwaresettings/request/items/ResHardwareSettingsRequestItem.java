/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Element;

import java.util.Date;

public abstract class ResHardwareSettingsRequestItem implements AbstractToElement {

    private Date lastUpdate;
    private String type;
    private Integer resCode;
    private String errorMessage;

    public ResHardwareSettingsRequestItem() {
    }

    public ResHardwareSettingsRequestItem(Integer resCode, String errorMessage) {
        this.resCode = resCode;
        this.errorMessage = errorMessage;
    }

    public void setAttributes(Element element) {
        Integer resCode = getResCode();
        String errorMessage = null;
        Date lastUpdate = getLastUpdate();

        if(null != lastUpdate) {
            XMLUtils.setAttributeIfNotNull(element, "LastUpdate", lastUpdate);
        }
        XMLUtils.setAttributeIfNotNull(element, "Res", resCode);
        if (resCode != null && resCode != 0) {
            XMLUtils.setAttributeIfNotNull(element, "Error", errorMessage);
        }
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
