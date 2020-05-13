/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.soap;

import ru.axetta.ecafe.processor.web.partner.preorder.dataflow.PreorderComplexItemExt;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PreorderAllComplexesOnDateResult")
public class PreorderAllComplexesOnDateResult {
    @XmlAttribute(name = "value")
    @XmlSchemaType(name = "date")
    private XMLGregorianCalendar date;

    @XmlElement(name = "complexItem")
    private List<PreorderComplexItemExt> items;

    public XMLGregorianCalendar getDate() {
        return date;
    }

    public void setDate(XMLGregorianCalendar date) {
        this.date = date;
    }

    public List<PreorderComplexItemExt> getItems() {
        return items;
    }

    public void setItems(List<PreorderComplexItemExt> items) {
        this.items = items;
    }
}
