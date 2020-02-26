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
        setResCode(resCode);
        this.numOfEntries = turnstileSettings.getNumOfEntries();
    }

    public ResTurnstileSettingRequestTSItem(Integer resCode, String errorMessage) {
        super(errorMessage, resCode);
    }

    public Element toElement(Document document) {
        Element element = document.createElement("TS");
        Integer resCode = getResCode();
        String errorMessage = null;
        if (null != numOfEntries) {
            XMLUtils.setAttributeIfNotNull(element, "NumOfEntries", numOfEntries);
        }
        XMLUtils.setAttributeIfNotNull(element, "Res", resCode);
        if (resCode != null && resCode != 0) {
            XMLUtils.setAttributeIfNotNull(element, "Error", errorMessage);
        }
        return element;
    }
}
