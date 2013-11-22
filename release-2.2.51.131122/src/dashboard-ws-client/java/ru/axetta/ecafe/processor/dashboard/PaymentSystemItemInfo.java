
package ru.axetta.ecafe.processor.dashboard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for paymentSystemItemInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="paymentSystemItemInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="error" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idOfContragent" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="lastOperationTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="numOfOperations" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "paymentSystemItemInfo", propOrder = {
    "error",
    "idOfContragent",
    "lastOperationTime",
    "numOfOperations"
})
public class PaymentSystemItemInfo {

    protected String error;
    protected long idOfContragent;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastOperationTime;
    protected long numOfOperations;

    /**
     * Gets the value of the error property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setError(String value) {
        this.error = value;
    }

    /**
     * Gets the value of the idOfContragent property.
     * 
     */
    public long getIdOfContragent() {
        return idOfContragent;
    }

    /**
     * Sets the value of the idOfContragent property.
     * 
     */
    public void setIdOfContragent(long value) {
        this.idOfContragent = value;
    }

    /**
     * Gets the value of the lastOperationTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastOperationTime() {
        return lastOperationTime;
    }

    /**
     * Sets the value of the lastOperationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastOperationTime(XMLGregorianCalendar value) {
        this.lastOperationTime = value;
    }

    /**
     * Gets the value of the numOfOperations property.
     * 
     */
    public long getNumOfOperations() {
        return numOfOperations;
    }

    /**
     * Sets the value of the numOfOperations property.
     * 
     */
    public void setNumOfOperations(long value) {
        this.numOfOperations = value;
    }

}
