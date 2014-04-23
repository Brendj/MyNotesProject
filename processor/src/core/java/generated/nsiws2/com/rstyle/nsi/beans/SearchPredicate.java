
package generated.nsiws2.com.rstyle.nsi.beans;

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
 *         &lt;element name="attributeName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="attributeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="attributeValue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="attributeOp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="caseInsensitive" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "attributeName",
    "attributeType",
    "attributeValue",
    "attributeOp",
    "caseInsensitive"
})
public class SearchPredicate {

    @XmlElement(required = true)
    protected String attributeName;
    @XmlElement(required = true)
    protected String attributeType;
    @XmlElement(required = true)
    protected String attributeValue;
    @XmlElement(required = true)
    protected String attributeOp;
    @XmlElement(defaultValue = "false")
    protected boolean caseInsensitive;

    /**
     * Gets the value of the attributeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Sets the value of the attributeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttributeName(String value) {
        this.attributeName = value;
    }

    /**
     * Gets the value of the attributeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttributeType() {
        return attributeType;
    }

    /**
     * Sets the value of the attributeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttributeType(String value) {
        this.attributeType = value;
    }

    /**
     * Gets the value of the attributeValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttributeValue() {
        return attributeValue;
    }

    /**
     * Sets the value of the attributeValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttributeValue(String value) {
        this.attributeValue = value;
    }

    /**
     * Gets the value of the attributeOp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttributeOp() {
        return attributeOp;
    }

    /**
     * Sets the value of the attributeOp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttributeOp(String value) {
        this.attributeOp = value;
    }

    /**
     * Gets the value of the caseInsensitive property.
     * 
     */
    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    /**
     * Sets the value of the caseInsensitive property.
     * 
     */
    public void setCaseInsensitive(boolean value) {
        this.caseInsensitive = value;
    }

}
