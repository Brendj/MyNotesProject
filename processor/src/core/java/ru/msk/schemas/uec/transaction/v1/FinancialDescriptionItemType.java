
package ru.msk.schemas.uec.transaction.v1;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import iso.std.iso._20022.tech.xsd.pain_001_001.PaymentInstructionInformation3;
import iso.std.iso._20022.tech.xsd.pain_008_001.PaymentInstructionInformation4;


/**
 * Финансовая часть транзакции
 * 
 * <p>Java class for FinancialDescriptionItemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FinancialDescriptionItemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all minOccurs="0">
 *         &lt;element name="financialCode">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="32"/>
 *               &lt;enumeration value="DBT"/>
 *               &lt;enumeration value="CRD"/>
 *               &lt;enumeration value="BALANCE"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="financialCodeName" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="255"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="financialAmount" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="financialCurrency">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="3"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="accountingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="paymentAdditionalInfo" type="{http://schemas.msk.ru/uec/transaction/v1}PaymentAdditionalInfoType" minOccurs="0"/>
 *         &lt;element name="paymentInfo" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element name="CstmrCdtTrfInitn" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.03}PaymentInstructionInformation3"/>
 *                   &lt;element name="CstmrDrctDbtInitn" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.001.02}PaymentInstructionInformation4"/>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FinancialDescriptionItemType", propOrder = {

})
public class FinancialDescriptionItemType {

    protected String financialCode;
    protected String financialCodeName;
    protected BigDecimal financialAmount;
    protected String financialCurrency;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar accountingDate;
    protected PaymentAdditionalInfoType paymentAdditionalInfo;
    protected FinancialDescriptionItemType.PaymentInfo paymentInfo;

    /**
     * Gets the value of the financialCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFinancialCode() {
        return financialCode;
    }

    /**
     * Sets the value of the financialCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFinancialCode(String value) {
        this.financialCode = value;
    }

    /**
     * Gets the value of the financialCodeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFinancialCodeName() {
        return financialCodeName;
    }

    /**
     * Sets the value of the financialCodeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFinancialCodeName(String value) {
        this.financialCodeName = value;
    }

    /**
     * Gets the value of the financialAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getFinancialAmount() {
        return financialAmount;
    }

    /**
     * Sets the value of the financialAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setFinancialAmount(BigDecimal value) {
        this.financialAmount = value;
    }

    /**
     * Gets the value of the financialCurrency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFinancialCurrency() {
        return financialCurrency;
    }

    /**
     * Sets the value of the financialCurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFinancialCurrency(String value) {
        this.financialCurrency = value;
    }

    /**
     * Gets the value of the accountingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAccountingDate() {
        return accountingDate;
    }

    /**
     * Sets the value of the accountingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAccountingDate(XMLGregorianCalendar value) {
        this.accountingDate = value;
    }

    /**
     * Gets the value of the paymentAdditionalInfo property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentAdditionalInfoType }
     *     
     */
    public PaymentAdditionalInfoType getPaymentAdditionalInfo() {
        return paymentAdditionalInfo;
    }

    /**
     * Sets the value of the paymentAdditionalInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentAdditionalInfoType }
     *     
     */
    public void setPaymentAdditionalInfo(PaymentAdditionalInfoType value) {
        this.paymentAdditionalInfo = value;
    }

    /**
     * Gets the value of the paymentInfo property.
     * 
     * @return
     *     possible object is
     *     {@link FinancialDescriptionItemType.PaymentInfo }
     *     
     */
    public FinancialDescriptionItemType.PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    /**
     * Sets the value of the paymentInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link FinancialDescriptionItemType.PaymentInfo }
     *     
     */
    public void setPaymentInfo(FinancialDescriptionItemType.PaymentInfo value) {
        this.paymentInfo = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;choice>
     *         &lt;element name="CstmrCdtTrfInitn" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.03}PaymentInstructionInformation3"/>
     *         &lt;element name="CstmrDrctDbtInitn" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.001.02}PaymentInstructionInformation4"/>
     *       &lt;/choice>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "cstmrCdtTrfInitn",
        "cstmrDrctDbtInitn"
    })
    public static class PaymentInfo {

        @XmlElement(name = "CstmrCdtTrfInitn")
        protected PaymentInstructionInformation3 cstmrCdtTrfInitn;
        @XmlElement(name = "CstmrDrctDbtInitn")
        protected PaymentInstructionInformation4 cstmrDrctDbtInitn;

        /**
         * Gets the value of the cstmrCdtTrfInitn property.
         * 
         * @return
         *     possible object is
         *     {@link PaymentInstructionInformation3 }
         *     
         */
        public PaymentInstructionInformation3 getCstmrCdtTrfInitn() {
            return cstmrCdtTrfInitn;
        }

        /**
         * Sets the value of the cstmrCdtTrfInitn property.
         * 
         * @param value
         *     allowed object is
         *     {@link PaymentInstructionInformation3 }
         *     
         */
        public void setCstmrCdtTrfInitn(PaymentInstructionInformation3 value) {
            this.cstmrCdtTrfInitn = value;
        }

        /**
         * Gets the value of the cstmrDrctDbtInitn property.
         * 
         * @return
         *     possible object is
         *     {@link PaymentInstructionInformation4 }
         *     
         */
        public PaymentInstructionInformation4 getCstmrDrctDbtInitn() {
            return cstmrDrctDbtInitn;
        }

        /**
         * Sets the value of the cstmrDrctDbtInitn property.
         * 
         * @param value
         *     allowed object is
         *     {@link PaymentInstructionInformation4 }
         *     
         */
        public void setCstmrDrctDbtInitn(PaymentInstructionInformation4 value) {
            this.cstmrDrctDbtInitn = value;
        }

    }

}
