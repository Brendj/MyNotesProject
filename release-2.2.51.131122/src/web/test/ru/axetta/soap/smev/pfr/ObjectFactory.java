
package ru.axetta.soap.smev.pfr;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.axetta.soap.smev.pfr package. 
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

    private final static QName _MessageClass_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "MessageClass");
    private final static QName _TestMsg_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "TestMsg");
    private final static QName _Process_QNAME = new QName("http://service.pfr.socit.ru", "Process");
    private final static QName _MessageData_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "MessageData");
    private final static QName _ExchangeType_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "ExchangeType");
    private final static QName _NodeId_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "NodeId");
    private final static QName _Sender_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "Sender");
    private final static QName _RequestIdRef_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "RequestIdRef");
    private final static QName _Include_QNAME = new QName("http://www.w3.org/2004/08/xop/include", "Include");
    private final static QName _Header_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "Header");
    private final static QName _TypeCode_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "TypeCode");
    private final static QName _Date_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "Date");
    private final static QName _MessageId_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "MessageId");
    private final static QName _ServiceCode_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "ServiceCode");
    private final static QName _Recipient_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "Recipient");
    private final static QName _CaseNumber_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "CaseNumber");
    private final static QName _OriginRequestIdRef_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "OriginRequestIdRef");
    private final static QName _ProcessResponse_QNAME = new QName("http://service.pfr.socit.ru", "ProcessResponse");
    private final static QName _DigestValue_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "DigestValue");
    private final static QName _Originator_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "Originator");
    private final static QName _AppDocument_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "AppDocument");
    private final static QName _Message_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "Message");
    private final static QName _FilePFR_QNAME = new QName("http://data.service.pfr.socit.ru", "FilePFR");
    private final static QName _BinaryData_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "BinaryData");
    private final static QName _AppData_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "AppData");
    private final static QName _Status_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "Status");
    private final static QName _Properties_QNAME = new QName("http://data.service.pfr.socit.ru", "Properties");
    private final static QName _TimeStamp_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "TimeStamp");
    private final static QName _BaseMessage_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "BaseMessage");
    private final static QName _Reference_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "Reference");
    private final static QName _Type_QNAME = new QName("http://data.service.pfr.socit.ru", "Type");
    private final static QName _RequestCode_QNAME = new QName("http://smev.gosuslugi.ru/rev111111", "RequestCode");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.axetta.soap.smev.pfr
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AppDataType }
     * 
     */
    public AppDataType createAppDataType() {
        return new AppDataType();
    }

    /**
     * Create an instance of {@link BaseMessageType }
     * 
     */
    public BaseMessageType createBaseMessageType() {
        return new BaseMessageType();
    }

    /**
     * Create an instance of {@link HeaderType }
     * 
     */
    public HeaderType createHeaderType() {
        return new HeaderType();
    }

    /**
     * Create an instance of {@link ReferenceType }
     * 
     */
    public ReferenceType createReferenceType() {
        return new ReferenceType();
    }

    /**
     * Create an instance of {@link Include }
     * 
     */
    public Include createInclude() {
        return new Include();
    }

    /**
     * Create an instance of {@link Properties }
     * 
     */
    public Properties createProperties() {
        return new Properties();
    }

    /**
     * Create an instance of {@link Property }
     * 
     */
    public Property createProperty() {
        return new Property();
    }

    /**
     * Create an instance of {@link OrgExternalType }
     * 
     */
    public OrgExternalType createOrgExternalType() {
        return new OrgExternalType();
    }

    /**
     * Create an instance of {@link MessageType }
     * 
     */
    public MessageType createMessageType() {
        return new MessageType();
    }

    /**
     * Create an instance of {@link MessageDataType }
     * 
     */
    public MessageDataType createMessageDataType() {
        return new MessageDataType();
    }

    /**
     * Create an instance of {@link AppDocumentType }
     * 
     */
    public AppDocumentType createAppDocumentType() {
        return new AppDocumentType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MessageClassType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "MessageClass")
    public JAXBElement<MessageClassType> createMessageClass(MessageClassType value) {
        return new JAXBElement<MessageClassType>(_MessageClass_QNAME, MessageClassType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "TestMsg")
    public JAXBElement<String> createTestMsg(String value) {
        return new JAXBElement<String>(_TestMsg_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BaseMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.pfr.socit.ru", name = "Process")
    public JAXBElement<BaseMessageType> createProcess(BaseMessageType value) {
        return new JAXBElement<BaseMessageType>(_Process_QNAME, BaseMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MessageDataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "MessageData")
    public JAXBElement<MessageDataType> createMessageData(MessageDataType value) {
        return new JAXBElement<MessageDataType>(_MessageData_QNAME, MessageDataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "ExchangeType")
    public JAXBElement<String> createExchangeType(String value) {
        return new JAXBElement<String>(_ExchangeType_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "NodeId")
    public JAXBElement<String> createNodeId(String value) {
        return new JAXBElement<String>(_NodeId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrgExternalType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "Sender")
    public JAXBElement<OrgExternalType> createSender(OrgExternalType value) {
        return new JAXBElement<OrgExternalType>(_Sender_QNAME, OrgExternalType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "RequestIdRef")
    public JAXBElement<String> createRequestIdRef(String value) {
        return new JAXBElement<String>(_RequestIdRef_QNAME, String.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link HeaderType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "Header")
    public JAXBElement<HeaderType> createHeader(HeaderType value) {
        return new JAXBElement<HeaderType>(_Header_QNAME, HeaderType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "TypeCode")
    public JAXBElement<String> createTypeCode(String value) {
        return new JAXBElement<String>(_TypeCode_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "Date")
    public JAXBElement<XMLGregorianCalendar> createDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_Date_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "MessageId")
    public JAXBElement<String> createMessageId(String value) {
        return new JAXBElement<String>(_MessageId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "ServiceCode")
    public JAXBElement<String> createServiceCode(String value) {
        return new JAXBElement<String>(_ServiceCode_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrgExternalType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "Recipient")
    public JAXBElement<OrgExternalType> createRecipient(OrgExternalType value) {
        return new JAXBElement<OrgExternalType>(_Recipient_QNAME, OrgExternalType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "CaseNumber")
    public JAXBElement<String> createCaseNumber(String value) {
        return new JAXBElement<String>(_CaseNumber_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "OriginRequestIdRef")
    public JAXBElement<String> createOriginRequestIdRef(String value) {
        return new JAXBElement<String>(_OriginRequestIdRef_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BaseMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.pfr.socit.ru", name = "ProcessResponse")
    public JAXBElement<BaseMessageType> createProcessResponse(BaseMessageType value) {
        return new JAXBElement<BaseMessageType>(_ProcessResponse_QNAME, BaseMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "DigestValue")
    public JAXBElement<byte[]> createDigestValue(byte[] value) {
        return new JAXBElement<byte[]>(_DigestValue_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrgExternalType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "Originator")
    public JAXBElement<OrgExternalType> createOriginator(OrgExternalType value) {
        return new JAXBElement<OrgExternalType>(_Originator_QNAME, OrgExternalType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AppDocumentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "AppDocument")
    public JAXBElement<AppDocumentType> createAppDocument(AppDocumentType value) {
        return new JAXBElement<AppDocumentType>(_AppDocument_QNAME, AppDocumentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "Message")
    public JAXBElement<MessageType> createMessage(MessageType value) {
        return new JAXBElement<MessageType>(_Message_QNAME, MessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.service.pfr.socit.ru", name = "FilePFR")
    public JAXBElement<byte[]> createFilePFR(byte[] value) {
        return new JAXBElement<byte[]>(_FilePFR_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "BinaryData")
    public JAXBElement<byte[]> createBinaryData(byte[] value) {
        return new JAXBElement<byte[]>(_BinaryData_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AppDataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "AppData")
    public JAXBElement<AppDataType> createAppData(AppDataType value) {
        return new JAXBElement<AppDataType>(_AppData_QNAME, AppDataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StatusType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "Status")
    public JAXBElement<StatusType> createStatus(StatusType value) {
        return new JAXBElement<StatusType>(_Status_QNAME, StatusType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Properties }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.service.pfr.socit.ru", name = "Properties")
    public JAXBElement<Properties> createProperties(Properties value) {
        return new JAXBElement<Properties>(_Properties_QNAME, Properties.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "TimeStamp")
    public JAXBElement<XMLGregorianCalendar> createTimeStamp(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_TimeStamp_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BaseMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "BaseMessage")
    public JAXBElement<BaseMessageType> createBaseMessage(BaseMessageType value) {
        return new JAXBElement<BaseMessageType>(_BaseMessage_QNAME, BaseMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "Reference")
    public JAXBElement<ReferenceType> createReference(ReferenceType value) {
        return new JAXBElement<ReferenceType>(_Reference_QNAME, ReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Type }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://data.service.pfr.socit.ru", name = "Type")
    public JAXBElement<Type> createType(Type value) {
        return new JAXBElement<Type>(_Type_QNAME, Type.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://smev.gosuslugi.ru/rev111111", name = "RequestCode")
    public JAXBElement<String> createRequestCode(String value) {
        return new JAXBElement<String>(_RequestCode_QNAME, String.class, null, value);
    }

}
