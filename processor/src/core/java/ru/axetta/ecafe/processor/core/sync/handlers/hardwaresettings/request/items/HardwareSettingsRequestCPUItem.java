/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HardwareSettingsRequestCPUItem extends HardwareSettingsRequestItem {

    private String value;

    public HardwareSettingsRequestCPUItem(String value, Date lastUpdate, String type, String errorMessage) {
        super(lastUpdate, type, errorMessage);
        this.value = value;
    }

    public static HardwareSettingsRequestCPUItem build(Node itemNode) {
        String value = null;
        Date lastUpdate = null;
        String type = "CPU";

        StringBuilder errorMessage = new StringBuilder();

        value = XMLUtils.getAttributeValue(itemNode, "Value");
        if (null == value || StringUtils.isEmpty(value)) {
            errorMessage.append("Attribute Value not found");
        }

        String requestDateString = XMLUtils.getAttributeValue(itemNode, "LastUpdate");
        if (StringUtils.isNotEmpty(requestDateString)) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                lastUpdate = simpleDateFormat.parse(requestDateString);
            } catch (Exception e) {
                errorMessage.append("Attribute LastUpdate not found or incorrect");
            }
        } else {
            errorMessage.append("Attribute RequestDate not found");
        }
        return new HardwareSettingsRequestCPUItem(value, lastUpdate, type, errorMessage.toString());
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
