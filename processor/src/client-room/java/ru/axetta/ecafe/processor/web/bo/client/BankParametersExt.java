
package ru.axetta.ecafe.processor.web.bo.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BankItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BankItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="LogoUrl" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="TerminalsUrl" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Rate" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="MinRate" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="EnrollmentType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BankParametersExt")
public class BankParametersExt {

    @XmlAttribute(name = "Name")
    protected String name;
    @XmlAttribute(name = "LogoUrl")
    protected String logoUrl;
    @XmlAttribute(name = "TerminalsUrl")
    protected String terminalsUrl;
    @XmlAttribute(name = "Rate")
    protected Double rate;
    @XmlAttribute(name = "MinRate")
    protected Double minRate;
    @XmlAttribute(name = "EnrollmentType")
    protected String enrollmentType;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the logoUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLogoUrl() {
        return logoUrl;
    }

    /**
     * Sets the value of the logoUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLogoUrl(String value) {
        this.logoUrl = value;
    }

    /**
     * Gets the value of the terminalsUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTerminalsUrl() {
        return terminalsUrl;
    }

    /**
     * Sets the value of the terminalsUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTerminalsUrl(String value) {
        this.terminalsUrl = value;
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
     * Gets the value of the minRate property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMinRate() {
        return minRate;
    }

    /**
     * Sets the value of the minRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMinRate(Double value) {
        this.minRate = value;
    }

    /**
     * Gets the value of the enrollmentType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnrollmentType() {
        return enrollmentType;
    }

    /**
     * Sets the value of the enrollmentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnrollmentType(String value) {
        this.enrollmentType = value;
    }

}
