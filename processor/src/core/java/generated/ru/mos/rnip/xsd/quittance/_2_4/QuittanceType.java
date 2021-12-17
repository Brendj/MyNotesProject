
package generated.ru.mos.rnip.xsd.quittance._2_4;

import generated.ru.mos.rnip.xsd.common._2_1.DiscountFixed;
import generated.ru.mos.rnip.xsd.common._2_1.DiscountSize;
import generated.ru.mos.rnip.xsd.common._2_1.DiscountType;
import generated.ru.mos.rnip.xsd.common._2_1.MultiplierSize;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/* Квитанция
         *
         * <p>Java class for QuittanceType complex type.
        *
        * <p>The following schema fragment specifies the expected content contained within this class.
        *
        * <pre>
 * &lt;complexType name="QuittanceType">
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence minOccurs="0">
         *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.4.0}Discount" minOccurs="0"/>
         *         &lt;element name="Refund" maxOccurs="20" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;attribute name="refundId" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}RefundIdType" />
         *                 &lt;attribute name="amount" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="supplierBillID" use="required">
         *         &lt;simpleType>
         *           &lt;union memberTypes=" {http://rnip.mos.ru/xsd/Common/2.4.0}SupplierBillIDType">
         *             &lt;simpleType>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *                 &lt;pattern value="\d{15}"/>
         *               &lt;/restriction>
         *             &lt;/simpleType>
         *           &lt;/union>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="totalAmount" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
         *       &lt;attribute name="creationDate" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
         *       &lt;attribute name="billStatus" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}AcknowledgmentStatusType" />
         *       &lt;attribute name="balance" type="{http://www.w3.org/2001/XMLSchema}long" />
         *       &lt;attribute name="paymentId" use="required">
         *         &lt;simpleType>
         *           &lt;union memberTypes=" {http://rnip.mos.ru/xsd/Common/2.4.0}PaymentIdType">
         *             &lt;simpleType>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *                 &lt;enumeration value="PaymentNotLoaded"/>
         *               &lt;/restriction>
         *             &lt;/simpleType>
         *           &lt;/union>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="amountPayment" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
         *       &lt;attribute name="payeeINN" type="{http://rnip.mos.ru/xsd/Common/2.4.0}INNType" />
         *       &lt;attribute name="payeeKPP">
         *         &lt;simpleType>
         *           &lt;union memberTypes=" {http://rnip.mos.ru/xsd/Common/2.4.0}KPPType {http://www.w3.org/2001/XMLSchema}string">
         *           &lt;/union>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="kbk" type="{http://rnip.mos.ru/xsd/Common/2.4.0}KBKType" />
         *       &lt;attribute name="oktmo" type="{http://rnip.mos.ru/xsd/Common/2.4.0}OKTMOType" />
         *       &lt;attribute name="payerIdentifier">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *             &lt;maxLength value="22"/>
         *             &lt;minLength value="1"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="accountNumber" type="{http://rnip.mos.ru/xsd/Common/2.4.0}AccountNumType" />
         *       &lt;attribute name="bik" type="{http://rnip.mos.ru/xsd/Common/2.4.0}BIKType" />
         *       &lt;attribute name="isRevoked" type="{http://www.w3.org/2001/XMLSchema}boolean" />
         *       &lt;attribute name="paymentPortal" type="{http://www.w3.org/2001/XMLSchema}boolean" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
        *
        *
        */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuittanceType", namespace = "http://rnip.mos.ru/xsd/Quittance/2.4.0", propOrder = {
        "discount",
        "refund"
})
public class QuittanceType {

    @XmlElementRef(name = "Discount", namespace = "http://rnip.mos.ru/xsd/Common/2.4.0", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends DiscountType> discount;
    @XmlElement(name = "Refund")
    protected List<Refund> refund;
    @XmlAttribute(name = "supplierBillID", required = true)
    protected String supplierBillID;
    @XmlAttribute(name = "totalAmount")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger totalAmount;
    @XmlAttribute(name = "creationDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creationDate;
    @XmlAttribute(name = "billStatus", required = true)
    protected String billStatus;
    @XmlAttribute(name = "balance")
    protected Long balance;
    @XmlAttribute(name = "paymentId", required = true)
    protected String paymentId;
    @XmlAttribute(name = "amountPayment")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger amountPayment;
    @XmlAttribute(name = "payeeINN")
    protected String payeeINN;
    @XmlAttribute(name = "payeeKPP")
    protected String payeeKPP;
    @XmlAttribute(name = "kbk")
    protected String kbk;
    @XmlAttribute(name = "oktmo")
    protected String oktmo;
    @XmlAttribute(name = "payerIdentifier")
    protected String payerIdentifier;
    @XmlAttribute(name = "accountNumber")
    protected String accountNumber;
    @XmlAttribute(name = "bik")
    protected String bik;
    @XmlAttribute(name = "isRevoked")
    protected Boolean isRevoked;
    @XmlAttribute(name = "paymentPortal")
    protected Boolean paymentPortal;

    /**
     * Дополнительные условия оплаты
     *
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
     * Gets the value of the refund property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the refund property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRefund().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Refund }
     *
     *
     */
    public List<Refund> getRefund() {
        if (refund == null) {
            refund = new ArrayList<Refund>();
        }
        return this.refund;
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
     * Gets the value of the creationDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the value of the creationDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setCreationDate(XMLGregorianCalendar value) {
        this.creationDate = value;
    }

    /**
     * Gets the value of the billStatus property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBillStatus() {
        return billStatus;
    }

    /**
     * Sets the value of the billStatus property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBillStatus(String value) {
        this.billStatus = value;
    }

    /**
     * Gets the value of the balance property.
     *
     * @return
     *     possible object is
     *     {@link Long }
     *
     */
    public Long getBalance() {
        return balance;
    }

    /**
     * Sets the value of the balance property.
     *
     * @param value
     *     allowed object is
     *     {@link Long }
     *
     */
    public void setBalance(Long value) {
        this.balance = value;
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
     * Gets the value of the amountPayment property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getAmountPayment() {
        return amountPayment;
    }

    /**
     * Sets the value of the amountPayment property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setAmountPayment(BigInteger value) {
        this.amountPayment = value;
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
     * Gets the value of the accountNumber property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the value of the accountNumber property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAccountNumber(String value) {
        this.accountNumber = value;
    }

    /**
     * Gets the value of the bik property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBik() {
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
    public void setBik(String value) {
        this.bik = value;
    }

    /**
     * Gets the value of the isRevoked property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isIsRevoked() {
        return isRevoked;
    }

    /**
     * Sets the value of the isRevoked property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setIsRevoked(Boolean value) {
        this.isRevoked = value;
    }

    /**
     * Gets the value of the paymentPortal property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isPaymentPortal() {
        return paymentPortal;
    }

    /**
     * Sets the value of the paymentPortal property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setPaymentPortal(Boolean value) {
        this.paymentPortal = value;
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
     *       &lt;attribute name="refundId" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}RefundIdType" />
     *       &lt;attribute name="amount" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Refund {

        @XmlAttribute(name = "refundId", required = true)
        protected String refundId;
        @XmlAttribute(name = "amount", required = true)
        @XmlSchemaType(name = "unsignedLong")
        protected BigInteger amount;

        /**
         * Gets the value of the refundId property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getRefundId() {
            return refundId;
        }

        /**
         * Sets the value of the refundId property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setRefundId(String value) {
            this.refundId = value;
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

    }

}
