
package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.organization;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.common.AddressesType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.common.ContactsType;


/**
 * ������ �� ����������� ��� �������� �����
 * 
 * <p>Java class for OrganizationCatalogType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrganizationCatalogType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Accounts" type="{http://roskazna.ru/gisgmp/xsd/116/Organization}AccountsType"/>
 *         &lt;element name="Addresses" type="{http://roskazna.ru/gisgmp/xsd/116/Common}AddressesType" minOccurs="0"/>
 *         &lt;element name="Contacts" type="{http://roskazna.ru/gisgmp/xsd/116/Common}ContactsType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="1"/>
 *             &lt;maxLength value="2000"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="INN" use="required" type="{http://roskazna.ru/gisgmp/xsd/116/Common}INNType" />
 *       &lt;attribute name="KPP" use="required" type="{http://roskazna.ru/gisgmp/xsd/116/Common}KPPType" />
 *       &lt;attribute name="OKTMO" use="required" type="{http://roskazna.ru/gisgmp/xsd/116/Common}OKTMOType" />
 *       &lt;attribute name="OGRN" type="{http://roskazna.ru/gisgmp/xsd/116/Common}OGRNType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganizationCatalogType", propOrder = {
    "accounts",
    "addresses",
    "contacts"
})
@XmlSeeAlso({
    PayeeType.class
})
public class OrganizationCatalogType {

    @XmlElement(name = "Accounts", required = true)
    protected AccountsType accounts;
    @XmlElement(name = "Addresses")
    protected AddressesType addresses;
    @XmlElement(name = "Contacts")
    protected ContactsType contacts;
    @XmlAttribute(required = true)
    protected String name;
    @XmlAttribute(name = "INN", required = true)
    protected String inn;
    @XmlAttribute(name = "KPP", required = true)
    protected String kpp;
    @XmlAttribute(name = "OKTMO", required = true)
    protected String oktmo;
    @XmlAttribute(name = "OGRN")
    protected String ogrn;

    /**
     * Gets the value of the accounts property.
     * 
     * @return
     *     possible object is
     *     {@link AccountsType }
     *     
     */
    public AccountsType getAccounts() {
        return accounts;
    }

    /**
     * Sets the value of the accounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountsType }
     *     
     */
    public void setAccounts(AccountsType value) {
        this.accounts = value;
    }

    /**
     * Gets the value of the addresses property.
     * 
     * @return
     *     possible object is
     *     {@link AddressesType }
     *     
     */
    public AddressesType getAddresses() {
        return addresses;
    }

    /**
     * Sets the value of the addresses property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressesType }
     *     
     */
    public void setAddresses(AddressesType value) {
        this.addresses = value;
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
     * Gets the value of the inn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINN() {
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
    public void setINN(String value) {
        this.inn = value;
    }

    /**
     * Gets the value of the kpp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKPP() {
        return kpp;
    }

    /**
     * Sets the value of the kpp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKPP(String value) {
        this.kpp = value;
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
     * Gets the value of the ogrn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOGRN() {
        return ogrn;
    }

    /**
     * Sets the value of the ogrn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOGRN(String value) {
        this.ogrn = value;
    }

}
