
package generated.ru.mos.rnip.xsd.charge._2_4;

import generated.ru.mos.rnip.xsd._package._2_1.ImportedChargeType;
import generated.ru.mos.rnip.xsd.charge._2_1.AbstractChargeType;
import generated.ru.mos.rnip.xsd.charge._2_1.Payer;
import generated.ru.mos.rnip.xsd.common._2_1.*;
import generated.ru.mos.rnip.xsd.organization._2_1.Payee;
import generated.ru.mos.rnip.xsd.services.export_charges._2_4.ExportChargesResponse;
import generated.ru.mos.rnip.xsd.services.export_charges._2_4.OffenseType;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * Данные нового начисления
 *
 * <p>Java class for ChargeType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ChargeType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Charge/2.4.0}AbstractChargeType">
 *       &lt;sequence>
 *         &lt;element name="LinkedChargesIdentifiers" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="SupplierBillID" type="{http://rnip.mos.ru/xsd/Common/2.4.0}SupplierBillIDType" maxOccurs="10"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Organization/2.4.0}Payee"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Charge/2.4.0}Payer"/>
 *         &lt;element name="BudgetIndex" type="{http://rnip.mos.ru/xsd/Common/2.4.0}BudgetIndexType"/>
 *         &lt;element name="ExecutiveProcedureInfo" type="{http://rnip.mos.ru/xsd/Common/2.4.0}ExecutiveProcedureInfoType" minOccurs="0"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Charge/2.4.0}AdditionalOffense" minOccurs="0"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.4.0}ChangeStatus"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.4.0}Discount" minOccurs="0"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.4.0}AdditionalData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://rnip.mos.ru/xsd/Charge/2.4.0}commonAttributeGroup"/>
 *       &lt;attribute name="supplierBillID" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://rnip.mos.ru/xsd/Common/2.4.0}SupplierBillIDType">
 *             &lt;pattern value="(\w{20})|(\d{25})"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="noticeTerm">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *             &lt;minInclusive value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="OKVED" type="{http://rnip.mos.ru/xsd/Charge/2.4.0}OKVEDType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChargeType", namespace = "http://rnip.mos.ru/xsd/Charge/2.4.0", propOrder = {
        "linkedChargesIdentifiers",
        "payee",
        "payer",
        "budgetIndex",
        "executiveProcedureInfo",
        "additionalOffense",
        "changeStatus",
        "discount",
        "additionalData"
})
@XmlSeeAlso({
        ImportedChargeType.class,
        ExportChargesResponse.ChargeInfo.class
})
public class ChargeType
        extends AbstractChargeType
{

    @XmlElement(name = "LinkedChargesIdentifiers")
    protected ChargeType.LinkedChargesIdentifiers linkedChargesIdentifiers;
    @XmlElement(name = "Payee", namespace = "http://rnip.mos.ru/xsd/Organization/2.4.0", required = true)
    protected Payee payee;
    @XmlElement(name = "Payer", required = true)
    protected Payer payer;
    @XmlElement(name = "BudgetIndex", required = true)
    protected BudgetIndexType budgetIndex;
    @XmlElement(name = "ExecutiveProcedureInfo")
    protected ExecutiveProcedureInfoType executiveProcedureInfo;
    @XmlElement(name = "AdditionalOffense")
    protected OffenseType additionalOffense;
    @XmlElement(name = "ChangeStatus", namespace = "http://rnip.mos.ru/xsd/Common/2.4.0", required = true)
    protected ChangeStatus changeStatus;
    @XmlElementRef(name = "Discount", namespace = "http://rnip.mos.ru/xsd/Common/2.4.0", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends DiscountType> discount;
    @XmlElement(name = "AdditionalData", namespace = "http://rnip.mos.ru/xsd/Common/2.4.0")
    protected List<AdditionalDataType> additionalData;
    @XmlAttribute(name = "supplierBillID", required = true)
    protected String supplierBillID;
    @XmlAttribute(name = "noticeTerm")
    protected BigInteger noticeTerm;
    @XmlAttribute(name = "OKVED")
    protected String okved;
    @XmlAttribute(name = "billDate", required = true)
    @XmlSchemaType(name = "dateTime")
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
    @XmlAttribute(name = "legalAct")
    protected String legalAct;
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
     * Данные организации, являющейся получателем средств
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
     * Gets the value of the payer property.
     *
     * @return
     *     possible object is
     *     {@link Payer }
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
     *     {@link Payer }
     *
     */
    public void setPayer(Payer value) {
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
     * Gets the value of the executiveProcedureInfo property.
     *
     * @return
     *     possible object is
     *     {@link ExecutiveProcedureInfoType }
     *
     */
    public ExecutiveProcedureInfoType getExecutiveProcedureInfo() {
        return executiveProcedureInfo;
    }

    /**
     * Sets the value of the executiveProcedureInfo property.
     *
     * @param value
     *     allowed object is
     *     {@link ExecutiveProcedureInfoType }
     *
     */
    public void setExecutiveProcedureInfo(ExecutiveProcedureInfoType value) {
        this.executiveProcedureInfo = value;
    }

    /**
     * Gets the value of the additionalOffense property.
     *
     * @return
     *     possible object is
     *     {@link OffenseType }
     *
     */
    public OffenseType getAdditionalOffense() {
        return additionalOffense;
    }

    /**
     * Sets the value of the additionalOffense property.
     *
     * @param value
     *     allowed object is
     *     {@link OffenseType }
     *
     */
    public void setAdditionalOffense(OffenseType value) {
        this.additionalOffense = value;
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
     * Дополнительные условия оплаты
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DiscountType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiplierSize }{@code >}
     *     {@link JAXBElement }{@code <}{@link DiscountSize }{@code >}
     *     {@link JAXBElement }{@code <}{@link DiscountFixed }{@code >}
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
     *     {@link JAXBElement }{@code <}{@link DiscountType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiplierSize }{@code >}
     *     {@link JAXBElement }{@code <}{@link DiscountSize }{@code >}
     *     {@link JAXBElement }{@code <}{@link DiscountFixed }{@code >}
     *
     */
    public void setDiscount(JAXBElement<? extends DiscountType> value) {
        this.discount = value;
    }

    /**
     * Дополнительные поля начисления Gets the value of the additionalData property.
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
     * Gets the value of the noticeTerm property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getNoticeTerm() {
        return noticeTerm;
    }

    /**
     * Sets the value of the noticeTerm property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setNoticeTerm(BigInteger value) {
        this.noticeTerm = value;
    }

    /**
     * Gets the value of the okved property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOKVED() {
        return okved;
    }

    /**
     * Sets the value of the okved property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOKVED(String value) {
        this.okved = value;
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
     * Gets the value of the legalAct property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLegalAct() {
        return legalAct;
    }

    /**
     * Sets the value of the legalAct property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLegalAct(String value) {
        this.legalAct = value;
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
     *         &lt;element name="SupplierBillID" type="{http://rnip.mos.ru/xsd/Common/2.4.0}SupplierBillIDType" maxOccurs="10"/>
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

        @XmlElement(name = "SupplierBillID", namespace = "http://rnip.mos.ru/xsd/Charge/2.4.0", required = true)
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

}
