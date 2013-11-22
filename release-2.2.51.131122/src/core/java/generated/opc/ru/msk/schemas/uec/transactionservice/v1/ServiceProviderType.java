
package generated.opc.ru.msk.schemas.uec.transactionservice.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import generated.opc.ru.msk.schemas.uec.identification.v1.LegalIdDescriptionType;
import generated.opc.ru.msk.schemas.uec.identification.v1.OrganizationType;


/**
 * Идентификация поставщика услуги
 * 
 * <p>Java class for ServiceProviderType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceProviderType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="legalId" type="{http://schemas.msk.ru/uec/identification/v1}LegalIdDescriptionType"/>
 *         &lt;element name="organizationType" type="{http://schemas.msk.ru/uec/identification/v1}OrganizationType"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceProviderType", propOrder = {

})
public class ServiceProviderType {

    @XmlElement(required = true)
    protected LegalIdDescriptionType legalId;
    @XmlElement(required = true)
    protected OrganizationType organizationType;

    /**
     * Gets the value of the legalId property.
     * 
     * @return
     *     possible object is
     *     {@link LegalIdDescriptionType }
     *     
     */
    public LegalIdDescriptionType getLegalId() {
        return legalId;
    }

    /**
     * Sets the value of the legalId property.
     * 
     * @param value
     *     allowed object is
     *     {@link LegalIdDescriptionType }
     *     
     */
    public void setLegalId(LegalIdDescriptionType value) {
        this.legalId = value;
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

}
