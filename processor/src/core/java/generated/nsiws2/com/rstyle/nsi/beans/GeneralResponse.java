
package generated.nsiws2.com.rstyle.nsi.beans;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GeneralResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GeneralResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://rstyle.com/nsi/beans}Response">
 *       &lt;sequence>
 *         &lt;element name="catalogName" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="tableName" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="primaryKey" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="categoryPath" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="attributeValue" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="lookupKey" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="history" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="queryResult" type="{http://rstyle.com/nsi/beans}QueryResult" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="attribute" type="{http://rstyle.com/nsi/beans}Attribute" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="item" type="{http://rstyle.com/nsi/beans}Item" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="category" type="{http://rstyle.com/nsi/beans}Category" maxOccurs="unbounded"/>
 *         &lt;element name="found" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="entry" type="{http://rstyle.com/nsi/beans}RsEntry" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="rsItem" type="{http://rstyle.com/nsi/beans}RsItem" maxOccurs="unbounded"/>
 *         &lt;element name="rsCategory" type="{http://rstyle.com/nsi/beans}RsCategory" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="batchCheck" type="{http://rstyle.com/nsi/beans}BatchCheck" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GeneralResponse", propOrder = {
    "catalogName",
    "tableName",
    "primaryKey",
    "categoryPath",
    "attributeValue",
    "lookupKey",
    "history",
    "queryResult",
    "attribute",
    "item",
    "category",
    "found",
    "entry",
    "rsItem",
    "rsCategory",
    "batchCheck"
})
public class GeneralResponse
    extends Response
{

    protected List<String> catalogName;
    protected List<String> tableName;
    protected List<String> primaryKey;
    protected List<String> categoryPath;
    protected List<String> attributeValue;
    protected List<String> lookupKey;
    protected List<String> history;
    protected List<QueryResult> queryResult;
    protected List<Attribute> attribute;
    protected List<Item> item;
    @XmlElement(required = true)
    protected List<Category> category;
    protected Long found;
    protected List<RsEntry> entry;
    @XmlElement(required = true)
    protected List<RsItem> rsItem;
    protected List<RsCategory> rsCategory;
    protected BatchCheck batchCheck;

    /**
     * Gets the value of the catalogName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the catalogName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCatalogName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCatalogName() {
        if (catalogName == null) {
            catalogName = new ArrayList<String>();
        }
        return this.catalogName;
    }

    /**
     * Gets the value of the tableName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tableName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTableName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTableName() {
        if (tableName == null) {
            tableName = new ArrayList<String>();
        }
        return this.tableName;
    }

    /**
     * Gets the value of the primaryKey property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the primaryKey property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrimaryKey().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getPrimaryKey() {
        if (primaryKey == null) {
            primaryKey = new ArrayList<String>();
        }
        return this.primaryKey;
    }

    /**
     * Gets the value of the categoryPath property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the categoryPath property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCategoryPath().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCategoryPath() {
        if (categoryPath == null) {
            categoryPath = new ArrayList<String>();
        }
        return this.categoryPath;
    }

    /**
     * Gets the value of the attributeValue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attributeValue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttributeValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAttributeValue() {
        if (attributeValue == null) {
            attributeValue = new ArrayList<String>();
        }
        return this.attributeValue;
    }

    /**
     * Gets the value of the lookupKey property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lookupKey property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLookupKey().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getLookupKey() {
        if (lookupKey == null) {
            lookupKey = new ArrayList<String>();
        }
        return this.lookupKey;
    }

    /**
     * Gets the value of the history property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the history property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHistory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getHistory() {
        if (history == null) {
            history = new ArrayList<String>();
        }
        return this.history;
    }

    /**
     * Gets the value of the queryResult property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the queryResult property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQueryResult().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QueryResult }
     * 
     * 
     */
    public List<QueryResult> getQueryResult() {
        if (queryResult == null) {
            queryResult = new ArrayList<QueryResult>();
        }
        return this.queryResult;
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

    /**
     * Gets the value of the item property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the item property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Item }
     * 
     * 
     */
    public List<Item> getItem() {
        if (item == null) {
            item = new ArrayList<Item>();
        }
        return this.item;
    }

    /**
     * Gets the value of the category property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the category property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCategory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Category }
     * 
     * 
     */
    public List<Category> getCategory() {
        if (category == null) {
            category = new ArrayList<Category>();
        }
        return this.category;
    }

    /**
     * Gets the value of the found property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFound() {
        return found;
    }

    /**
     * Sets the value of the found property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFound(Long value) {
        this.found = value;
    }

    /**
     * Gets the value of the entry property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the entry property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEntry().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RsEntry }
     * 
     * 
     */
    public List<RsEntry> getEntry() {
        if (entry == null) {
            entry = new ArrayList<RsEntry>();
        }
        return this.entry;
    }

    /**
     * Gets the value of the rsItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rsItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRsItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RsItem }
     * 
     * 
     */
    public List<RsItem> getRsItem() {
        if (rsItem == null) {
            rsItem = new ArrayList<RsItem>();
        }
        return this.rsItem;
    }

    /**
     * Gets the value of the rsCategory property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rsCategory property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRsCategory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RsCategory }
     * 
     * 
     */
    public List<RsCategory> getRsCategory() {
        if (rsCategory == null) {
            rsCategory = new ArrayList<RsCategory>();
        }
        return this.rsCategory;
    }

    /**
     * Gets the value of the batchCheck property.
     * 
     * @return
     *     possible object is
     *     {@link BatchCheck }
     *     
     */
    public BatchCheck getBatchCheck() {
        return batchCheck;
    }

    /**
     * Sets the value of the batchCheck property.
     * 
     * @param value
     *     allowed object is
     *     {@link BatchCheck }
     *     
     */
    public void setBatchCheck(BatchCheck value) {
        this.batchCheck = value;
    }

}
