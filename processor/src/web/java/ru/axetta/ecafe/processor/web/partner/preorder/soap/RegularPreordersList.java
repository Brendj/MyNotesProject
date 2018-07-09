/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by i.semenov on 09.07.2018.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegularPreorderList")
public class RegularPreordersList {
    @XmlElement(name = "regularPreorders")
    private List<RegularPreorderItem> regularPreorders;

    public List<RegularPreorderItem> getRegularPreorders() {
        return regularPreorders;
    }

    public void setRegularPreorders(List<RegularPreorderItem> regularPreorders) {
        this.regularPreorders = regularPreorders;
    }
}
