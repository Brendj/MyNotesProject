/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.persistence.HardwareSettings;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ResHardwareSettingsRequestHSItem extends ResHardwareSettingsRequestItem {

    private Long idOfHardwareSetting;

    public ResHardwareSettingsRequestHSItem(HardwareSettings hardwareSettings, Integer resCode) {
        this.idOfHardwareSetting = hardwareSettings.getIdOfHardwareSetting();
        setResCode(resCode);
    }

    public ResHardwareSettingsRequestHSItem(Integer resCode, String errorMessage) {
        super(resCode, errorMessage);
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("Id");
        if (null != idOfHardwareSetting) {
            XMLUtils.setAttributeIfNotNull(element, "Id", idOfHardwareSetting);
        }
        setAttributes(element);
        return element;
    }
}
