
package generated.ru.mos.rnip.xsd.services.import_chargestemplate._2_1;

import generated.ru.mos.rnip.xsd.charge._2_1.ChargeTemplateType;
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
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.1.0}RequestType">
 *       &lt;sequence>
 *         &lt;element name="ChargeTemplate" type="{http://rnip.mos.ru/xsd/Charge/2.1.0}ChargeTemplateType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="originatorId" type="{http://rnip.mos.ru/xsd/Common/2.1.0}URNType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "chargeTemplate"
})
@XmlRootElement(name = "ChargeCreationRequest")
public class ChargeCreationRequest
    extends RequestType
{

    @XmlElement(name = "ChargeTemplate", required = true)
    protected ChargeTemplateType chargeTemplate;
    @XmlAttribute(name = "originatorId")
    protected String originatorId;

    /**
     * Gets the value of the chargeTemplate property.
     * 
     * @return
     *     possible object is
     *     {@link ChargeTemplateType }
     *     
     */
    public ChargeTemplateType getChargeTemplate() {
        return chargeTemplate;
    }

    /**
     * Sets the value of the chargeTemplate property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargeTemplateType }
     *     
     */
    public void setChargeTemplate(ChargeTemplateType value) {
        this.chargeTemplate = value;
    }

    /**
     * Gets the value of the originatorId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginatorId() {
        return originatorId;
    }

    /**
     * Sets the value of the originatorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginatorId(String value) {
        this.originatorId = value;
    }

}
