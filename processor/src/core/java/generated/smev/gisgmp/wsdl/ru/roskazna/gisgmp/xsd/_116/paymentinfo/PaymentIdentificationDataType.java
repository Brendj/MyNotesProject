
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.paymentinfo;

import generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.organization.BankType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * ������ ��� ������������� �������
 * 
 * <p>Java class for PaymentIdentificationDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PaymentIdentificationDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="Bank" type="{http://roskazna.ru/gisgmp/xsd/116/Organization}BankType"/>
 *           &lt;element name="Other">
 *             &lt;simpleType>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                 &lt;enumeration value="CASH"/>
 *               &lt;/restriction>
 *             &lt;/simpleType>
 *           &lt;/element>
 *           &lt;element name="UFK">
 *             &lt;simpleType>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                 &lt;minLength value="1"/>
 *                 &lt;maxLength value="36"/>
 *                 &lt;whiteSpace value="preserve"/>
 *               &lt;/restriction>
 *             &lt;/simpleType>
 *           &lt;/element>
 *         &lt;/choice>
 *         &lt;element name="SystemIdentifier">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\P{S}{32}"/>
 *               &lt;pattern value="PaymentNotLoaded"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentIdentificationDataType", propOrder = {
    "bank",
    "other",
    "ufk",
    "systemIdentifier"
})
public class PaymentIdentificationDataType {

    @XmlElement(name = "Bank")
    protected BankType bank;
    @XmlElement(name = "Other")
    protected String other;
    @XmlElement(name = "UFK")
    protected String ufk;
    @XmlElement(name = "SystemIdentifier", required = true)
    protected String systemIdentifier;

    /**
     * Gets the value of the bank property.
     * 
     * @return
     *     possible object is
     *     {@link BankType }
     *     
     */
    public BankType getBank() {
        return bank;
    }

    /**
     * Sets the value of the bank property.
     * 
     * @param value
     *     allowed object is
     *     {@link BankType }
     *     
     */
    public void setBank(BankType value) {
        this.bank = value;
    }

    /**
     * Gets the value of the other property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOther() {
        return other;
    }

    /**
     * Sets the value of the other property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOther(String value) {
        this.other = value;
    }

    /**
     * Gets the value of the ufk property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUFK() {
        return ufk;
    }

    /**
     * Sets the value of the ufk property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUFK(String value) {
        this.ufk = value;
    }

    /**
     * Gets the value of the systemIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSystemIdentifier() {
        return systemIdentifier;
    }

    /**
     * Sets the value of the systemIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSystemIdentifier(String value) {
        this.systemIdentifier = value;
    }

}
