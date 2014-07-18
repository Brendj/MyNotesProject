
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

    private final static QName _UpdateEntriesResponse_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/entry.xsd", "updateEntriesResponse");
    private final static QName _DeleteEntriesResponse_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/entry.xsd", "deleteEntriesResponse");
    private final static QName _SelectEntriesRequest_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/entry.xsd", "selectEntriesRequest");
    private final static QName _ReceiveDataChangesRequest_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/entry.xsd", "receiveDataChangesRequest");
    private final static QName _AddEntriesRequest_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/entry.xsd", "addEntriesRequest");
    private final static QName _AddEntriesResponse_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/entry.xsd", "addEntriesResponse");
    private final static QName _UpdateEntriesRequest_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/entry.xsd", "updateEntriesRequest");
    private final static QName _DeleteEntriesRequest_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/entry.xsd", "deleteEntriesRequest");
    private final static QName _ReceiveDataChangesResponse_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/entry.xsd", "receiveDataChangesResponse");
    private final static QName _SelectEntriesResponse_QNAME = new QName("http://emp.mos.ru/schemas/storage/request/entry.xsd", "selectEntriesResponse");

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
     * Create an instance of {@link DeleteEntriesResponse }
     * 
     */
    public DeleteEntriesResponse createDeleteEntriesResponse() {
        return new DeleteEntriesResponse();
    }

    /**
     * Create an instance of {@link UpdateEntriesResponse.Result }
     * 
     */
    public UpdateEntriesResponse.Result createUpdateEntriesResponseResult() {
        return new UpdateEntriesResponse.Result();
    }

    /**
     * Create an instance of {@link BaseRequest }
     * 
     */
    public BaseRequest createBaseRequest() {
        return new BaseRequest();
    }

    /**
     * Create an instance of {@link EntryAttribute }
     * 
     */
    public EntryAttribute createEntryAttribute() {
        return new EntryAttribute();
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
     * Create an instance of {@link SelectEntriesResponse.Result }
     * 
     */
    public SelectEntriesResponse.Result createSelectEntriesResponseResult() {
        return new SelectEntriesResponse.Result();
    }

    /**
     * Create an instance of {@link EntryList }
     * 
     */
    public EntryList createEntryList() {
        return new EntryList();
    }

    /**
     * Create an instance of {@link ReceiveDataChangesResponse }
     * 
     */
    public ReceiveDataChangesResponse createReceiveDataChangesResponse() {
        return new ReceiveDataChangesResponse();
    }

    /**
     * Create an instance of {@link UpdateEntriesRequest }
     * 
     */
    public UpdateEntriesRequest createUpdateEntriesRequest() {
        return new UpdateEntriesRequest();
    }

    /**
     * Create an instance of {@link ReceiveDataChangesRequest }
     * 
     */
    public ReceiveDataChangesRequest createReceiveDataChangesRequest() {
        return new ReceiveDataChangesRequest();
    }

    /**
     * Create an instance of {@link SelectEntriesRequest }
     * 
     */
    public SelectEntriesRequest createSelectEntriesRequest() {
        return new SelectEntriesRequest();
    }

    /**
     * Create an instance of {@link DeleteEntriesResponse.Result }
     * 
     */
    public DeleteEntriesResponse.Result createDeleteEntriesResponseResult() {
        return new DeleteEntriesResponse.Result();
    }

    /**
     * Create an instance of {@link AddEntriesResponse }
     * 
     */
    public AddEntriesResponse createAddEntriesResponse() {
        return new AddEntriesResponse();
    }

    /**
     * Create an instance of {@link BaseResponse }
     * 
     */
    public BaseResponse createBaseResponse() {
        return new BaseResponse();
    }

    /**
     * Create an instance of {@link DeleteEntriesRequest }
     * 
     */
    public DeleteEntriesRequest createDeleteEntriesRequest() {
        return new DeleteEntriesRequest();
    }

    /**
     * Create an instance of {@link ReceiveDataChangesResponse.Result }
     * 
     */
    public ReceiveDataChangesResponse.Result createReceiveDataChangesResponseResult() {
        return new ReceiveDataChangesResponse.Result();
    }

    /**
     * Create an instance of {@link SelectEntriesResponse }
     * 
     */
    public SelectEntriesResponse createSelectEntriesResponse() {
        return new SelectEntriesResponse();
    }

    /**
     * Create an instance of {@link AddEntriesRequest }
     * 
     */
    public AddEntriesRequest createAddEntriesRequest() {
        return new AddEntriesRequest();
    }

    /**
     * Create an instance of {@link ReceiveDataChangesResponse.Result.Entry }
     * 
     */
    public ReceiveDataChangesResponse.Result.Entry createReceiveDataChangesResponseResultEntry() {
        return new ReceiveDataChangesResponse.Result.Entry();
    }

    /**
     * Create an instance of {@link Paging }
     * 
     */
    public Paging createPaging() {
        return new Paging();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteEntriesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", name = "deleteEntriesResponse")
    public JAXBElement<DeleteEntriesResponse> createDeleteEntriesResponse(DeleteEntriesResponse value) {
        return new JAXBElement<DeleteEntriesResponse>(_DeleteEntriesResponse_QNAME, DeleteEntriesResponse.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link ReceiveDataChangesRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", name = "receiveDataChangesRequest")
    public JAXBElement<ReceiveDataChangesRequest> createReceiveDataChangesRequest(ReceiveDataChangesRequest value) {
        return new JAXBElement<ReceiveDataChangesRequest>(_ReceiveDataChangesRequest_QNAME, ReceiveDataChangesRequest.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link AddEntriesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", name = "addEntriesResponse")
    public JAXBElement<AddEntriesResponse> createAddEntriesResponse(AddEntriesResponse value) {
        return new JAXBElement<AddEntriesResponse>(_AddEntriesResponse_QNAME, AddEntriesResponse.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteEntriesRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", name = "deleteEntriesRequest")
    public JAXBElement<DeleteEntriesRequest> createDeleteEntriesRequest(DeleteEntriesRequest value) {
        return new JAXBElement<DeleteEntriesRequest>(_DeleteEntriesRequest_QNAME, DeleteEntriesRequest.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectEntriesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", name = "selectEntriesResponse")
    public JAXBElement<SelectEntriesResponse> createSelectEntriesResponse(SelectEntriesResponse value) {
        return new JAXBElement<SelectEntriesResponse>(_SelectEntriesResponse_QNAME, SelectEntriesResponse.class, null, value);
    }

}
