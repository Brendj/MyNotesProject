
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.bo.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "enterEventsStatusListResult", propOrder = {
    "enterList",
    "resultCode",
    "description"
})
public class EnterEventStatusListResult {

    protected EnterEventStatusItemList enterList;
    protected Long resultCode;
    protected String description;

    public EnterEventStatusItemList getEnterList() {
        return enterList;
    }

    public void setEnterList(EnterEventStatusItemList value) {
        this.enterList = value;
    }

    /**
     * Gets the value of the resultCode property.
     *
     * @return
     *     possible object is
     *     {@link Long }
     *
     */
    public Long getResultCode() {
        return resultCode;
    }

    /**
     * Sets the value of the resultCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setResultCode(Long value) {
        this.resultCode = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

}
