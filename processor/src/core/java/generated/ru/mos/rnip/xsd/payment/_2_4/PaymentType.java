
package generated.ru.mos.rnip.xsd.payment._2_4;

import generated.ru.mos.rnip.xsd._package._2_1.ImportedPaymentType;
import generated.ru.mos.rnip.xsd.charge._2_1.Payer;
import generated.ru.mos.rnip.xsd.common._2_1.AccDocType;
import generated.ru.mos.rnip.xsd.common._2_1.AdditionalDataType;
import generated.ru.mos.rnip.xsd.common._2_1.BudgetIndexType;
import generated.ru.mos.rnip.xsd.common._2_1.ChangeStatus;
import generated.ru.mos.rnip.xsd.organization._2_1.Payee;
import generated.ru.mos.rnip.xsd.organization._2_1.PaymentOrgType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.income.IncomeType;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * Базовый тип для платежа
 *
 * <p>Java class for PaymentBaseType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PaymentBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PaymentOrg" type="{http://rnip.mos.ru/xsd/Organization/2.4.0}PaymentOrgType"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Payment/2.4.0}Payer" minOccurs="0"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Organization/2.4.0}Payee"/>
 *         &lt;element name="BudgetIndex" type="{http://rnip.mos.ru/xsd/Common/2.4.0}BudgetIndexType" minOccurs="0"/>
 *         &lt;element name="AccDoc" type="{http://rnip.mos.ru/xsd/Common/2.4.0}AccDocType" minOccurs="0"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.4.0}ChangeStatus"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.4.0}AdditionalData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="supplierBillID">
 *         &lt;simpleType>
 *           &lt;union memberTypes=" {http://rnip.mos.ru/xsd/Common/2.4.0}SupplierBillIDType">
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
 *             &lt;pattern value="\S+([\S\s]*\S+)*"/>
 *             &lt;maxLength value="210"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="amount" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
 *       &lt;attribute name="receiptDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="paymentExecDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="kbk" type="{http://rnip.mos.ru/xsd/Common/2.4.0}KBKType" />
 *       &lt;attribute name="oktmo" type="{http://rnip.mos.ru/xsd/Common/2.4.0}OKTMOType" />
 *       &lt;attribute name="transKind" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}TransKindType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentBaseType", namespace = "http://rnip.mos.ru/xsd/Payment/2.4.0", propOrder = {
        "paymentOrg",
        "payer",
        "payee",
        "budgetIndex",
        "accDoc",
        "changeStatus",
        "additionalData"
})
@XmlSeeAlso({
        PaymentType.class,
        IncomeType.class
})
public class PaymentType {

    @XmlElement(name = "PaymentOrg", required = true)
    protected PaymentOrgType paymentOrg;
    @XmlElement(name = "Payer")
    protected Payer payer;
    @XmlElement(name = "Payee", namespace = "http://rnip.mos.ru/xsd/Organization/2.4.0", required = true)
    protected Payee payee;
    @XmlElement(name = "BudgetIndex")
    protected BudgetIndexType budgetIndex;
    @XmlElement(name = "AccDoc")
    protected AccDocType accDoc;
    @XmlElement(name = "ChangeStatus", namespace = "http://rnip.mos.ru/xsd/Common/2.4.0", required = true)
    protected ChangeStatus changeStatus;
    @XmlElement(name = "AdditionalData", namespace = "http://rnip.mos.ru/xsd/Common/2.4.0")
    protected List<AdditionalDataType> additionalData;
    @XmlAttribute(name = "supplierBillID")
    protected String supplierBillID;
    @XmlAttribute(name = "purpose", required = true)
    protected String purpose;
    @XmlAttribute(name = "amount", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger amount;
    @XmlAttribute(name = "receiptDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar receiptDate;
    @XmlAttribute(name = "paymentExecDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar paymentExecDate;
    @XmlAttribute(name = "kbk")
    protected String kbk;
    @XmlAttribute(name = "oktmo")
    protected String oktmo;
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
     * Сведения о плательщике
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
     * Сведения о получателе средств
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
     * Сведения о статусе платежа и основаниях его изменения
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
     * Дополнительные поля Gets the value of the additionalData property.
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
     * Gets the value of the paymentExecDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getPaymentExecDate() {
        return paymentExecDate;
    }

    /**
     * Sets the value of the paymentExecDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setPaymentExecDate(XMLGregorianCalendar value) {
        this.paymentExecDate = value;
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

}