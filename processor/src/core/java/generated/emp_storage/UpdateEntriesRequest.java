
package generated.emp_storage;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UpdateEntriesRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateEntriesRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://emp.mos.ru/schemas/storage/request/common.xsd}BaseRequest">
 *       &lt;sequence>
 *         &lt;element name="catalogOwner" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="catalogName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="criteria" type="{http://emp.mos.ru/schemas/storage/entity/entry.xsd}EntryAttribute" maxOccurs="unbounded"/>
 *         &lt;element name="attribute" type="{http://emp.mos.ru/schemas/storage/entity/entry.xsd}EntryAttribute" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateEntriesRequest", propOrder = {
    "catalogOwner",
    "catalogName",
    "criteria",
    "attribute"
})
public class UpdateEntriesRequest
    extends BaseRequest
{

    protected String catalogOwner;
    @XmlElement(required = true)
    protected String catalogName;
    @XmlElement(required = true)
    protected List<EntryAttribute> criteria;
    @XmlElement(required = true)
    protected List<EntryAttribute> attribute;

    /**
     * Gets the value of the catalogOwner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCatalogOwner() {
        return catalogOwner;
    }

    /**
     * Sets the value of the catalogOwner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCatalogOwner(String value) {
        this.catalogOwner = value;
    }

    /**
     * Gets the value of the catalogName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCatalogName() {
        return catalogName;
    }

    /**
     * Sets the value of the catalogName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCatalogName(String value) {
        this.catalogName = value;
    }

    /**
     * Gets the value of the criteria property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the criteria property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCriteria().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EntryAttribute }
     * 
     * 
     */
    public List<EntryAttribute> getCriteria() {
        if (criteria == null) {
            criteria = new ArrayList<EntryAttribute>();
        }
        return this.criteria;
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
     * {@link EntryAttribute }
     * 
     * 
     */
    public List<EntryAttribute> getAttribute() {
        if (attribute == null) {
            attribute = new ArrayList<EntryAttribute>();
        }
        return this.attribute;
    }

}
