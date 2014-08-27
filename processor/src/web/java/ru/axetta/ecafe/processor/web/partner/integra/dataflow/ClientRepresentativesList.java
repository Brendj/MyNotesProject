/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for EnterEventList complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ClientRepresentativesList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Rep" type="{http://soap.integra.partner.web.processor.ecafe.axetta.ru/}EnterEventItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClientRepresentativesList", propOrder = {
        "Rep"
})
public class ClientRepresentativesList {

    @XmlElement(name = "Rep")
    protected List<ClientRepresentative> Rep;

    /**
     * Gets the value of the e property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the e property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRep().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EnterEventItem }
     *
     *
     */
    public List<ClientRepresentative> getRep() {
        if (Rep == null) {
            Rep = new ArrayList<ClientRepresentative>();
        }
        return this.Rep;
    }
}
