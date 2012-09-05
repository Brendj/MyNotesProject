
package ru.axetta.ecafe.processor.web.bo.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RBKMoneyConfigExt complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RBKMoneyConfigExt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="EshopId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ServiceName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ContragentName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="PurchaseUri" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="SecretKey" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Rate" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="Show" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RBKMoneyConfigExt")
public class RBKMoneyConfigExt {

    @XmlAttribute(name = "EshopId")
    protected String eshopId;
    @XmlAttribute(name = "ServiceName")
    protected String serviceName;
    @XmlAttribute(name = "ContragentName")
    protected String contragentName;
    @XmlAttribute(name = "PurchaseUri")
    protected String purchaseUri;
    @XmlAttribute(name = "SecretKey")
    protected String secretKey;
    @XmlAttribute(name = "Rate")
    protected Double rate;
    @XmlAttribute(name = "Show")
    protected Boolean show;

    /**
     * Gets the value of the eshopId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEshopId() {
        return eshopId;
    }

    /**
     * Sets the value of the eshopId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEshopId(String value) {
        this.eshopId = value;
    }

    /**
     * Gets the value of the serviceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Sets the value of the serviceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceName(String value) {
        this.serviceName = value;
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
     * Gets the value of the secretKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * Sets the value of the secretKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecretKey(String value) {
        this.secretKey = value;
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
