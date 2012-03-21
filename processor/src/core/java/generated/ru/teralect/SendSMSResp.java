
/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package generated.ru.teralect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="rc" type="{http://sms.teralect.ru/}RetCode" minOccurs="0"/>
 *         &lt;element name="trxId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "rc",
    "trxId"
})
@XmlRootElement(name = "SendSMSResp")
public class SendSMSResp {

    protected RetCode rc;
    @XmlElement(nillable = true)
    protected String trxId;

    /**
     * Gets the value of the rc property.
     * 
     * @return
     *     possible object is
     *     {@link RetCode }
     *     
     */
    public RetCode getRc() {
        return rc;
    }

    /**
     * Sets the value of the rc property.
     * 
     * @param value
     *     allowed object is
     *     {@link RetCode }
     *     
     */
    public void setRc(RetCode value) {
        this.rc = value;
    }

    /**
     * Gets the value of the trxId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrxId() {
        return trxId;
    }

    /**
     * Sets the value of the trxId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrxId(String value) {
        this.trxId = value;
    }

}
