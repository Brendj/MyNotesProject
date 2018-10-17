
package generated.ru.mos.rnip.xsd.payment._2_0;

import generated.ru.mos.rnip.xsd._package._2_0.ImportedPaymentType;
import generated.ru.mos.rnip.xsd.budgetindex._2_0.BudgetIndexType;
import generated.ru.mos.rnip.xsd.common._2_0.AccDocType;
import generated.ru.mos.rnip.xsd.common._2_0.AdditionalDataType;
import generated.ru.mos.rnip.xsd.common._2_0.ChangeStatus;
import generated.ru.mos.rnip.xsd.organization._2_0.Payee;
import generated.ru.mos.rnip.xsd.organization._2_0.PaymentOrgType;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * ������� ��� ��� �������
 * 
 * <p>Java class for PaymentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PaymentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PaymentOrg" type="{http://rnip.mos.ru/xsd/Organization/2.0.1}PaymentOrgType"/>
 *         &lt;element name="Payer" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="payerIdentifier" use="required">
 *                   &lt;simpleType>
 *                     &lt;union memberTypes=" {http://rnip.mos.ru/xsd/Common/2.0.1}PayerIdentifierType">
 *                       &lt;simpleType>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                           &lt;pattern value="0"/>
 *                         &lt;/restriction>
 *                       &lt;/simpleType>
 *                     &lt;/union>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="payerName">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;maxLength value="160"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="payerAccount">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;maxLength value="20"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Organization/2.0.1}Payee"/>
 *         &lt;element name="BudgetIndex" type="{http://rnip.mos.ru/xsd/BudgetIndex/2.0.1}BudgetIndexType" minOccurs="0"/>
 *         &lt;element name="AccDoc" type="{http://rnip.mos.ru/xsd/Common/2.0.1}AccDocType"/>
 *         &lt;element name="PartialPayt" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AccDoc" type="{http://rnip.mos.ru/xsd/Common/2.0.1}AccDocType"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="transKind" use="required" type="{http://rnip.mos.ru/xsd/Common/2.0.1}TransKindType" />
 *                 &lt;attribute name="paytNo">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;maxLength value="3"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="transContent">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;maxLength value="16"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="sumResidualPayt" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.0.1}ChangeStatus"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.0.1}AdditionalData" maxOccurs="5" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="paymentId" use="required" type="{http://rnip.mos.ru/xsd/Common/2.0.1}PaymentIdType" />
 *       &lt;attribute name="supplierBillID">
 *         &lt;simpleType>
 *           &lt;union memberTypes=" {http://rnip.mos.ru/xsd/Common/2.0.1}SupplierBillIDType">
 *             &lt;simpleType>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                 &lt;pattern value="\d{15}"/>
 *                 &lt;pattern value="0"/>
 *               &lt;/restriction>
 *             &lt;/simpleType>
 *           &lt;/union>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="purpose" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="\S+[\S\s]*\S+"/>
 *             &lt;maxLength value="210"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="amount" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
 *       &lt;attribute name="paymentDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="receiptDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="kbk" type="{http://rnip.mos.ru/xsd/Common/2.0.1}KBKType" />
 *       &lt;attribute name="oktmo" type="{http://rnip.mos.ru/xsd/Common/2.0.1}OKTMOType" />
 *       &lt;attribute name="deliveryDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="ESIA_ID">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="\d{3,10}"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="transKind" use="required" type="{http://rnip.mos.ru/xsd/Common/2.0.1}TransKindType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentType", propOrder = {
    "paymentOrg",
    "payer",
    "payee",
    "budgetIndex",
    "accDoc",
    "partialPayt",
    "changeStatus",
    "additionalData"
})
@XmlSeeAlso({
    ImportedPaymentType.class,
    generated.ru.mos.rnip.xsd.services.export_payments._2_0.ExportPaymentsResponse.PaymentInfo.class
})
public class PaymentType {

    @XmlElement(name = "PaymentOrg", required = true)
    protected PaymentOrgType paymentOrg;
    @XmlElement(name = "Payer")
    protected PaymentType.Payer payer;
    @XmlElement(name = "Payee", namespace = "http://rnip.mos.ru/xsd/Organization/2.0.1", required = true)
    protected Payee payee;
    @XmlElement(name = "BudgetIndex")
    protected BudgetIndexType budgetIndex;
    @XmlElement(name = "AccDoc", required = true)
    protected AccDocType accDoc;
    @XmlElement(name = "PartialPayt")
    protected PaymentType.PartialPayt partialPayt;
    @XmlElement(name = "ChangeStatus", namespace = "http://rnip.mos.ru/xsd/Common/2.0.1", required = true)
    protected ChangeStatus changeStatus;
    @XmlElement(name = "AdditionalData", namespace = "http://rnip.mos.ru/xsd/Common/2.0.1")
    protected List<AdditionalDataType> additionalData;
    @XmlAttribute(name = "paymentId", required = true)
    protected String paymentId;
    @XmlAttribute(name = "supplierBillID")
    protected String supplierBillID;
    @XmlAttribute(name = "purpose", required = true)
    protected String purpose;
    @XmlAttribute(name = "amount", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger amount;
    @XmlAttribute(name = "paymentDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar paymentDate;
    @XmlAttribute(name = "receiptDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar receiptDate;
    @XmlAttribute(name = "kbk")
    protected String kbk;
    @XmlAttribute(name = "oktmo")
    protected String oktmo;
    @XmlAttribute(name = "deliveryDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar deliveryDate;
    @XmlAttribute(name = "ESIA_ID")
    protected String esiaid;
    @XmlAttribute(name = "transKind", required = true)
    protected String transKind;

    /**
     * Gets the value of the paymentOrg property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentOrgType }
     *     
     */
    public PaymentOrgType getPaymentOrg() {
        return paymentOrg;
    }

    /**
     * Sets the value of the paymentOrg property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentOrgType }
     *     
     */
    public void setPaymentOrg(PaymentOrgType value) {
        this.paymentOrg = value;
    }

    /**
     * Gets the value of the payer property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentType.Payer }
     *     
     */
    public PaymentType.Payer getPayer() {
        return payer;
    }

    /**
     * Sets the value of the payer property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentType.Payer }
     *     
     */
    public void setPayer(PaymentType.Payer value) {
        this.payer = value;
    }

    /**
     * �������� � ���������� �������
     * 
     * @return
     *     possible object is
     *     {@link Payee }
     *     
     */
    public Payee getPayee() {
        return payee;
    }

    /**
     * Sets the value of the payee property.
     * 
     * @param value
     *     allowed object is
     *     {@link Payee }
     *     
     */
    public void setPayee(Payee value) {
        this.payee = value;
    }

    /**
     * Gets the value of the budgetIndex property.
     * 
     * @return
     *     possible object is
     *     {@link BudgetIndexType }
     *     
     */
    public BudgetIndexType getBudgetIndex() {
        return budgetIndex;
    }

    /**
     * Sets the value of the budgetIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link BudgetIndexType }
     *     
     */
    public void setBudgetIndex(BudgetIndexType value) {
        this.budgetIndex = value;
    }

    /**
     * Gets the value of the accDoc property.
     * 
     * @return
     *     possible object is
     *     {@link AccDocType }
     *     
     */
    public AccDocType getAccDoc() {
        return accDoc;
    }

    /**
     * Sets the value of the accDoc property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccDocType }
     *     
     */
    public void setAccDoc(AccDocType value) {
        this.accDoc = value;
    }

    /**
     * Gets the value of the partialPayt property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentType.PartialPayt }
     *     
     */
    public PaymentType.PartialPayt getPartialPayt() {
        return partialPayt;
    }

    /**
     * Sets the value of the partialPayt property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentType.PartialPayt }
     *     
     */
    public void setPartialPayt(PaymentType.PartialPayt value) {
        this.partialPayt = value;
    }

    /**
     * �������� � ������� ������� � ���������� ��� ���������
     * 
     * @return
     *     possible object is
     *     {@link ChangeStatus }
     *     
     */
    public ChangeStatus getChangeStatus() {
        return changeStatus;
    }

    /**
     * Sets the value of the changeStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChangeStatus }
     *     
     */
    public void setChangeStatus(ChangeStatus value) {
        this.changeStatus = value;
    }

    /**
     * �������������� ���� Gets the value of the additionalData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the additionalData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AdditionalDataType }
     * 
     * 
     */
    public List<AdditionalDataType> getAdditionalData() {
        if (additionalData == null) {
            additionalData = new ArrayList<AdditionalDataType>();
        }
        return this.additionalData;
    }

    /**
     * Gets the value of the paymentId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentId() {
        return paymentId;
    }

    /**
     * Sets the value of the paymentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentId(String value) {
        this.paymentId = value;
    }

    /**
     * Gets the value of the supplierBillID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSupplierBillID() {
        return supplierBillID;
    }

    /**
     * Sets the value of the supplierBillID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSupplierBillID(String value) {
        this.supplierBillID = value;
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
     * Gets the value of the amount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAmount(BigInteger value) {
        this.amount = value;
    }

    /**
     * Gets the value of the paymentDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPaymentDate() {
        return paymentDate;
    }

    /**
     * Sets the value of the paymentDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPaymentDate(XMLGregorianCalendar value) {
        this.paymentDate = value;
    }

    /**
     * Gets the value of the receiptDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReceiptDate() {
        return receiptDate;
    }

    /**
     * Sets the value of the receiptDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReceiptDate(XMLGregorianCalendar value) {
        this.receiptDate = value;
    }

    /**
     * Gets the value of the kbk property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKbk() {
        return kbk;
    }

    /**
     * Sets the value of the kbk property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKbk(String value) {
        this.kbk = value;
    }

    /**
     * Gets the value of the oktmo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOktmo() {
        return oktmo;
    }

    /**
     * Sets the value of the oktmo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOktmo(String value) {
        this.oktmo = value;
    }

    /**
     * Gets the value of the deliveryDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDeliveryDate() {
        return deliveryDate;
    }

    /**
     * Sets the value of the deliveryDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDeliveryDate(XMLGregorianCalendar value) {
        this.deliveryDate = value;
    }

    /**
     * Gets the value of the esiaid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getESIAID() {
        return esiaid;
    }

    /**
     * Sets the value of the esiaid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setESIAID(String value) {
        this.esiaid = value;
    }

    /**
     * Gets the value of the transKind property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransKind() {
        return transKind;
    }

    /**
     * Sets the value of the transKind property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransKind(String value) {
        this.transKind = value;
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
     *       &lt;sequence>
     *         &lt;element name="AccDoc" type="{http://rnip.mos.ru/xsd/Common/2.0.1}AccDocType"/>
     *       &lt;/sequence>
     *       &lt;attribute name="transKind" use="required" type="{http://rnip.mos.ru/xsd/Common/2.0.1}TransKindType" />
     *       &lt;attribute name="paytNo">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;maxLength value="3"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="transContent">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;maxLength value="16"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="sumResidualPayt" type="{http://www.w3.org/2001/XMLSchema}integer" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "accDoc"
    })
    public static class PartialPayt {

        @XmlElement(name = "AccDoc", required = true)
        protected AccDocType accDoc;
        @XmlAttribute(name = "transKind", required = true)
        protected String transKind;
        @XmlAttribute(name = "paytNo")
        protected String paytNo;
        @XmlAttribute(name = "transContent")
        protected String transContent;
        @XmlAttribute(name = "sumResidualPayt")
        protected BigInteger sumResidualPayt;

        /**
         * Gets the value of the accDoc property.
         * 
         * @return
         *     possible object is
         *     {@link AccDocType }
         *     
         */
        public AccDocType getAccDoc() {
            return accDoc;
        }

        /**
         * Sets the value of the accDoc property.
         * 
         * @param value
         *     allowed object is
         *     {@link AccDocType }
         *     
         */
        public void setAccDoc(AccDocType value) {
            this.accDoc = value;
        }

        /**
         * Gets the value of the transKind property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTransKind() {
            return transKind;
        }

        /**
         * Sets the value of the transKind property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTransKind(String value) {
            this.transKind = value;
        }

        /**
         * Gets the value of the paytNo property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPaytNo() {
            return paytNo;
        }

        /**
         * Sets the value of the paytNo property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPaytNo(String value) {
            this.paytNo = value;
        }

        /**
         * Gets the value of the transContent property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTransContent() {
            return transContent;
        }

        /**
         * Sets the value of the transContent property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTransContent(String value) {
            this.transContent = value;
        }

        /**
         * Gets the value of the sumResidualPayt property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getSumResidualPayt() {
            return sumResidualPayt;
        }

        /**
         * Sets the value of the sumResidualPayt property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setSumResidualPayt(BigInteger value) {
            this.sumResidualPayt = value;
        }

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
     *       &lt;attribute name="payerIdentifier" use="required">
     *         &lt;simpleType>
     *           &lt;union memberTypes=" {http://rnip.mos.ru/xsd/Common/2.0.1}PayerIdentifierType">
     *             &lt;simpleType>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                 &lt;pattern value="0"/>
     *               &lt;/restriction>
     *             &lt;/simpleType>
     *           &lt;/union>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="payerName">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;maxLength value="160"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="payerAccount">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;maxLength value="20"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Payer {

        @XmlAttribute(name = "payerIdentifier", required = true)
        protected String payerIdentifier;
        @XmlAttribute(name = "payerName")
        protected String payerName;
        @XmlAttribute(name = "payerAccount")
        protected String payerAccount;

        /**
         * Gets the value of the payerIdentifier property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPayerIdentifier() {
            return payerIdentifier;
        }

        /**
         * Sets the value of the payerIdentifier property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPayerIdentifier(String value) {
            this.payerIdentifier = value;
        }

        /**
         * Gets the value of the payerName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPayerName() {
            return payerName;
        }

        /**
         * Sets the value of the payerName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPayerName(String value) {
            this.payerName = value;
        }

        /**
         * Gets the value of the payerAccount property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPayerAccount() {
            return payerAccount;
        }

        /**
         * Sets the value of the payerAccount property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPayerAccount(String value) {
            this.payerAccount = value;
        }

    }

}
