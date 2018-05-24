/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.soap;

import ru.axetta.ecafe.processor.web.partner.preorder.dataflow.PreorderComplexGroup;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by i.semenov on 24.05.2018.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ComplexGroup")
public class ComplexGroup {
    @XmlElement(name = "complexGroup")
    private List<PreorderComplexGroup> complexesWithGroups;

    public ComplexGroup() {
        this.complexesWithGroups = new ArrayList<PreorderComplexGroup>();
    }

    public List<PreorderComplexGroup> getComplexesWithGroups() {
        return complexesWithGroups;
    }

    public void setComplexesWithGroups(List<PreorderComplexGroup> complexesWithGroups) {
        this.complexesWithGroups = complexesWithGroups;
    }
}
