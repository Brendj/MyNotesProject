/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.persistence.HardwareSettings;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ResHardwareSettingsRequestRAMItem extends ResHardwareSettingsRequestItem {

    private String ramSize;

    public ResHardwareSettingsRequestRAMItem(HardwareSettings hardwareSettings, Integer resCode) {
        this.ramSize = hardwareSettings.getRamSize();
        setResCode(resCode);
        setLastUpdate(hardwareSettings.getLastUpdateForIPHost());
    }

    public ResHardwareSettingsRequestRAMItem(Integer resCode, String errorMessage) {
        super(resCode, errorMessage);
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("RAM");
        if (null != ramSize) {
            XMLUtils.setAttributeIfNotNull(element, "Value", ramSize);
        }
        setAttributes(element);
        return element;
    }
}
