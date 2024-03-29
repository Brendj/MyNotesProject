
package ru.axetta.soap.test;

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
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="DateOfContract" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="StateOfContract" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Balance" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="OverdraftLimit" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="ExpenditureLimit" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="FirstName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Grade" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="OfficialName" type="{http://www.w3.org/2001/XMLSchema}string" />
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
    @XmlAttribute(name = "Grade")
    protected String grade;
    @XmlAttribute(name = "OfficialName")
    protected String officialName;

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

}
