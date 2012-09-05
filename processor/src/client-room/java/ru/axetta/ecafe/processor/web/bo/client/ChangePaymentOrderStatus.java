
package ru.axetta.ecafe.processor.web.bo.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for changePaymentOrderStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="changePaymentOrderStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idOfClient" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="idOfClientPaymentOrder" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="orderStatus" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "changePaymentOrderStatus", propOrder = {
    "idOfClient",
    "idOfClientPaymentOrder",
    "orderStatus"
})
public class ChangePaymentOrderStatus {

    protected Long idOfClient;
    protected Long idOfClientPaymentOrder;
    protected int orderStatus;

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
     * Gets the value of the idOfClientPaymentOrder property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdOfClientPaymentOrder() {
        return idOfClientPaymentOrder;
    }

    /**
     * Sets the value of the idOfClientPaymentOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdOfClientPaymentOrder(Long value) {
        this.idOfClientPaymentOrder = value;
    }

    /**
     * Gets the value of the orderStatus property.
     * 
     */
    public int getOrderStatus() {
        return orderStatus;
    }

    /**
     * Sets the value of the orderStatus property.
     * 
     */
    public void setOrderStatus(int value) {
        this.orderStatus = value;
    }

}
