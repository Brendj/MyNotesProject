
package ru.msk.schemas.uec.transaction.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import ru.msk.schemas.uec.identification.v1.LegalIdDescriptionType;
import ru.msk.schemas.uec.identification.v1.OrganizationType;


/**
 * Идентификация источника транзакции
 * 
 * <p>Java class for TransactionSourceDescriptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransactionSourceDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="transactionSourceId" type="{http://schemas.msk.ru/uec/identification/v1}LegalIdDescriptionType"/>
 *         &lt;element name="organizationType" type="{http://schemas.msk.ru/uec/identification/v1}OrganizationType"/>
 *         &lt;element name="transactionSystemCode">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="32"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="transactionSystemName" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="255"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionSourceDescriptionType", propOrder = {

})
public class TransactionSourceDescriptionType {

    @XmlElement(required = true)
    protected LegalIdDescriptionType transactionSourceId;
    @XmlElement(required = true)
    protected OrganizationType organizationType;
    @XmlElement(required = true)
    protected String transactionSystemCode;
    protected String transactionSystemName;

    /**
     * Gets the value of the transactionSourceId property.
     * 
     * @return
     *     possible object is
     *     {@link LegalIdDescriptionType }
     *     
     */
    public LegalIdDescriptionType getTransactionSourceId() {
        return transactionSourceId;
    }

    /**
     * Sets the value of the transactionSourceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link LegalIdDescriptionType }
     *     
     */
    public void setTransactionSourceId(LegalIdDescriptionType value) {
        this.transactionSourceId = value;
    }

    /**
     * Gets the value of the organizationType property.
     * 
     * @return
     *     possible object is
     *     {@link OrganizationType }
     *     
     */
    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    /**
     * Sets the value of the organizationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganizationType }
     *     
     */
    public void setOrganizationType(OrganizationType value) {
        this.organizationType = value;
    }

    /**
     * Gets the value of the transactionSystemCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionSystemCode() {
        return transactionSystemCode;
    }

    /**
     * Sets the value of the transactionSystemCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionSystemCode(String value) {
        this.transactionSystemCode = value;
    }

    /**
     * Gets the value of the transactionSystemName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionSystemName() {
        return transactionSystemName;
    }

    /**
     * Sets the value of the transactionSystemName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionSystemName(String value) {
        this.transactionSystemName = value;
    }

}
