/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 10.01.14
 * Time: 15:56
 */

@XmlRootElement(name = "ComplexInfoResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class ComplexInfoResult extends Result {

    @XmlElement(name = "ComplexInfoList")
    private ComplexInfoList complexInfoList;

    public ComplexInfoList getComplexInfoList() {
        return complexInfoList;
    }

    public void setComplexInfoList(ComplexInfoList complexInfoList) {
        this.complexInfoList = complexInfoList;
    }
}
