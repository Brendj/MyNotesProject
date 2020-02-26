/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.persistence.HardwareSettings;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ResHardwareSettingsRequestIPItem extends ResHardwareSettingsRequestItem {

    private String ipHost;

    public ResHardwareSettingsRequestIPItem(HardwareSettings hardwareSettings, Integer resCode) {
        this.ipHost = hardwareSettings.getIpHost();
        setResCode(resCode);
        setLastUpdate(hardwareSettings.getLastUpdateForIPHost());
    }

    public ResHardwareSettingsRequestIPItem(Integer resCode, String errorMessage) {
        super(resCode, errorMessage);
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("IP");
        if (null != ipHost) {
            XMLUtils.setAttributeIfNotNull(element, "Value", ipHost);
        }
        setAttributes(element);
        return element;
    }
}
