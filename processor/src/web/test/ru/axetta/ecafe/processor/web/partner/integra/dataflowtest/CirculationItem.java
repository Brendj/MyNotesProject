
package ru.axetta.ecafe.processor.web.partner.integra.dataflowtest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for CirculationItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CirculationItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Publication" type="{http://soap.integra.partner.web.processor.ecafe.axetta.ru/}PublicationItem"/>
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
@XmlType(name = "CirculationItem", propOrder = {
    "publication"
})
public class CirculationItem {

    @XmlElement(name = "Publication", required = true)
    protected PublicationItem publication;
    @XmlAttribute(name = "IssuanceDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar issuanceDate;
    @XmlAttribute(name = "RefundDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar refundDate;
    @XmlAttribute(name = "RealRefundDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar realRefundDate;
    @XmlAttribute(name = "Status")
    protected Integer status;

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
     * Gets the value of the issuanceDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getIssuanceDate() {
        return issuanceDate;
    }

    /**
     * Sets the value of the issuanceDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setIssuanceDate(XMLGregorianCalendar value) {
        this.issuanceDate = value;
    }

    /**
     * Gets the value of the refundDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRefundDate() {
        return refundDate;
    }

    /**
     * Sets the value of the refundDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRefundDate(XMLGregorianCalendar value) {
        this.refundDate = value;
    }

    /**
     * Gets the value of the realRefundDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRealRefundDate() {
        return realRefundDate;
    }

    /**
     * Sets the value of the realRefundDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRealRefundDate(XMLGregorianCalendar value) {
        this.realRefundDate = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setStatus(Integer value) {
        this.status = value;
    }

}
