
package generated.msr.stoplist.schema;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import generated.msr.stoplist.IncompleteTaskException;
import generated.msr.stoplist.RejectTaskException;
import generated.msr.stoplist.TaskNotFoundException;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated.msr.stoplist.schema package.
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

    private final static QName _AppsTaskRequest_QNAME = new QName("http://webservice.msr.com/sl/schema/", "appsTaskRequest");
    private final static QName _AreasRequest_QNAME = new QName("http://webservice.msr.com/sl/schema/", "areasRequest");
    private final static QName _AreasDiffTaskRequest_QNAME = new QName("http://webservice.msr.com/sl/schema/", "areasDiffTaskRequest");
    private final static QName _SubmitAllAppsTaskResponse_QNAME = new QName("http://webservice.msr.com/sl/schema/", "submitAllAppsTaskResponse");
    private final static QName _TaskCountRequest_QNAME = new QName("http://webservice.msr.com/sl/schema/", "taskCountRequest");
    private final static QName _AreasResponse_QNAME = new QName("http://webservice.msr.com/sl/schema/", "areasResponse");
    private final static QName _CardsDiffTaskRequest_QNAME = new QName("http://webservice.msr.com/sl/schema/", "cardsDiffTaskRequest");
    private final static QName _SubmitCardsDiffTaskResponse_QNAME = new QName("http://webservice.msr.com/sl/schema/", "submitCardsDiffTaskResponse");
    private final static QName _SubmitAreasDiffTaskResponse_QNAME = new QName("http://webservice.msr.com/sl/schema/", "submitAreasDiffTaskResponse");
    private final static QName _CardsResponse_QNAME = new QName("http://webservice.msr.com/sl/schema/", "cardsResponse");
    private final static QName _TaskCountResponse_QNAME = new QName("http://webservice.msr.com/sl/schema/", "taskCountResponse");
    private final static QName _AppsDiffTaskRequest_QNAME = new QName("http://webservice.msr.com/sl/schema/", "appsDiffTaskRequest");
    private final static QName _AreasTaskRequest_QNAME = new QName("http://webservice.msr.com/sl/schema/", "areasTaskRequest");
    private final static QName _AppsRequest_QNAME = new QName("http://webservice.msr.com/sl/schema/", "appsRequest");
    private final static QName _TaskNotFoundException_QNAME = new QName("http://webservice.msr.com/sl/schema/", "taskNotFoundException");
    private final static QName _AllCardsTaskRequest_QNAME = new QName("http://webservice.msr.com/sl/schema/", "allCardsTaskRequest");
    private final static QName _SubmitAllAreasTaskResponse_QNAME = new QName("http://webservice.msr.com/sl/schema/", "submitAllAreasTaskResponse");
    private final static QName _TaskStateRequest_QNAME = new QName("http://webservice.msr.com/sl/schema/", "taskStateRequest");
    private final static QName _CardsTaskRequest_QNAME = new QName("http://webservice.msr.com/sl/schema/", "cardsTaskRequest");
    private final static QName _AppsResponse_QNAME = new QName("http://webservice.msr.com/sl/schema/", "appsResponse");
    private final static QName _AllAreasTaskRequest_QNAME = new QName("http://webservice.msr.com/sl/schema/", "allAreasTaskRequest");
    private final static QName _SubmitAppsDiffTaskResponse_QNAME = new QName("http://webservice.msr.com/sl/schema/", "submitAppsDiffTaskResponse");
    private final static QName _SubmitAllCardsTaskResponse_QNAME = new QName("http://webservice.msr.com/sl/schema/", "submitAllCardsTaskResponse");
    private final static QName _RejectTaskException_QNAME = new QName("http://webservice.msr.com/sl/schema/", "rejectTaskException");
    private final static QName _SubmitCardsTaskResponse_QNAME = new QName("http://webservice.msr.com/sl/schema/", "submitCardsTaskResponse");
    private final static QName _IncompleteTaskException_QNAME = new QName("http://webservice.msr.com/sl/schema/", "incompleteTaskException");
    private final static QName _AllAppsTaskRequest_QNAME = new QName("http://webservice.msr.com/sl/schema/", "allAppsTaskRequest");
    private final static QName _SubmitAreasTaskResponse_QNAME = new QName("http://webservice.msr.com/sl/schema/", "submitAreasTaskResponse");
    private final static QName _SubmitAppsTaskResponse_QNAME = new QName("http://webservice.msr.com/sl/schema/", "submitAppsTaskResponse");
    private final static QName _CardsRequest_QNAME = new QName("http://webservice.msr.com/sl/schema/", "cardsRequest");
    private final static QName _TaskStateResponse_QNAME = new QName("http://webservice.msr.com/sl/schema/", "taskStateResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated.msr.stoplist.schema
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AreasDiffTaskRequest }
     * 
     */
    public AreasDiffTaskRequest createAreasDiffTaskRequest() {
        return new AreasDiffTaskRequest();
    }

    /**
     * Create an instance of {@link SubmitAppsTaskResponse }
     * 
     */
    public SubmitAppsTaskResponse createSubmitAppsTaskResponse() {
        return new SubmitAppsTaskResponse();
    }

    /**
     * Create an instance of {@link Range }
     * 
     */
    public Range createRange() {
        return new Range();
    }

    /**
     * Create an instance of {@link SubmitAllAreasTaskResponse }
     * 
     */
    public SubmitAllAreasTaskResponse createSubmitAllAreasTaskResponse() {
        return new SubmitAllAreasTaskResponse();
    }

    /**
     * Create an instance of {@link AreasTaskRequest }
     * 
     */
    public AreasTaskRequest createAreasTaskRequest() {
        return new AreasTaskRequest();
    }

    /**
     * Create an instance of {@link AppsRequest }
     * 
     */
    public AppsRequest createAppsRequest() {
        return new AppsRequest();
    }

    /**
     * Create an instance of {@link StopListAreaReply }
     * 
     */
    public StopListAreaReply createStopListAreaReply() {
        return new StopListAreaReply();
    }

    /**
     * Create an instance of {@link SubmitAllCardsTaskResponse }
     * 
     */
    public SubmitAllCardsTaskResponse createSubmitAllCardsTaskResponse() {
        return new SubmitAllCardsTaskResponse();
    }

    /**
     * Create an instance of {@link AppsTaskRequest }
     * 
     */
    public AppsTaskRequest createAppsTaskRequest() {
        return new AppsTaskRequest();
    }

    /**
     * Create an instance of {@link SubmitCardsTaskResponse }
     * 
     */
    public SubmitCardsTaskResponse createSubmitCardsTaskResponse() {
        return new SubmitCardsTaskResponse();
    }

    /**
     * Create an instance of {@link AllCardsTaskRequest }
     * 
     */
    public AllCardsTaskRequest createAllCardsTaskRequest() {
        return new AllCardsTaskRequest();
    }

    /**
     * Create an instance of {@link AreasResponse }
     * 
     */
    public AreasResponse createAreasResponse() {
        return new AreasResponse();
    }

    /**
     * Create an instance of {@link SubmitAreasDiffTaskResponse }
     * 
     */
    public SubmitAreasDiffTaskResponse createSubmitAreasDiffTaskResponse() {
        return new SubmitAreasDiffTaskResponse();
    }

    /**
     * Create an instance of {@link CardsResponse }
     * 
     */
    public CardsResponse createCardsResponse() {
        return new CardsResponse();
    }

    /**
     * Create an instance of {@link AppsResponse }
     * 
     */
    public AppsResponse createAppsResponse() {
        return new AppsResponse();
    }

    /**
     * Create an instance of {@link SubmitCardsDiffTaskResponse }
     * 
     */
    public SubmitCardsDiffTaskResponse createSubmitCardsDiffTaskResponse() {
        return new SubmitCardsDiffTaskResponse();
    }

    /**
     * Create an instance of {@link TaskCountRequest }
     * 
     */
    public TaskCountRequest createTaskCountRequest() {
        return new TaskCountRequest();
    }

    /**
     * Create an instance of {@link AreasRequest }
     * 
     */
    public AreasRequest createAreasRequest() {
        return new AreasRequest();
    }

    /**
     * Create an instance of {@link TaskStateResponse }
     * 
     */
    public TaskStateResponse createTaskStateResponse() {
        return new TaskStateResponse();
    }

    /**
     * Create an instance of {@link TaskCountResponse }
     * 
     */
    public TaskCountResponse createTaskCountResponse() {
        return new TaskCountResponse();
    }

    /**
     * Create an instance of {@link SubmitAreasTaskResponse }
     * 
     */
    public SubmitAreasTaskResponse createSubmitAreasTaskResponse() {
        return new SubmitAreasTaskResponse();
    }

    /**
     * Create an instance of {@link TaskStateRequest }
     * 
     */
    public TaskStateRequest createTaskStateRequest() {
        return new TaskStateRequest();
    }

    /**
     * Create an instance of {@link AppsDiffTaskRequest }
     * 
     */
    public AppsDiffTaskRequest createAppsDiffTaskRequest() {
        return new AppsDiffTaskRequest();
    }

    /**
     * Create an instance of {@link SubmitAppsDiffTaskResponse }
     * 
     */
    public SubmitAppsDiffTaskResponse createSubmitAppsDiffTaskResponse() {
        return new SubmitAppsDiffTaskResponse();
    }

    /**
     * Create an instance of {@link StopListCardReply }
     * 
     */
    public StopListCardReply createStopListCardReply() {
        return new StopListCardReply();
    }

    /**
     * Create an instance of {@link CardsTaskRequest }
     * 
     */
    public CardsTaskRequest createCardsTaskRequest() {
        return new CardsTaskRequest();
    }

    /**
     * Create an instance of {@link SubmitAllAppsTaskResponse }
     * 
     */
    public SubmitAllAppsTaskResponse createSubmitAllAppsTaskResponse() {
        return new SubmitAllAppsTaskResponse();
    }

    /**
     * Create an instance of {@link AllAppsTaskRequest }
     * 
     */
    public AllAppsTaskRequest createAllAppsTaskRequest() {
        return new AllAppsTaskRequest();
    }

    /**
     * Create an instance of {@link AllAreasTaskRequest }
     * 
     */
    public AllAreasTaskRequest createAllAreasTaskRequest() {
        return new AllAreasTaskRequest();
    }

    /**
     * Create an instance of {@link StopListAppReply }
     * 
     */
    public StopListAppReply createStopListAppReply() {
        return new StopListAppReply();
    }

    /**
     * Create an instance of {@link CardsRequest }
     * 
     */
    public CardsRequest createCardsRequest() {
        return new CardsRequest();
    }

    /**
     * Create an instance of {@link CardsDiffTaskRequest }
     * 
     */
    public CardsDiffTaskRequest createCardsDiffTaskRequest() {
        return new CardsDiffTaskRequest();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AppsTaskRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "appsTaskRequest")
    public JAXBElement<AppsTaskRequest> createAppsTaskRequest(AppsTaskRequest value) {
        return new JAXBElement<AppsTaskRequest>(_AppsTaskRequest_QNAME, AppsTaskRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AreasRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "areasRequest")
    public JAXBElement<AreasRequest> createAreasRequest(AreasRequest value) {
        return new JAXBElement<AreasRequest>(_AreasRequest_QNAME, AreasRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AreasDiffTaskRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "areasDiffTaskRequest")
    public JAXBElement<AreasDiffTaskRequest> createAreasDiffTaskRequest(AreasDiffTaskRequest value) {
        return new JAXBElement<AreasDiffTaskRequest>(_AreasDiffTaskRequest_QNAME, AreasDiffTaskRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubmitAllAppsTaskResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "submitAllAppsTaskResponse")
    public JAXBElement<SubmitAllAppsTaskResponse> createSubmitAllAppsTaskResponse(SubmitAllAppsTaskResponse value) {
        return new JAXBElement<SubmitAllAppsTaskResponse>(_SubmitAllAppsTaskResponse_QNAME, SubmitAllAppsTaskResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TaskCountRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "taskCountRequest")
    public JAXBElement<TaskCountRequest> createTaskCountRequest(TaskCountRequest value) {
        return new JAXBElement<TaskCountRequest>(_TaskCountRequest_QNAME, TaskCountRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AreasResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "areasResponse")
    public JAXBElement<AreasResponse> createAreasResponse(AreasResponse value) {
        return new JAXBElement<AreasResponse>(_AreasResponse_QNAME, AreasResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CardsDiffTaskRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "cardsDiffTaskRequest")
    public JAXBElement<CardsDiffTaskRequest> createCardsDiffTaskRequest(CardsDiffTaskRequest value) {
        return new JAXBElement<CardsDiffTaskRequest>(_CardsDiffTaskRequest_QNAME, CardsDiffTaskRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubmitCardsDiffTaskResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "submitCardsDiffTaskResponse")
    public JAXBElement<SubmitCardsDiffTaskResponse> createSubmitCardsDiffTaskResponse(SubmitCardsDiffTaskResponse value) {
        return new JAXBElement<SubmitCardsDiffTaskResponse>(_SubmitCardsDiffTaskResponse_QNAME, SubmitCardsDiffTaskResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubmitAreasDiffTaskResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "submitAreasDiffTaskResponse")
    public JAXBElement<SubmitAreasDiffTaskResponse> createSubmitAreasDiffTaskResponse(SubmitAreasDiffTaskResponse value) {
        return new JAXBElement<SubmitAreasDiffTaskResponse>(_SubmitAreasDiffTaskResponse_QNAME, SubmitAreasDiffTaskResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CardsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "cardsResponse")
    public JAXBElement<CardsResponse> createCardsResponse(CardsResponse value) {
        return new JAXBElement<CardsResponse>(_CardsResponse_QNAME, CardsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TaskCountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "taskCountResponse")
    public JAXBElement<TaskCountResponse> createTaskCountResponse(TaskCountResponse value) {
        return new JAXBElement<TaskCountResponse>(_TaskCountResponse_QNAME, TaskCountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AppsDiffTaskRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "appsDiffTaskRequest")
    public JAXBElement<AppsDiffTaskRequest> createAppsDiffTaskRequest(AppsDiffTaskRequest value) {
        return new JAXBElement<AppsDiffTaskRequest>(_AppsDiffTaskRequest_QNAME, AppsDiffTaskRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AreasTaskRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "areasTaskRequest")
    public JAXBElement<AreasTaskRequest> createAreasTaskRequest(AreasTaskRequest value) {
        return new JAXBElement<AreasTaskRequest>(_AreasTaskRequest_QNAME, AreasTaskRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AppsRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "appsRequest")
    public JAXBElement<AppsRequest> createAppsRequest(AppsRequest value) {
        return new JAXBElement<AppsRequest>(_AppsRequest_QNAME, AppsRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TaskNotFoundException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "taskNotFoundException")
    public JAXBElement<TaskNotFoundException> createTaskNotFoundException(TaskNotFoundException value) {
        return new JAXBElement<TaskNotFoundException>(_TaskNotFoundException_QNAME, TaskNotFoundException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AllCardsTaskRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "allCardsTaskRequest")
    public JAXBElement<AllCardsTaskRequest> createAllCardsTaskRequest(AllCardsTaskRequest value) {
        return new JAXBElement<AllCardsTaskRequest>(_AllCardsTaskRequest_QNAME, AllCardsTaskRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubmitAllAreasTaskResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "submitAllAreasTaskResponse")
    public JAXBElement<SubmitAllAreasTaskResponse> createSubmitAllAreasTaskResponse(SubmitAllAreasTaskResponse value) {
        return new JAXBElement<SubmitAllAreasTaskResponse>(_SubmitAllAreasTaskResponse_QNAME, SubmitAllAreasTaskResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TaskStateRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "taskStateRequest")
    public JAXBElement<TaskStateRequest> createTaskStateRequest(TaskStateRequest value) {
        return new JAXBElement<TaskStateRequest>(_TaskStateRequest_QNAME, TaskStateRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CardsTaskRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "cardsTaskRequest")
    public JAXBElement<CardsTaskRequest> createCardsTaskRequest(CardsTaskRequest value) {
        return new JAXBElement<CardsTaskRequest>(_CardsTaskRequest_QNAME, CardsTaskRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AppsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "appsResponse")
    public JAXBElement<AppsResponse> createAppsResponse(AppsResponse value) {
        return new JAXBElement<AppsResponse>(_AppsResponse_QNAME, AppsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AllAreasTaskRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "allAreasTaskRequest")
    public JAXBElement<AllAreasTaskRequest> createAllAreasTaskRequest(AllAreasTaskRequest value) {
        return new JAXBElement<AllAreasTaskRequest>(_AllAreasTaskRequest_QNAME, AllAreasTaskRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubmitAppsDiffTaskResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "submitAppsDiffTaskResponse")
    public JAXBElement<SubmitAppsDiffTaskResponse> createSubmitAppsDiffTaskResponse(SubmitAppsDiffTaskResponse value) {
        return new JAXBElement<SubmitAppsDiffTaskResponse>(_SubmitAppsDiffTaskResponse_QNAME, SubmitAppsDiffTaskResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubmitAllCardsTaskResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "submitAllCardsTaskResponse")
    public JAXBElement<SubmitAllCardsTaskResponse> createSubmitAllCardsTaskResponse(SubmitAllCardsTaskResponse value) {
        return new JAXBElement<SubmitAllCardsTaskResponse>(_SubmitAllCardsTaskResponse_QNAME, SubmitAllCardsTaskResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RejectTaskException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "rejectTaskException")
    public JAXBElement<RejectTaskException> createRejectTaskException(RejectTaskException value) {
        return new JAXBElement<RejectTaskException>(_RejectTaskException_QNAME, RejectTaskException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubmitCardsTaskResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "submitCardsTaskResponse")
    public JAXBElement<SubmitCardsTaskResponse> createSubmitCardsTaskResponse(SubmitCardsTaskResponse value) {
        return new JAXBElement<SubmitCardsTaskResponse>(_SubmitCardsTaskResponse_QNAME, SubmitCardsTaskResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IncompleteTaskException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "incompleteTaskException")
    public JAXBElement<IncompleteTaskException> createIncompleteTaskException(IncompleteTaskException value) {
        return new JAXBElement<IncompleteTaskException>(_IncompleteTaskException_QNAME, IncompleteTaskException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AllAppsTaskRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "allAppsTaskRequest")
    public JAXBElement<AllAppsTaskRequest> createAllAppsTaskRequest(AllAppsTaskRequest value) {
        return new JAXBElement<AllAppsTaskRequest>(_AllAppsTaskRequest_QNAME, AllAppsTaskRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubmitAreasTaskResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "submitAreasTaskResponse")
    public JAXBElement<SubmitAreasTaskResponse> createSubmitAreasTaskResponse(SubmitAreasTaskResponse value) {
        return new JAXBElement<SubmitAreasTaskResponse>(_SubmitAreasTaskResponse_QNAME, SubmitAreasTaskResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubmitAppsTaskResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "submitAppsTaskResponse")
    public JAXBElement<SubmitAppsTaskResponse> createSubmitAppsTaskResponse(SubmitAppsTaskResponse value) {
        return new JAXBElement<SubmitAppsTaskResponse>(_SubmitAppsTaskResponse_QNAME, SubmitAppsTaskResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CardsRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "cardsRequest")
    public JAXBElement<CardsRequest> createCardsRequest(CardsRequest value) {
        return new JAXBElement<CardsRequest>(_CardsRequest_QNAME, CardsRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TaskStateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.msr.com/sl/schema/", name = "taskStateResponse")
    public JAXBElement<TaskStateResponse> createTaskStateResponse(TaskStateResponse value) {
        return new JAXBElement<TaskStateResponse>(_TaskStateResponse_QNAME, TaskStateResponse.class, null, value);
    }

}
