/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 19.09.12
 * Time: 15:56
 * To change this template use File | Settings | File Templates.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ComplexDateList", propOrder = {
        "e"
})
public class ComplexDateList {
    @XmlElement(name = "E")
    List<ComplexDate> e;


    public List<ComplexDate> getE() {
        if (e == null) {
            e = new ArrayList<ComplexDate>();
        }
        return this.e;
    }


}
