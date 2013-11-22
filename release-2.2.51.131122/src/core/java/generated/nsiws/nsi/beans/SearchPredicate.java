
package generated.nsiws.nsi.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SearchPredicate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SearchPredicate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="attibuteName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="attibuteType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="attibuteValue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="attibuteOp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchPredicate", propOrder = {
    "attibuteName",
    "attibuteType",
    "attibuteValue",
    "attibuteOp"
})
public class SearchPredicate {

    @XmlElement(required = true)
    protected String attibuteName;
    @XmlElement(required = true)
    protected String attibuteType;
    @XmlElement(required = true)
    protected String attibuteValue;
    @XmlElement(required = true)
    protected String attibuteOp;

    /**
     * Gets the value of the attibuteName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttibuteName() {
        return attibuteName;
    }

    /**
     * Sets the value of the attibuteName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttibuteName(String value) {
        this.attibuteName = value;
    }

    /**
     * Gets the value of the attibuteType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttibuteType() {
        return attibuteType;
    }

    /**
     * Sets the value of the attibuteType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttibuteType(String value) {
        this.attibuteType = value;
    }

    /**
     * Gets the value of the attibuteValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttibuteValue() {
        return attibuteValue;
    }

    /**
     * Sets the value of the attibuteValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttibuteValue(String value) {
        this.attibuteValue = value;
    }

    /**
     * Gets the value of the attibuteOp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttibuteOp() {
        return attibuteOp;
    }

    /**
     * Sets the value of the attibuteOp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttibuteOp(String value) {
        this.attibuteOp = value;
    }

}
