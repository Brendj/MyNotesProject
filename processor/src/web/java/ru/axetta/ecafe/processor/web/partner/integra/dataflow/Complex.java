/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 19.09.12
 * Time: 15:59
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Complex", propOrder = {
        "e"
})
public class Complex {
    @XmlAttribute(name = "Name")
   protected String name;
    @XmlElement(name = "E")
   protected List<ComplexDetail> e;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ComplexDetail> getE() {
        if (e == null) {
            e = new ArrayList<ComplexDetail>();
        }
        return this.e;
    }

}
