
package generated.nsiws.nsi.beans;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.rstyle.nsi.beans package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Error_QNAME = new QName("http://rstyle.com/nsi/beans", "error");
    private final static QName _LoginResponse_QNAME = new QName("http://rstyle.com/nsi/beans", "loginResponse");
    private final static QName _Context_QNAME = new QName("http://rstyle.com/nsi/beans", "context");
    private final static QName _AttrName_QNAME = new QName("http://rstyle.com/nsi/beans", "attrName");
    private final static QName _ItemPk_QNAME = new QName("http://rstyle.com/nsi/beans", "itemPk");
    private final static QName _CategoryPath_QNAME = new QName("http://rstyle.com/nsi/beans", "categoryPath");
    private final static QName _CatalogName_QNAME = new QName("http://rstyle.com/nsi/beans", "catalogName");
    private final static QName _Item_QNAME = new QName("http://rstyle.com/nsi/beans", "item");
    private final static QName _From_QNAME = new QName("http://rstyle.com/nsi/beans", "from");
    private final static QName _Query_QNAME = new QName("http://rstyle.com/nsi/beans", "query");
    private final static QName _ToDate_QNAME = new QName("http://rstyle.com/nsi/beans", "toDate");
    private final static QName _Limit_QNAME = new QName("http://rstyle.com/nsi/beans", "limit");
    private final static QName _GeneralResponse_QNAME = new QName("http://rstyle.com/nsi/beans", "generalResponse");
    private final static QName _HierarchyName_QNAME = new QName("http://rstyle.com/nsi/beans", "hierarchyName");
    private final static QName _MatchAllCategories_QNAME = new QName("http://rstyle.com/nsi/beans", "matchAllCategories");
    private final static QName _SearchPredicate_QNAME = new QName("http://rstyle.com/nsi/beans", "searchPredicate");
    private final static QName _Category_QNAME = new QName("http://rstyle.com/nsi/beans", "category");
    private final static QName _FromDate_QNAME = new QName("http://rstyle.com/nsi/beans", "fromDate");
    private final static QName _SpecName_QNAME = new QName("http://rstyle.com/nsi/beans", "specName");
    private final static QName _GetSpecResponse_QNAME = new QName("http://rstyle.com/nsi/beans", "getSpecResponse");
    private final static QName _LookupKey_QNAME = new QName("http://rstyle.com/nsi/beans", "lookupKey");
    private final static QName _CategoryNames_QNAME = new QName("http://rstyle.com/nsi/beans", "categoryNames");
    private final static QName _LookupTableName_QNAME = new QName("http://rstyle.com/nsi/beans", "lookupTableName");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.rstyle.nsi.beans
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AttributeDef }
     * 
     */
    public AttributeDef createAttributeDef() {
        return new AttributeDef();
    }

    /**
     * Create an instance of {@link LoginResponse }
     * 
     */
    public LoginResponse createLoginResponse() {
        return new LoginResponse();
    }

    /**
     * Create an instance of {@link Category }
     * 
     */
    public Category createCategory() {
        return new Category();
    }

    /**
     * Create an instance of {@link Spec }
     * 
     */
    public Spec createSpec() {
        return new Spec();
    }

    /**
     * Create an instance of {@link GeneralResponse }
     * 
     */
    public GeneralResponse createGeneralResponse() {
        return new GeneralResponse();
    }

    /**
     * Create an instance of {@link Response }
     * 
     */
    public Response createResponse() {
        return new Response();
    }

    /**
     * Create an instance of {@link Context }
     * 
     */
    public Context createContext() {
        return new Context();
    }

    /**
     * Create an instance of {@link Entry }
     * 
     */
    public Entry createEntry() {
        return new Entry();
    }

    /**
     * Create an instance of {@link AttributeDef.LocalizedName }
     * 
     */
    public AttributeDef.LocalizedName createAttributeDefLocalizedName() {
        return new AttributeDef.LocalizedName();
    }

    /**
     * Create an instance of {@link Item }
     * 
     */
    public Item createItem() {
        return new Item();
    }

    /**
     * Create an instance of {@link SearchPredicate }
     * 
     */
    public SearchPredicate createSearchPredicate() {
        return new SearchPredicate();
    }

    /**
     * Create an instance of {@link Attribute }
     * 
     */
    public Attribute createAttribute() {
        return new Attribute();
    }

    /**
     * Create an instance of {@link NSIError }
     * 
     */
    public NSIError createNSIError() {
        return new NSIError();
    }

    /**
     * Create an instance of {@link GetSpecResponse }
     * 
     */
    public GetSpecResponse createGetSpecResponse() {
        return new GetSpecResponse();
    }

    /**
     * Create an instance of {@link QueryResult }
     * 
     */
    public QueryResult createQueryResult() {
        return new QueryResult();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIError }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "error")
    public JAXBElement<NSIError> createError(NSIError value) {
        return new JAXBElement<NSIError>(_Error_QNAME, NSIError.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LoginResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "loginResponse")
    public JAXBElement<LoginResponse> createLoginResponse(LoginResponse value) {
        return new JAXBElement<LoginResponse>(_LoginResponse_QNAME, LoginResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Context }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "context")
    public JAXBElement<Context> createContext(Context value) {
        return new JAXBElement<Context>(_Context_QNAME, Context.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "attrName")
    public JAXBElement<String> createAttrName(String value) {
        return new JAXBElement<String>(_AttrName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "itemPk")
    public JAXBElement<String> createItemPk(String value) {
        return new JAXBElement<String>(_ItemPk_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "categoryPath")
    public JAXBElement<String> createCategoryPath(String value) {
        return new JAXBElement<String>(_CategoryPath_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "catalogName")
    public JAXBElement<String> createCatalogName(String value) {
        return new JAXBElement<String>(_CatalogName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Item }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "item")
    public JAXBElement<Item> createItem(Item value) {
        return new JAXBElement<Item>(_Item_QNAME, Item.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "from")
    public JAXBElement<Long> createFrom(Long value) {
        return new JAXBElement<Long>(_From_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "query")
    public JAXBElement<String> createQuery(String value) {
        return new JAXBElement<String>(_Query_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "toDate")
    public JAXBElement<XMLGregorianCalendar> createToDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_ToDate_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "limit")
    public JAXBElement<Integer> createLimit(Integer value) {
        return new JAXBElement<Integer>(_Limit_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GeneralResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "generalResponse")
    public JAXBElement<GeneralResponse> createGeneralResponse(GeneralResponse value) {
        return new JAXBElement<GeneralResponse>(_GeneralResponse_QNAME, GeneralResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "hierarchyName")
    public JAXBElement<String> createHierarchyName(String value) {
        return new JAXBElement<String>(_HierarchyName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "matchAllCategories")
    public JAXBElement<Boolean> createMatchAllCategories(Boolean value) {
        return new JAXBElement<Boolean>(_MatchAllCategories_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchPredicate }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "searchPredicate")
    public JAXBElement<SearchPredicate> createSearchPredicate(SearchPredicate value) {
        return new JAXBElement<SearchPredicate>(_SearchPredicate_QNAME, SearchPredicate.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Category }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "category")
    public JAXBElement<Category> createCategory(Category value) {
        return new JAXBElement<Category>(_Category_QNAME, Category.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "fromDate")
    public JAXBElement<XMLGregorianCalendar> createFromDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_FromDate_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "specName")
    public JAXBElement<String> createSpecName(String value) {
        return new JAXBElement<String>(_SpecName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSpecResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "getSpecResponse")
    public JAXBElement<GetSpecResponse> createGetSpecResponse(GetSpecResponse value) {
        return new JAXBElement<GetSpecResponse>(_GetSpecResponse_QNAME, GetSpecResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "lookupKey")
    public JAXBElement<String> createLookupKey(String value) {
        return new JAXBElement<String>(_LookupKey_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "categoryNames")
    public JAXBElement<String> createCategoryNames(String value) {
        return new JAXBElement<String>(_CategoryNames_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/beans", name = "lookupTableName")
    public JAXBElement<String> createLookupTableName(String value) {
        return new JAXBElement<String>(_LookupTableName_QNAME, String.class, null, value);
    }

}
