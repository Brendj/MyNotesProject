
package ru.axetta.ecafe.processor.web.bo.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ChronopayConfigExt complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ChronopayConfigExt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="SharedSec" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Rate" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="IP" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ContragentName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="PurchaseUri" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="CallbackUrl" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Show" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChronopayConfigExt")
public class ChronopayConfigExt {

    @XmlAttribute(name = "SharedSec")
    protected String sharedSec;
    @XmlAttribute(name = "Rate")
    protected Double rate;
    @XmlAttribute(name = "IP")
    protected String ip;
    @XmlAttribute(name = "ContragentName")
    protected String contragentName;
    @XmlAttribute(name = "PurchaseUri")
    protected String purchaseUri;
    @XmlAttribute(name = "CallbackUrl")
    protected String callbackUrl;
    @XmlAttribute(name = "Show")
    protected Boolean show;

    /**
     * Gets the value of the sharedSec property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSharedSec() {
        return sharedSec;
    }

    /**
     * Sets the value of the sharedSec property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSharedSec(String value) {
        this.sharedSec = value;
    }

    /**
     * Gets the value of the rate property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRate() {
        return rate;
    }

    /**
     * Sets the value of the rate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRate(Double value) {
        this.rate = value;
    }

    /**
     * Gets the value of the ip property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIP() {
        return ip;
    }

    /**
     * Sets the value of the ip property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIP(String value) {
        this.ip = value;
    }

    /**
     * Gets the value of the contragentName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContragentName() {
        return contragentName;
    }

    /**
     * Sets the value of the contragentName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContragentName(String value) {
        this.contragentName = value;
    }

    /**
     * Gets the value of the purchaseUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPurchaseUri() {
        return purchaseUri;
    }

    /**
     * Sets the value of the purchaseUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPurchaseUri(String value) {
        this.purchaseUri = value;
    }

    /**
     * Gets the value of the callbackUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCallbackUrl() {
        return callbackUrl;
    }

    /**
     * Sets the value of the callbackUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCallbackUrl(String value) {
        this.callbackUrl = value;
    }

    /**
     * Gets the value of the show property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShow() {
        return show;
    }

    /**
     * Sets the value of the show property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShow(Boolean value) {
        this.show = value;
    }

}
