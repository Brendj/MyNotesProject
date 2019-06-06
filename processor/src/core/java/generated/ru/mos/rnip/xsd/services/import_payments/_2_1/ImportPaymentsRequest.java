
package generated.ru.mos.rnip.xsd.services.import_payments._2_1;

import generated.ru.mos.rnip.xsd._package._2_1.PaymentsPackage;
import generated.ru.mos.rnip.xsd.common._2_1.RequestType;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.1.1}RequestType">
 *       &lt;sequence>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Package/2.1.1}PaymentsPackage"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "paymentsPackage"
})
@XmlRootElement(name = "ImportPaymentsRequest")
public class ImportPaymentsRequest
    extends RequestType
{

    @XmlElement(name = "PaymentsPackage", namespace = "http://rnip.mos.ru/xsd/Package/2.1.1", required = true)
    protected PaymentsPackage paymentsPackage;

    /**
     * Gets the value of the paymentsPackage property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentsPackage }
     *     
     */
    public PaymentsPackage getPaymentsPackage() {
        return paymentsPackage;
    }

    /**
     * Sets the value of the paymentsPackage property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentsPackage }
     *     
     */
    public void setPaymentsPackage(PaymentsPackage value) {
        this.paymentsPackage = value;
    }

}
