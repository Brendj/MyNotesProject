
package generated.nsiws.nsi.services.in;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.rstyle.nsi.services.in package. 
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
    private final static QName _GetLookupTableValuesRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getLookupTableValuesRequest");
    private final static QName _GetAllLookupTablesUsingSpecRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getAllLookupTablesUsingSpecRequest");
    private final static QName _GetAllItemsInCatalogRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getAllItemsInCatalogRequest");
    private final static QName _GetCatalogSpecRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getCatalogSpecRequest");
    private final static QName _CreateItemRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "createItemRequest");
    private final static QName _DeleteCategoryRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "deleteCategoryRequest");
    private final static QName _GetCatalogChangesRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getCatalogChangesRequest");
    private final static QName _GetItemAttributeValueRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getItemAttributeValueRequest");
    private final static QName _GetItemCategoriesRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getItemCategoriesRequest");
    private final static QName _GetAllEntriesInLookupTableRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getAllEntriesInLookupTableRequest");
    private final static QName _DeleteLookupEntryRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "deleteLookupEntryRequest");
    private final static QName _GetAllCatalogsRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getAllCatalogsRequest");
    private final static QName _GetHistoryCatalogRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getHistoryCatalogRequest");
    private final static QName _GetDynamicItemAttributeValuesRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getDynamicItemAttributeValuesRequest");
    private final static QName _GetQueryResultsRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getQueryResultsRequest");
    private final static QName _GetAllLookupTablesRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getAllLookupTablesRequest");
    private final static QName _GetLookupAttributeValuesRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getLookupAttributeValuesRequest");
    private final static QName _GetCatalogsUsingSpecRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getCatalogsUsingSpecRequest");
    private final static QName _CreateCategoryRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "createCategoryRequest");
    private final static QName _LoginRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "loginRequest");
    private final static QName _SetLookupAttributeValuesRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "setLookupAttributeValuesRequest");
    private final static QName _SetItemAttributeValuesRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "setItemAttributeValuesRequest");
    private final static QName _SearchItemsInCatalogRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "searchItemsInCatalogRequest");
    private final static QName _DeleteItemRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "deleteItemRequest");
    private final static QName _GetHistoryItemRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getHistoryItemRequest");
    private final static QName _GetHierarchySpecRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getHierarchySpecRequest");
    private final static QName _GetCategoryAttributeValuesRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getCategoryAttributeValuesRequest");
    private final static QName _SetCategoryAttributeValuesRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "setCategoryAttributeValuesRequest");
    private final static QName _CreateLookupEntryRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "createLookupEntryRequest");
    private final static QName _GetCategoriesForCatalogRequest_QNAME = new QName("http://rstyle.com/nsi/services/in", "getCategoriesForCatalogRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.rstyle.nsi.services.in
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NSIRequestType }
     * 
     */
    public NSIRequestType createNSIRequestType() {
        return new NSIRequestType();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getLookupTableValuesRequest")
    public JAXBElement<NSIRequestType> createGetLookupTableValuesRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetLookupTableValuesRequest_QNAME, NSIRequestType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "createItemRequest")
    public JAXBElement<NSIRequestType> createCreateItemRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_CreateItemRequest_QNAME, NSIRequestType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getCatalogChangesRequest")
    public JAXBElement<NSIRequestType> createGetCatalogChangesRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetCatalogChangesRequest_QNAME, NSIRequestType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getItemCategoriesRequest")
    public JAXBElement<NSIRequestType> createGetItemCategoriesRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetItemCategoriesRequest_QNAME, NSIRequestType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getAllCatalogsRequest")
    public JAXBElement<NSIRequestType> createGetAllCatalogsRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetAllCatalogsRequest_QNAME, NSIRequestType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "getLookupAttributeValuesRequest")
    public JAXBElement<NSIRequestType> createGetLookupAttributeValuesRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_GetLookupAttributeValuesRequest_QNAME, NSIRequestType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "createCategoryRequest")
    public JAXBElement<NSIRequestType> createCreateCategoryRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_CreateCategoryRequest_QNAME, NSIRequestType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/in", name = "setLookupAttributeValuesRequest")
    public JAXBElement<NSIRequestType> createSetLookupAttributeValuesRequest(NSIRequestType value) {
        return new JAXBElement<NSIRequestType>(_SetLookupAttributeValuesRequest_QNAME, NSIRequestType.class, null, value);
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

}
