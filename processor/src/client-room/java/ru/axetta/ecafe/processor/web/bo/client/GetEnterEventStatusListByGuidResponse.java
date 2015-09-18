
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.bo.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getCirculationListResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getCirculationListResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://soap.integra.partner.web.processor.ecafe.axetta.ru/}circulationListResult" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getCirculationListResponse", propOrder = {
    "_return"
})
public class GetEnterEventStatusListByGuidResponse {

    @XmlElement(name = "return")
    protected CirculationListResult _return;

    /**
     * Gets the value of the return property.
     *
     * @return
     *     possible object is
     *     {@link ru.axetta.ecafe.processor.web.bo.client.CirculationListResult }
     *
     */
    public CirculationListResult getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     *
     * @param value
     *     allowed object is
     *     {@link ru.axetta.ecafe.processor.web.bo.client.CirculationListResult }
     *     
     */
    public void setReturn(CirculationListResult value) {
        this._return = value;
    }

}
