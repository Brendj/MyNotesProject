/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PublicationInstancesItem", propOrder = {
        "publication"
})
public class PublicationInstancesItem {

    @XmlElement(name = "Publication", required = true)
    protected PublicationItem publication;
    @XmlAttribute(name = "InstancesAmount")
    protected Integer instancesAmount;
    @XmlAttribute(name = "InstancesAvailable")
    protected Integer instancesAvailable;

    /**
     * Gets the value of the publication property.
     *
     * @return
     *     possible object is
     *     {@link PublicationItem }
     *
     */
    public PublicationItem getPublication() {
        return publication;
    }

    /**
     * Sets the value of the publication property.
     *
     * @param value
     *     allowed object is
     *     {@link PublicationItem }
     *
     */
    public void setPublication(PublicationItem value) {
        this.publication = value;
    }

    /**
     * Gets the value of the instancesAmount property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getInstancesAmount() {
        return instancesAmount;
    }

    /**
     * Sets the value of the instancesAmount property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setInstancesAmount(Integer value) {
        this.instancesAmount = value;
    }

    /**
     * Gets the value of the instancesAvailable property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getInstancesAvailable() {
        return instancesAvailable;
    }

    /**
     * Sets the value of the instancesAvailable property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setInstancesAvailable(Integer value) {
        this.instancesAvailable = value;
    }

}
