
/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package generated.contingent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for setBenefits complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="setBenefits">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="request" type="{urn:contingent.mos.ru:ws:ispp}SetBenefitsRequest" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "setBenefits", propOrder = {
    "request"
})
public class SetBenefits {

    protected SetBenefitsRequest request;

    /**
     * Gets the value of the request property.
     * 
     * @return
     *     possible object is
     *     {@link SetBenefitsRequest }
     *     
     */
    public SetBenefitsRequest getRequest() {
        return request;
    }

    /**
     * Sets the value of the request property.
     * 
     * @param value
     *     allowed object is
     *     {@link SetBenefitsRequest }
     *     
     */
    public void setRequest(SetBenefitsRequest value) {
        this.request = value;
    }

}
