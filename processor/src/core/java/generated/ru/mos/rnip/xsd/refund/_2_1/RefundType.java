
package generated.ru.mos.rnip.xsd.refund._2_1;

import generated.ru.mos.rnip.xsd._package._2_1.ImportedRefundType;
import generated.ru.mos.rnip.xsd.common._2_1.AccountType;
import generated.ru.mos.rnip.xsd.common._2_1.AdditionalDataType;
import generated.ru.mos.rnip.xsd.common._2_1.ChangeStatus;
import generated.ru.mos.rnip.xsd.common._2_1.PayerType;
import generated.ru.mos.rnip.xsd.organization._2_1.RefundPayer;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * Базовый тип для возврата
 * 
 * <p>Java class for RefundType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RefundType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Organization/2.1.1}RefundPayer"/>
 *         &lt;element name="RefundApplication">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="appNum" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;minLength value="1"/>
 *                       &lt;maxLength value="15"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="appDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *                 &lt;attribute name="paymentId" use="required" type="{http://rnip.mos.ru/xsd/Common/2.1.1}PaymentIdType" />
 *                 &lt;attribute name="cashType" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                       &lt;enumeration value="1"/>
 *                       &lt;enumeration value="3"/>
 *                       &lt;enumeration value="4"/>
 *                       &lt;enumeration value="5"/>
 *                       &lt;enumeration value="6"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="amount" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedLong">
 *                       &lt;minInclusive value="1"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="purpose" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;maxLength value="210"/>
 *                       &lt;pattern value="\S+[\S\s]*\S+"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="RefundBasis">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="docKind" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;minLength value="1"/>
 *                       &lt;maxLength value="160"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="docNumber" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;pattern value="\d{1,6}"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="docDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="RefundPayee">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.1.1}PayerType">
 *                 &lt;sequence>
 *                   &lt;element name="BankAccountNumber" type="{http://rnip.mos.ru/xsd/Common/2.1.1}AccountType"/>
 *                   &lt;element name="PayeeAccount" type="{http://rnip.mos.ru/xsd/Common/2.1.1}PayeeAccountType" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="name" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;minLength value="1"/>
 *                       &lt;maxLength value="160"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="kbk" type="{http://rnip.mos.ru/xsd/Common/2.1.1}KBKType" />
 *                 &lt;attribute name="oktmo" type="{http://rnip.mos.ru/xsd/Common/2.1.1}OKTMOType" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.1.1}ChangeStatus"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.1.1}AdditionalData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="refundId" use="required" type="{http://rnip.mos.ru/xsd/Common/2.1.1}RefundIdType" />
 *       &lt;attribute name="refundDocDate" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="budgetLevel" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="1"/>
 *             &lt;enumeration value="2"/>
 *             &lt;enumeration value="3"/>
 *             &lt;enumeration value="4"/>
 *             &lt;enumeration value="5"/>
 *             &lt;enumeration value="6"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="kbk" type="{http://rnip.mos.ru/xsd/Common/2.1.1}KBKType" />
 *       &lt;attribute name="oktmo" type="{http://rnip.mos.ru/xsd/Common/2.1.1}OKTMOType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RefundType", propOrder = {
    "refundPayer",
    "refundApplication",
    "refundBasis",
    "refundPayee",
    "changeStatus",
    "additionalData"
})
@XmlSeeAlso({
    ImportedRefundType.class
})
public class RefundType {

    @XmlElement(name = "RefundPayer", namespace = "http://rnip.mos.ru/xsd/Organization/2.1.1", required = true)
    protected RefundPayer refundPayer;
    @XmlElement(name = "RefundApplication", required = true)
    protected RefundType.RefundApplication refundApplication;
    @XmlElement(name = "RefundBasis", required = true)
    protected RefundType.RefundBasis refundBasis;
    @XmlElement(name = "RefundPayee", required = true)
    protected RefundType.RefundPayee refundPayee;
    @XmlElement(name = "ChangeStatus", namespace = "http://rnip.mos.ru/xsd/Common/2.1.1", required = true)
    protected ChangeStatus changeStatus;
    @XmlElement(name = "AdditionalData", namespace = "http://rnip.mos.ru/xsd/Common/2.1.1")
    protected List<AdditionalDataType> additionalData;
    @XmlAttribute(name = "refundId", required = true)
    protected String refundId;
    @XmlAttribute(name = "refundDocDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar refundDocDate;
    @XmlAttribute(name = "budgetLevel", required = true)
    protected String budgetLevel;
    @XmlAttribute(name = "kbk")
    protected String kbk;
    @XmlAttribute(name = "oktmo")
    protected String oktmo;

    /**
     * Сведения об организации, осуществляющей возврат денежных средств
     * 					
     * 
     * @return
     *     possible object is
     *     {@link RefundPayer }
     *     
     */
    public RefundPayer getRefundPayer() {
        return refundPayer;
    }

    /**
     * Sets the value of the refundPayer property.
     * 
     * @param value
     *     allowed object is
     *     {@link RefundPayer }
     *     
     */
    public void setRefundPayer(RefundPayer value) {
        this.refundPayer = value;
    }

    /**
     * Gets the value of the refundApplication property.
     * 
     * @return
     *     possible object is
     *     {@link RefundType.RefundApplication }
     *     
     */
    public RefundType.RefundApplication getRefundApplication() {
        return refundApplication;
    }

    /**
     * Sets the value of the refundApplication property.
     * 
     * @param value
     *     allowed object is
     *     {@link RefundType.RefundApplication }
     *     
     */
    public void setRefundApplication(RefundType.RefundApplication value) {
        this.refundApplication = value;
    }

    /**
     * Gets the value of the refundBasis property.
     * 
     * @return
     *     possible object is
     *     {@link RefundType.RefundBasis }
     *     
     */
    public RefundType.RefundBasis getRefundBasis() {
        return refundBasis;
    }

    /**
     * Sets the value of the refundBasis property.
     * 
     * @param value
     *     allowed object is
     *     {@link RefundType.RefundBasis }
     *     
     */
    public void setRefundBasis(RefundType.RefundBasis value) {
        this.refundBasis = value;
    }

    /**
     * Gets the value of the refundPayee property.
     * 
     * @return
     *     possible object is
     *     {@link RefundType.RefundPayee }
     *     
     */
    public RefundType.RefundPayee getRefundPayee() {
        return refundPayee;
    }

    /**
     * Sets the value of the refundPayee property.
     * 
     * @param value
     *     allowed object is
     *     {@link RefundType.RefundPayee }
     *     
     */
    public void setRefundPayee(RefundType.RefundPayee value) {
        this.refundPayee = value;
    }

    /**
     * Сведения о статусе возврата и основаниях его изменения
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
     * Gets the value of the refundDocDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRefundDocDate() {
        return refundDocDate;
    }

    /**
     * Sets the value of the refundDocDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRefundDocDate(XMLGregorianCalendar value) {
        this.refundDocDate = value;
    }

    /**
     * Gets the value of the budgetLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBudgetLevel() {
        return budgetLevel;
    }

    /**
     * Sets the value of the budgetLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBudgetLevel(String value) {
        this.budgetLevel = value;
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
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="appNum" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;minLength value="1"/>
     *             &lt;maxLength value="15"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="appDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
     *       &lt;attribute name="paymentId" use="required" type="{http://rnip.mos.ru/xsd/Common/2.1.1}PaymentIdType" />
     *       &lt;attribute name="cashType" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *             &lt;enumeration value="1"/>
     *             &lt;enumeration value="3"/>
     *             &lt;enumeration value="4"/>
     *             &lt;enumeration value="5"/>
     *             &lt;enumeration value="6"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="amount" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedLong">
     *             &lt;minInclusive value="1"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="purpose" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;maxLength value="210"/>
     *             &lt;pattern value="\S+[\S\s]*\S+"/>
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
    public static class RefundApplication {

        @XmlAttribute(name = "appNum", required = true)
        protected String appNum;
        @XmlAttribute(name = "appDate", required = true)
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar appDate;
        @XmlAttribute(name = "paymentId", required = true)
        protected String paymentId;
        @XmlAttribute(name = "cashType", required = true)
        protected int cashType;
        @XmlAttribute(name = "amount", required = true)
        protected BigInteger amount;
        @XmlAttribute(name = "purpose", required = true)
        protected String purpose;

        /**
         * Gets the value of the appNum property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAppNum() {
            return appNum;
        }

        /**
         * Sets the value of the appNum property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAppNum(String value) {
            this.appNum = value;
        }

        /**
         * Gets the value of the appDate property.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getAppDate() {
            return appDate;
        }

        /**
         * Sets the value of the appDate property.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setAppDate(XMLGregorianCalendar value) {
            this.appDate = value;
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
         * Gets the value of the cashType property.
         * 
         */
        public int getCashType() {
            return cashType;
        }

        /**
         * Sets the value of the cashType property.
         * 
         */
        public void setCashType(int value) {
            this.cashType = value;
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
     *       &lt;attribute name="docKind" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;minLength value="1"/>
     *             &lt;maxLength value="160"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="docNumber" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;pattern value="\d{1,6}"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="docDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class RefundBasis {

        @XmlAttribute(name = "docKind", required = true)
        protected String docKind;
        @XmlAttribute(name = "docNumber", required = true)
        protected String docNumber;
        @XmlAttribute(name = "docDate", required = true)
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar docDate;

        /**
         * Gets the value of the docKind property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDocKind() {
            return docKind;
        }

        /**
         * Sets the value of the docKind property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDocKind(String value) {
            this.docKind = value;
        }

        /**
         * Gets the value of the docNumber property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDocNumber() {
            return docNumber;
        }

        /**
         * Sets the value of the docNumber property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDocNumber(String value) {
            this.docNumber = value;
        }

        /**
         * Gets the value of the docDate property.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getDocDate() {
            return docDate;
        }

        /**
         * Sets the value of the docDate property.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setDocDate(XMLGregorianCalendar value) {
            this.docDate = value;
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
     *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.1.1}PayerType">
     *       &lt;sequence>
     *         &lt;element name="BankAccountNumber" type="{http://rnip.mos.ru/xsd/Common/2.1.1}AccountType"/>
     *         &lt;element name="PayeeAccount" type="{http://rnip.mos.ru/xsd/Common/2.1.1}PayeeAccountType" minOccurs="0"/>
     *       &lt;/sequence>
     *       &lt;attribute name="name" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;minLength value="1"/>
     *             &lt;maxLength value="160"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="kbk" type="{http://rnip.mos.ru/xsd/Common/2.1.1}KBKType" />
     *       &lt;attribute name="oktmo" type="{http://rnip.mos.ru/xsd/Common/2.1.1}OKTMOType" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "bankAccountNumber",
        "payeeAccount"
    })
    public static class RefundPayee
        extends PayerType
    {

        @XmlElement(name = "BankAccountNumber", required = true)
        protected AccountType bankAccountNumber;
        @XmlElement(name = "PayeeAccount")
        protected String payeeAccount;
        @XmlAttribute(name = "name", required = true)
        protected String name;
        @XmlAttribute(name = "kbk")
        protected String kbk;
        @XmlAttribute(name = "oktmo")
        protected String oktmo;

        /**
         * Gets the value of the bankAccountNumber property.
         * 
         * @return
         *     possible object is
         *     {@link AccountType }
         *     
         */
        public AccountType getBankAccountNumber() {
            return bankAccountNumber;
        }

        /**
         * Sets the value of the bankAccountNumber property.
         * 
         * @param value
         *     allowed object is
         *     {@link AccountType }
         *     
         */
        public void setBankAccountNumber(AccountType value) {
            this.bankAccountNumber = value;
        }

        /**
         * Gets the value of the payeeAccount property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPayeeAccount() {
            return payeeAccount;
        }

        /**
         * Sets the value of the payeeAccount property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPayeeAccount(String value) {
            this.payeeAccount = value;
        }

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

    }

}
