
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.paymentinfo;

import generated.smev.gisgmp.wsdl.org.w3._2000._09.xmldsig_.SignatureType;
import generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.budgetindex.BudgetIndexType;
import generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.common.AdditionalDataType;
import generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.organization.AccountType;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * ������� ��� ��� �������/������ ����������
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
 *         &lt;element name="SupplierBillID">
 *           &lt;simpleType>
 *             &lt;union memberTypes=" {http://roskazna.ru/gisgmp/xsd/116/Common}SupplierBillIDType">
 *               &lt;simpleType>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                   &lt;pattern value="0"/>
 *                   &lt;pattern value="\d{15}"/>
 *                 &lt;/restriction>
 *               &lt;/simpleType>
 *             &lt;/union>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Narrative">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="210"/>
 *               &lt;minLength value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Amount">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedLong">
 *               &lt;minInclusive value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="PaymentDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="ReceiptDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="BudgetIndex" type="{http://roskazna.ru/gisgmp/xsd/116/BudgetIndex}BudgetIndexType"/>
 *         &lt;element name="PaymentIdentificationData" type="{http://roskazna.ru/gisgmp/xsd/116/PaymentInfo}PaymentIdentificationDataType"/>
 *         &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/PaymentInfo}AccDoc" minOccurs="0"/>
 *         &lt;element name="Payer">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Common}PayerIdentifier"/>
 *                   &lt;element name="PayerName" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="160"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="PayerAccount" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="20"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Payee">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="PayeeName">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="500"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="payeeINN" type="{http://roskazna.ru/gisgmp/xsd/116/Common}INNType"/>
 *                   &lt;element name="payeeKPP" type="{http://roskazna.ru/gisgmp/xsd/116/Common}KPPType"/>
 *                   &lt;element name="PayeeBankAcc" type="{http://roskazna.ru/gisgmp/xsd/116/Organization}AccountType"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="RecipientServicesIdentifier" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="1"/>
 *               &lt;maxLength value="25"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="PayerPA" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="1"/>
 *               &lt;maxLength value="50"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ChangeStatus">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence minOccurs="0">
 *                   &lt;element name="Reason">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="512"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="meaning" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;length value="1"/>
 *                       &lt;enumeration value="1"/>
 *                       &lt;enumeration value="2"/>
 *                       &lt;enumeration value="3"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KBK" type="{http://roskazna.ru/gisgmp/xsd/116/Common}KBKType"/>
 *         &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Common}TransKind" minOccurs="0"/>
 *         &lt;element name="TransContent" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="16"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="PaytCondition" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *               &lt;totalDigits value="1"/>
 *               &lt;minInclusive value="1"/>
 *               &lt;enumeration value="1"/>
 *               &lt;enumeration value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="AcptTerm" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *               &lt;totalDigits value="1"/>
 *               &lt;minInclusive value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="MaturityDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="DocDispatchDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="PartialPayt" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="PaytNo" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="3"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Common}TransKind"/>
 *                   &lt;element name="SumResidualPayt" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *                         &lt;totalDigits value="18"/>
 *                         &lt;minInclusive value="0"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/PaymentInfo}AccDoc"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Priority" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="1"/>
 *               &lt;pattern value="[0-6]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="OKTMO" type="{http://roskazna.ru/gisgmp/xsd/116/Common}OKTMOType"/>
 *         &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Common}AdditionalData" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}Signature"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}ID">
 *             &lt;maxLength value="50"/>
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
@XmlType(name = "PaymentType", propOrder = {
    "supplierBillID",
    "narrative",
    "amount",
    "paymentDate",
    "receiptDate",
    "budgetIndex",
    "paymentIdentificationData",
    "accDoc",
    "payer",
    "payee",
    "recipientServicesIdentifier",
    "payerPA",
    "changeStatus",
    "kbk",
    "transKind",
    "transContent",
    "paytCondition",
    "acptTerm",
    "maturityDate",
    "docDispatchDate",
    "partialPayt",
    "priority",
    "oktmo",
    "additionalData",
    "signature"
})
public class PaymentType {

    @XmlElement(name = "SupplierBillID", required = true)
    protected String supplierBillID;
    @XmlElement(name = "Narrative", required = true)
    protected String narrative;
    @XmlElement(name = "Amount", required = true)
    protected BigInteger amount;
    @XmlElement(name = "PaymentDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar paymentDate;
    @XmlElement(name = "ReceiptDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar receiptDate;
    @XmlElement(name = "BudgetIndex", required = true)
    protected BudgetIndexType budgetIndex;
    @XmlElement(name = "PaymentIdentificationData", required = true)
    protected PaymentIdentificationDataType paymentIdentificationData;
    @XmlElement(name = "AccDoc")
    protected AccDoc accDoc;
    @XmlElement(name = "Payer", required = true)
    protected Payer payer;
    @XmlElement(name = "Payee", required = true)
    protected Payee payee;
    @XmlElement(name = "RecipientServicesIdentifier")
    protected String recipientServicesIdentifier;
    @XmlElement(name = "PayerPA")
    protected String payerPA;
    @XmlElement(name = "ChangeStatus", required = true)
    protected ChangeStatus changeStatus;
    @XmlElement(name = "KBK", required = true)
    protected String kbk;
    @XmlElement(name = "TransKind", namespace = "http://roskazna.ru/gisgmp/xsd/116/Common")
    protected String transKind;
    @XmlElement(name = "TransContent")
    protected String transContent;
    @XmlElement(name = "PaytCondition")
    protected BigInteger paytCondition;
    @XmlElement(name = "AcptTerm")
    protected BigInteger acptTerm;
    @XmlElement(name = "MaturityDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar maturityDate;
    @XmlElement(name = "DocDispatchDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar docDispatchDate;
    @XmlElement(name = "PartialPayt")
    protected PartialPayt partialPayt;
    @XmlElement(name = "Priority")
    protected String priority;
    @XmlElement(name = "OKTMO", required = true)
    protected String oktmo;
    @XmlElement(name = "AdditionalData", namespace = "http://roskazna.ru/gisgmp/xsd/116/Common")
    protected List<AdditionalDataType> additionalData;
    @XmlElement(name = "Signature", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected SignatureType signature;
    @XmlAttribute(name = "Id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    protected String id;

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
     * Gets the value of the narrative property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNarrative() {
        return narrative;
    }

    /**
     * Sets the value of the narrative property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNarrative(String value) {
        this.narrative = value;
    }

    /**
     * Gets the value of the amount property.
     * 
     * @return
     *     possible object is
     *     {@link java.math.BigInteger }
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
     *     {@link java.math.BigInteger }
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
     *     {@link javax.xml.datatype.XMLGregorianCalendar }
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
     *     {@link javax.xml.datatype.XMLGregorianCalendar }
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
     *     {@link javax.xml.datatype.XMLGregorianCalendar }
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
     *     {@link javax.xml.datatype.XMLGregorianCalendar }
     *     
     */
    public void setReceiptDate(XMLGregorianCalendar value) {
        this.receiptDate = value;
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
     * Gets the value of the paymentIdentificationData property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentIdentificationDataType }
     *     
     */
    public PaymentIdentificationDataType getPaymentIdentificationData() {
        return paymentIdentificationData;
    }

    /**
     * Sets the value of the paymentIdentificationData property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentIdentificationDataType }
     *     
     */
    public void setPaymentIdentificationData(PaymentIdentificationDataType value) {
        this.paymentIdentificationData = value;
    }

    /**
     * Gets the value of the accDoc property.
     * 
     * @return
     *     possible object is
     *     {@link AccDoc }
     *     
     */
    public AccDoc getAccDoc() {
        return accDoc;
    }

    /**
     * Sets the value of the accDoc property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccDoc }
     *     
     */
    public void setAccDoc(AccDoc value) {
        this.accDoc = value;
    }

    /**
     * Gets the value of the payer property.
     * 
     * @return
     *     possible object is
     *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.paymentinfo.PaymentType.Payer }
     *     
     */
    public Payer getPayer() {
        return payer;
    }

    /**
     * Sets the value of the payer property.
     * 
     * @param value
     *     allowed object is
     *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.paymentinfo.PaymentType.Payer }
     *     
     */
    public void setPayer(Payer value) {
        this.payer = value;
    }

    /**
     * Gets the value of the payee property.
     * 
     * @return
     *     possible object is
     *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.paymentinfo.PaymentType.Payee }
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
     *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.paymentinfo.PaymentType.Payee }
     *     
     */
    public void setPayee(Payee value) {
        this.payee = value;
    }

    /**
     * Gets the value of the recipientServicesIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecipientServicesIdentifier() {
        return recipientServicesIdentifier;
    }

    /**
     * Sets the value of the recipientServicesIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecipientServicesIdentifier(String value) {
        this.recipientServicesIdentifier = value;
    }

    /**
     * Gets the value of the payerPA property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPayerPA() {
        return payerPA;
    }

    /**
     * Sets the value of the payerPA property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPayerPA(String value) {
        this.payerPA = value;
    }

    /**
     * Gets the value of the changeStatus property.
     * 
     * @return
     *     possible object is
     *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.paymentinfo.PaymentType.ChangeStatus }
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
     *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.paymentinfo.PaymentType.ChangeStatus }
     *     
     */
    public void setChangeStatus(ChangeStatus value) {
        this.changeStatus = value;
    }

    /**
     * Gets the value of the kbk property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKBK() {
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
    public void setKBK(String value) {
        this.kbk = value;
    }

    /**
     * ��� ��������.
     * ����������� ���� ���������� ���������. ��������� ��������: 
     *  01 ���������� ���������; 
     *  06 - ���������� ���������; 
     *  02 - ��������� ����������;
     *  16 - ��������� �����;
     * �� � ��������� �������� ��
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
     * Gets the value of the paytCondition property.
     * 
     * @return
     *     possible object is
     *     {@link java.math.BigInteger }
     *     
     */
    public BigInteger getPaytCondition() {
        return paytCondition;
    }

    /**
     * Sets the value of the paytCondition property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.math.BigInteger }
     *     
     */
    public void setPaytCondition(BigInteger value) {
        this.paytCondition = value;
    }

    /**
     * Gets the value of the acptTerm property.
     * 
     * @return
     *     possible object is
     *     {@link java.math.BigInteger }
     *     
     */
    public BigInteger getAcptTerm() {
        return acptTerm;
    }

    /**
     * Sets the value of the acptTerm property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.math.BigInteger }
     *     
     */
    public void setAcptTerm(BigInteger value) {
        this.acptTerm = value;
    }

    /**
     * Gets the value of the maturityDate property.
     * 
     * @return
     *     possible object is
     *     {@link javax.xml.datatype.XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMaturityDate() {
        return maturityDate;
    }

    /**
     * Sets the value of the maturityDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link javax.xml.datatype.XMLGregorianCalendar }
     *     
     */
    public void setMaturityDate(XMLGregorianCalendar value) {
        this.maturityDate = value;
    }

    /**
     * Gets the value of the docDispatchDate property.
     * 
     * @return
     *     possible object is
     *     {@link javax.xml.datatype.XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDocDispatchDate() {
        return docDispatchDate;
    }

    /**
     * Sets the value of the docDispatchDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link javax.xml.datatype.XMLGregorianCalendar }
     *     
     */
    public void setDocDispatchDate(XMLGregorianCalendar value) {
        this.docDispatchDate = value;
    }

    /**
     * Gets the value of the partialPayt property.
     * 
     * @return
     *     possible object is
     *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.paymentinfo.PaymentType.PartialPayt }
     *     
     */
    public PartialPayt getPartialPayt() {
        return partialPayt;
    }

    /**
     * Sets the value of the partialPayt property.
     * 
     * @param value
     *     allowed object is
     *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.paymentinfo.PaymentType.PartialPayt }
     *     
     */
    public void setPartialPayt(PartialPayt value) {
        this.partialPayt = value;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPriority(String value) {
        this.priority = value;
    }

    /**
     * Gets the value of the oktmo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOKTMO() {
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
    public void setOKTMO(String value) {
        this.oktmo = value;
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
     * �� xml-���������
     * 
     * @return
     *     possible object is
     *     {@link SignatureType }
     *     
     */
    public SignatureType getSignature() {
        return signature;
    }

    /**
     * Sets the value of the signature property.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureType }
     *     
     */
    public void setSignature(SignatureType value) {
        this.signature = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
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
     *       &lt;sequence minOccurs="0">
     *         &lt;element name="Reason">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="512"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="meaning" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;length value="1"/>
     *             &lt;enumeration value="1"/>
     *             &lt;enumeration value="2"/>
     *             &lt;enumeration value="3"/>
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
    @XmlType(name = "", propOrder = {
        "reason"
    })
    public static class ChangeStatus {

        @XmlElement(name = "Reason")
        protected String reason;
        @XmlAttribute(required = true)
        protected String meaning;

        /**
         * Gets the value of the reason property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getReason() {
            return reason;
        }

        /**
         * Sets the value of the reason property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setReason(String value) {
            this.reason = value;
        }

        /**
         * Gets the value of the meaning property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMeaning() {
            return meaning;
        }

        /**
         * Sets the value of the meaning property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMeaning(String value) {
            this.meaning = value;
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
     *       &lt;sequence>
     *         &lt;element name="PaytNo" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="3"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Common}TransKind"/>
     *         &lt;element name="SumResidualPayt" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
     *               &lt;totalDigits value="18"/>
     *               &lt;minInclusive value="0"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/PaymentInfo}AccDoc"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "paytNo",
        "transKind",
        "sumResidualPayt",
        "accDoc"
    })
    public static class PartialPayt {

        @XmlElement(name = "PaytNo")
        protected String paytNo;
        @XmlElement(name = "TransKind", namespace = "http://roskazna.ru/gisgmp/xsd/116/Common", required = true)
        protected String transKind;
        @XmlElement(name = "SumResidualPayt")
        protected BigInteger sumResidualPayt;
        @XmlElement(name = "AccDoc", required = true)
        protected AccDoc accDoc;

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
         * ��� ��������
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
         * Gets the value of the sumResidualPayt property.
         * 
         * @return
         *     possible object is
         *     {@link java.math.BigInteger }
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
         *     {@link java.math.BigInteger }
         *     
         */
        public void setSumResidualPayt(BigInteger value) {
            this.sumResidualPayt = value;
        }

        /**
         * Gets the value of the accDoc property.
         * 
         * @return
         *     possible object is
         *     {@link AccDoc }
         *     
         */
        public AccDoc getAccDoc() {
            return accDoc;
        }

        /**
         * Sets the value of the accDoc property.
         * 
         * @param value
         *     allowed object is
         *     {@link AccDoc }
         *     
         */
        public void setAccDoc(AccDoc value) {
            this.accDoc = value;
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
     *       &lt;sequence>
     *         &lt;element name="PayeeName">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="500"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="payeeINN" type="{http://roskazna.ru/gisgmp/xsd/116/Common}INNType"/>
     *         &lt;element name="payeeKPP" type="{http://roskazna.ru/gisgmp/xsd/116/Common}KPPType"/>
     *         &lt;element name="PayeeBankAcc" type="{http://roskazna.ru/gisgmp/xsd/116/Organization}AccountType"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "payeeName",
        "payeeINN",
        "payeeKPP",
        "payeeBankAcc"
    })
    public static class Payee {

        @XmlElement(name = "PayeeName", required = true)
        protected String payeeName;
        @XmlElement(required = true)
        protected String payeeINN;
        @XmlElement(required = true)
        protected String payeeKPP;
        @XmlElement(name = "PayeeBankAcc", required = true)
        protected AccountType payeeBankAcc;

        /**
         * Gets the value of the payeeName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPayeeName() {
            return payeeName;
        }

        /**
         * Sets the value of the payeeName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPayeeName(String value) {
            this.payeeName = value;
        }

        /**
         * Gets the value of the payeeINN property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPayeeINN() {
            return payeeINN;
        }

        /**
         * Sets the value of the payeeINN property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPayeeINN(String value) {
            this.payeeINN = value;
        }

        /**
         * Gets the value of the payeeKPP property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPayeeKPP() {
            return payeeKPP;
        }

        /**
         * Sets the value of the payeeKPP property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPayeeKPP(String value) {
            this.payeeKPP = value;
        }

        /**
         * Gets the value of the payeeBankAcc property.
         * 
         * @return
         *     possible object is
         *     {@link AccountType }
         *     
         */
        public AccountType getPayeeBankAcc() {
            return payeeBankAcc;
        }

        /**
         * Sets the value of the payeeBankAcc property.
         * 
         * @param value
         *     allowed object is
         *     {@link AccountType }
         *     
         */
        public void setPayeeBankAcc(AccountType value) {
            this.payeeBankAcc = value;
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
     *       &lt;sequence>
     *         &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Common}PayerIdentifier"/>
     *         &lt;element name="PayerName" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="160"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="PayerAccount" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="20"/>
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
    @XmlType(name = "", propOrder = {
        "payerIdentifier",
        "payerName",
        "payerAccount"
    })
    public static class Payer {

        @XmlElement(name = "PayerIdentifier", namespace = "http://roskazna.ru/gisgmp/xsd/116/Common", required = true)
        protected String payerIdentifier;
        @XmlElement(name = "PayerName")
        protected String payerName;
        @XmlElement(name = "PayerAccount")
        protected String payerAccount;

        /**
         * ������������� �����������
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
