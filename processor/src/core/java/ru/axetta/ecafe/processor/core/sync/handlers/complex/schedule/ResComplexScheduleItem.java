/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.complex.schedule;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 16.02.16
 * Time: 13:42
 * To change this template use File | Settings | File Templates.
 */
public class ResComplexScheduleItem {

    private String guid;
    private Long version;
    private Integer resultCode;
    private String resultMessage;

    public ResComplexScheduleItem(String guid, Long version, Integer resultCode, String resultMessage) {
        this.guid = guid;
        this.version = version;
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "V", version);
        XMLUtils.setAttributeIfNotNull(element, "Guid", guid);
        XMLUtils.setAttributeIfNotNull(element, "Res", resultCode);
        XMLUtils.setAttributeIfNotNull(element, "Error", resultMessage);
        return element;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}
