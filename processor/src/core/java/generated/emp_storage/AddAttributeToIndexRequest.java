
package generated.emp_storage;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AddAttributeToIndexRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AddAttributeToIndexRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://emp.mos.ru/schemas/storage/request/common.xsd}BaseRequest">
 *       &lt;sequence>
 *         &lt;element name="catalogName" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}catalogName"/>
 *         &lt;element name="indexName" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}indexName"/>
 *         &lt;element name="attribute" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="name" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}attributeName"/>
 *                   &lt;element name="sort" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}indexAttributeSort"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddAttributeToIndexRequest", propOrder = {
    "catalogName",
    "indexName",
    "attribute"
})
public class AddAttributeToIndexRequest
    extends BaseRequest
{

    @XmlElement(required = true)
    protected String catalogName;
    @XmlElement(required = true)
    protected String indexName;
    @XmlElement(required = true)
    protected List<AddAttributeToIndexRequest.Attribute> attribute;

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
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndexName() {
        return indexName;
    }

    /**
     * Sets the value of the indexName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndexName(String value) {
        this.indexName = value;
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
     * {@link AddAttributeToIndexRequest.Attribute }
     * 
     * 
     */
    public List<AddAttributeToIndexRequest.Attribute> getAttribute() {
        if (attribute == null) {
            attribute = new ArrayList<AddAttributeToIndexRequest.Attribute>();
        }
        return this.attribute;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="name" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}attributeName"/>
     *         &lt;element name="sort" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}indexAttributeSort"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "name",
        "sort"
    })
    public static class Attribute {

        @XmlElement(required = true)
        protected String name;
        @XmlElement(required = true)
        protected IndexAttributeSort sort;

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * Gets the value of the sort property.
         * 
         * @return
         *     possible object is
         *     {@link IndexAttributeSort }
         *     
         */
        public IndexAttributeSort getSort() {
            return sort;
        }

        /**
         * Sets the value of the sort property.
         * 
         * @param value
         *     allowed object is
         *     {@link IndexAttributeSort }
         *     
         */
        public void setSort(IndexAttributeSort value) {
            this.sort = value;
        }

    }

}
