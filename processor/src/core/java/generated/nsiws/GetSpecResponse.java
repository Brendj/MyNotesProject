
package generated.nsiws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetSpecResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetSpecResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://rstyle.com/nsi/beans}Response">
 *       &lt;sequence>
 *         &lt;element name="primarySpec" type="{http://rstyle.com/nsi/beans}Spec"/>
 *         &lt;element name="secondarySpec" type="{http://rstyle.com/nsi/beans}Spec" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetSpecResponse", propOrder = {
    "primarySpec",
    "secondarySpec"
})
public class GetSpecResponse
    extends Response
{

    @XmlElement(required = true)
    protected Spec primarySpec;
    protected List<Spec> secondarySpec;

    /**
     * Gets the value of the primarySpec property.
     * 
     * @return
     *     possible object is
     *     {@link Spec }
     *     
     */
    public Spec getPrimarySpec() {
        return primarySpec;
    }

    /**
     * Sets the value of the primarySpec property.
     * 
     * @param value
     *     allowed object is
     *     {@link Spec }
     *     
     */
    public void setPrimarySpec(Spec value) {
        this.primarySpec = value;
    }

    /**
     * Gets the value of the secondarySpec property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the secondarySpec property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSecondarySpec().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Spec }
     * 
     * 
     */
    public List<Spec> getSecondarySpec() {
        if (secondarySpec == null) {
            secondarySpec = new ArrayList<Spec>();
        }
        return this.secondarySpec;
    }

}
