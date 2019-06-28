
package generated.ru.mos.rnip.xsd.services.import_chargestemplate._2_1;

import generated.ru.mos.rnip.xsd.charge._2_1.ChargeType;
import generated.ru.mos.rnip.xsd.common._2_1.ResponseType;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.1.1}ResponseType">
 *       &lt;sequence>
 *         &lt;element name="Charge" type="{http://rnip.mos.ru/xsd/Charge/2.1.1}ChargeType"/>
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
    "charge"
})
@XmlRootElement(name = "ChargeCreationResponse")
public class ChargeCreationResponse
    extends ResponseType
{

    @XmlElement(name = "Charge", required = true)
    protected ChargeType charge;

    /**
     * Gets the value of the charge property.
     * 
     * @return
     *     possible object is
     *     {@link ChargeType }
     *     
     */
    public ChargeType getCharge() {
        return charge;
    }

    /**
     * Sets the value of the charge property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargeType }
     *     
     */
    public void setCharge(ChargeType value) {
        this.charge = value;
    }

}
