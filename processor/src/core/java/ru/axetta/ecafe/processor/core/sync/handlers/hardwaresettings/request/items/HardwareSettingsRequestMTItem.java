/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.HardwareSettingsRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.Date;

public class HardwareSettingsRequestMTItem extends HardwareSettingsRequestItem {

    private Integer installStatus;
    private Integer value;

    public HardwareSettingsRequestMTItem(Integer value, Integer installStatus, Date lastUpdate,
            HardwareSettingsRequest.ModuleType type, String errorMessage) {
        super(lastUpdate, type, errorMessage);
        this.installStatus = installStatus;
        this.value = value;
    }

    public static HardwareSettingsRequestMTItem build(Node itemNode) {
        Integer value;
        Integer installStatus;
        HardwareSettingsRequest.ModuleType type = HardwareSettingsRequest.ModuleType.MT;

        StringBuilder errorMessage = new StringBuilder();

        value = XMLUtils.getIntegerAttributeValue(itemNode, "Value");
        if (null == value) {
            errorMessage.append("Attribute Value not found");
        }

        installStatus = XMLUtils.getIntegerAttributeValue(itemNode, "InstallStatus");
        if (null == installStatus) {
            errorMessage.append("Attribute InstallStatus not found");
        }

        return new HardwareSettingsRequestMTItem(value, installStatus, getLastUpdate(itemNode, errorMessage),
                type, errorMessage.toString());
    }

    public Integer getInstallStatus() {
        return installStatus;
    }

    public void setInstallStatus(Integer installStatus) {
        this.installStatus = installStatus;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}

