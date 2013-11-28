
package ru.axetta.ecafe.processor.web.bo.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for createPaymentOrder complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createPaymentOrder">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idOfClient" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="idOfContragent" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="paymentMethod" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="copecksAmount" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="contragentSum" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createPaymentOrder", propOrder = {
    "idOfClient",
    "idOfContragent",
    "paymentMethod",
    "copecksAmount",
    "contragentSum"
})
public class CreatePaymentOrder {

    protected Long idOfClient;
    protected Long idOfContragent;
    protected int paymentMethod;
    protected Long copecksAmount;
    protected Long contragentSum;

    /**
     * Gets the value of the idOfClient property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdOfClient() {
        return idOfClient;
    }

    /**
     * Sets the value of the idOfClient property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdOfClient(Long value) {
        this.idOfClient = value;
    }

    /**
     * Gets the value of the idOfContragent property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdOfContragent() {
        return idOfContragent;
    }

    /**
     * Sets the value of the idOfContragent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdOfContragent(Long value) {
        this.idOfContragent = value;
    }

    /**
     * Gets the value of the paymentMethod property.
     * 
     */
    public int getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the value of the paymentMethod property.
     * 
     */
    public void setPaymentMethod(int value) {
        this.paymentMethod = value;
    }

    /**
     * Gets the value of the copecksAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCopecksAmount() {
        return copecksAmount;
    }

    /**
     * Sets the value of the copecksAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCopecksAmount(Long value) {
        this.copecksAmount = value;
    }

    /**
     * Gets the value of the contragentSum property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getContragentSum() {
        return contragentSum;
    }

    /**
     * Sets the value of the contragentSum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setContragentSum(Long value) {
        this.contragentSum = value;
    }

}
