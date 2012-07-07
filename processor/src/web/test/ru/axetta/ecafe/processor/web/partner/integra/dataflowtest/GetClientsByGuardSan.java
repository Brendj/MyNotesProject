
package ru.axetta.ecafe.processor.web.partner.integra.dataflowtest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getClientsByGuardSan complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getClientsByGuardSan">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="san" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getClientsByGuardSan", propOrder = {
    "san"
})
public class GetClientsByGuardSan {

    protected String san;

    /**
     * Gets the value of the san property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSan() {
        return san;
    }

    /**
     * Sets the value of the san property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSan(String value) {
        this.san = value;
    }

}
