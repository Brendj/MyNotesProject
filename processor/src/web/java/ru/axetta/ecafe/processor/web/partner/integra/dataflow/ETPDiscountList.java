/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ETPDiscountList")
public class ETPDiscountList {
    @XmlElement(name = "discount")
    List<ETPDiscountItem> itemList;

    public List<ETPDiscountItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<ETPDiscountItem> itemList) {
        this.itemList = itemList;
    }
}
