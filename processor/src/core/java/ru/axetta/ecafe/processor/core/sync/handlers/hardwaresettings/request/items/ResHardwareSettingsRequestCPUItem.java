/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.persistence.HardwareSettings;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ResHardwareSettingsRequestCPUItem extends ResHardwareSettingsRequestItem {

    private String cpuHost;

    public ResHardwareSettingsRequestCPUItem(HardwareSettings hardwareSettings, Integer resCode) {
        this.cpuHost = hardwareSettings.getCpuHost();
        setLastUpdate(hardwareSettings.getLastUpdateForCPUHost());
        setResCode(resCode);
    }

    public ResHardwareSettingsRequestCPUItem(Integer resCode, String errorMessage) {
        super(resCode, errorMessage);
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("CPU");
        if (null != cpuHost) {
            XMLUtils.setAttributeIfNotNull(element, "Value", cpuHost);
        }
        setAttributes(element);
        return element;
    }
}
