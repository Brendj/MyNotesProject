
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.budgetindex;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * �������������� ��������� �������, ��������������� �������� ������� ������ �� 12 ������ 2013 �. �107�
 * 
 * <p>Java class for BudgetIndexType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BudgetIndexType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Status">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="2"/>
 *               &lt;enumeration value="01"/>
 *               &lt;enumeration value="02"/>
 *               &lt;enumeration value="03"/>
 *               &lt;enumeration value="04"/>
 *               &lt;enumeration value="05"/>
 *               &lt;enumeration value="06"/>
 *               &lt;enumeration value="07"/>
 *               &lt;enumeration value="08"/>
 *               &lt;enumeration value="09"/>
 *               &lt;enumeration value="10"/>
 *               &lt;enumeration value="11"/>
 *               &lt;enumeration value="12"/>
 *               &lt;enumeration value="13"/>
 *               &lt;enumeration value="14"/>
 *               &lt;enumeration value="15"/>
 *               &lt;enumeration value="16"/>
 *               &lt;enumeration value="17"/>
 *               &lt;enumeration value="18"/>
 *               &lt;enumeration value="19"/>
 *               &lt;enumeration value="20"/>
 *               &lt;enumeration value="21"/>
 *               &lt;enumeration value="22"/>
 *               &lt;enumeration value="23"/>
 *               &lt;enumeration value="24"/>
 *               &lt;enumeration value="25"/>
 *               &lt;enumeration value="26"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Purpose">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="2"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="00"/>
 *               &lt;enumeration value="0"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="TaxPeriod">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="��\.(0[0-9]|1[012])\.\d{4}"/>
 *               &lt;pattern value="��\.0[1-4]\.\d{4}"/>
 *               &lt;pattern value="��\.0[1-2]\.\d{4}"/>
 *               &lt;pattern value="��\.00\.\d{4}"/>
 *               &lt;pattern value="(0[1-9]|[12][0-9]|3[01])\.(0[1-9]|1[012])\.\d{4}"/>
 *               &lt;pattern value="\d{8}"/>
 *               &lt;pattern value="0"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="TaxDocNumber">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *               &lt;maxLength value="25"/>
 *               &lt;minLength value="1"/>
 *               &lt;whiteSpace value="collapse"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="TaxDocDate">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="(0[1-9]|[12][0-9]|3[01])\.(0[1-9]|1[012])\.\d{4}"/>
 *               &lt;pattern value="0"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="PaymentType" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="��"/>
 *               &lt;enumeration value="0"/>
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
@XmlType(name = "BudgetIndexType", propOrder = {
    "status",
    "purpose",
    "taxPeriod",
    "taxDocNumber",
    "taxDocDate",
    "paymentType"
})
public class BudgetIndexType {

    @XmlElement(name = "Status", required = true)
    protected String status;
    @XmlElement(name = "Purpose", required = true, defaultValue = "0")
    protected String purpose;
    @XmlElement(name = "TaxPeriod", required = true, defaultValue = "0")
    protected String taxPeriod;
    @XmlElement(name = "TaxDocNumber", required = true, defaultValue = "0")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String taxDocNumber;
    @XmlElement(name = "TaxDocDate", required = true, defaultValue = "0")
    protected String taxDocDate;
    @XmlElement(name = "PaymentType", defaultValue = "0")
    protected String paymentType;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the purpose property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * Sets the value of the purpose property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPurpose(String value) {
        this.purpose = value;
    }

    /**
     * Gets the value of the taxPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxPeriod() {
        return taxPeriod;
    }

    /**
     * Sets the value of the taxPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxPeriod(String value) {
        this.taxPeriod = value;
    }

    /**
     * Gets the value of the taxDocNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxDocNumber() {
        return taxDocNumber;
    }

    /**
     * Sets the value of the taxDocNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxDocNumber(String value) {
        this.taxDocNumber = value;
    }

    /**
     * Gets the value of the taxDocDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxDocDate() {
        return taxDocDate;
    }

    /**
     * Sets the value of the taxDocDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxDocDate(String value) {
        this.taxDocDate = value;
    }

    /**
     * Gets the value of the paymentType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentType() {
        return paymentType;
    }

    /**
     * Sets the value of the paymentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentType(String value) {
        this.paymentType = value;
    }

}
