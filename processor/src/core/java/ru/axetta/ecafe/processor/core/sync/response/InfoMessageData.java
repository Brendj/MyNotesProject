/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.sync.ResultOperation;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.DateFormat;
import java.util.List;

/**
 * Created by i.semenov on 04.04.2017.
 */
public class InfoMessageData implements AbstractToElement {
    private List<InfoMessageItem> infoMessageItems;
    private ResultOperation result;

    public InfoMessageData(ResultOperation result) {
        this.result = result;
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("InfoMessages");
        if(this.result != null){
            XMLUtils.setAttributeIfNotNull(element, "ResCode", result.getCode());
            XMLUtils.setAttributeIfNotNull(element, "ResultMessage", result.getMessage());
        }
        DateFormat timeFormat = CalendarUtils.getDateTimeFormatLocal();
        for (InfoMessageItem item : this.getInfoMessageItems()) {
            element.appendChild(item.toElement(document, "Message", timeFormat));
        }
        return element;
    }

    public List<InfoMessageItem> getInfoMessageItems() {
        return infoMessageItems;
    }

    public void setInfoMessageItems(List<InfoMessageItem> infoMessageItems) {
        this.infoMessageItems = infoMessageItems;
    }
}
