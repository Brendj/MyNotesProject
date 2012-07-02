
package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ClientSummaryExt complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClientSummaryExt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="ContractId" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="DateOfContract" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="StateOfContract" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Balance" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="OverdraftLimit" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="ExpenditureLimit" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="FirstName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="LastName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="MiddleName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Grade" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="OfficialName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="LastEnterEventCode" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="LastEnterEventTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="NotifyViaSMS" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="NotifyViaEmail" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="MobilePhone" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Email" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="DefaultMerchantId" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="DefaultMerchantInfo" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClientSummaryExt")
public class ClientSummaryExt {

    @XmlAttribute(name = "ContractId")
    protected Long contractId;
    @XmlAttribute(name = "DateOfContract")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfContract;
    @XmlAttribute(name = "StateOfContract")
    protected String stateOfContract;
    @XmlAttribute(name = "Balance")
    protected Long balance;
    @XmlAttribute(name = "OverdraftLimit")
    protected Long overdraftLimit;
    @XmlAttribute(name = "ExpenditureLimit")
    protected Long expenditureLimit;
    @XmlAttribute(name = "FirstName")
    protected String firstName;
    @XmlAttribute(name = "LastName")
    protected String lastName;
    @XmlAttribute(name = "MiddleName")
    protected String middleName;
    @XmlAttribute(name = "Grade")
    protected String grade;
    @XmlAttribute(name = "OfficialName")
    protected String officialName;
    @XmlAttribute(name = "LastEnterEventCode")
    protected Integer lastEnterEventCode;
    @XmlAttribute(name = "LastEnterEventTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastEnterEventTime;
    @XmlAttribute(name = "NotifyViaSMS")
    protected Boolean notifyViaSMS;
    @XmlAttribute(name = "NotifyViaEmail")
    protected Boolean notifyViaEmail;
    @XmlAttribute(name = "MobilePhone")
    protected String mobilePhone;
    @XmlAttribute(name = "Email")
    protected String email;
    @XmlAttribute(name = "DefaultMerchantId")
    protected Long defaultMerchantId;
    @XmlAttribute(name = "DefaultMerchantInfo")
    protected String defaultMerchantInfo;

    /**
     * Gets the value of the contractId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getContractId() {
        return contractId;
    }

    /**
     * Sets the value of the contractId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setContractId(Long value) {
        this.contractId = value;
    }

    /**
     * Gets the value of the dateOfContract property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOfContract() {
        return dateOfContract;
    }

    /**
     * Sets the value of the dateOfContract property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOfContract(XMLGregorianCalendar value) {
        this.dateOfContract = value;
    }

    /**
     * Gets the value of the stateOfContract property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStateOfContract() {
        return stateOfContract;
    }

    /**
     * Sets the value of the stateOfContract property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStateOfContract(String value) {
        this.stateOfContract = value;
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
     * Gets the value of the overdraftLimit property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getOverdraftLimit() {
        return overdraftLimit;
    }

    /**
     * Sets the value of the overdraftLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setOverdraftLimit(Long value) {
        this.overdraftLimit = value;
    }

    /**
     * Gets the value of the expenditureLimit property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getExpenditureLimit() {
        return expenditureLimit;
    }

    /**
     * Sets the value of the expenditureLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setExpenditureLimit(Long value) {
        this.expenditureLimit = value;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the lastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /**
     * Gets the value of the middleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the value of the middleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiddleName(String value) {
        this.middleName = value;
    }

    /**
     * Gets the value of the grade property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGrade() {
        return grade;
    }

    /**
     * Sets the value of the grade property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGrade(String value) {
        this.grade = value;
    }

    /**
     * Gets the value of the officialName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOfficialName() {
        return officialName;
    }

    /**
     * Sets the value of the officialName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOfficialName(String value) {
        this.officialName = value;
    }

    /**
     * Gets the value of the lastEnterEventCode property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLastEnterEventCode() {
        return lastEnterEventCode;
    }

    /**
     * Sets the value of the lastEnterEventCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLastEnterEventCode(Integer value) {
        this.lastEnterEventCode = value;
    }

    /**
     * Gets the value of the lastEnterEventTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastEnterEventTime() {
        return lastEnterEventTime;
    }

    /**
     * Sets the value of the lastEnterEventTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastEnterEventTime(XMLGregorianCalendar value) {
        this.lastEnterEventTime = value;
    }

    /**
     * Gets the value of the notifyViaSMS property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNotifyViaSMS() {
        return notifyViaSMS;
    }

    /**
     * Sets the value of the notifyViaSMS property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNotifyViaSMS(Boolean value) {
        this.notifyViaSMS = value;
    }

    /**
     * Gets the value of the notifyViaEmail property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNotifyViaEmail() {
        return notifyViaEmail;
    }

    /**
     * Sets the value of the notifyViaEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNotifyViaEmail(Boolean value) {
        this.notifyViaEmail = value;
    }

    /**
     * Gets the value of the mobilePhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMobilePhone() {
        return mobilePhone;
    }

    /**
     * Sets the value of the mobilePhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMobilePhone(String value) {
        this.mobilePhone = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the defaultMerchantId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDefaultMerchantId() {
        return defaultMerchantId;
    }

    /**
     * Sets the value of the defaultMerchantId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDefaultMerchantId(Long value) {
        this.defaultMerchantId = value;
    }

    /**
     * Gets the value of the defaultMerchantInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultMerchantInfo() {
        return defaultMerchantInfo;
    }

    /**
     * Sets the value of the defaultMerchantInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultMerchantInfo(String value) {
        this.defaultMerchantInfo = value;
    }

}
