
package generated.nsiws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for AppDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AppDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://rstyle.com/nsi/beans}context" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;sequence>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}catalogName" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}specName" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}hierarchyName" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}itemPk" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}categoryPath" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}lookupTableName" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}lookupKey" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}attrName" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}query" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}categoryNames" maxOccurs="unbounded" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}matchAllCategories" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}category" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}item" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}searchPredicate" maxOccurs="unbounded" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}fromDate" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}toDate" minOccurs="0"/>
 *           &lt;/sequence>
 *           &lt;element ref="{http://rstyle.com/nsi/beans}loginResponse"/>
 *           &lt;element ref="{http://rstyle.com/nsi/beans}generalResponse"/>
 *           &lt;element ref="{http://rstyle.com/nsi/beans}getSpecResponse"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AppDataType", namespace = "http://smev.gosuslugi.ru/rev110801", propOrder = {
    "context",
    "catalogName",
    "specName",
    "hierarchyName",
    "itemPk",
    "categoryPath",
    "lookupTableName",
    "lookupKey",
    "attrName",
    "query",
    "categoryNames",
    "matchAllCategories",
    "category",
    "item",
    "searchPredicate",
    "fromDate",
    "toDate",
    "loginResponse",
    "generalResponse",
    "getSpecResponse"
})
public class AppDataType {

    protected Context context;
    protected String catalogName;
    protected String specName;
    protected String hierarchyName;
    protected String itemPk;
    protected String categoryPath;
    protected String lookupTableName;
    protected String lookupKey;
    protected String attrName;
    protected String query;
    protected List<String> categoryNames;
    @XmlElement(nillable = true)
    protected Boolean matchAllCategories;
    protected Category category;
    protected Item item;
    protected List<SearchPredicate> searchPredicate;
    @XmlElement(nillable = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar fromDate;
    @XmlElement(nillable = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar toDate;
    protected LoginResponse loginResponse;
    @XmlElement(name = "generalResponse", namespace = "http://rstyle.com/nsi/beans")
    protected GeneralResponse generalResponse;
    protected GetSpecResponse getSpecResponse;

    /**
     * Gets the value of the context property.
     * 
     * @return
     *     possible object is
     *     {@link Context }
     *     
     */
    public Context getContext() {
        return context;
    }

    /**
     * Sets the value of the context property.
     * 
     * @param value
     *     allowed object is
     *     {@link Context }
     *     
     */
    public void setContext(Context value) {
        this.context = value;
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
     * Gets the value of the specName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecName() {
        return specName;
    }

    /**
     * Sets the value of the specName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecName(String value) {
        this.specName = value;
    }

    /**
     * Gets the value of the hierarchyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHierarchyName() {
        return hierarchyName;
    }

    /**
     * Sets the value of the hierarchyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHierarchyName(String value) {
        this.hierarchyName = value;
    }

    /**
     * Gets the value of the itemPk property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemPk() {
        return itemPk;
    }

    /**
     * Sets the value of the itemPk property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemPk(String value) {
        this.itemPk = value;
    }

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

    /**
     * Gets the value of the lookupTableName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLookupTableName() {
        return lookupTableName;
    }

    /**
     * Sets the value of the lookupTableName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLookupTableName(String value) {
        this.lookupTableName = value;
    }

    /**
     * Gets the value of the lookupKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLookupKey() {
        return lookupKey;
    }

    /**
     * Sets the value of the lookupKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLookupKey(String value) {
        this.lookupKey = value;
    }

    /**
     * Gets the value of the attrName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttrName() {
        return attrName;
    }

    /**
     * Sets the value of the attrName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttrName(String value) {
        this.attrName = value;
    }

    /**
     * Gets the value of the query property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the value of the query property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuery(String value) {
        this.query = value;
    }

    /**
     * Gets the value of the categoryNames property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the categoryNames property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCategoryNames().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCategoryNames() {
        if (categoryNames == null) {
            categoryNames = new ArrayList<String>();
        }
        return this.categoryNames;
    }

    /**
     * Gets the value of the matchAllCategories property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMatchAllCategories() {
        return matchAllCategories;
    }

    /**
     * Sets the value of the matchAllCategories property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMatchAllCategories(Boolean value) {
        this.matchAllCategories = value;
    }

    /**
     * Gets the value of the category property.
     * 
     * @return
     *     possible object is
     *     {@link Category }
     *     
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Sets the value of the category property.
     * 
     * @param value
     *     allowed object is
     *     {@link Category }
     *     
     */
    public void setCategory(Category value) {
        this.category = value;
    }

    /**
     * Gets the value of the item property.
     * 
     * @return
     *     possible object is
     *     {@link Item }
     *     
     */
    public Item getItem() {
        return item;
    }

    /**
     * Sets the value of the item property.
     * 
     * @param value
     *     allowed object is
     *     {@link Item }
     *     
     */
    public void setItem(Item value) {
        this.item = value;
    }

    /**
     * Gets the value of the searchPredicate property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the searchPredicate property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSearchPredicate().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SearchPredicate }
     * 
     * 
     */
    public List<SearchPredicate> getSearchPredicate() {
        if (searchPredicate == null) {
            searchPredicate = new ArrayList<SearchPredicate>();
        }
        return this.searchPredicate;
    }

    /**
     * Gets the value of the fromDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFromDate() {
        return fromDate;
    }

    /**
     * Sets the value of the fromDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFromDate(XMLGregorianCalendar value) {
        this.fromDate = value;
    }

    /**
     * Gets the value of the toDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getToDate() {
        return toDate;
    }

    /**
     * Sets the value of the toDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setToDate(XMLGregorianCalendar value) {
        this.toDate = value;
    }

    /**
     * Gets the value of the loginResponse property.
     * 
     * @return
     *     possible object is
     *     {@link LoginResponse }
     *     
     */
    public LoginResponse getLoginResponse() {
        return loginResponse;
    }

    /**
     * Sets the value of the loginResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link LoginResponse }
     *     
     */
    public void setLoginResponse(LoginResponse value) {
        this.loginResponse = value;
    }

    /**
     * Gets the value of the generalResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GeneralResponse }
     *     
     */
    public GeneralResponse getGeneralResponse() {
        return generalResponse;
    }

    /**
     * Sets the value of the generalResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeneralResponse }
     *     
     */
    public void setGeneralResponse(GeneralResponse value) {
        this.generalResponse = value;
    }

    /**
     * Gets the value of the getSpecResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetSpecResponse }
     *     
     */
    public GetSpecResponse getGetSpecResponse() {
        return getSpecResponse;
    }

    /**
     * Sets the value of the getSpecResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetSpecResponse }
     *     
     */
    public void setGetSpecResponse(GetSpecResponse value) {
        this.getSpecResponse = value;
    }

}
