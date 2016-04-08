
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.quittance;

import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.paymentinfo.PaymentIdentificationDataType;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * ���������
 * 
 * <p>Java class for QuittanceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QuittanceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SupplierBillID">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *               &lt;pattern value="\c{20}"/>
 *               &lt;pattern value="\c{25}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CreationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="BillStatus">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="1"/>
 *               &lt;enumeration value="1"/>
 *               &lt;enumeration value="2"/>
 *               &lt;enumeration value="3"/>
 *               &lt;enumeration value="4"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="payeeINN" type="{http://roskazna.ru/gisgmp/xsd/116/Common}INNType" minOccurs="0"/>
 *         &lt;element name="payeeKPP" type="{http://roskazna.ru/gisgmp/xsd/116/Common}KPPType" minOccurs="0"/>
 *         &lt;element name="KBK" type="{http://roskazna.ru/gisgmp/xsd/116/Common}KBKType" minOccurs="0"/>
 *         &lt;element name="OKTMO" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{0,11}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Balance" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="PayerIdentifier" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *               &lt;minLength value="1"/>
 *               &lt;maxLength value="25"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="AccountNumber" type="{http://roskazna.ru/gisgmp/xsd/116/Common}AccountNumType" minOccurs="0"/>
 *         &lt;element name="BIK" type="{http://roskazna.ru/gisgmp/xsd/116/Common}BIKType" minOccurs="0"/>
 *         &lt;element name="PaymentIdentificationData" type="{http://roskazna.ru/gisgmp/xsd/116/PaymentInfo}PaymentIdentificationDataType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuittanceType", propOrder = {
    "supplierBillID",
    "creationDate",
    "billStatus",
    "payeeINN",
    "payeeKPP",
    "kbk",
    "oktmo",
    "balance",
    "payerIdentifier",
    "accountNumber",
    "bik",
    "paymentIdentificationData"
})
@XmlSeeAlso({
        generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.doacknowledgment.DoAcknowledgmentResponseType.Quittances.Quittance.class,
        generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.exportquittanceresponse.ExportQuittanceResponseType.Quittances.Quittance.class
})
public class QuittanceType {

    @XmlElement(name = "SupplierBillID", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String supplierBillID;
    @XmlElement(name = "CreationDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar creationDate;
    @XmlElement(name = "BillStatus", required = true)
    protected String billStatus;
    protected String payeeINN;
    protected String payeeKPP;
    @XmlElement(name = "KBK")
    protected String kbk;
    @XmlElement(name = "OKTMO")
    protected String oktmo;
    @XmlElement(name = "Balance")
    protected Long balance;
    @XmlElement(name = "PayerIdentifier")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String payerIdentifier;
    @XmlElement(name = "AccountNumber")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String accountNumber;
    @XmlElement(name = "BIK")
    protected String bik;
    @XmlElement(name = "PaymentIdentificationData", required = true)
    protected PaymentIdentificationDataType paymentIdentificationData;

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
     * Gets the value of the creationDate property.
     * 
     * @return
     *     possible object is
     *     {@link javax.xml.datatype.XMLGregorianCalendar }
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
     *     {@link javax.xml.datatype.XMLGregorianCalendar }
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
    public String getBIK() {
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
    public void setBIK(String value) {
        this.bik = value;
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

}
