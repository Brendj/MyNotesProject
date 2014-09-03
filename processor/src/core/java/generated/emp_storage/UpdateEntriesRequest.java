
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
 *         &lt;element name="catalogName" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}catalogName"/>
 *         &lt;element name="criteria" type="{http://emp.mos.ru/schemas/storage/entity/entry.xsd}EntryAttribute" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "UpdateEntriesRequest", namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", propOrder = {
    "catalogName",
    "criteria",
    "attribute"
})
public class UpdateEntriesRequest
    extends BaseRequest
{

    @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", required = true)
    protected String catalogName;
    @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd")
    protected List<EntryAttribute> criteria;
    @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", required = true)
    protected List<EntryAttribute> attribute;

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
