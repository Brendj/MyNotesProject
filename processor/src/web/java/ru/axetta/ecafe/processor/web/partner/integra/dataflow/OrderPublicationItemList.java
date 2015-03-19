
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


/**
 * <p>Java class for OrderPublicationItemList complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="OrderPublicationItemList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="C" type="{}OrderPublicationItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderPublicationItemList", propOrder = {
        "c"
})
public class OrderPublicationItemList {

    @XmlElement(name = "C")
    protected List<OrderPublicationItem> c;

    /**
     * Gets the value of the c property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the c property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getC().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrderPublicationItem }
     *
     *
     */
    public List<OrderPublicationItem> getC() {
        if (c == null) {
            c = new ArrayList<OrderPublicationItem>();
        }
        return this.c;
    }

}
