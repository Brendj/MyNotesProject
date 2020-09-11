/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by i.semenov on 14.03.2018.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PreorderCalendarItem")
public class PreorderListWithComplexesGroupResult extends Result {
    @XmlElement(name = "item")
    private List<PreorderComplexGroup> complexesWithGroups;

    public PreorderListWithComplexesGroupResult() {

    }

    public List<PreorderComplexGroup> getComplexesWithGroups() {
        return complexesWithGroups;
    }

    public void setComplexesWithGroups(List<PreorderComplexGroup> complexesWithGroups) {
        this.complexesWithGroups = complexesWithGroups;
    }
}
