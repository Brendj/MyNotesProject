/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.persistence.HardwareSettings;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ResHardwareSettingsRequestOsVerItem extends ResHardwareSettingsRequestItem {

    private String oSVer;

    public ResHardwareSettingsRequestOsVerItem(HardwareSettings hardwareSettings, Integer resCode) {
        this.oSVer = hardwareSettings.getoSVer();
        setResCode(resCode);
        setLastUpdate(hardwareSettings.getLastUpdateForOSVer());
    }

    public ResHardwareSettingsRequestOsVerItem(Integer resCode, String errorMessage) {
        super(resCode, errorMessage);
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("OsVer");
        if (null != oSVer) {
            XMLUtils.setAttributeIfNotNull(element, "Value", oSVer);
        }
        setAttributes(element);
        return element;
    }
}
