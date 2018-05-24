/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.soap;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by i.semenov on 24.05.2018.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PreorderComplexesResult")
public class PreorderComplexesResult extends Result {
    @XmlElement(name = "complexGroups")
    private ComplexGroup complexGroup;

    public PreorderComplexesResult() {

    }

    public ComplexGroup getComplexGroup() {
        return complexGroup;
    }

    public void setComplexGroup(ComplexGroup complexGroup) {
        this.complexGroup = complexGroup;
    }
}
