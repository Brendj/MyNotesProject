
package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.chargecreationresponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ChargeCreationResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ChargeCreationResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ChargeData" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChargeCreationResponseType", propOrder = {
    "chargeData"
})
public class ChargeCreationResponseType {

    @XmlElement(name = "ChargeData", required = true)
    protected byte[] chargeData;

    /**
     * Gets the value of the chargeData property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getChargeData() {
        return chargeData;
    }

    /**
     * Sets the value of the chargeData property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setChargeData(byte[] value) {
        this.chargeData = ((byte[]) value);
    }

}
