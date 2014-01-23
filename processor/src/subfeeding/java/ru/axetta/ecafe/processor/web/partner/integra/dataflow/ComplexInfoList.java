/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 10.01.14
 * Time: 14:44
 */

@XmlRootElement(name = "ComplexInfoList")
@XmlAccessorType(XmlAccessType.FIELD)
public class ComplexInfoList {

    @XmlElement(name = "ComplexInfo")
    private List<ComplexInfoExt> list;

    public List<ComplexInfoExt> getList() {
        return list;
    }

    public void setList(List<ComplexInfoExt> list) {
        this.list = list;
    }
}
