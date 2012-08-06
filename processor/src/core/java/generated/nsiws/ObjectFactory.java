
package generated.nsiws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated.nsiws package. 
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

    private final static QName _GetItemAttributeValuesRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getItemAttributeValuesRequest");
    private final static QName _NsiResponse_QNAME = new QName("http://rstyle.com/nsi/services/out", "nsiResponse");
    private final static QName _AppData_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "AppData");
    private final static QName _GetLookupTableValuesRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getLookupTableValuesRequest");
    private final static QName _LoginResponse_QNAME = new QName("http://rstyle.com/nsi/beans", "loginResponse");
    private final static QName _GetAllLookupTablesUsingSpecRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getAllLookupTablesUsingSpecRequest");
    private final static QName _Context_QNAME = new QName("http://rstyle.com/nsi/beans", "context");
    private final static QName _RequestCode_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "RequestCode");
    private final static QName _GetAllItemsInCatalogRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getAllItemsInCatalogRequest");
    private final static QName _GetCatalogSpecRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getCatalogSpecRequest");
    private final static QName _AttrName_QNAME = new QName("http://rstyle.com/nsi/beans", "attrName");
    private final static QName _CreateItemRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "createItemRequest");
    private final static QName _ItemPk_QNAME = new QName("http://rstyle.com/nsi/beans", "itemPk");
    private final static QName _DeleteCategoryRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "deleteCategoryRequest");
    private final static QName _CategoryPath_QNAME = new QName("http://rstyle.com/nsi/beans", "categoryPath");
    private final static QName _GetCatalogChangesRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getCatalogChangesRequest");
    private final static QName _CatalogName_QNAME = new QName("http://rstyle.com/nsi/beans", "catalogName");
    private final static QName _GetItemAttributeValueRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getItemAttributeValueRequest");
    private final static QName _BaseMessage_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "BaseMessage");
    private final static QName _Item_QNAME = new QName("http://rstyle.com/nsi/beans", "item");
    private final static QName _GetItemCategoriesRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getItemCategoriesRequest");
    private final static QName _MessageClass_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "MessageClass");
    private final static QName _TestMsg_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "TestMsg");
    private final static QName _Query_QNAME = new QName("http://rstyle.com/nsi/beans", "query");
    private final static QName _GetAllEntriesInLookupTableRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getAllEntriesInLookupTableRequest");
    private final static QName _DeleteLookupEntryRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "deleteLookupEntryRequest");
    private final static QName _ServiceCode_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "ServiceCode");
    private final static QName _CaseNumber_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "CaseNumber");
    private final static QName _Recipient_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "Recipient");
    private final static QName _OriginRequestIdRef_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "OriginRequestIdRef");
    private final static QName _GetAllCatalogsRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getAllCatalogsRequest");
    private final static QName _ExchangeType_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "ExchangeType");
    private final static QName _RequestIdRef_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "RequestIdRef");
    private final static QName _GetHistoryCatalogRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getHistoryCatalogRequest");
    private final static QName _GetDynamicItemAttributeValuesRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getDynamicItemAttributeValuesRequest");
    private final static QName _Date_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "Date");
    private final static QName _TypeCode_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "TypeCode");
    private final static QName _MessageId_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "MessageId");
    private final static QName _ToDate_QNAME = new QName("http://rstyle.com/nsi/beans", "toDate");
    private final static QName _BinaryData_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "BinaryData");
    private final static QName _GetQueryResultsRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getQueryResultsRequest");
    private final static QName _GetAllLookupTablesRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getAllLookupTablesRequest");
    private final static QName _DigestValue_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "DigestValue");
    private final static QName _GeneralResponse_QNAME = new QName("http://rstyle.com/nsi/beans", "generalResponse");
    private final static QName _GetLookupAttributeValuesRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getLookupAttributeValuesRequest");
    private final static QName _AppDocument_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "AppDocument");
    private final static QName _Originator_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "Originator");
    private final static QName _GetCatalogsUsingSpecRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getCatalogsUsingSpecRequest");
    private final static QName _Message_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "Message");
    private final static QName _CreateCategoryRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "createCategoryRequest");
    private final static QName _HierarchyName_QNAME = new QName("http://rstyle.com/nsi/beans", "hierarchyName");
    private final static QName _MatchAllCategories_QNAME = new QName("http://rstyle.com/nsi/beans", "matchAllCategories");
    private final static QName _SearchPredicate_QNAME = new QName("http://rstyle.com/nsi/beans", "searchPredicate");
    private final static QName _Include_QNAME = new QName("http://www.w3.org/2004/08/xop/include", "Include");
    private final static QName _LoginRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "loginRequest");
    private final static QName _Category_QNAME = new QName("http://rstyle.com/nsi/beans", "category");
    private final static QName _Status_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "Status");
    private final static QName _TimeStamp_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "TimeStamp");
    private final static QName _FromDate_QNAME = new QName("http://rstyle.com/nsi/beans", "fromDate");
    private final static QName _SpecName_QNAME = new QName("http://rstyle.com/nsi/beans", "specName");
    private final static QName _GetSpecResponse_QNAME = new QName("http://rstyle.com/nsi/beans", "getSpecResponse");
    private final static QName _SetLookupAttributeValuesRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "setLookupAttributeValuesRequest");
    private final static QName _MessageData_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "MessageData");
    private final static QName _LookupKey_QNAME = new QName("http://rstyle.com/nsi/beans", "lookupKey");
    private final static QName _SetItemAttributeValuesRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "setItemAttributeValuesRequest");
    private final static QName _SearchItemsInCatalogRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "searchItemsInCatalogRequest");
    private final static QName _DeleteItemRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "deleteItemRequest");
    private final static QName _GetHistoryItemRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getHistoryItemRequest");
    private final static QName _GetHierarchySpecRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getHierarchySpecRequest");
    private final static QName _CategoryNames_QNAME = new QName("http://rstyle.com/nsi/beans", "categoryNames");
    private final static QName _GetCategoryAttributeValuesRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getCategoryAttributeValuesRequest");
    private final static QName _SetCategoryAttributeValuesRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "setCategoryAttributeValuesRequest");
    private final static QName _CreateLookupEntryRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "createLookupEntryRequest");
    private final static QName _GetCategoriesForCatalogRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getCategoriesForCatalogRequest");
    private final static QName _Sender_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "Sender");
    private final static QName _NodeId_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "NodeId");
    private final static QName _Reference_QNAME = new QName("http://rstyle.com/nsi/services/out", "reference");
    private final static QName _Header_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "Header");
    private final static QName _LookupTableName_QNAME = new QName("http://rstyle.com/nsi/beans", "lookupTableName");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated.nsiws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NSIResponseType }
     * 
     */
    public NSIResponseType createNSIResponseType() {
        return new NSIResponseType();
    }

    /**
     * Create an instance of {@link AppDataType }
     * 
     */
    public AppDataType createAppDataType() {
        return new AppDataType();
    }

    /**
     * Create an instance of {@link AppDocumentType }
     * 
     */
    public AppDocumentType createAppDocumentType() {
        return new AppDocumentType();
    }

    /**
     * Create an instance of {@link Response }
     * 
     */
    public Response createResponse() {
        return new Response();
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
     * Create an instance of {@link NSIRequestType }
     * 
     */
    public NSIRequestType createNSIRequestType() {
        return new NSIRequestType();
    }

    /**
     * Create an instance of {@link AttributeDef }
     * 
     */
    public AttributeDef createAttributeDef() {
        return new AttributeDef();
    }

    /**
     * Create an instance of {@link SearchPredicate }
     * 
     */
    public SearchPredicate createSearchPredicate() {
        return new SearchPredicate();
    }

    /**
     * Create an instance of {@link Category }
     * 
     */
    public Category createCategory() {
        return new Category();
    }

    /**
     * Create an instance of {@link MessageType }
     * 
     */
    public MessageType createMessageType() {
        return new MessageType();
    }

    /**
     * Create an instance of {@link HeaderType }
     * 
     */
    public HeaderType createHeaderType() {
        return new HeaderType();
    }

    /**
     * Create an instance of {@link LoginResponse }
     * 
     */
    public LoginResponse createLoginResponse() {
        return new LoginResponse();
    }

    /**
     * Create an instance of {@link GeneralResponse }
     * 
     */
    public GeneralResponse createGeneralResponse() {
        return new GeneralResponse();
    }

    /**
     * Create an instance of {@link Include }
     * 
     */
    public Include createInclude() {
        return new Include();
    }

    /**
     * Create an instance of {@link Item }
     * 
     */
    public Item createItem() {
        return new Item();
    }

    /**
     * Create an instance of {@link AttributeDef.LocalizedName }
     * 
     */
    public AttributeDef.LocalizedName createAttributeDefLocalizedName() {
        return new AttributeDef.LocalizedName();
    }

    /**
     * Create an instance of {@link MessageDataType }
     * 
     */
    public MessageDataType createMessageDataType() {
        return new MessageDataType();
    }

    /**
     * Create an instance of {@link Context }
     * 
     */
    public Context createContext() {
        return new Context();
    }

    /**
     * Create an instance of {@link Spec }
     * 
     */
    public Spec createSpec() {
        return new Spec();
    }

    /**
     * Create an instance of {@link OrgExternalType }
     * 
     */
    public OrgExternalType createOrgExternalType() {
        return new OrgExternalType();
    }

    /**
     * Create an instance of {@link BaseMessageType }
     * 
     */
    public BaseMessageType createBaseMessageType() {
        return new BaseMessageType();
    }

    /**
     * Create an instance of {@link Attribute }
     * 
     */
    public Attribute createAttribute() {
        return new Attribute();
    }

    /**
     * Create an instance of {@link QueryResult }
     * 
     */
    public QueryResult createQueryResult() {
        return new QueryResult();
    }

    /**
     * Create an instance of {@link Entry }
     * 
     */
    public Entry createEntry() {
        return new Entry();
    }

    /**
     * Create an instance of {@link ReferenceType }
     * 
     */
    public ReferenceType createReferenceType() {
        return new ReferenceType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getItemAttributeValuesRequest")
    public JAXBElement<NSIRequestType> createGetItemAttributeValuesRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetItemAttributeValuesRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/out", name = "nsiResponse")
    public JAXBElement<NSIResponseType> createNsiResponse(NSIResponseType value) {
        return new JAXBElement<NSIResponseType>(_NsiResponse_QNAME, NSIResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AppDataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "AppData")
    public JAXBElement<AppDataType> createAppData(AppDataType value) {
        return new JAXBElement<AppDataType>(_AppData_QNAME, AppDataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getLookupTableValuesRequest")
    public JAXBElement<NSIRequestType> createGetLookupTableValuesRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetLookupTableValuesRequest_QNAME, NSIRequestType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getAllLookupTablesUsingSpecRequest")
    public JAXBElement<NSIRequestType> createGetAllLookupTablesUsingSpecRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetAllLookupTablesUsingSpecRequest_QNAME, NSIRequestType.class, null, value);
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
     * Create an instance of {@link SmevReference }}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "Reference")
    public SmevReference createSmevReference(ReferenceType value) {
        return new SmevReference(value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "RequestCode")
    public JAXBElement<String> createRequestCode(String value) {
        return new JAXBElement<String>(_RequestCode_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getAllItemsInCatalogRequest")
    public JAXBElement<NSIRequestType> createGetAllItemsInCatalogRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetAllItemsInCatalogRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getCatalogSpecRequest")
    public JAXBElement<NSIRequestType> createGetCatalogSpecRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetCatalogSpecRequest_QNAME, NSIRequestType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "createItemRequest")
    public JAXBElement<NSIRequestType> createCreateItemRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_CreateItemRequest_QNAME, NSIRequestType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "deleteCategoryRequest")
    public JAXBElement<NSIRequestType> createDeleteCategoryRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_DeleteCategoryRequest_QNAME, NSIRequestType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getCatalogChangesRequest")
    public JAXBElement<NSIRequestType> createGetCatalogChangesRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetCatalogChangesRequest_QNAME, NSIRequestType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getItemAttributeValueRequest")
    public JAXBElement<NSIRequestType> createGetItemAttributeValueRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetItemAttributeValueRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BaseMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "BaseMessage")
    public JAXBElement<BaseMessageType> createBaseMessage(BaseMessageType value) {
        return new JAXBElement<BaseMessageType>(_BaseMessage_QNAME, BaseMessageType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getItemCategoriesRequest")
    public JAXBElement<NSIRequestType> createGetItemCategoriesRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetItemCategoriesRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MessageClassType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "MessageClass")
    public JAXBElement<MessageClassType> createMessageClass(MessageClassType value) {
        return new JAXBElement<MessageClassType>(_MessageClass_QNAME, MessageClassType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "TestMsg")
    public JAXBElement<String> createTestMsg(String value) {
        return new JAXBElement<String>(_TestMsg_QNAME, String.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getAllEntriesInLookupTableRequest")
    public JAXBElement<NSIRequestType> createGetAllEntriesInLookupTableRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetAllEntriesInLookupTableRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "deleteLookupEntryRequest")
    public JAXBElement<NSIRequestType> createDeleteLookupEntryRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_DeleteLookupEntryRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "ServiceCode")
    public JAXBElement<String> createServiceCode(String value) {
        return new JAXBElement<String>(_ServiceCode_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "CaseNumber")
    public JAXBElement<String> createCaseNumber(String value) {
        return new JAXBElement<String>(_CaseNumber_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrgExternalType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "Recipient")
    public JAXBElement<OrgExternalType> createRecipient(OrgExternalType value) {
        return new JAXBElement<OrgExternalType>(_Recipient_QNAME, OrgExternalType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "OriginRequestIdRef")
    public JAXBElement<String> createOriginRequestIdRef(String value) {
        return new JAXBElement<String>(_OriginRequestIdRef_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getAllCatalogsRequest")
    public JAXBElement<NSIRequestType> createGetAllCatalogsRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetAllCatalogsRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "ExchangeType")
    public JAXBElement<String> createExchangeType(String value) {
        return new JAXBElement<String>(_ExchangeType_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "RequestIdRef")
    public JAXBElement<String> createRequestIdRef(String value) {
        return new JAXBElement<String>(_RequestIdRef_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getHistoryCatalogRequest")
    public JAXBElement<NSIRequestType> createGetHistoryCatalogRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetHistoryCatalogRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getDynamicItemAttributeValuesRequest")
    public JAXBElement<NSIRequestType> createGetDynamicItemAttributeValuesRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetDynamicItemAttributeValuesRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "Date")
    public JAXBElement<XMLGregorianCalendar> createDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_Date_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TypeCodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "TypeCode")
    public JAXBElement<TypeCodeType> createTypeCode(TypeCodeType value) {
        return new JAXBElement<TypeCodeType>(_TypeCode_QNAME, TypeCodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "MessageId")
    public JAXBElement<String> createMessageId(String value) {
        return new JAXBElement<String>(_MessageId_QNAME, String.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "BinaryData")
    public JAXBElement<byte[]> createBinaryData(byte[] value) {
        return new JAXBElement<byte[]>(_BinaryData_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getQueryResultsRequest")
    public JAXBElement<NSIRequestType> createGetQueryResultsRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetQueryResultsRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getAllLookupTablesRequest")
    public JAXBElement<NSIRequestType> createGetAllLookupTablesRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetAllLookupTablesRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "DigestValue")
    public JAXBElement<byte[]> createDigestValue(byte[] value) {
        return new JAXBElement<byte[]>(_DigestValue_QNAME, byte[].class, null, ((byte[]) value));
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
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getLookupAttributeValuesRequest")
    public JAXBElement<NSIRequestType> createGetLookupAttributeValuesRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetLookupAttributeValuesRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AppDocumentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "AppDocument")
    public JAXBElement<AppDocumentType> createAppDocument(AppDocumentType value) {
        return new JAXBElement<AppDocumentType>(_AppDocument_QNAME, AppDocumentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrgExternalType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "Originator")
    public JAXBElement<OrgExternalType> createOriginator(OrgExternalType value) {
        return new JAXBElement<OrgExternalType>(_Originator_QNAME, OrgExternalType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getCatalogsUsingSpecRequest")
    public JAXBElement<NSIRequestType> createGetCatalogsUsingSpecRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetCatalogsUsingSpecRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "Message")
    public JAXBElement<MessageType> createMessage(MessageType value) {
        return new JAXBElement<MessageType>(_Message_QNAME, MessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "createCategoryRequest")
    public JAXBElement<NSIRequestType> createCreateCategoryRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_CreateCategoryRequest_QNAME, NSIRequestType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link Include }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2004/08/xop/include", name = "Include")
    public JAXBElement<Include> createInclude(Include value) {
        return new JAXBElement<Include>(_Include_QNAME, Include.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "loginRequest")
    public JAXBElement<NSIRequestType> createLoginRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_LoginRequest_QNAME, NSIRequestType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link StatusType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "Status")
    public JAXBElement<StatusType> createStatus(StatusType value) {
        return new JAXBElement<StatusType>(_Status_QNAME, StatusType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "TimeStamp")
    public JAXBElement<XMLGregorianCalendar> createTimeStamp(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_TimeStamp_QNAME, XMLGregorianCalendar.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "setLookupAttributeValuesRequest")
    public JAXBElement<NSIRequestType> createSetLookupAttributeValuesRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_SetLookupAttributeValuesRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MessageDataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "MessageData")
    public JAXBElement<MessageDataType> createMessageData(MessageDataType value) {
        return new JAXBElement<MessageDataType>(_MessageData_QNAME, MessageDataType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "setItemAttributeValuesRequest")
    public JAXBElement<NSIRequestType> createSetItemAttributeValuesRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_SetItemAttributeValuesRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "searchItemsInCatalogRequest")
    public JAXBElement<NSIRequestType> createSearchItemsInCatalogRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_SearchItemsInCatalogRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "deleteItemRequest")
    public JAXBElement<NSIRequestType> createDeleteItemRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_DeleteItemRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getHistoryItemRequest")
    public JAXBElement<NSIRequestType> createGetHistoryItemRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetHistoryItemRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getHierarchySpecRequest")
    public JAXBElement<NSIRequestType> createGetHierarchySpecRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetHierarchySpecRequest_QNAME, NSIRequestType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getCategoryAttributeValuesRequest")
    public JAXBElement<NSIRequestType> createGetCategoryAttributeValuesRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetCategoryAttributeValuesRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "setCategoryAttributeValuesRequest")
    public JAXBElement<NSIRequestType> createSetCategoryAttributeValuesRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_SetCategoryAttributeValuesRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "createLookupEntryRequest")
    public JAXBElement<NSIRequestType> createCreateLookupEntryRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_CreateLookupEntryRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getCategoriesForCatalogRequest")
    public JAXBElement<NSIRequestType> createGetCategoriesForCatalogRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetCategoriesForCatalogRequest_QNAME, NSIRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrgExternalType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "Sender")
    public JAXBElement<OrgExternalType> createSender(OrgExternalType value) {
        return new JAXBElement<OrgExternalType>(_Sender_QNAME, OrgExternalType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "NodeId")
    public JAXBElement<String> createNodeId(String value) {
        return new JAXBElement<String>(_NodeId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/out", name = "reference")
    public JAXBElement<byte[]> createReference(byte[] value) {
        return new JAXBElement<byte[]>(_Reference_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HeaderType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "Header")
    public JAXBElement<HeaderType> createHeader(HeaderType value) {
        return new JAXBElement<HeaderType>(_Header_QNAME, HeaderType.class, null, value);
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
