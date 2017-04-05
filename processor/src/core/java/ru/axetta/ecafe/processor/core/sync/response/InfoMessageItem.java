/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.persistence.InfoMessage;
import ru.axetta.ecafe.processor.core.sync.ResultOperation;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by i.semenov on 04.04.2017.
 */
public class InfoMessageItem {
    private Date createDate;
    private String header;
    private String content;
    private Long version;
    private ResultOperation result;

    public InfoMessageItem(InfoMessage infoMessage) {
        this.createDate = infoMessage.getCreatedDate();
        this.header = infoMessage.getHeader();
        this.content = infoMessage.getContent();
        this.version = infoMessage.getVersion();
        this.result = null;
    }

    public Element toElement(Document document, String elementName, DateFormat timeFormat) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "CreateDate", timeFormat.format(createDate));
        XMLUtils.setAttributeIfNotNull(element, "Header", header);
        XMLUtils.setAttributeIfNotNull(element, "Content", content);
        XMLUtils.setAttributeIfNotNull(element, "V", version);
        if(this.result!=null){
            XMLUtils.setAttributeIfNotNull(element, "ResCode", result.getCode());
            XMLUtils.setAttributeIfNotNull(element, "ResultMessage", result.getMessage());
        }
        return element;
    }
}
