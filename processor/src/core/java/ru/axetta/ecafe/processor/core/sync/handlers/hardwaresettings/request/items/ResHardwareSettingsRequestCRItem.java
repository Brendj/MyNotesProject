/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.persistence.HardwareSettings;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

public class ResHardwareSettingsRequestCRItem extends ResHardwareSettingsRequestItem {

    private Integer readerUsedByModule;
    private String readerName;
    private String firmwareVer;
    private Date lastUpdateReader;
    private Integer resCode;
    private String errorMessage;
    private String type;

    public ResHardwareSettingsRequestCRItem(HardwareSettings hardwareSettings, Integer resCode) {
        this.readerUsedByModule = hardwareSettings.getUsedByModule();
        this.readerName = hardwareSettings.getReaderName();
        this.firmwareVer = hardwareSettings.getFirmwareVer();
        this.lastUpdateReader = getLastUpdate();
        this.resCode = resCode;
        setLastUpdate(hardwareSettings.getLastUpdateForIPHost());
    }

    public ResHardwareSettingsRequestCRItem(Integer resCode, String errorMessage) {
        this.resCode = resCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("Readers");
        if (null != readerUsedByModule) {
            XMLUtils.setAttributeIfNotNull(element, "UsedByModule", readerUsedByModule);
        }
        if (null != readerName) {
            XMLUtils.setAttributeIfNotNull(element, "ReaderName", readerName);
        }
        if(null!= firmwareVer) {
            XMLUtils.setAttributeIfNotNull(element,"FirmwareVer",firmwareVer);
        }
        Date lastUpdate = getLastUpdate();
        if (null != lastUpdate) {
            XMLUtils.setAttributeIfNotNull(element, "LastUpdate", lastUpdateReader);
        }
        return element;
    }

    public Integer getReaderUsedByModule() {
        return readerUsedByModule;
    }

    public void setReaderUsedByModule(Integer readerUsedByModule) {
        this.readerUsedByModule = readerUsedByModule;
    }

    public String getReaderName() {
        return readerName;
    }

    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    public String getFirmwareVer() {
        return firmwareVer;
    }

    public void setFirmwareVer(String firmwareVer) {
        this.firmwareVer = firmwareVer;
    }

    public Date getLastUpdateReader() {
        return lastUpdateReader;
    }

    public void setLastUpdateReader(Date lastUpdateReader) {
        this.lastUpdateReader = lastUpdateReader;
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
