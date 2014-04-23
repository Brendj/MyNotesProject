
package generated.nsiws2.com.rstyle.nsi.beans;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RsEntry complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RsEntry">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element name="error" type="{http://rstyle.com/nsi/beans}NSIError" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element name="primaryKey" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *           &lt;element name="attribute" type="{http://rstyle.com/nsi/beans}Attribute" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/sequence>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RsEntry", propOrder = {
    "error",
    "primaryKey",
    "attribute"
})
@XmlSeeAlso({
    RsItem.class,
    RsCategory.class
})
public class RsEntry {

    protected NSIError error;
    protected Object primaryKey;
    protected List<Attribute> attribute;

    /**
     * Gets the value of the error property.
     * 
     * @return
     *     possible object is
     *     {@link NSIError }
     *     
     */
    public NSIError getError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value
     *     allowed object is
     *     {@link NSIError }
     *     
     */
    public void setError(NSIError value) {
        this.error = value;
    }

    /**
     * Gets the value of the primaryKey property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getPrimaryKey() {
        return primaryKey;
    }

    /**
     * Sets the value of the primaryKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setPrimaryKey(Object value) {
        this.primaryKey = value;
    }

    /**
     * Gets the value of the attribute property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attribute property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Attribute }
     * 
     * 
     */
    public List<Attribute> getAttribute() {
        if (attribute == null) {
            attribute = new ArrayList<Attribute>();
        }
        return this.attribute;
    }

}
