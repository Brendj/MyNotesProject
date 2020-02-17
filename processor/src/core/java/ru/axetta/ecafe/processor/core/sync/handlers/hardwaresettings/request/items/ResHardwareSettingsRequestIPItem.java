/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.persistence.HardwareSettings;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

public class ResHardwareSettingsRequestIPItem extends ResHardwareSettingsRequestItem {

    private String ipHost;
    private Date lastUpdateForIPHost;
    private Integer resCode;
    private String errorMessage;
    private String type;

    public ResHardwareSettingsRequestIPItem(HardwareSettings hardwareSettings, Integer resCode) {
        this.ipHost = hardwareSettings.getIpHost();
        this.lastUpdateForIPHost = hardwareSettings.getLastUpdateForIPHost();
        this.resCode = resCode;
        setLastUpdate(hardwareSettings.getLastUpdateForIPHost());
    }


    public ResHardwareSettingsRequestIPItem(Integer resCode, String errorMessage) {
        this.resCode = resCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("IP");
        if (null != ipHost) {
            XMLUtils.setAttributeIfNotNull(element, "Value", ipHost);
        }
        Date lastUpdate = getLastUpdate();
        if (null != lastUpdate) {
            XMLUtils.setAttributeIfNotNull(element, "LastUpdate", lastUpdate);
        }
        return element;
    }

    public String getIpHost() {
        return ipHost;
    }

    public void setIpHost(String ipHost) {
        this.ipHost = ipHost;
    }

    public Date getLastUpdateForIPHost() {
        return lastUpdateForIPHost;
    }

    public void setLastUpdateForIPHost(Date lastUpdateForIPHost) {
        this.lastUpdateForIPHost = lastUpdateForIPHost;
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
