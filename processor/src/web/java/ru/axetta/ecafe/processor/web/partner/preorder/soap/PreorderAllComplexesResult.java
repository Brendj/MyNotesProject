/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.soap;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PreorderAllComplexesResult")
public class PreorderAllComplexesResult extends Result {
    @XmlElement(name = "date")
    private List<PreorderAllComplexesOnDateResult> list;

    @XmlElement(name = "regularPreorders")
    private RegularPreordersList regularPreorders;

    public RegularPreordersList getRegularPreorders() {
        return regularPreorders;
    }

    public void setRegularPreorders(RegularPreordersList regularPreorders) {
        this.regularPreorders = regularPreorders;
    }

    public List<PreorderAllComplexesOnDateResult> getList() {
        return list;
    }

    public void setList(List<PreorderAllComplexesOnDateResult> list) {
        this.list = list;
    }
}
