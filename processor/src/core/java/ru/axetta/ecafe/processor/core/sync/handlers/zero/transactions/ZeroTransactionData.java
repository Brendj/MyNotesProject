/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.zero.transactions;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 28.03.16
 * Time: 12:56
 * To change this template use File | Settings | File Templates.
 */
public class ZeroTransactionData extends AbstractToElement {
    private List<ResZeroTransactionItem> items;

    public ZeroTransactionData() {

    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ZeroTransactions");
        for (ResZeroTransactionItem item : this.getItems()) {
            element.appendChild(item.toElement(document, "ZT"));
        }
        return element;
    }

    public List<ResZeroTransactionItem> getItems() {
        return items;
    }

    public void setItems(List<ResZeroTransactionItem> items) {
        this.items = items;
    }
}
