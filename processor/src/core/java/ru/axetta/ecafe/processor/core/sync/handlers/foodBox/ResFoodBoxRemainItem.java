/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.foodBox;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

public class ResFoodBoxRemainItem {

    private String errorMessage;
    private Integer status;

    public ResFoodBoxRemainItem() {
    }

    public ResFoodBoxRemainItem(Integer status, String errorMessage) {
        this.errorMessage = errorMessage;
        this.status = status;
    }
    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "Status", status);
        if (status != null && status != 0) {
            XMLUtils.setAttributeIfNotNull(element, "ErrorMessage", errorMessage);
        }
        return element;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
