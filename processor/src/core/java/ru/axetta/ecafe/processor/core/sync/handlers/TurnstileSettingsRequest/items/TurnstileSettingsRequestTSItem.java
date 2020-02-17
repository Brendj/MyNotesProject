/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest.items;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

public class TurnstileSettingsRequestTSItem extends TurnstileSettingsRequestItem {

    private Integer numOfEntries;

    public TurnstileSettingsRequestTSItem(Integer numOfEntries, String type, String errorMessage) {
        super(type, errorMessage);
        this.numOfEntries = numOfEntries;
    }

    public static TurnstileSettingsRequestTSItem build(Node itemNode) {
        Integer numOfEntries = null;
        String type = "TS";

        StringBuilder errorMessage = new StringBuilder();

        numOfEntries = XMLUtils.getIntegerAttributeValue(itemNode, "NumOfEntries");
        if (null == numOfEntries) {
            errorMessage.append("Attribute NumOfEntries not found");
        }
        return new TurnstileSettingsRequestTSItem(numOfEntries, type, errorMessage.toString());
    }

    public Integer getNumOfEntries() {
        return numOfEntries;
    }

    public void setNumOfEntries(Integer numOfEntries) {
        this.numOfEntries = numOfEntries;
    }
}
