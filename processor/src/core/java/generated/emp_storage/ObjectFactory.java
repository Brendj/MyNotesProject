
package generated.emp_storage;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated.emp_storage package. 
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

    private final static QName _GetCatalogResponse_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "getCatalogResponse");
    private final static QName _SelectEntriesRequest_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/entry.xsd", "selectEntriesRequest");
    private final static QName _AddEntriesRequest_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/entry.xsd", "addEntriesRequest");
    private final static QName _DeleteIndexResponse_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "deleteIndexResponse");
    private final static QName _AddEntriesResponse_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/entry.xsd", "addEntriesResponse");
    private final static QName _AddAttributeResponse_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "addAttributeResponse");
    private final static QName _UpdateEntriesRequest_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/entry.xsd", "updateEntriesRequest");
    private final static QName _RemoveAttributeFromIndexResponse_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "removeAttributeFromIndexResponse");
    private final static QName _DeleteEntriesRequest_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/entry.xsd", "deleteEntriesRequest");
    private final static QName _AddCatalogRequest_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "addCatalogRequest");
    private final static QName _UpdateEntriesResponse_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/entry.xsd", "updateEntriesResponse");
    private final static QName _AddCatalogResponse_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "addCatalogResponse");
    private final static QName _UpdateCatalogRequest_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "updateCatalogRequest");
    private final static QName _DeleteEntriesResponse_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/entry.xsd", "deleteEntriesResponse");
    private final static QName _ReceiveDataChangesRequest_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/entry.xsd", "receiveDataChangesRequest");
    private final static QName _DeleteAttributeRequest_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "deleteAttributeRequest");
    private final static QName _AddAttributeToIndexRequest_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "addAttributeToIndexRequest");
    private final static QName _DeleteCatalogResponse_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "deleteCatalogResponse");
    private final static QName _SelectCatalogsResponse_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "selectCatalogsResponse");
    private final static QName _SelectEntriesResponse_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/entry.xsd", "selectEntriesResponse");
    private final static QName _GetCatalogRequest_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "getCatalogRequest");
    private final static QName _DeleteAttributeResponse_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "deleteAttributeResponse");
    private final static QName _AddIndexRequest_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "addIndexRequest");
    private final static QName _AddAttributeToIndexResponse_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "addAttributeToIndexResponse");
    private final static QName _DeleteCatalogRequest_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "deleteCatalogRequest");
    private final static QName _ReceiveDataChangesResponse_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/entry.xsd", "receiveDataChangesResponse");
    private final static QName _AddIndexResponse_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "addIndexResponse");
    private final static QName _SelectCatalogsRequest_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "selectCatalogsRequest");
    private final static QName _DeleteIndexRequest_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "deleteIndexRequest");
    private final static QName _AddAttributeRequest_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "addAttributeRequest");
    private final static QName _UpdateCatalogResponse_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "updateCatalogResponse");
    private final static QName _RemoveAttributeFromIndexRequest_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "removeAttributeFromIndexRequest");
    private final static QName _GetCatalogResponseResult_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/catalog.xsd", "result");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated.emp_storage
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AddEntriesResponse.Result }
     * 
     */
    public AddEntriesResponse.Result createAddEntriesResponseResult() {
        return new AddEntriesResponse.Result();
    }

    /**
     * Create an instance of {@link Paging }
     * 
     */
    public Paging createPaging() {
        return new Paging();
    }

    /**
     * Create an instance of {@link RemoveAttributeFromIndexResponse }
     * 
     */
    public RemoveAttributeFromIndexResponse createRemoveAttributeFromIndexResponse() {
        return new RemoveAttributeFromIndexResponse();
    }

    /**
     * Create an instance of {@link DeleteIndexResponse }
     * 
     */
    public DeleteIndexResponse createDeleteIndexResponse() {
        return new DeleteIndexResponse();
    }

    /**
     * Create an instance of {@link GetCatalogResponse }
     * 
     */
    public GetCatalogResponse createGetCatalogResponse() {
        return new GetCatalogResponse();
    }

    /**
     * Create an instance of {@link AddAttributeResponse }
     * 
     */
    public AddAttributeResponse createAddAttributeResponse() {
        return new AddAttributeResponse();
    }

    /**
     * Create an instance of {@link BaseResponse }
     * 
     */
    public BaseResponse createBaseResponse() {
        return new BaseResponse();
    }

    /**
     * Create an instance of {@link AddAttributeToIndexResponse }
     * 
     */
    public AddAttributeToIndexResponse createAddAttributeToIndexResponse() {
        return new AddAttributeToIndexResponse();
    }

    /**
     * Create an instance of {@link Index }
     * 
     */
    public Index createIndex() {
        return new Index();
    }

    /**
     * Create an instance of {@link DeleteAttributeRequest }
     * 
     */
    public DeleteAttributeRequest createDeleteAttributeRequest() {
        return new DeleteAttributeRequest();
    }

    /**
     * Create an instance of {@link AddEntriesRequest }
     * 
     */
    public AddEntriesRequest createAddEntriesRequest() {
        return new AddEntriesRequest();
    }

    /**
     * Create an instance of {@link AddCatalogRequest }
     * 
     */
    public AddCatalogRequest createAddCatalogRequest() {
        return new AddCatalogRequest();
    }

    /**
     * Create an instance of {@link SelectCatalogsResponse }
     * 
     */
    public SelectCatalogsResponse createSelectCatalogsResponse() {
        return new SelectCatalogsResponse();
    }

    /**
     * Create an instance of {@link AddAttributeRequest }
     * 
     */
    public AddAttributeRequest createAddAttributeRequest() {
        return new AddAttributeRequest();
    }

    /**
     * Create an instance of {@link DeleteIndexRequest }
     * 
     */
    public DeleteIndexRequest createDeleteIndexRequest() {
        return new DeleteIndexRequest();
    }

    /**
     * Create an instance of {@link SelectCatalogsResponse.Result }
     * 
     */
    public SelectCatalogsResponse.Result createSelectCatalogsResponseResult() {
        return new SelectCatalogsResponse.Result();
    }

    /**
     * Create an instance of {@link UpdateCatalogResponse }
     * 
     */
    public UpdateCatalogResponse createUpdateCatalogResponse() {
        return new UpdateCatalogResponse();
    }

    /**
     * Create an instance of {@link ReceiveDataChangesResponse.Result }
     * 
     */
    public ReceiveDataChangesResponse.Result createReceiveDataChangesResponseResult() {
        return new ReceiveDataChangesResponse.Result();
    }

    /**
     * Create an instance of {@link BaseRequest }
     * 
     */
    public BaseRequest createBaseRequest() {
        return new BaseRequest();
    }

    /**
     * Create an instance of {@link DeleteAttributeResponse }
     * 
     */
    public DeleteAttributeResponse createDeleteAttributeResponse() {
        return new DeleteAttributeResponse();
    }

    /**
     * Create an instance of {@link DeleteEntriesResponse.Result }
     * 
     */
    public DeleteEntriesResponse.Result createDeleteEntriesResponseResult() {
        return new DeleteEntriesResponse.Result();
    }

    /**
     * Create an instance of {@link EntryAttribute }
     * 
     */
    public EntryAttribute createEntryAttribute() {
        return new EntryAttribute();
    }

    /**
     * Create an instance of {@link ReceiveDataChangesResponse.Result.Entry.Identifier }
     * 
     */
    public ReceiveDataChangesResponse.Result.Entry.Identifier createReceiveDataChangesResponseResultEntryIdentifier() {
        return new ReceiveDataChangesResponse.Result.Entry.Identifier();
    }

    /**
     * Create an instance of {@link RemoveAttributeFromIndexRequest }
     * 
     */
    public RemoveAttributeFromIndexRequest createRemoveAttributeFromIndexRequest() {
        return new RemoveAttributeFromIndexRequest();
    }

    /**
     * Create an instance of {@link AddIndexRequest }
     * 
     */
    public AddIndexRequest createAddIndexRequest() {
        return new AddIndexRequest();
    }

    /**
     * Create an instance of {@link IndexList }
     * 
     */
    public IndexList createIndexList() {
        return new IndexList();
    }

    /**
     * Create an instance of {@link AddCatalogResponse }
     * 
     */
    public AddCatalogResponse createAddCatalogResponse() {
        return new AddCatalogResponse();
    }

    /**
     * Create an instance of {@link GetCatalogRequest }
     * 
     */
    public GetCatalogRequest createGetCatalogRequest() {
        return new GetCatalogRequest();
    }

    /**
     * Create an instance of {@link AddIndexResponse }
     * 
     */
    public AddIndexResponse createAddIndexResponse() {
        return new AddIndexResponse();
    }

    /**
     * Create an instance of {@link UpdateEntriesResponse }
     * 
     */
    public UpdateEntriesResponse createUpdateEntriesResponse() {
        return new UpdateEntriesResponse();
    }

    /**
     * Create an instance of {@link generated.emp_storage.Entry }
     * 
     */
    public generated.emp_storage.Entry createEntry() {
        return new generated.emp_storage.Entry();
    }

    /**
     * Create an instance of {@link ReceiveDataChangesResponse.Result.Entry }
     * 
     */
    public ReceiveDataChangesResponse.Result.Entry createReceiveDataChangesResponseResultEntry() {
        return new ReceiveDataChangesResponse.Result.Entry();
    }

    /**
     * Create an instance of {@link SelectEntriesResponse }
     * 
     */
    public SelectEntriesResponse createSelectEntriesResponse() {
        return new SelectEntriesResponse();
    }

    /**
     * Create an instance of {@link ReceiveDataChangesResponse }
     * 
     */
    public ReceiveDataChangesResponse createReceiveDataChangesResponse() {
        return new ReceiveDataChangesResponse();
    }

    /**
     * Create an instance of {@link Catalog }
     * 
     */
    public Catalog createCatalog() {
        return new Catalog();
    }

    /**
     * Create an instance of {@link AttributeList }
     * 
     */
    public AttributeList createAttributeList() {
        return new AttributeList();
    }

    /**
     * Create an instance of {@link ReceiveDataChangesResponse.Result.Entry.Attribute }
     * 
     */
    public ReceiveDataChangesResponse.Result.Entry.Attribute createReceiveDataChangesResponseResultEntryAttribute() {
        return new ReceiveDataChangesResponse.Result.Entry.Attribute();
    }

    /**
     * Create an instance of {@link CatalogList }
     * 
     */
    public CatalogList createCatalogList() {
        return new CatalogList();
    }

    /**
     * Create an instance of {@link UpdateEntriesResponse.Result }
     * 
     */
    public UpdateEntriesResponse.Result createUpdateEntriesResponseResult() {
        return new UpdateEntriesResponse.Result();
    }

    /**
     * Create an instance of {@link generated.emp_storage.Attribute }
     * 
     */
    public generated.emp_storage.Attribute createAttribute() {
        return new generated.emp_storage.Attribute();
    }

    /**
     * Create an instance of {@link DeleteCatalogRequest }
     * 
     */
    public DeleteCatalogRequest createDeleteCatalogRequest() {
        return new DeleteCatalogRequest();
    }

    /**
     * Create an instance of {@link EntryList }
     * 
     */
    public EntryList createEntryList() {
        return new EntryList();
    }

    /**
     * Create an instance of {@link DeleteCatalogResponse }
     * 
     */
    public DeleteCatalogResponse createDeleteCatalogResponse() {
        return new DeleteCatalogResponse();
    }

    /**
     * Create an instance of {@link AddAttributeToIndexRequest }
     * 
     */
    public AddAttributeToIndexRequest createAddAttributeToIndexRequest() {
        return new AddAttributeToIndexRequest();
    }

    /**
     * Create an instance of {@link UpdateEntriesRequest }
     * 
     */
    public UpdateEntriesRequest createUpdateEntriesRequest() {
        return new UpdateEntriesRequest();
    }

    /**
     * Create an instance of {@link SelectEntriesResponse.Result }
     * 
     */
    public SelectEntriesResponse.Result createSelectEntriesResponseResult() {
        return new SelectEntriesResponse.Result();
    }

    /**
     * Create an instance of {@link AddEntriesResponse }
     * 
     */
    public AddEntriesResponse createAddEntriesResponse() {
        return new AddEntriesResponse();
    }

    /**
     * Create an instance of {@link DeleteEntriesResponse }
     * 
     */
    public DeleteEntriesResponse createDeleteEntriesResponse() {
        return new DeleteEntriesResponse();
    }

    /**
     * Create an instance of {@link SelectEntriesRequest }
     * 
     */
    public SelectEntriesRequest createSelectEntriesRequest() {
        return new SelectEntriesRequest();
    }

    /**
     * Create an instance of {@link DeleteEntriesRequest }
     * 
     */
    public DeleteEntriesRequest createDeleteEntriesRequest() {
        return new DeleteEntriesRequest();
    }

    /**
     * Create an instance of {@link ReceiveDataChangesRequest }
     * 
     */
    public ReceiveDataChangesRequest createReceiveDataChangesRequest() {
        return new ReceiveDataChangesRequest();
    }

    /**
     * Create an instance of {@link SelectCatalogsRequest }
     * 
     */
    public SelectCatalogsRequest createSelectCatalogsRequest() {
        return new SelectCatalogsRequest();
    }

    /**
     * Create an instance of {@link Index.Attribute }
     * 
     */
    public Index.Attribute createIndexAttribute() {
        return new Index.Attribute();
    }

    /**
     * Create an instance of {@link UpdateCatalogRequest }
     * 
     */
    public UpdateCatalogRequest createUpdateCatalogRequest() {
        return new UpdateCatalogRequest();
    }

    /**
     * Create an instance of {@link AddAttributeToIndexRequest.Attribute }
     * 
     */
    public AddAttributeToIndexRequest.Attribute createAddAttributeToIndexRequestAttribute() {
        return new AddAttributeToIndexRequest.Attribute();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCatalogResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "getCatalogResponse")
    public JAXBElement<GetCatalogResponse> createGetCatalogResponse(GetCatalogResponse value) {
        return new JAXBElement<GetCatalogResponse>(_GetCatalogResponse_QNAME, GetCatalogResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectEntriesRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", name = "selectEntriesRequest")
    public JAXBElement<SelectEntriesRequest> createSelectEntriesRequest(SelectEntriesRequest value) {
        return new JAXBElement<SelectEntriesRequest>(_SelectEntriesRequest_QNAME, SelectEntriesRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddEntriesRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", name = "addEntriesRequest")
    public JAXBElement<AddEntriesRequest> createAddEntriesRequest(AddEntriesRequest value) {
        return new JAXBElement<AddEntriesRequest>(_AddEntriesRequest_QNAME, AddEntriesRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteIndexResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "deleteIndexResponse")
    public JAXBElement<DeleteIndexResponse> createDeleteIndexResponse(DeleteIndexResponse value) {
        return new JAXBElement<DeleteIndexResponse>(_DeleteIndexResponse_QNAME, DeleteIndexResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddEntriesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", name = "addEntriesResponse")
    public JAXBElement<AddEntriesResponse> createAddEntriesResponse(AddEntriesResponse value) {
        return new JAXBElement<AddEntriesResponse>(_AddEntriesResponse_QNAME, AddEntriesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddAttributeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "addAttributeResponse")
    public JAXBElement<AddAttributeResponse> createAddAttributeResponse(AddAttributeResponse value) {
        return new JAXBElement<AddAttributeResponse>(_AddAttributeResponse_QNAME, AddAttributeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateEntriesRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", name = "updateEntriesRequest")
    public JAXBElement<UpdateEntriesRequest> createUpdateEntriesRequest(UpdateEntriesRequest value) {
        return new JAXBElement<UpdateEntriesRequest>(_UpdateEntriesRequest_QNAME, UpdateEntriesRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RemoveAttributeFromIndexResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "removeAttributeFromIndexResponse")
    public JAXBElement<RemoveAttributeFromIndexResponse> createRemoveAttributeFromIndexResponse(RemoveAttributeFromIndexResponse value) {
        return new JAXBElement<RemoveAttributeFromIndexResponse>(_RemoveAttributeFromIndexResponse_QNAME, RemoveAttributeFromIndexResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteEntriesRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", name = "deleteEntriesRequest")
    public JAXBElement<DeleteEntriesRequest> createDeleteEntriesRequest(DeleteEntriesRequest value) {
        return new JAXBElement<DeleteEntriesRequest>(_DeleteEntriesRequest_QNAME, DeleteEntriesRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddCatalogRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "addCatalogRequest")
    public JAXBElement<AddCatalogRequest> createAddCatalogRequest(AddCatalogRequest value) {
        return new JAXBElement<AddCatalogRequest>(_AddCatalogRequest_QNAME, AddCatalogRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateEntriesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", name = "updateEntriesResponse")
    public JAXBElement<UpdateEntriesResponse> createUpdateEntriesResponse(UpdateEntriesResponse value) {
        return new JAXBElement<UpdateEntriesResponse>(_UpdateEntriesResponse_QNAME, UpdateEntriesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddCatalogResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "addCatalogResponse")
    public JAXBElement<AddCatalogResponse> createAddCatalogResponse(AddCatalogResponse value) {
        return new JAXBElement<AddCatalogResponse>(_AddCatalogResponse_QNAME, AddCatalogResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateCatalogRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "updateCatalogRequest")
    public JAXBElement<UpdateCatalogRequest> createUpdateCatalogRequest(UpdateCatalogRequest value) {
        return new JAXBElement<UpdateCatalogRequest>(_UpdateCatalogRequest_QNAME, UpdateCatalogRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteEntriesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", name = "deleteEntriesResponse")
    public JAXBElement<DeleteEntriesResponse> createDeleteEntriesResponse(DeleteEntriesResponse value) {
        return new JAXBElement<DeleteEntriesResponse>(_DeleteEntriesResponse_QNAME, DeleteEntriesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReceiveDataChangesRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", name = "receiveDataChangesRequest")
    public JAXBElement<ReceiveDataChangesRequest> createReceiveDataChangesRequest(ReceiveDataChangesRequest value) {
        return new JAXBElement<ReceiveDataChangesRequest>(_ReceiveDataChangesRequest_QNAME, ReceiveDataChangesRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteAttributeRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "deleteAttributeRequest")
    public JAXBElement<DeleteAttributeRequest> createDeleteAttributeRequest(DeleteAttributeRequest value) {
        return new JAXBElement<DeleteAttributeRequest>(_DeleteAttributeRequest_QNAME, DeleteAttributeRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddAttributeToIndexRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "addAttributeToIndexRequest")
    public JAXBElement<AddAttributeToIndexRequest> createAddAttributeToIndexRequest(AddAttributeToIndexRequest value) {
        return new JAXBElement<AddAttributeToIndexRequest>(_AddAttributeToIndexRequest_QNAME, AddAttributeToIndexRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteCatalogResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "deleteCatalogResponse")
    public JAXBElement<DeleteCatalogResponse> createDeleteCatalogResponse(DeleteCatalogResponse value) {
        return new JAXBElement<DeleteCatalogResponse>(_DeleteCatalogResponse_QNAME, DeleteCatalogResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectCatalogsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "selectCatalogsResponse")
    public JAXBElement<SelectCatalogsResponse> createSelectCatalogsResponse(SelectCatalogsResponse value) {
        return new JAXBElement<SelectCatalogsResponse>(_SelectCatalogsResponse_QNAME, SelectCatalogsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectEntriesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", name = "selectEntriesResponse")
    public JAXBElement<SelectEntriesResponse> createSelectEntriesResponse(SelectEntriesResponse value) {
        return new JAXBElement<SelectEntriesResponse>(_SelectEntriesResponse_QNAME, SelectEntriesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCatalogRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "getCatalogRequest")
    public JAXBElement<GetCatalogRequest> createGetCatalogRequest(GetCatalogRequest value) {
        return new JAXBElement<GetCatalogRequest>(_GetCatalogRequest_QNAME, GetCatalogRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteAttributeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "deleteAttributeResponse")
    public JAXBElement<DeleteAttributeResponse> createDeleteAttributeResponse(DeleteAttributeResponse value) {
        return new JAXBElement<DeleteAttributeResponse>(_DeleteAttributeResponse_QNAME, DeleteAttributeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddIndexRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "addIndexRequest")
    public JAXBElement<AddIndexRequest> createAddIndexRequest(AddIndexRequest value) {
        return new JAXBElement<AddIndexRequest>(_AddIndexRequest_QNAME, AddIndexRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddAttributeToIndexResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "addAttributeToIndexResponse")
    public JAXBElement<AddAttributeToIndexResponse> createAddAttributeToIndexResponse(AddAttributeToIndexResponse value) {
        return new JAXBElement<AddAttributeToIndexResponse>(_AddAttributeToIndexResponse_QNAME, AddAttributeToIndexResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteCatalogRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "deleteCatalogRequest")
    public JAXBElement<DeleteCatalogRequest> createDeleteCatalogRequest(DeleteCatalogRequest value) {
        return new JAXBElement<DeleteCatalogRequest>(_DeleteCatalogRequest_QNAME, DeleteCatalogRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReceiveDataChangesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", name = "receiveDataChangesResponse")
    public JAXBElement<ReceiveDataChangesResponse> createReceiveDataChangesResponse(ReceiveDataChangesResponse value) {
        return new JAXBElement<ReceiveDataChangesResponse>(_ReceiveDataChangesResponse_QNAME, ReceiveDataChangesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddIndexResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "addIndexResponse")
    public JAXBElement<AddIndexResponse> createAddIndexResponse(AddIndexResponse value) {
        return new JAXBElement<AddIndexResponse>(_AddIndexResponse_QNAME, AddIndexResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectCatalogsRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "selectCatalogsRequest")
    public JAXBElement<SelectCatalogsRequest> createSelectCatalogsRequest(SelectCatalogsRequest value) {
        return new JAXBElement<SelectCatalogsRequest>(_SelectCatalogsRequest_QNAME, SelectCatalogsRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteIndexRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "deleteIndexRequest")
    public JAXBElement<DeleteIndexRequest> createDeleteIndexRequest(DeleteIndexRequest value) {
        return new JAXBElement<DeleteIndexRequest>(_DeleteIndexRequest_QNAME, DeleteIndexRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddAttributeRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "addAttributeRequest")
    public JAXBElement<AddAttributeRequest> createAddAttributeRequest(AddAttributeRequest value) {
        return new JAXBElement<AddAttributeRequest>(_AddAttributeRequest_QNAME, AddAttributeRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateCatalogResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "updateCatalogResponse")
    public JAXBElement<UpdateCatalogResponse> createUpdateCatalogResponse(UpdateCatalogResponse value) {
        return new JAXBElement<UpdateCatalogResponse>(_UpdateCatalogResponse_QNAME, UpdateCatalogResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RemoveAttributeFromIndexRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "removeAttributeFromIndexRequest")
    public JAXBElement<RemoveAttributeFromIndexRequest> createRemoveAttributeFromIndexRequest(RemoveAttributeFromIndexRequest value) {
        return new JAXBElement<RemoveAttributeFromIndexRequest>(_RemoveAttributeFromIndexRequest_QNAME, RemoveAttributeFromIndexRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Catalog }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", name = "result", scope = GetCatalogResponse.class)
    public JAXBElement<Catalog> createGetCatalogResponseResult(Catalog value) {
        return new JAXBElement<Catalog>(_GetCatalogResponseResult_QNAME, Catalog.class, GetCatalogResponse.class, value);
    }

}
