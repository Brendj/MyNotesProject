/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.persistence.HardwareSettings;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

public class ResHardwareSettingsRequestRAMItem extends ResHardwareSettingsRequestItem {

    private String ramSize;
    private Date lastUpdateForRAMSize;
    private Integer resCode;
    private String errorMessage;
    private String type;

    public ResHardwareSettingsRequestRAMItem(HardwareSettings hardwareSettings, Integer resCode) {
        this.ramSize = hardwareSettings.getRamSize();
        this.lastUpdateForRAMSize = hardwareSettings.getLastUpdateForRAMSize();
        this.resCode = resCode;
        setLastUpdate(hardwareSettings.getLastUpdateForIPHost());
    }

    public ResHardwareSettingsRequestRAMItem(Integer resCode, String errorMessage) {
        this.resCode = resCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("RAM");
        if (null != ramSize) {
            XMLUtils.setAttributeIfNotNull(element, "Value", ramSize);
        }
        Date lastUpdate = getLastUpdate();
        if (null != lastUpdate) {
            XMLUtils.setAttributeIfNotNull(element, "LastUpdate", lastUpdateForRAMSize);
        }
        return element;
    }

    public String getRamSize() {
        return ramSize;
    }

    public void setRamSize(String ramSize) {
        this.ramSize = ramSize;
    }

    public Date getLastUpdateForRAMSize() {
        return lastUpdateForRAMSize;
    }

    public void setLastUpdateForRAMSize(Date lastUpdateForRAMSize) {
        this.lastUpdateForRAMSize = lastUpdateForRAMSize;
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
