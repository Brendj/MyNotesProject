
package generated.ru.mos.rnip.xsd.services.import_charges._2_0;

import generated.ru.mos.rnip.xsd._package._2_0.ChargesPackage;
import generated.ru.mos.rnip.xsd.common._2_0.RequestType;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.0.1}RequestType">
 *       &lt;sequence>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Package/2.0.1}ChargesPackage"/>
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
    "chargesPackage"
})
@XmlRootElement(name = "ImportChargesRequest")
public class ImportChargesRequest
    extends RequestType
{

    @XmlElement(name = "ChargesPackage", namespace = "http://rnip.mos.ru/xsd/Package/2.0.1", required = true)
    protected ChargesPackage chargesPackage;

    /**
     * Gets the value of the chargesPackage property.
     * 
     * @return
     *     possible object is
     *     {@link ChargesPackage }
     *     
     */
    public ChargesPackage getChargesPackage() {
        return chargesPackage;
    }

    /**
     * Sets the value of the chargesPackage property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargesPackage }
     *     
     */
    public void setChargesPackage(ChargesPackage value) {
        this.chargesPackage = value;
    }

}
