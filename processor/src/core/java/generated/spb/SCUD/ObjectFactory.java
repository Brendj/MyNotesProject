
package generated.spb.SCUD;

import ru.axetta.ecafe.processor.core.service.scud.EventDataItem;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated.spb.SCUD package.
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

    public final static QName _Login_QNAME = new QName("http://petersburgedu.ru/service/webservice/scud/wsdl", "login");
    public final static QName _PushZipFileRequest_QNAME = new QName("http://petersburgedu.ru/service/webservice/scud/wsdl", "pushZipFileRequest");
    public final static QName _Password_QNAME = new QName("http://petersburgedu.ru/service/webservice/scud/wsdl", "password");

    private final String FORMATER = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated.spb.SCUD
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PushResponse }
     * 
     */
    public PushResponse createPushResponse() {
        return new PushResponse();
    }

    /**
     * Create an instance of {@link EventList }
     * 
     */
    public EventList createEventList() {
        return new EventList();
    }

    public EventList createEventList(List<EventDataItem> items) throws Exception{
        EventList events = this.createEventList();

        for(EventDataItem element : items){
                EventType event = this.createEventType(element);
                events.getEvent().add(event);
        }
        return events;
    }


    /**
     * Create an instance of {@link EventType }
     * 
     */
    public EventType createEventType() {
        return new EventType();
    }

    private EventType createEventType(EventDataItem element) throws Exception {
        EventType eventType = new EventType();
        DateFormat format = new SimpleDateFormat(FORMATER);

        XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(format.format(element.getEventDate()));

        eventType.setEventDate(date);
        eventType.setDirectionType(element.getDirectionType());
        eventType.setCardUid(element.getCardUid());
        eventType.setOrganizationUid(element.getOrganizationUid());
        eventType.setReaderUid(element.getReaderUid());
        eventType.setStudentUid(element.getStudentUid());
        eventType.setSystemUid(element.getSystemUid());

        return eventType;

    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://petersburgedu.ru/service/webservice/scud/wsdl", name = "login")
    public JAXBElement<String> createLogin(String value) {
        return new JAXBElement<String>(_Login_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://petersburgedu.ru/service/webservice/scud/wsdl", name = "pushZipFileRequest")
    public JAXBElement<String> createPushZipFileRequest(String value) {
        return new JAXBElement<String>(_PushZipFileRequest_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://petersburgedu.ru/service/webservice/scud/wsdl", name = "password")
    public JAXBElement<String> createPassword(String value) {
        return new JAXBElement<String>(_Password_QNAME, String.class, null, value);
    }


}
