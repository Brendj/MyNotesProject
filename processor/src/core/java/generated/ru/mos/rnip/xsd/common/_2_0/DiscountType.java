
package generated.ru.mos.rnip.xsd.common._2_0;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for DiscountType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DiscountType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Value" type="{http://rnip.mos.ru/xsd/Common/2.0.1}DiscountValueType"/>
 *         &lt;element name="Expiry" type="{http://rnip.mos.ru/xsd/Common/2.0.1}DiscountDateType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DiscountType", propOrder = {
    "value",
    "expiry"
})
@XmlSeeAlso({
    MultiplierSize.class,
    DiscountFixed.class,
    DiscountSize.class
})
public abstract class DiscountType {

    @XmlElement(name = "Value")
    protected float value;
    @XmlElement(name = "Expiry", required = true)
    protected String expiry;

    /**
     * Gets the value of the value property.
     * 
     */
    public float getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     */
    public void setValue(float value) {
        this.value = value;
    }

    /**
     * Gets the value of the expiry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpiry() {
        return expiry;
    }

    /**
     * Sets the value of the expiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpiry(String value) {
        this.expiry = value;
    }

}
