/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.soap;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Created by i.semenov on 22.05.2018.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PreorderCalendarItem")
public class PreorderCalendarItem {
    @XmlAttribute(name = "onDate")
    @XmlSchemaType(name = "date")
    private XMLGregorianCalendar date;

    @XmlAttribute(name = "editForbidden")
    private Integer editForbidden;

    @XmlAttribute(name = "preorderExists")
    private Integer preorderExists;

    @XmlAttribute(name = "address")
    private String address;

    @XmlAttribute(name = "summ")
    private Long summ;

    public PreorderCalendarItem() {

    }

    public XMLGregorianCalendar getDate() {
        return date;
    }

    public void setDate(XMLGregorianCalendar date) {
        this.date = date;
    }

    public Integer getEditForbidden() {
        return editForbidden;
    }

    public void setEditForbidden(Integer editForbidden) {
        this.editForbidden = editForbidden;
    }

    public Integer getPreorderExists() {
        return preorderExists;
    }

    public void setPreorderExists(Integer preorderExists) {
        this.preorderExists = preorderExists;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getSumm() {
        return summ;
    }

    public void setSumm(Long summ) {
        this.summ = summ;
    }
}
