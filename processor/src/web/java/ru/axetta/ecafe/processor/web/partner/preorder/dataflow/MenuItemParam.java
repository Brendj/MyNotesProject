/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by i.semenov on 13.03.2018.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MenuItemParam")
public class MenuItemParam {
    @XmlAttribute(name = "idOfMenuDetail")
    private Long idOfMenuDetail;
    @XmlAttribute(name = "amount")
    private Integer amount;

    public Long getIdOfMenuDetail() {
        return idOfMenuDetail;
    }

    public void setIdOfMenuDetail(Long idOfMenuDetail) {
        this.idOfMenuDetail = idOfMenuDetail;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
