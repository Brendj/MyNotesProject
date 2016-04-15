/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package generated.registry.manual_synch;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for registryChangeItemParam complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="registryChangeItemParam">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fieldNameParam" type="{http://www.w3.org/2001/XMLSchema}String" minOccurs="0"/>
 *         &lt;element name="fieldValueParam" type="{http://www.w3.org/2001/XMLSchema}String"  minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "registryChangeItemParam", propOrder = {
        "fieldNameParam",
        "fieldValueParam"
})
public class RegistryChangeItemParam {

    public String fieldNameParam;
    public String fieldValueParam;

    /**
     * Gets the value of the fieldNameParam property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFieldNameParam() {
        return fieldNameParam;
    }

    /**
     * Sets the value of the fieldNameParam property.
     *
     * @param fieldNameParam
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFieldNameParam(String fieldNameParam) {
        this.fieldNameParam = fieldNameParam;
    }

    /**
     * Gets the value of the fieldValueParam property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFieldValueParam() {
        return fieldValueParam;
    }

    /**
     * Sets the value of the fieldValueParam property.
     *
     * @param fieldValueParam
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFieldValueParam(String fieldValueParam) {
        this.fieldValueParam = fieldValueParam;
    }
}
