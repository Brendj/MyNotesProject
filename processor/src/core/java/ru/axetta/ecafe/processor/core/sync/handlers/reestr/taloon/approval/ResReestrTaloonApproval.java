/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 16.02.16
 * Time: 12:13
 * To change this template use File | Settings | File Templates.
 */
public class ResReestrTaloonApproval implements AbstractToElement {
    private List<ResTaloonApprovalItem> items;

    public ResReestrTaloonApproval() {

    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResReestrTaloonApproval");
        for (ResTaloonApprovalItem item : this.getItems()) {
            element.appendChild(item.toElement(document, "RTAR"));
        }
        return element;
    }

    public List<ResTaloonApprovalItem> getItems() {
        return items;
    }

    public void setItems(List<ResTaloonApprovalItem> items) {
        this.items = items;
    }
}
