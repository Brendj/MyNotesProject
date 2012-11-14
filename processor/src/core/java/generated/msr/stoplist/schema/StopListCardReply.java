
package generated.msr.stoplist.schema;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for stopListCardReply complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="stopListCardReply">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="type" type="{http://webservice.msr.com/sl/schema/}identityType" form="qualified"/>
 *         &lt;element name="identifier" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="currentState" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="range" type="{http://webservice.msr.com/sl/schema/}range" maxOccurs="unbounded" minOccurs="0" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "stopListCardReply", propOrder = {
    "type",
    "identifier",
    "currentState",
    "range"
})
@XmlSeeAlso({
    StopListAppReply.class
})
public class StopListCardReply {

    @XmlElement(required = true)
    protected IdentityType type;
    @XmlElement(required = true)
    protected String identifier;
    @XmlElement(required = true)
    protected String currentState;
    protected List<Range> range;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link IdentityType }
     *     
     */
    public IdentityType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentityType }
     *     
     */
    public void setType(IdentityType value) {
        this.type = value;
    }

    /**
     * Gets the value of the identifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentifier(String value) {
        this.identifier = value;
    }

    /**
     * Gets the value of the currentState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrentState() {
        return currentState;
    }

    /**
     * Sets the value of the currentState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrentState(String value) {
        this.currentState = value;
    }

    /**
     * Gets the value of the range property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the range property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRange().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Range }
     * 
     * 
     */
    public List<Range> getRange() {
        if (range == null) {
            range = new ArrayList<Range>();
        }
        return this.range;
    }

}
