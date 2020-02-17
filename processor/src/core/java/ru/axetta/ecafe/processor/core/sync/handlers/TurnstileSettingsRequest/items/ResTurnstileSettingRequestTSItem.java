/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest.items;

import ru.axetta.ecafe.processor.core.persistence.TurnstileSettings;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ResTurnstileSettingRequestTSItem extends ResTurnstileSettingsRequestItem {

    private Integer numOfEntries;

    public ResTurnstileSettingRequestTSItem(TurnstileSettings turnstileSettings, Integer resCode) {
        super(resCode);
        this.numOfEntries = turnstileSettings.getNumOfEntries();
    }

    public ResTurnstileSettingRequestTSItem(Integer resCode, String errorMessage) {
        super(errorMessage, resCode);
    }

    public Element toElement(Document document) {
        Element element = document.createElement("TS");
        if (null != numOfEntries) {
            XMLUtils.setAttributeIfNotNull(element, "NumOfEntries", numOfEntries);
        }
        return element;
    }

    public Integer getNumOfEntries() {
        return numOfEntries;
    }

    public void setNumOfEntries(Integer numOfEntries) {
        this.numOfEntries = numOfEntries;
    }
}
