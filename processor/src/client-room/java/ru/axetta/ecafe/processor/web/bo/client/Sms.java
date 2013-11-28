
package ru.axetta.ecafe.processor.web.bo.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Sms complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Sms">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="ServiceSendTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="Price" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="TransactionSum" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="CardNo" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="DeliveryStatus" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="ContentsType" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Sms")
public class Sms {

    @XmlAttribute(name = "ServiceSendTime")
    protected XMLGregorianCalendar serviceSendTime;
    @XmlAttribute(name = "Price")
    protected Long price;
    @XmlAttribute(name = "TransactionSum")
    protected Long transactionSum;
    @XmlAttribute(name = "CardNo")
    protected Long cardNo;
    @XmlAttribute(name = "DeliveryStatus")
    protected Integer deliveryStatus;
    @XmlAttribute(name = "ContentsType")
    protected Integer contentsType;

    /**
     * Gets the value of the serviceSendTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getServiceSendTime() {
        return serviceSendTime;
    }

    /**
     * Sets the value of the serviceSendTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setServiceSendTime(XMLGregorianCalendar value) {
        this.serviceSendTime = value;
    }

    /**
     * Gets the value of the price property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPrice() {
        return price;
    }

    /**
     * Sets the value of the price property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPrice(Long value) {
        this.price = value;
    }

    /**
     * Gets the value of the transactionSum property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTransactionSum() {
        return transactionSum;
    }

    /**
     * Sets the value of the transactionSum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTransactionSum(Long value) {
        this.transactionSum = value;
    }

    /**
     * Gets the value of the cardNo property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCardNo() {
        return cardNo;
    }

    /**
     * Sets the value of the cardNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCardNo(Long value) {
        this.cardNo = value;
    }

    /**
     * Gets the value of the deliveryStatus property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDeliveryStatus() {
        return deliveryStatus;
    }

    /**
     * Sets the value of the deliveryStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDeliveryStatus(Integer value) {
        this.deliveryStatus = value;
    }

    /**
     * Gets the value of the contentsType property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getContentsType() {
        return contentsType;
    }

    /**
     * Sets the value of the contentsType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setContentsType(Integer value) {
        this.contentsType = value;
    }

}
