
package generated.ru.mos.rnip.xsd.searchconditions._2_4;

import generated.ru.mos.rnip.xsd.common._2_1.TimeIntervalType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ChargesConditionsType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ChargesConditionsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SupplierBillID" type="{http://rnip.mos.ru/xsd/Common/2.4.0}SupplierBillIDType" maxOccurs="100"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.4.0}TimeInterval" minOccurs="0"/>
 *         &lt;element name="paymentMethod" type="{http://rnip.mos.ru/xsd/Common/2.4.0}PaymentMethodType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChargesConditionsType", namespace = "http://rnip.mos.ru/xsd/SearchConditions/2.4.0", propOrder = {
        "supplierBillID",
        "timeInterval",
        "paymentMethod"
})
public class ChargesConditionsType {

    @XmlElement(name = "SupplierBillID", required = true)
    protected List<String> supplierBillID;
    @XmlElement(name = "TimeInterval", namespace = "http://rnip.mos.ru/xsd/Common/2.4.0")
    protected TimeIntervalType timeInterval;
    protected String paymentMethod;

    /**
     * Gets the value of the supplierBillID property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supplierBillID property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupplierBillID().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getSupplierBillID() {
        if (supplierBillID == null) {
            supplierBillID = new ArrayList<String>();
        }
        return this.supplierBillID;
    }

    /**
     * Временной интервал, за который запрашивается информация из ГИС ГМП
     *
     *
     * @return
     *     possible object is
     *     {@link TimeIntervalType }
     *
     */
    public TimeIntervalType getTimeInterval() {
        return timeInterval;
    }

    /**
     * Sets the value of the timeInterval property.
     *
     * @param value
     *     allowed object is
     *     {@link TimeIntervalType }
     *
     */
    public void setTimeInterval(TimeIntervalType value) {
        this.timeInterval = value;
    }

    /**
     * Gets the value of the paymentMethod property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the value of the paymentMethod property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPaymentMethod(String value) {
        this.paymentMethod = value;
    }

}