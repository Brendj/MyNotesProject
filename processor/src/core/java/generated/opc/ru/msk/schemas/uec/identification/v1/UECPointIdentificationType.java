
package generated.opc.ru.msk.schemas.uec.identification.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Идентификация точки обслуживания УЭК
 * 
 * <p>Java class for UECPointIdentificationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UECPointIdentificationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="organization" type="{http://schemas.msk.ru/uec/identification/v1}LegalIdDescriptionType"/>
 *         &lt;element name="providerCode">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="32"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="person" type="{http://schemas.msk.ru/uec/identification/v1}HolderIdDescriptionType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UECPointIdentificationType", propOrder = {
    "organization",
    "providerCode",
    "person"
})
public class UECPointIdentificationType {

    @XmlElement(required = true)
    protected LegalIdDescriptionType organization;
    @XmlElement(required = true)
    protected String providerCode;
    protected HolderIdDescriptionType person;

    /**
     * Gets the value of the organization property.
     * 
     * @return
     *     possible object is
     *     {@link LegalIdDescriptionType }
     *     
     */
    public LegalIdDescriptionType getOrganization() {
        return organization;
    }

    /**
     * Sets the value of the organization property.
     * 
     * @param value
     *     allowed object is
     *     {@link LegalIdDescriptionType }
     *     
     */
    public void setOrganization(LegalIdDescriptionType value) {
        this.organization = value;
    }

    /**
     * Gets the value of the providerCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProviderCode() {
        return providerCode;
    }

    /**
     * Sets the value of the providerCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProviderCode(String value) {
        this.providerCode = value;
    }

    /**
     * Gets the value of the person property.
     * 
     * @return
     *     possible object is
     *     {@link HolderIdDescriptionType }
     *     
     */
    public HolderIdDescriptionType getPerson() {
        return person;
    }

    /**
     * Sets the value of the person property.
     * 
     * @param value
     *     allowed object is
     *     {@link HolderIdDescriptionType }
     *     
     */
    public void setPerson(HolderIdDescriptionType value) {
        this.person = value;
    }

}
