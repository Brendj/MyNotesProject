/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import org.w3c.dom.Node;

import java.util.Date;

public class HardwareSettingsRequestRAMItem extends HardwareSettingsRequestItem {

    private String value;

    public HardwareSettingsRequestRAMItem(String value, Date lastUpdate, String type, String errorMessage) {
        super(lastUpdate, type, errorMessage);
        this.value = value;
    }

    public static HardwareSettingsRequestRAMItem build(Node itemNode) {
        String type = "RAM";

        StringBuilder errorMessage = new StringBuilder();

        return new HardwareSettingsRequestRAMItem(getValue(itemNode, errorMessage),
                getLastUpdate(itemNode, errorMessage), type, errorMessage.toString());
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
