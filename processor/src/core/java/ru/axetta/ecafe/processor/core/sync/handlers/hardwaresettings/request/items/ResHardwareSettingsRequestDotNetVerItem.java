/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.persistence.HardwareSettings;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ResHardwareSettingsRequestDotNetVerItem extends ResHardwareSettingsRequestItem {

    private String dotNetVer;

    public ResHardwareSettingsRequestDotNetVerItem(HardwareSettings hardwareSettings, Integer resCode) {
        this.dotNetVer = hardwareSettings.getDotNetVer();
        setResCode(resCode);
        setLastUpdate(hardwareSettings.getLastUpdateForIPHost());
    }

    public ResHardwareSettingsRequestDotNetVerItem(Integer resCode, String errorMessage) {
        super(resCode, errorMessage);
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("DotNetVer");
        if (null != dotNetVer) {
            XMLUtils.setAttributeIfNotNull(element, "Value", dotNetVer);
        }
        setAttributes(element);
        return element;
    }
}
