
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.bo.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetEnterEventStatusListByGuid", propOrder = {
    "contractId",
    "guids"
})
public class GetEnterEventStatusListByGuid {

    protected Long contractId;
    protected List<String> guids;

    /**
     * Gets the value of the contractId property.
     *
     * @return
     *     possible object is
     *     {@link Long }
     *
     */
    public Long getContractId() {
        return contractId;
    }

    /**
     * Sets the value of the contractId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setContractId(Long value) {
        this.contractId = value;
    }

    /**
     * Gets the value of the guids property.
     * 
     */
    public List<String> getGuids() {
        return guids;
    }

    /**
     * Sets the value of the guids property.
     * 
     */
    public void setGuids(List<String> value) {
        this.guids = value;
    }

}
