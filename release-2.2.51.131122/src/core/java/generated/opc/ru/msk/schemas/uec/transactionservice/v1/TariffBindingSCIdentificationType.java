
package generated.opc.ru.msk.schemas.uec.transactionservice.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import generated.opc.ru.msk.schemas.uec.identification.v1.OrganizationType;


/**
 * Идентификация взаиморасчета по Заказчикам услуг
 * 
 * <p>Java class for TariffBindingSCIdentificationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TariffBindingSCIdentificationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="serviceCustomerCode">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="1"/>
 *               &lt;maxLength value="36"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="scType" type="{http://schemas.msk.ru/uec/identification/v1}OrganizationType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TariffBindingSCIdentificationType", propOrder = {
    "serviceCustomerCode",
    "scType"
})
public class TariffBindingSCIdentificationType {

    protected String serviceCustomerCode;
    protected OrganizationType scType;

    /**
     * Gets the value of the serviceCustomerCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceCustomerCode() {
        return serviceCustomerCode;
    }

    /**
     * Sets the value of the serviceCustomerCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceCustomerCode(String value) {
        this.serviceCustomerCode = value;
    }

    /**
     * Gets the value of the scType property.
     * 
     * @return
     *     possible object is
     *     {@link OrganizationType }
     *     
     */
    public OrganizationType getScType() {
        return scType;
    }

    /**
     * Sets the value of the scType property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganizationType }
     *     
     */
    public void setScType(OrganizationType value) {
        this.scType = value;
    }

}
