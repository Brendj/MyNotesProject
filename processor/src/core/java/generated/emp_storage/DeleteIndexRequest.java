
package generated.emp_storage;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DeleteIndexRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DeleteIndexRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://emp.mos.ru/schemas/storage/request/common.xsd}BaseRequest">
 *       &lt;sequence>
 *         &lt;element name="catalogName" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}catalogName"/>
 *         &lt;element name="indexName" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}indexName" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeleteIndexRequest", propOrder = {
    "catalogName",
    "indexName"
})
public class DeleteIndexRequest
    extends BaseRequest
{

    @XmlElement(required = true)
    protected String catalogName;
    @XmlElement(required = true)
    protected List<String> indexName;

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
     * Gets the value of the indexName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the indexName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIndexName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getIndexName() {
        if (indexName == null) {
            indexName = new ArrayList<String>();
        }
        return this.indexName;
    }

}
