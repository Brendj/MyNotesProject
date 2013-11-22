
package generated.opc.ru.msk.schemas.uec.identification.v1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * Персональная информация
 * 
 * <p>Java class for HolderInfoDescriptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HolderInfoDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="personIds">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="personId" type="{http://schemas.msk.ru/uec/identification/v1}HolderIdDescriptionType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="clientCategory" type="{http://schemas.msk.ru/uec/identification/v1}ClientCategoryType" minOccurs="0"/>
 *         &lt;element name="personInfo" type="{http://schemas.msk.ru/uec/identification/v1}PersonInfoType" minOccurs="0"/>
 *         &lt;element name="registrationAddress" type="{http://schemas.msk.ru/uec/identification/v1}AddressType" minOccurs="0"/>
 *         &lt;element name="document" type="{http://schemas.msk.ru/uec/identification/v1}DocumentType" minOccurs="0"/>
 *         &lt;element name="residenceAddress" type="{http://schemas.msk.ru/uec/identification/v1}AddressType" minOccurs="0"/>
 *         &lt;element name="bank" type="{http://schemas.msk.ru/uec/identification/v1}BankType" minOccurs="0"/>
 *         &lt;element name="contacts" type="{http://schemas.msk.ru/uec/identification/v1}ContactsType" minOccurs="0"/>
 *         &lt;element name="oms" type="{http://schemas.msk.ru/uec/identification/v1}OMSType" minOccurs="0"/>
 *         &lt;element name="additionalInfo" type="{http://schemas.msk.ru/uec/identification/v1}AdditionalInfoType" minOccurs="0"/>
 *         &lt;element name="authorityPerson" type="{http://schemas.msk.ru/uec/identification/v1}AuthorityPersonType" minOccurs="0"/>
 *         &lt;element name="universityInfo" type="{http://schemas.msk.ru/uec/identification/v1}UniversityInfoType" minOccurs="0"/>
 *         &lt;element name="schoolInfo" type="{http://schemas.msk.ru/uec/identification/v1}SchoolInfoType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HolderInfoDescriptionType", propOrder = {
    "personIds",
    "clientCategory",
    "personInfo",
    "registrationAddress",
    "document",
    "residenceAddress",
    "bank",
    "contacts",
    "oms",
    "additionalInfo",
    "authorityPerson",
    "universityInfo",
    "schoolInfo"
})
public class HolderInfoDescriptionType {

    @XmlElement(required = true)
    protected HolderInfoDescriptionType.PersonIds personIds;
    protected ClientCategoryType clientCategory;
    protected PersonInfoType personInfo;
    protected AddressType registrationAddress;
    protected DocumentType document;
    @XmlElementRef(name = "residenceAddress", namespace = "http://schemas.msk.ru/uec/identification/v1", type = JAXBElement.class)
    protected JAXBElement<AddressType> residenceAddress;
    protected BankType bank;
    protected ContactsType contacts;
    @XmlElementRef(name = "oms", namespace = "http://schemas.msk.ru/uec/identification/v1", type = JAXBElement.class)
    protected JAXBElement<OMSType> oms;
    protected AdditionalInfoType additionalInfo;
    @XmlElementRef(name = "authorityPerson", namespace = "http://schemas.msk.ru/uec/identification/v1", type = JAXBElement.class)
    protected JAXBElement<AuthorityPersonType> authorityPerson;
    @XmlElementRef(name = "universityInfo", namespace = "http://schemas.msk.ru/uec/identification/v1", type = JAXBElement.class)
    protected JAXBElement<UniversityInfoType> universityInfo;
    @XmlElementRef(name = "schoolInfo", namespace = "http://schemas.msk.ru/uec/identification/v1", type = JAXBElement.class)
    protected JAXBElement<SchoolInfoType> schoolInfo;

    /**
     * Gets the value of the personIds property.
     * 
     * @return
     *     possible object is
     *     {@link HolderInfoDescriptionType.PersonIds }
     *     
     */
    public HolderInfoDescriptionType.PersonIds getPersonIds() {
        return personIds;
    }

    /**
     * Sets the value of the personIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link HolderInfoDescriptionType.PersonIds }
     *     
     */
    public void setPersonIds(HolderInfoDescriptionType.PersonIds value) {
        this.personIds = value;
    }

    /**
     * Gets the value of the clientCategory property.
     * 
     * @return
     *     possible object is
     *     {@link ClientCategoryType }
     *     
     */
    public ClientCategoryType getClientCategory() {
        return clientCategory;
    }

    /**
     * Sets the value of the clientCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClientCategoryType }
     *     
     */
    public void setClientCategory(ClientCategoryType value) {
        this.clientCategory = value;
    }

    /**
     * Gets the value of the personInfo property.
     * 
     * @return
     *     possible object is
     *     {@link PersonInfoType }
     *     
     */
    public PersonInfoType getPersonInfo() {
        return personInfo;
    }

    /**
     * Sets the value of the personInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonInfoType }
     *     
     */
    public void setPersonInfo(PersonInfoType value) {
        this.personInfo = value;
    }

    /**
     * Gets the value of the registrationAddress property.
     * 
     * @return
     *     possible object is
     *     {@link AddressType }
     *     
     */
    public AddressType getRegistrationAddress() {
        return registrationAddress;
    }

    /**
     * Sets the value of the registrationAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressType }
     *     
     */
    public void setRegistrationAddress(AddressType value) {
        this.registrationAddress = value;
    }

    /**
     * Gets the value of the document property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentType }
     *     
     */
    public DocumentType getDocument() {
        return document;
    }

    /**
     * Sets the value of the document property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentType }
     *     
     */
    public void setDocument(DocumentType value) {
        this.document = value;
    }

    /**
     * Gets the value of the residenceAddress property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AddressType }{@code >}
     *     
     */
    public JAXBElement<AddressType> getResidenceAddress() {
        return residenceAddress;
    }

    /**
     * Sets the value of the residenceAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AddressType }{@code >}
     *     
     */
    public void setResidenceAddress(JAXBElement<AddressType> value) {
        this.residenceAddress = ((JAXBElement<AddressType> ) value);
    }

    /**
     * Gets the value of the bank property.
     * 
     * @return
     *     possible object is
     *     {@link BankType }
     *     
     */
    public BankType getBank() {
        return bank;
    }

    /**
     * Sets the value of the bank property.
     * 
     * @param value
     *     allowed object is
     *     {@link BankType }
     *     
     */
    public void setBank(BankType value) {
        this.bank = value;
    }

    /**
     * Gets the value of the contacts property.
     * 
     * @return
     *     possible object is
     *     {@link ContactsType }
     *     
     */
    public ContactsType getContacts() {
        return contacts;
    }

    /**
     * Sets the value of the contacts property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactsType }
     *     
     */
    public void setContacts(ContactsType value) {
        this.contacts = value;
    }

    /**
     * Gets the value of the oms property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link OMSType }{@code >}
     *     
     */
    public JAXBElement<OMSType> getOms() {
        return oms;
    }

    /**
     * Sets the value of the oms property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link OMSType }{@code >}
     *     
     */
    public void setOms(JAXBElement<OMSType> value) {
        this.oms = ((JAXBElement<OMSType> ) value);
    }

    /**
     * Gets the value of the additionalInfo property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalInfoType }
     *     
     */
    public AdditionalInfoType getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Sets the value of the additionalInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalInfoType }
     *     
     */
    public void setAdditionalInfo(AdditionalInfoType value) {
        this.additionalInfo = value;
    }

    /**
     * Gets the value of the authorityPerson property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AuthorityPersonType }{@code >}
     *     
     */
    public JAXBElement<AuthorityPersonType> getAuthorityPerson() {
        return authorityPerson;
    }

    /**
     * Sets the value of the authorityPerson property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AuthorityPersonType }{@code >}
     *     
     */
    public void setAuthorityPerson(JAXBElement<AuthorityPersonType> value) {
        this.authorityPerson = ((JAXBElement<AuthorityPersonType> ) value);
    }

    /**
     * Gets the value of the universityInfo property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link UniversityInfoType }{@code >}
     *     
     */
    public JAXBElement<UniversityInfoType> getUniversityInfo() {
        return universityInfo;
    }

    /**
     * Sets the value of the universityInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link UniversityInfoType }{@code >}
     *     
     */
    public void setUniversityInfo(JAXBElement<UniversityInfoType> value) {
        this.universityInfo = ((JAXBElement<UniversityInfoType> ) value);
    }

    /**
     * Gets the value of the schoolInfo property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SchoolInfoType }{@code >}
     *     
     */
    public JAXBElement<SchoolInfoType> getSchoolInfo() {
        return schoolInfo;
    }

    /**
     * Sets the value of the schoolInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SchoolInfoType }{@code >}
     *     
     */
    public void setSchoolInfo(JAXBElement<SchoolInfoType> value) {
        this.schoolInfo = ((JAXBElement<SchoolInfoType> ) value);
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
     *         &lt;element name="personId" type="{http://schemas.msk.ru/uec/identification/v1}HolderIdDescriptionType" maxOccurs="unbounded"/>
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
        "personId"
    })
    public static class PersonIds {

        @XmlElement(required = true)
        protected List<HolderIdDescriptionType> personId;

        /**
         * Gets the value of the personId property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the personId property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPersonId().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link HolderIdDescriptionType }
         * 
         * 
         */
        public List<HolderIdDescriptionType> getPersonId() {
            if (personId == null) {
                personId = new ArrayList<HolderIdDescriptionType>();
            }
            return this.personId;
        }

    }

}
