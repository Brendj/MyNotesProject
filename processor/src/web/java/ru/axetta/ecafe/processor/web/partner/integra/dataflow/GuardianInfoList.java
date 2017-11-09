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

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GuardianInfoList", propOrder = {
        "items"
})
public class GuardianInfoList {
    @XmlElement(name = "item")
    protected List<GuardianInfo> items;

    public List<GuardianInfo> getItems() {
        if (items == null) {
            items = new ArrayList<GuardianInfo>();
        }
        return items;
    }

    public void setItems(List<GuardianInfo> items) {
        this.items = items;
    }
}
