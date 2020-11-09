/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow.allEnterEvents;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.*;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "enterEventList"
})
@XmlRootElement(name = "DataAllEvents")
public class DataAllEvents {

    @XmlElement(name = "EnterEventList")
    protected AllEventList enterEventList;
    @XmlAttribute(name = "ResultCode")
    protected Long resultCode;
    @XmlAttribute(name = "Description")
    protected String description;

    /**
     * Gets the value of the enterEventList property.
     * 
     * @return
     *     possible object is
     *     {@link EnterEventList }
     *     
     */
    public AllEventList getEnterEventList() {
        return enterEventList;
    }

    /**
     * Sets the value of the enterEventList property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnterEventList }
     *     
     */
    public void setEnterEventList(AllEventList value) {
        this.enterEventList = value;
    }

    public Long getResultCode() {
        return resultCode;
    }

    public void setResultCode(Long resultCode) {
        this.resultCode = resultCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
