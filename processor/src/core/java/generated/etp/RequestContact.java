
package generated.etp;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * ���.���� ���������
 * 
 * <p>Java class for RequestContact complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestContact">
 *   &lt;complexContent>
 *     &lt;extension base="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}BaseDeclarant">
 *       &lt;sequence>
 *         &lt;element name="LastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FirstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MiddleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Gender" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}GenderType"/>
 *         &lt;element name="BirthDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="Snils" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Inn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RegAddress" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}Address" minOccurs="0"/>
 *         &lt;element name="FactAddress" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}Address" minOccurs="0"/>
 *         &lt;element name="MobilePhone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="WorkPhone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HomePhone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EMail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Nation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Citizenship" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CitizenshipType" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}CitizenshipType"/>
 *         &lt;element name="CitizenshipCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BirthAddress" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}Address" minOccurs="0"/>
 *         &lt;element name="JobTitle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OMSNum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OMSDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="OMSCompany" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OMSValidityPeriod" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="SsoId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RegAddressType" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}RegType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestContact", propOrder = {
    "lastName",
    "firstName",
    "middleName",
    "gender",
    "birthDate",
    "snils",
    "inn",
    "regAddress",
    "factAddress",
    "mobilePhone",
    "workPhone",
    "homePhone",
    "eMail",
    "nation",
    "citizenship",
    "citizenshipType",
    "citizenshipCode",
    "birthAddress",
    "jobTitle",
    "omsNum",
    "omsDate",
    "omsCompany",
    "omsValidityPeriod",
    "ssoId",
    "regAddressType"
})
public class RequestContact
    extends BaseDeclarant
{

    @XmlElement(name = "LastName")
    protected String lastName;
    @XmlElement(name = "FirstName")
    protected String firstName;
    @XmlElement(name = "MiddleName")
    protected String middleName;
    @XmlElement(name = "Gender", required = true, nillable = true)
    protected GenderType gender;
    @XmlElement(name = "BirthDate", required = true, nillable = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar birthDate;
    @XmlElement(name = "Snils")
    protected String snils;
    @XmlElement(name = "Inn")
    protected String inn;
    @XmlElement(name = "RegAddress")
    protected Address regAddress;
    @XmlElement(name = "FactAddress")
    protected Address factAddress;
    @XmlElement(name = "MobilePhone")
    protected String mobilePhone;
    @XmlElement(name = "WorkPhone")
    protected String workPhone;
    @XmlElement(name = "HomePhone")
    protected String homePhone;
    @XmlElement(name = "EMail")
    protected String eMail;
    @XmlElement(name = "Nation")
    protected String nation;
    @XmlElement(name = "Citizenship")
    protected String citizenship;
    @XmlElement(name = "CitizenshipType", required = true, nillable = true)
    protected CitizenshipType citizenshipType;
    @XmlElement(name = "CitizenshipCode")
    protected String citizenshipCode;
    @XmlElement(name = "BirthAddress")
    protected Address birthAddress;
    @XmlElement(name = "JobTitle")
    protected String jobTitle;
    @XmlElement(name = "OMSNum")
    protected String omsNum;
    @XmlElementRef(name = "OMSDate", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> omsDate;
    @XmlElement(name = "OMSCompany")
    protected String omsCompany;
    @XmlElementRef(name = "OMSValidityPeriod", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> omsValidityPeriod;
    @XmlElement(name = "SsoId")
    protected String ssoId;
    @XmlElementRef(name = "RegAddressType", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", type = JAXBElement.class)
    protected JAXBElement<RegType> regAddressType;
    @XmlAttribute(name = "Id")
    protected String id;

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
     * Gets the value of the gender property.
     * 
     * @return
     *     possible object is
     *     {@link GenderType }
     *     
     */
    public GenderType getGender() {
        return gender;
    }

    /**
     * Sets the value of the gender property.
     * 
     * @param value
     *     allowed object is
     *     {@link GenderType }
     *     
     */
    public void setGender(GenderType value) {
        this.gender = value;
    }

    /**
     * Gets the value of the birthDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the value of the birthDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBirthDate(XMLGregorianCalendar value) {
        this.birthDate = value;
    }

    /**
     * Gets the value of the snils property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSnils() {
        return snils;
    }

    /**
     * Sets the value of the snils property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSnils(String value) {
        this.snils = value;
    }

    /**
     * Gets the value of the inn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInn() {
        return inn;
    }

    /**
     * Sets the value of the inn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInn(String value) {
        this.inn = value;
    }

    /**
     * Gets the value of the regAddress property.
     * 
     * @return
     *     possible object is
     *     {@link Address }
     *     
     */
    public Address getRegAddress() {
        return regAddress;
    }

    /**
     * Sets the value of the regAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link Address }
     *     
     */
    public void setRegAddress(Address value) {
        this.regAddress = value;
    }

    /**
     * Gets the value of the factAddress property.
     * 
     * @return
     *     possible object is
     *     {@link Address }
     *     
     */
    public Address getFactAddress() {
        return factAddress;
    }

    /**
     * Sets the value of the factAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link Address }
     *     
     */
    public void setFactAddress(Address value) {
        this.factAddress = value;
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
     * Gets the value of the workPhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWorkPhone() {
        return workPhone;
    }

    /**
     * Sets the value of the workPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWorkPhone(String value) {
        this.workPhone = value;
    }

    /**
     * Gets the value of the homePhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHomePhone() {
        return homePhone;
    }

    /**
     * Sets the value of the homePhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHomePhone(String value) {
        this.homePhone = value;
    }

    /**
     * Gets the value of the eMail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEMail() {
        return eMail;
    }

    /**
     * Sets the value of the eMail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEMail(String value) {
        this.eMail = value;
    }

    /**
     * Gets the value of the nation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNation() {
        return nation;
    }

    /**
     * Sets the value of the nation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNation(String value) {
        this.nation = value;
    }

    /**
     * Gets the value of the citizenship property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitizenship() {
        return citizenship;
    }

    /**
     * Sets the value of the citizenship property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitizenship(String value) {
        this.citizenship = value;
    }

    /**
     * Gets the value of the citizenshipType property.
     * 
     * @return
     *     possible object is
     *     {@link CitizenshipType }
     *     
     */
    public CitizenshipType getCitizenshipType() {
        return citizenshipType;
    }

    /**
     * Sets the value of the citizenshipType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CitizenshipType }
     *     
     */
    public void setCitizenshipType(CitizenshipType value) {
        this.citizenshipType = value;
    }

    /**
     * Gets the value of the citizenshipCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitizenshipCode() {
        return citizenshipCode;
    }

    /**
     * Sets the value of the citizenshipCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitizenshipCode(String value) {
        this.citizenshipCode = value;
    }

    /**
     * Gets the value of the birthAddress property.
     * 
     * @return
     *     possible object is
     *     {@link Address }
     *     
     */
    public Address getBirthAddress() {
        return birthAddress;
    }

    /**
     * Sets the value of the birthAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link Address }
     *     
     */
    public void setBirthAddress(Address value) {
        this.birthAddress = value;
    }

    /**
     * Gets the value of the jobTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJobTitle() {
        return jobTitle;
    }

    /**
     * Sets the value of the jobTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJobTitle(String value) {
        this.jobTitle = value;
    }

    /**
     * Gets the value of the omsNum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOMSNum() {
        return omsNum;
    }

    /**
     * Sets the value of the omsNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOMSNum(String value) {
        this.omsNum = value;
    }

    /**
     * Gets the value of the omsDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getOMSDate() {
        return omsDate;
    }

    /**
     * Sets the value of the omsDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setOMSDate(JAXBElement<XMLGregorianCalendar> value) {
        this.omsDate = value;
    }

    /**
     * Gets the value of the omsCompany property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOMSCompany() {
        return omsCompany;
    }

    /**
     * Sets the value of the omsCompany property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOMSCompany(String value) {
        this.omsCompany = value;
    }

    /**
     * Gets the value of the omsValidityPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getOMSValidityPeriod() {
        return omsValidityPeriod;
    }

    /**
     * Sets the value of the omsValidityPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setOMSValidityPeriod(JAXBElement<XMLGregorianCalendar> value) {
        this.omsValidityPeriod = value;
    }

    /**
     * Gets the value of the ssoId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSsoId() {
        return ssoId;
    }

    /**
     * Sets the value of the ssoId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSsoId(String value) {
        this.ssoId = value;
    }

    /**
     * Gets the value of the regAddressType property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link RegType }{@code >}
     *     
     */
    public JAXBElement<RegType> getRegAddressType() {
        return regAddressType;
    }

    /**
     * Sets the value of the regAddressType property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link RegType }{@code >}
     *     
     */
    public void setRegAddressType(JAXBElement<RegType> value) {
        this.regAddressType = value;
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

}
