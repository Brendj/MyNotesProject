/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.persistence.HardwareSettings;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ResHardwareSettingsRequestCRItem extends ResHardwareSettingsRequestItem {

    private Integer readerUsedByModule;
    private String readerName;
    private String firmwareVer;

    public ResHardwareSettingsRequestCRItem(HardwareSettings hardwareSettings, Integer resCode) {
        this.readerUsedByModule = hardwareSettings.getUsedByModule();
        this.readerName = hardwareSettings.getReaderName();
        this.firmwareVer = hardwareSettings.getFirmwareVer();
        setResCode(resCode);
        setLastUpdate(hardwareSettings.getLastUpdateForReader());
    }

    public ResHardwareSettingsRequestCRItem(Integer resCode, String errorMessage) {
        super(resCode, errorMessage);
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

        setAttributes(element);
        return element;
    }
}
