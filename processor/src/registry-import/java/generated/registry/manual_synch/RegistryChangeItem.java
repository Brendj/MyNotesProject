
package generated.registry.manual_synch;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for registryChangeItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="registryChangeItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="applied" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="clientGUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="createDate" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="error" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firstNameFrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="groupName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="groupNameFrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idOfClient" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="idOfMigrateOrgFrom" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="idOfMigrateOrgTo" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="idOfOrg" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="idOfRegistryChange" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="operation" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="secondName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="secondNameFrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="surname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="surnameFrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "registryChangeItem", propOrder = {
    "applied",
    "clientGUID",
    "createDate",
    "error",
    "firstName",
    "firstNameFrom",
    "groupName",
    "groupNameFrom",
    "idOfClient",
    "idOfMigrateOrgFrom",
    "idOfMigrateOrgTo",
    "idOfOrg",
    "idOfRegistryChange",
    "operation",
    "secondName",
    "secondNameFrom",
    "surname",
    "surnameFrom"
})
public class RegistryChangeItem {

    protected Boolean applied;
    protected String clientGUID;
    protected Long createDate;
    protected String error;
    protected String firstName;
    protected String firstNameFrom;
    protected String groupName;
    protected String groupNameFrom;
    protected Long idOfClient;
    protected Long idOfMigrateOrgFrom;
    protected Long idOfMigrateOrgTo;
    protected Long idOfOrg;
    protected Long idOfRegistryChange;
    protected Integer operation;
    protected String secondName;
    protected String secondNameFrom;
    protected String surname;
    protected String surnameFrom;

    /**
     * Gets the value of the applied property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isApplied() {
        return applied;
    }

    /**
     * Sets the value of the applied property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setApplied(Boolean value) {
        this.applied = value;
    }

    /**
     * Gets the value of the clientGUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientGUID() {
        return clientGUID;
    }

    /**
     * Sets the value of the clientGUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientGUID(String value) {
        this.clientGUID = value;
    }

    /**
     * Gets the value of the createDate property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCreateDate() {
        return createDate;
    }

    /**
     * Sets the value of the createDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCreateDate(Long value) {
        this.createDate = value;
    }

    /**
     * Gets the value of the error property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setError(String value) {
        this.error = value;
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
     * Gets the value of the firstNameFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstNameFrom() {
        return firstNameFrom;
    }

    /**
     * Sets the value of the firstNameFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstNameFrom(String value) {
        this.firstNameFrom = value;
    }

    /**
     * Gets the value of the groupName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Sets the value of the groupName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupName(String value) {
        this.groupName = value;
    }

    /**
     * Gets the value of the groupNameFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupNameFrom() {
        return groupNameFrom;
    }

    /**
     * Sets the value of the groupNameFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupNameFrom(String value) {
        this.groupNameFrom = value;
    }

    /**
     * Gets the value of the idOfClient property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdOfClient() {
        return idOfClient;
    }

    /**
     * Sets the value of the idOfClient property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdOfClient(Long value) {
        this.idOfClient = value;
    }

    /**
     * Gets the value of the idOfMigrateOrgFrom property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdOfMigrateOrgFrom() {
        return idOfMigrateOrgFrom;
    }

    /**
     * Sets the value of the idOfMigrateOrgFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdOfMigrateOrgFrom(Long value) {
        this.idOfMigrateOrgFrom = value;
    }

    /**
     * Gets the value of the idOfMigrateOrgTo property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdOfMigrateOrgTo() {
        return idOfMigrateOrgTo;
    }

    /**
     * Sets the value of the idOfMigrateOrgTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdOfMigrateOrgTo(Long value) {
        this.idOfMigrateOrgTo = value;
    }

    /**
     * Gets the value of the idOfOrg property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdOfOrg() {
        return idOfOrg;
    }

    /**
     * Sets the value of the idOfOrg property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdOfOrg(Long value) {
        this.idOfOrg = value;
    }

    /**
     * Gets the value of the idOfRegistryChange property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdOfRegistryChange() {
        return idOfRegistryChange;
    }

    /**
     * Sets the value of the idOfRegistryChange property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdOfRegistryChange(Long value) {
        this.idOfRegistryChange = value;
    }

    /**
     * Gets the value of the operation property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOperation() {
        return operation;
    }

    /**
     * Sets the value of the operation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOperation(Integer value) {
        this.operation = value;
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
     * Gets the value of the secondNameFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecondNameFrom() {
        return secondNameFrom;
    }

    /**
     * Sets the value of the secondNameFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecondNameFrom(String value) {
        this.secondNameFrom = value;
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
     * Gets the value of the surnameFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSurnameFrom() {
        return surnameFrom;
    }

    /**
     * Sets the value of the surnameFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSurnameFrom(String value) {
        this.surnameFrom = value;
    }

}
