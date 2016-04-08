
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.organization;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * ������ �����
 * 
 * <p>Java class for BankType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BankType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="1"/>
 *               &lt;maxLength value="160"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;choice>
 *           &lt;element name="BIK" type="{http://roskazna.ru/gisgmp/xsd/116/Common}BIKType"/>
 *           &lt;element name="SWIFT" type="{http://roskazna.ru/gisgmp/xsd/116/Common}SWIFTType"/>
 *         &lt;/choice>
 *         &lt;element name="CorrespondentBankAccount" type="{http://roskazna.ru/gisgmp/xsd/116/Common}AccountNumType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BankType", propOrder = {
    "name",
    "bik",
    "swift",
    "correspondentBankAccount"
})
public class BankType {

    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "BIK")
    protected String bik;
    @XmlElement(name = "SWIFT")
    protected String swift;
    @XmlElement(name = "CorrespondentBankAccount")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String correspondentBankAccount;

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
     * Gets the value of the bik property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBIK() {
        return bik;
    }

    /**
     * Sets the value of the bik property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBIK(String value) {
        this.bik = value;
    }

    /**
     * Gets the value of the swift property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSWIFT() {
        return swift;
    }

    /**
     * Sets the value of the swift property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSWIFT(String value) {
        this.swift = value;
    }

    /**
     * Gets the value of the correspondentBankAccount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCorrespondentBankAccount() {
        return correspondentBankAccount;
    }

    /**
     * Sets the value of the correspondentBankAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCorrespondentBankAccount(String value) {
        this.correspondentBankAccount = value;
    }

}
