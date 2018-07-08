/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import javax.xml.bind.annotation.*;

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
    @XmlElement(name = "regularMenuDetail")
    private RegularPreorderParam regularMenuDetail;

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

    public RegularPreorderParam getRegularMenuDetail() {
        return regularMenuDetail;
    }

    public void setRegularMenuDetail(RegularPreorderParam regularMenuDetail) {
        this.regularMenuDetail = regularMenuDetail;
    }

}
