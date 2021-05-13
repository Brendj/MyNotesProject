/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.HardwareSettingsRequest;

import org.w3c.dom.Node;

import java.util.Date;

public class HardwareSettingsRequestOsVerItem extends HardwareSettingsRequestItem {

    private String value;

    public HardwareSettingsRequestOsVerItem(String value, Date lastUpdate, HardwareSettingsRequest.ModuleType type, String errorMessage) {
        super(lastUpdate, type, errorMessage);
        this.value = value;
    }

    public static HardwareSettingsRequestOsVerItem build(Node itemNode) {
        HardwareSettingsRequest.ModuleType type = HardwareSettingsRequest.ModuleType.OSVER;

        StringBuilder errorMessage = new StringBuilder();

        return new HardwareSettingsRequestOsVerItem(getValue(itemNode, errorMessage),
                getLastUpdate(itemNode, errorMessage), type, errorMessage.toString());
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
