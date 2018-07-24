/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menus.calendar;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by baloun on 15.06.2018.
 */
public class ResMenusCalendarItem {
    private String guid;
    private Integer resultCode;
    private String errorMessage;
    private Long version;

    public ResMenusCalendarItem() {

    }

    public Element toResElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "Guid", guid);
        XMLUtils.setAttributeIfNotNull(element, "Res", resultCode);
        if (resultCode != null && resultCode != 0) {
            XMLUtils.setAttributeIfNotNull(element, "Error", errorMessage);
        }
        XMLUtils.setAttributeIfNotNull(element, "V", version);
        return element;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
