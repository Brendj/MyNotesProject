/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.persistence.HardwareSettings;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

public class ResHardwareSettingsRequestOsVerItem extends ResHardwareSettingsRequestItem {

    private String oSVer;
    private Date lastUpdateForOsVer;
    private Integer resCode;
    private String errorMessage;
    private String type;

    public ResHardwareSettingsRequestOsVerItem(HardwareSettings hardwareSettings, Integer resCode) {
        this.oSVer = hardwareSettings.getoSVer();
        this.lastUpdateForOsVer = hardwareSettings.getLastUpdateForOSVer();
        this.resCode = resCode;
        setLastUpdate(hardwareSettings.getLastUpdateForIPHost());
    }

    public ResHardwareSettingsRequestOsVerItem(Integer resCode, String errorMessage) {
        this.resCode = resCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("OsVer");
        if (null != oSVer) {
            XMLUtils.setAttributeIfNotNull(element, "Value", oSVer);
        }
        Date lastUpdate = getLastUpdate();
        if (null != lastUpdate) {
            XMLUtils.setAttributeIfNotNull(element, "LastUpdate", lastUpdate);
        }
        return element;
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
