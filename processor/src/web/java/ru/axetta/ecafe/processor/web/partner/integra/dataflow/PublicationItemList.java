/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PublicationItemList", propOrder = {
        "c"
})
public class PublicationItemList {
    @XmlElement(name = "C")
    protected List<PublicationInstancesItem> c;

    public List<PublicationInstancesItem> getC() {
        if (c == null) {
            c = new ArrayList<PublicationInstancesItem>();
        }
        return this.c;
    }
}
