/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.persistence.HardwareSettingsMT;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ResHardwareSettingsRequestMTItem extends ResHardwareSettingsRequestItem {

    private Integer installStatus;
    private Integer moduleType;

    public ResHardwareSettingsRequestMTItem(HardwareSettingsMT hardwareSettingsMT, Integer resCode) {
        this.moduleType = hardwareSettingsMT.getModuleType();
        this.installStatus = hardwareSettingsMT.getInstallStatus();
        hardwareSettingsMT.getHardwareSettings().getIdOfHardwareSetting();

        setLastUpdate(hardwareSettingsMT.getLastUpdate());
        setResCode(resCode);
    }

    public ResHardwareSettingsRequestMTItem(Integer resCode, String errorMessage) {
        super(resCode, errorMessage);
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("MT");
        if (null != installStatus) {
            XMLUtils.setAttributeIfNotNull(element, "InstallStatus", installStatus);
        }
        if (null != moduleType) {
            XMLUtils.setAttributeIfNotNull(element, "Value", moduleType);
        }
        setAttributes(element);
        return element;
    }
}
