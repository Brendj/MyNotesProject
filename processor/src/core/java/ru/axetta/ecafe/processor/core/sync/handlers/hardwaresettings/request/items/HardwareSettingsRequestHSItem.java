/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

public class HardwareSettingsRequestHSItem extends HardwareSettingsRequestItem {

    private Long idOfHardwareSetting;

    public HardwareSettingsRequestHSItem(Long idOfHardwareSetting, String type, String errorMessage) {
        super(type, errorMessage);
        this.idOfHardwareSetting = idOfHardwareSetting;
    }

    public static HardwareSettingsRequestHSItem build(Node itemNode) {
        Long idOfHardwareSetting;
        String type = "HS";

        StringBuilder errorMessage = new StringBuilder();

        idOfHardwareSetting = XMLUtils.getLongAttributeValue(itemNode, "HostId");
        if (null == idOfHardwareSetting) {
            errorMessage.append("Attribute HostId not found");
        }
        return new HardwareSettingsRequestHSItem(idOfHardwareSetting, type,errorMessage.toString());
    }

    public Long getIdOfHardwareSetting() {
        return idOfHardwareSetting;
    }

    public void setIdOfHardwareSetting(Long idOfHardwareSetting) {
        this.idOfHardwareSetting = idOfHardwareSetting;
    }
}
