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
@XmlType(name = "ETPDiscountsResult")
public class ETPDiscountsResult extends Result {
    @XmlElement(name = "discounts")
    ETPDiscountList discountList;

    public ETPDiscountsResult() {

    }

    public ETPDiscountList getDiscountList() {
        return discountList;
    }

    public void setDiscountList(ETPDiscountList discountList) {
        this.discountList = discountList;
    }
}
