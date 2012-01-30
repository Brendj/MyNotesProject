//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.01.30 at 05:41:38 PM MSK 
//


package ru.msk.schemas.uec.transaction.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import ru.msk.schemas.uec.common.v1.ErrorType;


/**
 * ������� ����������
 * 
 * <p>Java class for TransactionStatusDescriptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransactionStatusDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="interfaceType" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="32"/>
 *               &lt;enumeration value="MIFARE"/>
 *               &lt;enumeration value="TCL"/>
 *               &lt;enumeration value="T0"/>
 *               &lt;enumeration value="T1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="cardStatus" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="NORM"/>
 *               &lt;enumeration value="BLOCK"/>
 *               &lt;enumeration value="ERROR"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="isPinUsed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="isModuleUsed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="isServiceDone" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="error" type="{http://schemas.msk.ru/uec/common/v1}ErrorType" minOccurs="0"/>
 *         &lt;element name="otherStatuses" type="{http://schemas.msk.ru/uec/transaction/v1}OtherStatusesType" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionStatusDescriptionType", propOrder = {

})
public class TransactionStatusDescriptionType {

    protected String interfaceType;
    protected String cardStatus;
    protected Boolean isPinUsed;
    protected Boolean isModuleUsed;
    protected Boolean isServiceDone;
    protected ErrorType error;
    protected OtherStatusesType otherStatuses;

    /**
     * Gets the value of the interfaceType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInterfaceType() {
        return interfaceType;
    }

    /**
     * Sets the value of the interfaceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInterfaceType(String value) {
        this.interfaceType = value;
    }

    /**
     * Gets the value of the cardStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardStatus() {
        return cardStatus;
    }

    /**
     * Sets the value of the cardStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardStatus(String value) {
        this.cardStatus = value;
    }

    /**
     * Gets the value of the isPinUsed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsPinUsed() {
        return isPinUsed;
    }

    /**
     * Sets the value of the isPinUsed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsPinUsed(Boolean value) {
        this.isPinUsed = value;
    }

    /**
     * Gets the value of the isModuleUsed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsModuleUsed() {
        return isModuleUsed;
    }

    /**
     * Sets the value of the isModuleUsed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsModuleUsed(Boolean value) {
        this.isModuleUsed = value;
    }

    /**
     * Gets the value of the isServiceDone property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsServiceDone() {
        return isServiceDone;
    }

    /**
     * Sets the value of the isServiceDone property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsServiceDone(Boolean value) {
        this.isServiceDone = value;
    }

    /**
     * Gets the value of the error property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorType }
     *     
     */
    public ErrorType getError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorType }
     *     
     */
    public void setError(ErrorType value) {
        this.error = value;
    }

    /**
     * Gets the value of the otherStatuses property.
     * 
     * @return
     *     possible object is
     *     {@link OtherStatusesType }
     *     
     */
    public OtherStatusesType getOtherStatuses() {
        return otherStatuses;
    }

    /**
     * Sets the value of the otherStatuses property.
     * 
     * @param value
     *     allowed object is
     *     {@link OtherStatusesType }
     *     
     */
    public void setOtherStatuses(OtherStatusesType value) {
        this.otherStatuses = value;
    }

}
