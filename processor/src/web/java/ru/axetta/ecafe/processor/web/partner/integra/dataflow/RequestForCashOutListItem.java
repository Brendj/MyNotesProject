/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by nuc on 12.10.2018.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestForCashOutListItem")
public class RequestForCashOutListItem {
    @XmlElement(name = "item")
    private List<RequestForCashOutItem> items;

    public List<RequestForCashOutItem> getItems() {
        return items;
    }

    public void setItems(List<RequestForCashOutItem> items) {
        this.items = items;
    }
}
