/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.persistence.HardwareSettings;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

public class ResHardwareSettingsRequestCPUItem extends ResHardwareSettingsRequestItem {

    private String cpuHost;
    private Date lastUpdateForCPUHost;
    private Integer resCode;
    private String errorMessage;
    private String type;

    public ResHardwareSettingsRequestCPUItem(HardwareSettings hardwareSettings, Integer resCode) {
        this.cpuHost = hardwareSettings.getCpuHost();
        this.lastUpdateForCPUHost = hardwareSettings.getLastUpdateForCPUHost();
        this.resCode = resCode;
        setLastUpdate(hardwareSettings.getLastUpdateForIPHost());
    }

    public ResHardwareSettingsRequestCPUItem(Integer resCode, String errorMessage) {
        this.resCode = resCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("CPU");
        if (null != cpuHost) {
            XMLUtils.setAttributeIfNotNull(element, "Value", cpuHost);
        }
        Date lastUpdate = getLastUpdate();
        if (null != lastUpdate) {
            XMLUtils.setAttributeIfNotNull(element, "LastUpdate", lastUpdateForCPUHost);
        }
        return element;
    }

    public String getCpuHost() {
        return cpuHost;
    }

    public void setCpuHost(String cpuHost) {
        this.cpuHost = cpuHost;
    }

    public Date getLastUpdateForCPUHost() {
        return lastUpdateForCPUHost;
    }

    public void setLastUpdateForCPUHost(Date lastUpdateForCPUHost) {
        this.lastUpdateForCPUHost = lastUpdateForCPUHost;
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
