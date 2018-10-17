
package generated.ru.mos.rnip.xsd.charge._2_0;

import generated.ru.mos.rnip.xsd._package._2_0.ImportedChargeType;
import generated.ru.mos.rnip.xsd.budgetindex._2_0.BudgetIndexType;
import generated.ru.mos.rnip.xsd.common._2_0.*;
import generated.ru.mos.rnip.xsd.organization._2_0.OrganizationType;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * ������ ���������� 
 * 
 * <p>Java class for ChargeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ChargeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LinkedChargesIdentifiers" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="SupplierBillID" type="{http://rnip.mos.ru/xsd/Common/2.0.1}SupplierBillIDType" maxOccurs="10"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Payee">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://rnip.mos.ru/xsd/Organization/2.0.1}OrganizationType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.0.1}OrgAccount"/>
 *                 &lt;/sequence>
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Payer">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.0.1}PayerType">
 *                 &lt;attribute name="additionalPayerIdentifier">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://rnip.mos.ru/xsd/Common/2.0.1}PayerIdentifierType">
 *                       &lt;pattern value="(1((0[1-9])|(1[0-5])|(2[12456789])|(3[0])|(99))[0-9a-zA-Z�-��-�]{19})|(200\d{14}[A-Z0-9]{2}\d{3})|(300\d{14}[A-Z0-9]{2}\d{3}|3[0]{7}\d{9}[A-Z0-9]{2}\d{3})|(4[0]{9}\d{12})"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="BudgetIndex" type="{http://rnip.mos.ru/xsd/BudgetIndex/2.0.1}BudgetIndexType"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.0.1}ChangeStatus"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.0.1}Discount" minOccurs="0"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.0.1}AdditionalData" maxOccurs="5" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="supplierBillID" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://rnip.mos.ru/xsd/Common/2.0.1}SupplierBillIDType">
 *             &lt;pattern value="(\w{20})|(\d{25})"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="billDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="validUntil" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="totalAmount" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
 *       &lt;attribute name="purpose" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="\S+[\S\s]*\S+"/>
 *             &lt;maxLength value="210"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="kbk" use="required" type="{http://rnip.mos.ru/xsd/Common/2.0.1}KBKType" />
 *       &lt;attribute name="oktmo" use="required" type="{http://rnip.mos.ru/xsd/Common/2.0.1}OKTMOType" />
 *       &lt;attribute name="deliveryDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="paymentTerm" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="origin">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="PRIOR"/>
 *             &lt;pattern value="TEMP"/>
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
@XmlType(name = "ChargeType", propOrder = {
    "linkedChargesIdentifiers",
    "payee",
    "payer",
    "budgetIndex",
    "changeStatus",
    "discount",
    "additionalData"
})
@XmlSeeAlso({
    ImportedChargeType.class,
    generated.ru.mos.rnip.xsd.services.export_charges._2_0.ExportChargesResponse.ChargeInfo.class
})
public class ChargeType {

    @XmlElement(name = "LinkedChargesIdentifiers")
    protected ChargeType.LinkedChargesIdentifiers linkedChargesIdentifiers;
    @XmlElement(name = "Payee", required = true)
    protected ChargeType.Payee payee;
    @XmlElement(name = "Payer", required = true)
    protected ChargeType.Payer payer;
    @XmlElement(name = "BudgetIndex", required = true)
    protected BudgetIndexType budgetIndex;
    @XmlElement(name = "ChangeStatus", namespace = "http://rnip.mos.ru/xsd/Common/2.0.1", required = true)
    protected ChangeStatus changeStatus;
    @XmlElementRef(name = "Discount", namespace = "http://rnip.mos.ru/xsd/Common/2.0.1", type = JAXBElement.class)
    protected JAXBElement<? extends DiscountType> discount;
    @XmlElement(name = "AdditionalData", namespace = "http://rnip.mos.ru/xsd/Common/2.0.1")
    protected List<AdditionalDataType> additionalData;
    @XmlAttribute(name = "supplierBillID", required = true)
    protected String supplierBillID;
    @XmlAttribute(name = "billDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar billDate;
    @XmlAttribute(name = "validUntil")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar validUntil;
    @XmlAttribute(name = "totalAmount", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger totalAmount;
    @XmlAttribute(name = "purpose", required = true)
    protected String purpose;
    @XmlAttribute(name = "kbk", required = true)
    protected String kbk;
    @XmlAttribute(name = "oktmo", required = true)
    protected String oktmo;
    @XmlAttribute(name = "deliveryDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar deliveryDate;
    @XmlAttribute(name = "paymentTerm")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar paymentTerm;
    @XmlAttribute(name = "origin")
    protected String origin;

    /**
     * Gets the value of the linkedChargesIdentifiers property.
     * 
     * @return
     *     possible object is
     *     {@link ChargeType.LinkedChargesIdentifiers }
     *     
     */
    public ChargeType.LinkedChargesIdentifiers getLinkedChargesIdentifiers() {
        return linkedChargesIdentifiers;
    }

    /**
     * Sets the value of the linkedChargesIdentifiers property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargeType.LinkedChargesIdentifiers }
     *     
     */
    public void setLinkedChargesIdentifiers(ChargeType.LinkedChargesIdentifiers value) {
        this.linkedChargesIdentifiers = value;
    }

    /**
     * Gets the value of the payee property.
     * 
     * @return
     *     possible object is
     *     {@link ChargeType.Payee }
     *     
     */
    public ChargeType.Payee getPayee() {
        return payee;
    }

    /**
     * Sets the value of the payee property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargeType.Payee }
     *     
     */
    public void setPayee(ChargeType.Payee value) {
        this.payee = value;
    }

    /**
     * Gets the value of the payer property.
     * 
     * @return
     *     possible object is
     *     {@link ChargeType.Payer }
     *     
     */
    public ChargeType.Payer getPayer() {
        return payer;
    }

    /**
     * Sets the value of the payer property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargeType.Payer }
     *     
     */
    public void setPayer(ChargeType.Payer value) {
        this.payer = value;
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
     * Gets the value of the changeStatus property.
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
     * �������������� ������� ������
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DiscountFixed }{@code >}
     *     {@link JAXBElement }{@code <}{@link DiscountSize }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiplierSize }{@code >}
     *     {@link JAXBElement }{@code <}{@link DiscountType }{@code >}
     *     
     */
    public JAXBElement<? extends DiscountType> getDiscount() {
        return discount;
    }

    /**
     * Sets the value of the discount property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link DiscountFixed }{@code >}
     *     {@link JAXBElement }{@code <}{@link DiscountSize }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiplierSize }{@code >}
     *     {@link JAXBElement }{@code <}{@link DiscountType }{@code >}
     *     
     */
    public void setDiscount(JAXBElement<? extends DiscountType> value) {
        this.discount = value;
    }

    /**
     * �������������� ���� ���������� Gets the value of the additionalData property.
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
     * Gets the value of the billDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBillDate() {
        return billDate;
    }

    /**
     * Sets the value of the billDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBillDate(XMLGregorianCalendar value) {
        this.billDate = value;
    }

    /**
     * Gets the value of the validUntil property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getValidUntil() {
        return validUntil;
    }

    /**
     * Sets the value of the validUntil property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setValidUntil(XMLGregorianCalendar value) {
        this.validUntil = value;
    }

    /**
     * Gets the value of the totalAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets the value of the totalAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTotalAmount(BigInteger value) {
        this.totalAmount = value;
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
     * Gets the value of the paymentTerm property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPaymentTerm() {
        return paymentTerm;
    }

    /**
     * Sets the value of the paymentTerm property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPaymentTerm(XMLGregorianCalendar value) {
        this.paymentTerm = value;
    }

    /**
     * Gets the value of the origin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * Sets the value of the origin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrigin(String value) {
        this.origin = value;
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
     *         &lt;element name="SupplierBillID" type="{http://rnip.mos.ru/xsd/Common/2.0.1}SupplierBillIDType" maxOccurs="10"/>
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
        "supplierBillID"
    })
    public static class LinkedChargesIdentifiers {

        @XmlElement(name = "SupplierBillID", required = true)
        protected List<String> supplierBillID;

        /**
         * Gets the value of the supplierBillID property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the supplierBillID property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSupplierBillID().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getSupplierBillID() {
            if (supplierBillID == null) {
                supplierBillID = new ArrayList<String>();
            }
            return this.supplierBillID;
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
     *     &lt;extension base="{http://rnip.mos.ru/xsd/Organization/2.0.1}OrganizationType">
     *       &lt;sequence>
     *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.0.1}OrgAccount"/>
     *       &lt;/sequence>
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "orgAccount"
    })
    public static class Payee
        extends OrganizationType
    {

        @XmlElement(name = "OrgAccount", namespace = "http://rnip.mos.ru/xsd/Common/2.0.1", required = true)
        protected OrgAccount orgAccount;

        /**
         * ��������� ����� �����������
         * 
         * @return
         *     possible object is
         *     {@link OrgAccount }
         *     
         */
        public OrgAccount getOrgAccount() {
            return orgAccount;
        }

        /**
         * Sets the value of the orgAccount property.
         * 
         * @param value
         *     allowed object is
         *     {@link OrgAccount }
         *     
         */
        public void setOrgAccount(OrgAccount value) {
            this.orgAccount = value;
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
     *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.0.1}PayerType">
     *       &lt;attribute name="additionalPayerIdentifier">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://rnip.mos.ru/xsd/Common/2.0.1}PayerIdentifierType">
     *             &lt;pattern value="(1((0[1-9])|(1[0-5])|(2[12456789])|(3[0])|(99))[0-9a-zA-Z�-��-�]{19})|(200\d{14}[A-Z0-9]{2}\d{3})|(300\d{14}[A-Z0-9]{2}\d{3}|3[0]{7}\d{9}[A-Z0-9]{2}\d{3})|(4[0]{9}\d{12})"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Payer
        extends PayerType
    {

        @XmlAttribute(name = "additionalPayerIdentifier")
        protected String additionalPayerIdentifier;

        /**
         * Gets the value of the additionalPayerIdentifier property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAdditionalPayerIdentifier() {
            return additionalPayerIdentifier;
        }

        /**
         * Sets the value of the additionalPayerIdentifier property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAdditionalPayerIdentifier(String value) {
            this.additionalPayerIdentifier = value;
        }

    }

}
