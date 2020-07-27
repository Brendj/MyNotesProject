
/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package generated.contingent.ispp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for setBenefitsResponseSmall complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="setBenefitsResponseSmall">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="result" type="{urn:contingent.mos.ru:ws:ispp}SetBenefitsResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "setBenefitsResponse", propOrder = {
    "result"
})
public class SetBenefitsResponseSmall {

    protected SetBenefitsResponse result;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link SetBenefitsResponse }
     *     
     */
    public SetBenefitsResponse getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link SetBenefitsResponse }
     *     
     */
    public void setResult(SetBenefitsResponse value) {
        this.result = value;
    }

}
