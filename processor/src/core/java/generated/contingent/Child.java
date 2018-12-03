
/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package generated.contingent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Child complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Child">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Guid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BenefitCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Child", propOrder = {
    "guid",
    "benefitCode"
})
public class Child {

    @XmlElement(name = "Guid", required = true)
    protected String guid;
    @XmlElement(name = "BenefitCode", required = true)
    protected String benefitCode;

    /**
     * Gets the value of the guid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuid() {
        return guid;
    }

    /**
     * Sets the value of the guid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuid(String value) {
        this.guid = value;
    }

    /**
     * Gets the value of the benefitCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBenefitCode() {
        return benefitCode;
    }

    /**
     * Sets the value of the benefitCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBenefitCode(String value) {
        this.benefitCode = value;
    }

}
