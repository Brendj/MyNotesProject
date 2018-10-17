
package generated.ru.mos.rnip.xsd.searchconditions._2_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for PaymentsConditionsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PaymentsConditionsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PaymentId" maxOccurs="100">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://rnip.mos.ru/xsd/Common/2.0.1}PaymentIdType">
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ServicesCodesList" type="{http://rnip.mos.ru/xsd/SearchConditions/2.0.1}ServicesConditionsType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentsConditionsType", propOrder = {
    "paymentId",
    "servicesCodesList"
})
public class PaymentsConditionsType {

    @XmlElement(name = "PaymentId", required = true)
    protected List<String> paymentId;
    @XmlElement(name = "ServicesCodesList")
    protected ServicesConditionsType servicesCodesList;

    /**
     * Gets the value of the paymentId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the paymentId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPaymentId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getPaymentId() {
        if (paymentId == null) {
            paymentId = new ArrayList<String>();
        }
        return this.paymentId;
    }

    /**
     * Gets the value of the servicesCodesList property.
     * 
     * @return
     *     possible object is
     *     {@link ServicesConditionsType }
     *     
     */
    public ServicesConditionsType getServicesCodesList() {
        return servicesCodesList;
    }

    /**
     * Sets the value of the servicesCodesList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServicesConditionsType }
     *     
     */
    public void setServicesCodesList(ServicesConditionsType value) {
        this.servicesCodesList = value;
    }

}
