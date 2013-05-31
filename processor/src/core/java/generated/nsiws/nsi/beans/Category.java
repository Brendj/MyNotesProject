
package generated.nsiws.nsi.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Category complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Category">
 *   &lt;complexContent>
 *     &lt;extension base="{http://rstyle.com/nsi/beans}Entry">
 *       &lt;sequence>
 *         &lt;element name="categoryPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Category", propOrder = {
    "categoryPath"
})
public class Category
    extends Entry
{

    @XmlElement(required = true)
    protected String categoryPath;

    /**
     * Gets the value of the categoryPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategoryPath() {
        return categoryPath;
    }

    /**
     * Sets the value of the categoryPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategoryPath(String value) {
        this.categoryPath = value;
    }

}
