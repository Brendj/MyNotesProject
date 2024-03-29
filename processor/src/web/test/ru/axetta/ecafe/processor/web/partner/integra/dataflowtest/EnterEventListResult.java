
package ru.axetta.ecafe.processor.web.partner.integra.dataflowtest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enterEventListResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="enterEventListResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="enterEventList" type="{http://soap.integra.partner.web.processor.ecafe.axetta.ru/}EnterEventList" minOccurs="0"/>
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
@XmlType(name = "enterEventListResult", propOrder = {
    "enterEventList",
    "resultCode",
    "description"
})
public class EnterEventListResult {

    protected EnterEventList enterEventList;
    protected Long resultCode;
    protected String description;

    /**
     * Gets the value of the enterEventList property.
     * 
     * @return
     *     possible object is
     *     {@link EnterEventList }
     *     
     */
    public EnterEventList getEnterEventList() {
        return enterEventList;
    }

    /**
     * Sets the value of the enterEventList property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnterEventList }
     *     
     */
    public void setEnterEventList(EnterEventList value) {
        this.enterEventList = value;
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
