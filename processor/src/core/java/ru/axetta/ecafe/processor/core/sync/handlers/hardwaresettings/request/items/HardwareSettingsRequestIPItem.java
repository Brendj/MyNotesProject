/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.HardwareSettingsRequest;

import org.w3c.dom.Node;

import java.util.Date;

public class HardwareSettingsRequestIPItem extends HardwareSettingsRequestItem {

    private String value;

    public HardwareSettingsRequestIPItem(String value, Date lastUpdate, HardwareSettingsRequest.ModuleType type, String errorMessage) {
        super(lastUpdate, type, errorMessage);
        this.value = value;
    }

    public static HardwareSettingsRequestIPItem build(Node itemNode) {
        HardwareSettingsRequest.ModuleType type = HardwareSettingsRequest.ModuleType.IP;
        StringBuilder errorMessage = new StringBuilder();

        return new HardwareSettingsRequestIPItem(getValue(itemNode, errorMessage),
                getLastUpdate(itemNode, errorMessage), type, errorMessage.toString());
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
