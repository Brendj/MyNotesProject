
package generated.nsiws_delta;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReceiveNSIDeltaRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReceiveNSIDeltaRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="delta" type="{http://rstyle.com/nsi/delta/service}DeltaType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReceiveNSIDeltaRequestType", propOrder = {
    "delta"
})
public class ReceiveNSIDeltaRequestType {

    @XmlElement(required = true)
    protected DeltaType delta;

    /**
     * Gets the value of the delta property.
     * 
     * @return
     *     possible object is
     *     {@link DeltaType }
     *     
     */
    public DeltaType getDelta() {
        return delta;
    }

    /**
     * Sets the value of the delta property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeltaType }
     *     
     */
    public void setDelta(DeltaType value) {
        this.delta = value;
    }

}
