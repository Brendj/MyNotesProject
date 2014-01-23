/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
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
@XmlType(name = "ComplexDate", propOrder = {
        "e"
})
public class ComplexDate {
    @XmlAttribute(name = "Date")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar date;


    @XmlElement(name = "E")
    List<Complex> e;


    public XMLGregorianCalendar getDate() {
        return date;
    }

    public void setDate(XMLGregorianCalendar date) {
        this.date = date;
    }

    public List<Complex> getE() {
        if (e == null) {
            e = new ArrayList<Complex>();
        }
        return this.e;
    }

}
