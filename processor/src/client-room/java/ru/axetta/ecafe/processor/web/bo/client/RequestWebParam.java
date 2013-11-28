
package ru.axetta.ecafe.processor.web.bo.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for requestWebParam complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="requestWebParam">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pParam" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateParam" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractIdParam" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "requestWebParam", propOrder = {
    "url",
    "pParam",
    "dateParam",
    "contractIdParam"
})
public class RequestWebParam {

    protected String url;
    protected String pParam;
    protected String dateParam;
    protected String contractIdParam;

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the pParam property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPParam() {
        return pParam;
    }

    /**
     * Sets the value of the pParam property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPParam(String value) {
        this.pParam = value;
    }

    /**
     * Gets the value of the dateParam property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateParam() {
        return dateParam;
    }

    /**
     * Sets the value of the dateParam property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateParam(String value) {
        this.dateParam = value;
    }

    /**
     * Gets the value of the contractIdParam property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractIdParam() {
        return contractIdParam;
    }

    /**
     * Sets the value of the contractIdParam property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractIdParam(String value) {
        this.contractIdParam = value;
    }

}
