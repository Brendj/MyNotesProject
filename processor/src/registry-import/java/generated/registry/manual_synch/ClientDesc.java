
package generated.registry.manual_synch;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for clientDesc complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="clientDesc">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="recId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="contractSurname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractSecondName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractDoc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="surname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="secondName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="doc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mobilePhone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="group" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="snils" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="notifyBySms" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="notifyByEmail" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="comments" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cardNo" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="cardPrintedNo" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="cardType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="cardExpiry" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="cardIssued" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "clientDesc", propOrder = {
    "recId",
    "contractSurname",
    "contractName",
    "contractSecondName",
    "contractDoc",
    "surname",
    "name",
    "secondName",
    "doc",
    "address",
    "phone",
    "mobilePhone",
    "email",
    "group",
    "snils",
    "notifyBySms",
    "notifyByEmail",
    "comments",
    "cardNo",
    "cardPrintedNo",
    "cardType",
    "cardExpiry",
    "cardIssued"
})
public class ClientDesc {

    protected int recId;
    protected String contractSurname;
    protected String contractName;
    protected String contractSecondName;
    protected String contractDoc;
    protected String surname;
    protected String name;
    protected String secondName;
    protected String doc;
    protected String address;
    protected String phone;
    protected String mobilePhone;
    protected String email;
    protected String group;
    protected String snils;
    protected boolean notifyBySms;
    protected boolean notifyByEmail;
    protected String comments;
    protected Long cardNo;
    protected Long cardPrintedNo;
    protected int cardType;
    protected XMLGregorianCalendar cardExpiry;
    protected XMLGregorianCalendar cardIssued;

    /**
     * Gets the value of the recId property.
     * 
     */
    public int getRecId() {
        return recId;
    }

    /**
     * Sets the value of the recId property.
     * 
     */
    public void setRecId(int value) {
        this.recId = value;
    }

    /**
     * Gets the value of the contractSurname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractSurname() {
        return contractSurname;
    }

    /**
     * Sets the value of the contractSurname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractSurname(String value) {
        this.contractSurname = value;
    }

    /**
     * Gets the value of the contractName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractName() {
        return contractName;
    }

    /**
     * Sets the value of the contractName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractName(String value) {
        this.contractName = value;
    }

    /**
     * Gets the value of the contractSecondName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractSecondName() {
        return contractSecondName;
    }

    /**
     * Sets the value of the contractSecondName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractSecondName(String value) {
        this.contractSecondName = value;
    }

    /**
     * Gets the value of the contractDoc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractDoc() {
        return contractDoc;
    }

    /**
     * Sets the value of the contractDoc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractDoc(String value) {
        this.contractDoc = value;
    }

    /**
     * Gets the value of the surname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the value of the surname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSurname(String value) {
        this.surname = value;
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
     * Gets the value of the secondName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecondName() {
        return secondName;
    }

    /**
     * Sets the value of the secondName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecondName(String value) {
        this.secondName = value;
    }

    /**
     * Gets the value of the doc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDoc() {
        return doc;
    }

    /**
     * Sets the value of the doc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDoc(String value) {
        this.doc = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddress(String value) {
        this.address = value;
    }

    /**
     * Gets the value of the phone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the value of the phone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhone(String value) {
        this.phone = value;
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
     * Gets the value of the group property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets the value of the group property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroup(String value) {
        this.group = value;
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
     * Gets the value of the notifyBySms property.
     * 
     */
    public boolean isNotifyBySms() {
        return notifyBySms;
    }

    /**
     * Sets the value of the notifyBySms property.
     * 
     */
    public void setNotifyBySms(boolean value) {
        this.notifyBySms = value;
    }

    /**
     * Gets the value of the notifyByEmail property.
     * 
     */
    public boolean isNotifyByEmail() {
        return notifyByEmail;
    }

    /**
     * Sets the value of the notifyByEmail property.
     * 
     */
    public void setNotifyByEmail(boolean value) {
        this.notifyByEmail = value;
    }

    /**
     * Gets the value of the comments property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComments() {
        return comments;
    }

    /**
     * Sets the value of the comments property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComments(String value) {
        this.comments = value;
    }

    /**
     * Gets the value of the cardNo property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCardNo() {
        return cardNo;
    }

    /**
     * Sets the value of the cardNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCardNo(Long value) {
        this.cardNo = value;
    }

    /**
     * Gets the value of the cardPrintedNo property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCardPrintedNo() {
        return cardPrintedNo;
    }

    /**
     * Sets the value of the cardPrintedNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCardPrintedNo(Long value) {
        this.cardPrintedNo = value;
    }

    /**
     * Gets the value of the cardType property.
     * 
     */
    public int getCardType() {
        return cardType;
    }

    /**
     * Sets the value of the cardType property.
     * 
     */
    public void setCardType(int value) {
        this.cardType = value;
    }

    /**
     * Gets the value of the cardExpiry property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCardExpiry() {
        return cardExpiry;
    }

    /**
     * Sets the value of the cardExpiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCardExpiry(XMLGregorianCalendar value) {
        this.cardExpiry = value;
    }

    /**
     * Gets the value of the cardIssued property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCardIssued() {
        return cardIssued;
    }

    /**
     * Sets the value of the cardIssued property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCardIssued(XMLGregorianCalendar value) {
        this.cardIssued = value;
    }

}
