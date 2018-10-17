
package generated.ru.mos.rnip.xsd.services.forced_ackmowledgement._2_0;

import generated.ru.mos.rnip.xsd.common._2_0.ResponseType;
import generated.ru.mos.rnip.xsd.quittance._2_0.QuittanceType;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.0.1}ResponseType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element name="Quittance" type="{http://rnip.mos.ru/xsd/Quittance/2.0.1}QuittanceType" maxOccurs="100"/>
 *         &lt;/sequence>
 *         &lt;element name="Done" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "quittance",
    "done"
})
@XmlRootElement(name = "ForcedAcknowledgementResponse")
public class ForcedAcknowledgementResponse
    extends ResponseType
{

    @XmlElement(name = "Quittance")
    protected List<QuittanceType> quittance;
    @XmlElement(name = "Done")
    protected Boolean done;

    /**
     * Gets the value of the quittance property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the quittance property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQuittance().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QuittanceType }
     * 
     * 
     */
    public List<QuittanceType> getQuittance() {
        if (quittance == null) {
            quittance = new ArrayList<QuittanceType>();
        }
        return this.quittance;
    }

    /**
     * Gets the value of the done property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDone() {
        return done;
    }

    /**
     * Sets the value of the done property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDone(Boolean value) {
        this.done = value;
    }

}
