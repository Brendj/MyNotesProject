/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.persistence.HardwareSettings;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

public class ResHardwareSettingsRequestDotNetVerItem extends ResHardwareSettingsRequestItem {

    private String dotNetVer;
    private Date lastUpdateForDotNetVer;
    private Integer resCode;
    private String errorMessage;
    private String type;

    public ResHardwareSettingsRequestDotNetVerItem(HardwareSettings hardwareSettings, Integer resCode) {
        this.dotNetVer = hardwareSettings.getDotNetVer();
        this.lastUpdateForDotNetVer = hardwareSettings.getLastUpdateForDotNetVer();
        this.resCode = resCode;
        setLastUpdate(hardwareSettings.getLastUpdateForIPHost());
    }

    public ResHardwareSettingsRequestDotNetVerItem(Integer resCode, String errorMessage) {
        this.resCode = resCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("DotNetVer");
        if (null != dotNetVer) {
            XMLUtils.setAttributeIfNotNull(element, "Value", dotNetVer);
        }
        Date lastUpdate = getLastUpdate();
        if (null != lastUpdate) {
            XMLUtils.setAttributeIfNotNull(element, "LastUpdate", lastUpdate);
        }
        return element;
    }

    public String getDotNetVer() {
        return dotNetVer;
    }

    public void setDotNetVer(String dotNetVer) {
        this.dotNetVer = dotNetVer;
    }

    public Date getLastUpdateForDotNetVer() {
        return lastUpdateForDotNetVer;
    }

    public void setLastUpdateForDotNetVer(Date lastUpdateForDotNetVer) {
        this.lastUpdateForDotNetVer = lastUpdateForDotNetVer;
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

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }
}
