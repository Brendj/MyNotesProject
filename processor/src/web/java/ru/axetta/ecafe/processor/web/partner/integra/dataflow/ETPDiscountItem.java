/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscountDSZN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ETPDiscount")
public class ETPDiscountItem {
    @XmlElement(name = "code")
    private Long code;
    @XmlElement(name = "description")
    private String description;

    public ETPDiscountItem() {

    }

    public ETPDiscountItem(CategoryDiscountDSZN discountDSZN) {
        this.code = discountDSZN.getETPCode();
        this.description = discountDSZN.getDescription();
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
