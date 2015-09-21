
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.bo.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for EnterEventStatusItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EnterEventStatusItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Publication" type="{}PublicationItem"/>
 *       &lt;/sequence>
 *       &lt;attribute name="IssuanceDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="RefundDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="RealRefundDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="Status" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "enterEventStatusItem", propOrder = {
    "lastEnterEventDateTime"
})
public class EnterEventStatusItem {

    @XmlAttribute(name = "Guid")
    protected String guid;
    @XmlAttribute(name = "Inside")
    protected Boolean inside;
    @XmlAttribute(name = "LastEnterEventDirection")
    protected Integer lastEnterEventDirection;
    @XmlAttribute(name = "LastEnterEventDateTime")
    protected XMLGregorianCalendar lastEnterEventDateTime;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String value) {
        this.guid = value;
    }

    public XMLGregorianCalendar getLastEnterEventDateTime() {
        return lastEnterEventDateTime;
    }

    public void setLastEnterEventDateTime(XMLGregorianCalendar value) {
        this.lastEnterEventDateTime = value;
    }

    public Boolean getInside() {
        return inside;
    }

    public void setInside(Boolean value) {
        this.inside = value;
    }

    public Integer getLastEnterEventDirection() {
        return lastEnterEventDirection;
    }

    public void setLastEnterEventDirection(Integer value) {
        this.lastEnterEventDirection = value;
    }

}
