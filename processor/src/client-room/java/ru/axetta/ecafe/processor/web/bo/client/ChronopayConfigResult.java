
package ru.axetta.ecafe.processor.web.bo.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for chronopayConfigResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="chronopayConfigResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="chronopayConfig" type="{http://soap.integra.partner.web.processor.ecafe.axetta.ru/}ChronopayConfigExt" minOccurs="0"/>
 *         &lt;element name="resultCode" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "chronopayConfigResult", propOrder = {
    "chronopayConfig",
    "resultCode",
    "description"
})
public class ChronopayConfigResult {

    protected ChronopayConfigExt chronopayConfig;
    protected Long resultCode;
    protected String description;

    /**
     * Gets the value of the chronopayConfig property.
     * 
     * @return
     *     possible object is
     *     {@link ChronopayConfigExt }
     *     
     */
    public ChronopayConfigExt getChronopayConfig() {
        return chronopayConfig;
    }

    /**
     * Sets the value of the chronopayConfig property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChronopayConfigExt }
     *     
     */
    public void setChronopayConfig(ChronopayConfigExt value) {
        this.chronopayConfig = value;
    }

    /**
     * Gets the value of the resultCode property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getResultCode() {
        return resultCode;
    }

    /**
     * Sets the value of the resultCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setResultCode(Long value) {
        this.resultCode = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

}
