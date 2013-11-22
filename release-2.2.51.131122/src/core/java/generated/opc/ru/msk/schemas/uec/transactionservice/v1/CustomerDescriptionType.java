
package generated.opc.ru.msk.schemas.uec.transactionservice.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import generated.opc.ru.msk.schemas.uec.identification.v1.HolderIdDescriptionType;


/**
 * Описание заказчика услуги
 * 
 * <p>Java class for CustomerDescriptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CustomerDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="organizationCustomer" type="{http://schemas.msk.ru/uec/TransactionService/v1}ServiceCustomerType"/>
 *         &lt;element name="personCustomer" type="{http://schemas.msk.ru/uec/identification/v1}HolderIdDescriptionType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerDescriptionType", propOrder = {
    "organizationCustomer",
    "personCustomer"
})
public class CustomerDescriptionType {

    protected ServiceCustomerType organizationCustomer;
    protected HolderIdDescriptionType personCustomer;

    /**
     * Gets the value of the organizationCustomer property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceCustomerType }
     *     
     */
    public ServiceCustomerType getOrganizationCustomer() {
        return organizationCustomer;
    }

    /**
     * Sets the value of the organizationCustomer property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceCustomerType }
     *     
     */
    public void setOrganizationCustomer(ServiceCustomerType value) {
        this.organizationCustomer = value;
    }

    /**
     * Gets the value of the personCustomer property.
     * 
     * @return
     *     possible object is
     *     {@link HolderIdDescriptionType }
     *     
     */
    public HolderIdDescriptionType getPersonCustomer() {
        return personCustomer;
    }

    /**
     * Sets the value of the personCustomer property.
     * 
     * @param value
     *     allowed object is
     *     {@link HolderIdDescriptionType }
     *     
     */
    public void setPersonCustomer(HolderIdDescriptionType value) {
        this.personCustomer = value;
    }

}
