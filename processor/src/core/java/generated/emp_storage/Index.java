
package generated.emp_storage;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;


/**
 * Индекс
 * 
 * <p>Java class for Index complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Index">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}indexName"/>
 *         &lt;element name="flags" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}indexFlagList" minOccurs="0"/>
 *         &lt;element name="label" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Index", namespace = "http://emp.mos.ru/schemas/storage/entity/catalog.xsd", propOrder = {
    "name",
    "flags",
    "label",
    "description",
    "attribute"
})
public class Index {

    @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/entity/catalog.xsd", required = true)
    protected String name;
    @XmlList
    @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/entity/catalog.xsd")
    protected List<IndexFlags> flags;
    @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/entity/catalog.xsd", required = true)
    protected String label;
    @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/entity/catalog.xsd")
    protected String description;
    @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/entity/catalog.xsd", required = true)
    protected List<Index.Attribute> attribute;

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
     * Gets the value of the flags property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the flags property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFlags().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IndexFlags }
     * 
     * 
     */
    public List<IndexFlags> getFlags() {
        if (flags == null) {
            flags = new ArrayList<IndexFlags>();
        }
        return this.flags;
    }

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
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
     * {@link Index.Attribute }
     * 
     * 
     */
    public List<Index.Attribute> getAttribute() {
        if (attribute == null) {
            attribute = new ArrayList<Index.Attribute>();
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

        @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/entity/catalog.xsd", required = true)
        protected String name;
        @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/entity/catalog.xsd", required = true)
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
