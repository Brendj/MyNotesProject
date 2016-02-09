
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for OrderPublicationItem complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="OrderPublicationItem">
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
@XmlType(name = "OrderPublicationItem", propOrder = {
        "publication"
})
public class OrderPublicationItem {

    @XmlElement(name = "Publication", required = true)
    protected PublicationItem publication;
    @XmlAttribute(name = "OrderDate")
    protected XMLGregorianCalendar orderDate;
    @XmlAttribute(name = "OrderId")
    protected Long orderId;
    @XmlAttribute(name = "OrderStatus")
    protected String orderStatus;
    @XmlAttribute(name = "OrgHolder")
    protected String orgHolder;

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
     * Gets the value of the orderDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getOrderDate() {
        return orderDate;
    }

    /**
     * Sets the value of the orderDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setOrderDate(XMLGregorianCalendar value) {
        this.orderDate = value;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        if (orderStatus == null) {
            return "";
        }
        else {
            return orderStatus;
        }
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrgHolder() {
        return orgHolder;
    }

    public void setOrgHolder(String orgHolder) {
        this.orgHolder = orgHolder;
    }
}
