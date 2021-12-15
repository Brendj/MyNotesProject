
package generated.ru.mos.rnip.xsd.charge._2_4;

import generated.ru.mos.rnip.xsd.charge._2_1.AbstractChargeType;
import generated.ru.mos.rnip.xsd.charge._2_1.Payer;
import generated.ru.mos.rnip.xsd.common._2_1.*;
import generated.ru.mos.rnip.xsd.organization._2_1.Payee;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * Данные шаблона формирования начисления
 * 
 * <p>Java class for ChargeTemplateType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ChargeTemplateType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Charge/2.1.1}AbstractChargeType">
 *       &lt;sequence>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Organization/2.1.1}Payee"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Charge/2.1.1}Payer"/>
 *         &lt;element name="BudgetIndex" type="{http://rnip.mos.ru/xsd/Common/2.1.1}BudgetIndexType"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.1.1}Discount" minOccurs="0"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.1.1}AdditionalData" maxOccurs="10" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://rnip.mos.ru/xsd/Charge/2.1.1}commonAttributeGroup"/>
 *       &lt;attribute name="supplierBillID">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://rnip.mos.ru/xsd/Common/2.1.1}SupplierBillIDType">
 *             &lt;pattern value="(\w{20})|(\d{25})"/>
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
@XmlType(name = "ChargeTemplateType", propOrder = {
    "payee",
    "payer",
    "budgetIndex",
    "discount",
    "additionalData"
})
public class ChargeTemplateType
    extends AbstractChargeType
{

    @XmlElement(name = "Payee", namespace = "http://rnip.mos.ru/xsd/Organization/2.1.1", required = true)
    protected Payee payee;
    @XmlElement(name = "Payer", required = true)
    protected generated.ru.mos.rnip.xsd.charge._2_1.Payer payer;
    @XmlElement(name = "BudgetIndex", required = true)
    protected BudgetIndexType budgetIndex;
    @XmlElementRef(name = "Discount", namespace = "http://rnip.mos.ru/xsd/Common/2.1.1", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends DiscountType> discount;
    @XmlElement(name = "AdditionalData", namespace = "http://rnip.mos.ru/xsd/Common/2.1.1")
    protected List<AdditionalDataType> additionalData;
    @XmlAttribute(name = "supplierBillID")
    protected String supplierBillID;
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
     * Сведения о плательщике
     * 
     * @return
     *     possible object is
     *     {@link generated.ru.mos.rnip.xsd.charge._2_1.Payer }
     *     
     */
    public generated.ru.mos.rnip.xsd.charge._2_1.Payer getPayer() {
        return payer;
    }

    /**
     * Sets the value of the payer property.
     * 
     * @param value
     *     allowed object is
     *     {@link generated.ru.mos.rnip.xsd.charge._2_1.Payer }
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
     * Дополнительные условия оплаты
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DiscountType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DiscountFixed }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiplierSize }{@code >}
     *     {@link JAXBElement }{@code <}{@link DiscountSize }{@code >}
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
     *     {@link JAXBElement }{@code <}{@link DiscountFixed }{@code >}
     *     {@link JAXBElement }{@code <}{@link MultiplierSize }{@code >}
     *     {@link JAXBElement }{@code <}{@link DiscountSize }{@code >}
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

}
