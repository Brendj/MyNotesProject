/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by i.semenov on 23.10.2017.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionInfoList", propOrder = {
        "items"
})
public class TransactionInfoList {
    @XmlElement(name = "item")
    protected List<TransactionInfo> items;

    public List<TransactionInfo> getItems() {
        if (items == null) {
            items = new ArrayList<TransactionInfo>();
        }
        return items;
    }

    public void setItems(List<TransactionInfo> items) {
        this.items = items;
    }
}
