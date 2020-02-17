/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.persistence.HardwareSettings;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

public class ResHardwareSettingsRequestMTItem extends ResHardwareSettingsRequestItem {

    private Integer installStatus;
    private Integer moduleType;
    private Integer resCode;
    private String errorMessage;
    private String type;

    public ResHardwareSettingsRequestMTItem(HardwareSettings hardwareSettings, Integer resCode) {
        this.moduleType = hardwareSettings.getModuleType();
        this.installStatus = hardwareSettings.getInstallStatus();
        setLastUpdate(hardwareSettings.getLastUpdateForModuleType());
        this.resCode = resCode;
    }

    public ResHardwareSettingsRequestMTItem(Integer resCode, String errorMessage) {
        this.resCode = resCode;
        this.errorMessage = errorMessage;
    }


    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("MT");
        if (null != installStatus) {
            XMLUtils.setAttributeIfNotNull(element, "InstallStatus", installStatus);
        }
        if (null != moduleType) {
            XMLUtils.setAttributeIfNotNull(element, "Value", moduleType);
        }
        Date lastUpdate = getLastUpdate();
        if (null != lastUpdate) {
            XMLUtils.setAttributeIfNotNull(element, "LastUpdate", lastUpdate);
        }
        return element;
    }

    public Integer getInstallStatus() {
        return installStatus;
    }

    public void setInstallStatus(Integer installStatus) {
        this.installStatus = installStatus;
    }

    public Integer getModuleType() {
        return moduleType;
    }

    public void setModuleType(Integer moduleType) {
        this.moduleType = moduleType;
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
