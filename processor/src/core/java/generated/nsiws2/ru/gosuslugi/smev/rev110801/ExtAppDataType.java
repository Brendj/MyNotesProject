
package generated.nsiws2.ru.gosuslugi.smev.rev110801;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import generated.nsiws2.com.rstyle.nsi.beans.Category;
import generated.nsiws2.com.rstyle.nsi.beans.Context;
import generated.nsiws2.com.rstyle.nsi.beans.GeneralResponse;
import generated.nsiws2.com.rstyle.nsi.beans.GetInformationFromEgrulRequest;
import generated.nsiws2.com.rstyle.nsi.beans.GetSpecResponse;
import generated.nsiws2.com.rstyle.nsi.beans.Item;
import generated.nsiws2.com.rstyle.nsi.beans.LoginResponse;
import generated.nsiws2.com.rstyle.nsi.beans.SearchPredicate;
import generated.nsiws2.com.rstyle.nsi.beans.StudentSearch;


/**
 * <p>Java class for ExtAppDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExtAppDataType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://smev.gosuslugi.ru/rev110801}AppDataType">
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
 *             &lt;element ref="{http://rstyle.com/nsi/beans}category" maxOccurs="unbounded" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}item" maxOccurs="unbounded" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}searchPredicate" maxOccurs="unbounded" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}fromDate" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}toDate" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}from" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}limit" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}checkRequest" minOccurs="0"/>
 *             &lt;element ref="{http://rstyle.com/nsi/beans}studentSearch" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;/sequence>
 *           &lt;element ref="{http://rstyle.com/nsi/beans}getInformationFromEgrulRequest"/>
 *           &lt;element ref="{http://rstyle.com/nsi/beans}loginResponse"/>
 *           &lt;element ref="{http://rstyle.com/nsi/beans}generalResponse"/>
 *           &lt;element ref="{http://rstyle.com/nsi/beans}getSpecResponse"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtAppDataType", propOrder = {
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
    "from",
    "limit",
    "checkRequest",
    "studentSearch",
    "getInformationFromEgrulRequest",
    "loginResponse",
    "generalResponse",
    "getSpecResponse"
})
public class ExtAppDataType
    extends AppDataType
{

    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected Context context;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected String catalogName;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected String specName;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected String hierarchyName;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected String itemPk;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected String categoryPath;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected String lookupTableName;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected String lookupKey;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected String attrName;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected String query;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected List<String> categoryNames;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected Boolean matchAllCategories;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected List<Category> category;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected List<Item> item;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected List<SearchPredicate> searchPredicate;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar fromDate;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar toDate;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected Long from;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected Integer limit;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected String checkRequest;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected List<StudentSearch> studentSearch;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected GetInformationFromEgrulRequest getInformationFromEgrulRequest;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected LoginResponse loginResponse;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
    protected GeneralResponse generalResponse;
    @XmlElement(namespace = "http://rstyle.com/nsi/beans")
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
     * Gets the value of the from property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFrom() {
        return from;
    }

    /**
     * Sets the value of the from property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFrom(Long value) {
        this.from = value;
    }

    /**
     * Gets the value of the limit property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * Sets the value of the limit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLimit(Integer value) {
        this.limit = value;
    }

    /**
     * Gets the value of the checkRequest property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCheckRequest() {
        return checkRequest;
    }

    /**
     * Sets the value of the checkRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCheckRequest(String value) {
        this.checkRequest = value;
    }

    /**
     * Gets the value of the studentSearch property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the studentSearch property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStudentSearch().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StudentSearch }
     * 
     * 
     */
    public List<StudentSearch> getStudentSearch() {
        if (studentSearch == null) {
            studentSearch = new ArrayList<StudentSearch>();
        }
        return this.studentSearch;
    }

    /**
     * Gets the value of the getInformationFromEgrulRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetInformationFromEgrulRequest }
     *     
     */
    public GetInformationFromEgrulRequest getGetInformationFromEgrulRequest() {
        return getInformationFromEgrulRequest;
    }

    /**
     * Sets the value of the getInformationFromEgrulRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetInformationFromEgrulRequest }
     *     
     */
    public void setGetInformationFromEgrulRequest(GetInformationFromEgrulRequest value) {
        this.getInformationFromEgrulRequest = value;
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
