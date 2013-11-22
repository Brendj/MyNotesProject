
package ru.axetta.soap.smev;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.axetta.soap.smev package. 
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

    private final static QName _SyncResponse_QNAME = new QName("http://smev.gosuslugi.ru/MsgExample/xsd/types", "SyncResponse");
    private final static QName _SyncRequest_QNAME = new QName("http://smev.gosuslugi.ru/MsgExample/xsd/types", "SyncRequest");
    private final static QName _BinaryData_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "BinaryData");
    private final static QName _AppData_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "AppData");
    private final static QName _DigestValue_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "DigestValue");
    private final static QName _ASyncResultRequest_QNAME = new QName("http://smev.gosuslugi.ru/MsgExample/xsd/types", "aSyncResultRequest");
    private final static QName _Originator_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "Originator");
    private final static QName _AppDocument_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "AppDocument");
    private final static QName _ASyncReqResponse_QNAME = new QName("http://smev.gosuslugi.ru/MsgExample/xsd/types", "aSyncReqResponse");
    private final static QName _Message_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "Message");
    private final static QName _MessageStatus_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "MessageStatus");
    private final static QName _Reference_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "Reference");
    private final static QName _ASyncResultResponse_QNAME = new QName("http://smev.gosuslugi.ru/MsgExample/xsd/types", "aSyncResultResponse");
    private final static QName _TimeStamp_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "TimeStamp");
    private final static QName _MessageData_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "MessageData");
    private final static QName _ASyncReqRequest_QNAME = new QName("http://smev.gosuslugi.ru/MsgExample/xsd/types", "aSyncReqRequest");
    private final static QName _ServiceCode_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "ServiceCode");
    private final static QName _Recipient_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "Recipient");
    private final static QName _CaseNumber_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "CaseNumber");
    private final static QName _OriginRequestIdRef_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "OriginRequestIdRef");
    private final static QName _Sender_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "Sender");
    private final static QName _RequestIdRef_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "RequestIdRef");
    private final static QName _Header_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "Header");
    private final static QName _TypeCode_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "TypeCode");
    private final static QName _Date_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "Date");
    private final static QName _MessageId_QNAME = new QName("http://smev.gosuslugi.ru/rev110801", "MessageId");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.axetta.soap.smev
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SyncRequestType }
     * 
     */
    public SyncRequestType createSyncRequestType() {
        return new SyncRequestType();
    }

    /**
     * Create an instance of {@link ReferenceType }
     * 
     */
    public ReferenceType createReferenceType() {
        return new ReferenceType();
    }

    /**
     * Create an instance of {@link HeaderType }
     * 
     */
    public HeaderType createHeaderType() {
        return new HeaderType();
    }

    /**
     * Create an instance of {@link SyncResponseType }
     * 
     */
    public SyncResponseType createSyncResponseType() {
        return new SyncResponseType();
    }

    /**
     * Create an instance of {@link MessageDataType }
     * 
     */
    public MessageDataType createMessageDataType() {
        return new MessageDataType();
    }

    /**
     * Create an instance of {@link MessageType }
     * 
     */
    public MessageType createMessageType() {
        return new MessageType();
    }

    /**
     * Create an instance of {@link MessageStatusType }
     * 
     */
    public MessageStatusType createMessageStatusType() {
        return new MessageStatusType();
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
     * Create an instance of {@link OrgExternalType }
     * 
     */
    public OrgExternalType createOrgExternalType() {
        return new OrgExternalType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SyncResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/MsgExample/xsd/types", name = "SyncResponse")
    public JAXBElement<SyncResponseType> createSyncResponse(SyncResponseType value) {
        return new JAXBElement<SyncResponseType>(_SyncResponse_QNAME, SyncResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SyncRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/MsgExample/xsd/types", name = "SyncRequest")
    public JAXBElement<SyncRequestType> createSyncRequest(SyncRequestType value) {
        return new JAXBElement<SyncRequestType>(_SyncRequest_QNAME, SyncRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "BinaryData")
    public JAXBElement<String> createBinaryData(String value) {
        return new JAXBElement<String>(_BinaryData_QNAME, String.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "DigestValue")
    public JAXBElement<byte[]> createDigestValue(byte[] value) {
        return new JAXBElement<byte[]>(_DigestValue_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SyncRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/MsgExample/xsd/types", name = "aSyncResultRequest")
    public JAXBElement<SyncRequestType> createASyncResultRequest(SyncRequestType value) {
        return new JAXBElement<SyncRequestType>(_ASyncResultRequest_QNAME, SyncRequestType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link AppDocumentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "AppDocument")
    public JAXBElement<AppDocumentType> createAppDocument(AppDocumentType value) {
        return new JAXBElement<AppDocumentType>(_AppDocument_QNAME, AppDocumentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SyncResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/MsgExample/xsd/types", name = "aSyncReqResponse")
    public JAXBElement<SyncResponseType> createASyncReqResponse(SyncResponseType value) {
        return new JAXBElement<SyncResponseType>(_ASyncReqResponse_QNAME, SyncResponseType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link MessageStatusType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "MessageStatus")
    public JAXBElement<MessageStatusType> createMessageStatus(MessageStatusType value) {
        return new JAXBElement<MessageStatusType>(_MessageStatus_QNAME, MessageStatusType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "Reference")
    public JAXBElement<ReferenceType> createReference(ReferenceType value) {
        return new JAXBElement<ReferenceType>(_Reference_QNAME, ReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SyncResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/MsgExample/xsd/types", name = "aSyncResultResponse")
    public JAXBElement<SyncResponseType> createASyncResultResponse(SyncResponseType value) {
        return new JAXBElement<SyncResponseType>(_ASyncResultResponse_QNAME, SyncResponseType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link MessageDataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "MessageData")
    public JAXBElement<MessageDataType> createMessageData(MessageDataType value) {
        return new JAXBElement<MessageDataType>(_MessageData_QNAME, MessageDataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SyncRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/MsgExample/xsd/types", name = "aSyncReqRequest")
    public JAXBElement<SyncRequestType> createASyncReqRequest(SyncRequestType value) {
        return new JAXBElement<SyncRequestType>(_ASyncReqRequest_QNAME, SyncRequestType.class, null, value);
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
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "CaseNumber")
    public JAXBElement<String> createCaseNumber(String value) {
        return new JAXBElement<String>(_CaseNumber_QNAME, String.class, null, value);
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
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "RequestIdRef")
    public JAXBElement<String> createRequestIdRef(String value) {
        return new JAXBElement<String>(_RequestIdRef_QNAME, String.class, null, value);
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
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "TypeCode")
    public JAXBElement<String> createTypeCode(String value) {
        return new JAXBElement<String>(_TypeCode_QNAME, String.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev110801", name = "MessageId")
    public JAXBElement<String> createMessageId(String value) {
        return new JAXBElement<String>(_MessageId_QNAME, String.class, null, value);
    }

}
