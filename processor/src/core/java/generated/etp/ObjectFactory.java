
package generated.etp;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.math.BigDecimal;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated.etp package.
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

    private final static QName _RequestStatusValidityPeriod_QNAME = new QName("http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", "ValidityPeriod");
    private final static QName _GetRequestDocumentsMessage_QNAME = new QName("http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", "GetRequestDocumentsMessage");
    private final static QName _ServiceHeader_QNAME = new QName("http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", "ServiceHeader");
    private final static QName _DepartmentSystemCode_QNAME = new QName("http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", "SystemCode");
    private final static QName _RequestDocumentsDataResponseDate_QNAME = new QName("http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", "ResponseDate");
    private final static QName _RequestDocumentsDataPlanDate_QNAME = new QName("http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", "PlanDate");
    private final static QName _RequestAccountOgrnDate_QNAME = new QName("http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", "OgrnDate");
    private final static QName _RequestAccountInnDate_QNAME = new QName("http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", "InnDate");
    private final static QName _RequestContactOMSDate_QNAME = new QName("http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", "OMSDate");
    private final static QName _RequestContactRegAddressType_QNAME = new QName("http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", "RegAddressType");
    private final static QName _RequestContactOMSValidityPeriod_QNAME = new QName("http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", "OMSValidityPeriod");
    private final static QName _RequestServiceForSignCopies_QNAME = new QName("http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", "Copies");
    private final static QName _RequestServiceOutputTargetDate_QNAME = new QName("http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", "OutputTargetDate");
    private final static QName _RequestServicePrepareTargetDate_QNAME = new QName("http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", "PrepareTargetDate");
    private final static QName _RequestServiceServicePrice_QNAME = new QName("http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", "ServicePrice");
    private final static QName _RequestServiceOutputFactDate_QNAME = new QName("http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", "OutputFactDate");
    private final static QName _RequestServicePrepareFactDate_QNAME = new QName("http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", "PrepareFactDate");
    private final static QName _CoordinateFileReferenceFileHash_QNAME = new QName("http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", "FileHash");
    private final static QName _ServiceDocumentDocDate_QNAME = new QName("http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", "DocDate");
    private final static QName _ServiceDocumentCopyCount_QNAME = new QName("http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", "CopyCount");
    private final static QName _ServiceDocumentListCount_QNAME = new QName("http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", "ListCount");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated.etp
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ServiceDocument }
     * 
     */
    public ServiceDocument createServiceDocument() {
        return new ServiceDocument();
    }

    /**
     * Create an instance of {@link TaskDataType }
     * 
     */
    public TaskDataType createTaskDataType() {
        return new TaskDataType();
    }

    /**
     * Create an instance of {@link TaskResult }
     * 
     */
    public TaskResult createTaskResult() {
        return new TaskResult();
    }

    /**
     * Create an instance of {@link RequestServiceForSign }
     * 
     */
    public RequestServiceForSign createRequestServiceForSign() {
        return new RequestServiceForSign();
    }

    /**
     * Create an instance of {@link SendTasksMessage }
     * 
     */
    public SendTasksMessage createSendTasksMessage() {
        return new SendTasksMessage();
    }

    /**
     * Create an instance of {@link ArrayOfCoordinateTaskData }
     * 
     */
    public ArrayOfCoordinateTaskData createArrayOfCoordinateTaskData() {
        return new ArrayOfCoordinateTaskData();
    }

    /**
     * Create an instance of {@link ArrayOfCoordinateFile }
     * 
     */
    public ArrayOfCoordinateFile createArrayOfCoordinateFile() {
        return new ArrayOfCoordinateFile();
    }

    /**
     * Create an instance of {@link Headers }
     * 
     */
    public Headers createHeaders() {
        return new Headers();
    }

    /**
     * Create an instance of {@link CoordinateSendTaskStatusesMessage }
     * 
     */
    public CoordinateSendTaskStatusesMessage createCoordinateSendTaskStatusesMessage() {
        return new CoordinateSendTaskStatusesMessage();
    }

    /**
     * Create an instance of {@link CoordinateTaskStatusData }
     * 
     */
    public CoordinateTaskStatusData createCoordinateTaskStatusData() {
        return new CoordinateTaskStatusData();
    }

    /**
     * Create an instance of {@link SetFilesAndStatusesMessage }
     * 
     */
    public SetFilesAndStatusesMessage createSetFilesAndStatusesMessage() {
        return new SetFilesAndStatusesMessage();
    }

    /**
     * Create an instance of {@link ArrayOfSetFilesAndStatusesData }
     * 
     */
    public ArrayOfSetFilesAndStatusesData createArrayOfSetFilesAndStatusesData() {
        return new ArrayOfSetFilesAndStatusesData();
    }

    /**
     * Create an instance of {@link CoordinateTaskMessage }
     * 
     */
    public CoordinateTaskMessage createCoordinateTaskMessage() {
        return new CoordinateTaskMessage();
    }

    /**
     * Create an instance of {@link CoordinateTaskData }
     * 
     */
    public CoordinateTaskData createCoordinateTaskData() {
        return new CoordinateTaskData();
    }

    /**
     * Create an instance of {@link CoordinateStatusMessage }
     * 
     */
    public CoordinateStatusMessage createCoordinateStatusMessage() {
        return new CoordinateStatusMessage();
    }

    /**
     * Create an instance of {@link CoordinateStatusData }
     * 
     */
    public CoordinateStatusData createCoordinateStatusData() {
        return new CoordinateStatusData();
    }

    /**
     * Create an instance of {@link SendTaskStatusesMessages }
     * 
     */
    public SendTaskStatusesMessages createSendTaskStatusesMessages() {
        return new SendTaskStatusesMessages();
    }

    /**
     * Create an instance of {@link ArrayOfCoordinateTaskStatusData }
     * 
     */
    public ArrayOfCoordinateTaskStatusData createArrayOfCoordinateTaskStatusData() {
        return new ArrayOfCoordinateTaskStatusData();
    }

    /**
     * Create an instance of {@link ErrorMessage }
     * 
     */
    public ErrorMessage createErrorMessage() {
        return new ErrorMessage();
    }

    /**
     * Create an instance of {@link ErrorMessageData }
     * 
     */
    public ErrorMessageData createErrorMessageData() {
        return new ErrorMessageData();
    }

    /**
     * Create an instance of {@link SendRequestsMessage }
     * 
     */
    public SendRequestsMessage createSendRequestsMessage() {
        return new SendRequestsMessage();
    }

    /**
     * Create an instance of {@link ArrayOfCoordinateData }
     * 
     */
    public ArrayOfCoordinateData createArrayOfCoordinateData() {
        return new ArrayOfCoordinateData();
    }

    /**
     * Create an instance of {@link RequestDocumentsData }
     * 
     */
    public RequestDocumentsData createRequestDocumentsData() {
        return new RequestDocumentsData();
    }

    /**
     * Create an instance of {@link CoordinateMessage }
     * 
     */
    public CoordinateMessage createCoordinateMessage() {
        return new CoordinateMessage();
    }

    /**
     * Create an instance of {@link CoordinateData }
     * 
     */
    public CoordinateData createCoordinateData() {
        return new CoordinateData();
    }

    /**
     * Create an instance of {@link StatusType }
     * 
     */
    public StatusType createStatusType() {
        return new StatusType();
    }

    /**
     * Create an instance of {@link RequestResult }
     * 
     */
    public RequestResult createRequestResult() {
        return new RequestResult();
    }

    /**
     * Create an instance of {@link DigestType }
     * 
     */
    public DigestType createDigestType() {
        return new DigestType();
    }

    /**
     * Create an instance of {@link ArrayOfRequestStatus }
     * 
     */
    public ArrayOfRequestStatus createArrayOfRequestStatus() {
        return new ArrayOfRequestStatus();
    }

    /**
     * Create an instance of {@link SetFilesAndStatusesData }
     * 
     */
    public SetFilesAndStatusesData createSetFilesAndStatusesData() {
        return new SetFilesAndStatusesData();
    }

    /**
     * Create an instance of {@link CoordinateFileReference }
     * 
     */
    public CoordinateFileReference createCoordinateFileReference() {
        return new CoordinateFileReference();
    }

    /**
     * Create an instance of {@link DictionaryItem }
     * 
     */
    public DictionaryItem createDictionaryItem() {
        return new DictionaryItem();
    }

    /**
     * Create an instance of {@link RequestService }
     * 
     */
    public RequestService createRequestService() {
        return new RequestService();
    }

    /**
     * Create an instance of {@link TaskType }
     * 
     */
    public TaskType createTaskType() {
        return new TaskType();
    }

    /**
     * Create an instance of {@link RequestDocument }
     * 
     */
    public RequestDocument createRequestDocument() {
        return new RequestDocument();
    }

    /**
     * Create an instance of {@link RequestStatus }
     * 
     */
    public RequestStatus createRequestStatus() {
        return new RequestStatus();
    }

    /**
     * Create an instance of {@link RequestContact }
     * 
     */
    public RequestContact createRequestContact() {
        return new RequestContact();
    }

    /**
     * Create an instance of {@link ArrayOfBaseDeclarant }
     * 
     */
    public ArrayOfBaseDeclarant createArrayOfBaseDeclarant() {
        return new ArrayOfBaseDeclarant();
    }

    /**
     * Create an instance of {@link CoordinateFile }
     * 
     */
    public CoordinateFile createCoordinateFile() {
        return new CoordinateFile();
    }

    /**
     * Create an instance of {@link Department }
     * 
     */
    public Department createDepartment() {
        return new Department();
    }

    /**
     * Create an instance of {@link ArrayOfDictionaryItem }
     * 
     */
    public ArrayOfDictionaryItem createArrayOfDictionaryItem() {
        return new ArrayOfDictionaryItem();
    }

    /**
     * Create an instance of {@link ArrayOfNote }
     * 
     */
    public ArrayOfNote createArrayOfNote() {
        return new ArrayOfNote();
    }

    /**
     * Create an instance of {@link Note }
     * 
     */
    public Note createNote() {
        return new Note();
    }

    /**
     * Create an instance of {@link RequestAccount }
     * 
     */
    public RequestAccount createRequestAccount() {
        return new RequestAccount();
    }

    /**
     * Create an instance of {@link ArrayOfCoordinateFileReference }
     * 
     */
    public ArrayOfCoordinateFileReference createArrayOfCoordinateFileReference() {
        return new ArrayOfCoordinateFileReference();
    }

    /**
     * Create an instance of {@link Address }
     * 
     */
    public Address createAddress() {
        return new Address();
    }

    /**
     * Create an instance of {@link ArrayOfServiceDocument }
     * 
     */
    public ArrayOfServiceDocument createArrayOfServiceDocument() {
        return new ArrayOfServiceDocument();
    }

    /**
     * Create an instance of {@link Person }
     * 
     */
    public Person createPerson() {
        return new Person();
    }

    /**
     * Create an instance of {@link ServiceDocument.CustomAttributes }
     * 
     */
    public ServiceDocument.CustomAttributes createServiceDocumentCustomAttributes() {
        return new ServiceDocument.CustomAttributes();
    }

    /**
     * Create an instance of {@link TaskDataType.Parameter }
     * 
     */
    public TaskDataType.Parameter createTaskDataTypeParameter() {
        return new TaskDataType.Parameter();
    }

    /**
     * Create an instance of {@link TaskResult.XmlView }
     * 
     */
    public TaskResult.XmlView createTaskResultXmlView() {
        return new TaskResult.XmlView();
    }

    /**
     * Create an instance of {@link RequestServiceForSign.CustomAttributes }
     * 
     */
    public RequestServiceForSign.CustomAttributes createRequestServiceForSignCustomAttributes() {
        return new RequestServiceForSign.CustomAttributes();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "ValidityPeriod", scope = RequestStatus.class)
    public JAXBElement<XMLGregorianCalendar> createRequestStatusValidityPeriod(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RequestStatusValidityPeriod_QNAME, XMLGregorianCalendar.class, RequestStatus.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestDocumentsData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "GetRequestDocumentsMessage")
    public JAXBElement<RequestDocumentsData> createGetRequestDocumentsMessage(RequestDocumentsData value) {
        return new JAXBElement<RequestDocumentsData>(_GetRequestDocumentsMessage_QNAME, RequestDocumentsData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Headers }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "ServiceHeader")
    public JAXBElement<Headers> createServiceHeader(Headers value) {
        return new JAXBElement<Headers>(_ServiceHeader_QNAME, Headers.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "SystemCode", scope = Department.class)
    public JAXBElement<String> createDepartmentSystemCode(String value) {
        return new JAXBElement<String>(_DepartmentSystemCode_QNAME, String.class, Department.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "ResponseDate", scope = RequestDocumentsData.class)
    public JAXBElement<XMLGregorianCalendar> createRequestDocumentsDataResponseDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RequestDocumentsDataResponseDate_QNAME, XMLGregorianCalendar.class, RequestDocumentsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "PlanDate", scope = RequestDocumentsData.class)
    public JAXBElement<XMLGregorianCalendar> createRequestDocumentsDataPlanDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RequestDocumentsDataPlanDate_QNAME, XMLGregorianCalendar.class, RequestDocumentsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "OgrnDate", scope = RequestAccount.class)
    public JAXBElement<XMLGregorianCalendar> createRequestAccountOgrnDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RequestAccountOgrnDate_QNAME, XMLGregorianCalendar.class, RequestAccount.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "InnDate", scope = RequestAccount.class)
    public JAXBElement<XMLGregorianCalendar> createRequestAccountInnDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RequestAccountInnDate_QNAME, XMLGregorianCalendar.class, RequestAccount.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "OMSDate", scope = RequestContact.class)
    public JAXBElement<XMLGregorianCalendar> createRequestContactOMSDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RequestContactOMSDate_QNAME, XMLGregorianCalendar.class, RequestContact.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "RegAddressType", scope = RequestContact.class)
    public JAXBElement<RegType> createRequestContactRegAddressType(RegType value) {
        return new JAXBElement<RegType>(_RequestContactRegAddressType_QNAME, RegType.class, RequestContact.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "OMSValidityPeriod", scope = RequestContact.class)
    public JAXBElement<XMLGregorianCalendar> createRequestContactOMSValidityPeriod(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RequestContactOMSValidityPeriod_QNAME, XMLGregorianCalendar.class, RequestContact.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "Copies", scope = RequestServiceForSign.class)
    public JAXBElement<Integer> createRequestServiceForSignCopies(Integer value) {
        return new JAXBElement<Integer>(_RequestServiceForSignCopies_QNAME, Integer.class, RequestServiceForSign.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "OutputTargetDate", scope = RequestService.class)
    public JAXBElement<XMLGregorianCalendar> createRequestServiceOutputTargetDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RequestServiceOutputTargetDate_QNAME, XMLGregorianCalendar.class, RequestService.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "PrepareTargetDate", scope = RequestService.class)
    public JAXBElement<XMLGregorianCalendar> createRequestServicePrepareTargetDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RequestServicePrepareTargetDate_QNAME, XMLGregorianCalendar.class, RequestService.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "ServicePrice", scope = RequestService.class)
    public JAXBElement<BigDecimal> createRequestServiceServicePrice(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_RequestServiceServicePrice_QNAME, BigDecimal.class, RequestService.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "OutputFactDate", scope = RequestService.class)
    public JAXBElement<XMLGregorianCalendar> createRequestServiceOutputFactDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RequestServiceOutputFactDate_QNAME, XMLGregorianCalendar.class, RequestService.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "PrepareFactDate", scope = RequestService.class)
    public JAXBElement<XMLGregorianCalendar> createRequestServicePrepareFactDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RequestServicePrepareFactDate_QNAME, XMLGregorianCalendar.class, RequestService.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "FileHash", scope = CoordinateFileReference.class)
    public JAXBElement<byte[]> createCoordinateFileReferenceFileHash(byte[] value) {
        return new JAXBElement<byte[]>(_CoordinateFileReferenceFileHash_QNAME, byte[].class, CoordinateFileReference.class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "DocDate", scope = ServiceDocument.class)
    public JAXBElement<XMLGregorianCalendar> createServiceDocumentDocDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_ServiceDocumentDocDate_QNAME, XMLGregorianCalendar.class, ServiceDocument.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "CopyCount", scope = ServiceDocument.class)
    public JAXBElement<Integer> createServiceDocumentCopyCount(Integer value) {
        return new JAXBElement<Integer>(_ServiceDocumentCopyCount_QNAME, Integer.class, ServiceDocument.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "ListCount", scope = ServiceDocument.class)
    public JAXBElement<Integer> createServiceDocumentListCount(Integer value) {
        return new JAXBElement<Integer>(_ServiceDocumentListCount_QNAME, Integer.class, ServiceDocument.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "ValidityPeriod", scope = ServiceDocument.class)
    public JAXBElement<XMLGregorianCalendar> createServiceDocumentValidityPeriod(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RequestStatusValidityPeriod_QNAME, XMLGregorianCalendar.class, ServiceDocument.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "ResponseDate", scope = CoordinateStatusData.class)
    public JAXBElement<XMLGregorianCalendar> createCoordinateStatusDataResponseDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RequestDocumentsDataResponseDate_QNAME, XMLGregorianCalendar.class, CoordinateStatusData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", name = "PlanDate", scope = CoordinateStatusData.class)
    public JAXBElement<XMLGregorianCalendar> createCoordinateStatusDataPlanDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RequestDocumentsDataPlanDate_QNAME, XMLGregorianCalendar.class, CoordinateStatusData.class, value);
    }

}
