/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.report.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.04.13
 * Time: 16:21
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TradeMaterialGoodList", propOrder = {
        "t"
})
public class TradeMaterialGoodList {
    @XmlElement(name = "T")
    protected List<TradeMaterialGoodItem> t;

    public List<TradeMaterialGoodItem> getT() {
        if (t == null) {
            t = new ArrayList<TradeMaterialGoodItem>();
        }
        return t;
    }

    public void setT(List<TradeMaterialGoodItem> t) {
        this.t = t;
    }
}
