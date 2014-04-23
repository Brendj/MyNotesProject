
package generated.nsiws2.com.rstyle.nsi.services.out;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated.nsiws2.com.rstyle.nsi.services.out package.
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

    private final static QName _Reference_QNAME = new QName("http://rstyle.com/nsi/services/out", "reference");
    private final static QName _NsiResponse_QNAME = new QName("http://rstyle.com/nsi/services/out", "nsiResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated.nsiws2.com.rstyle.nsi.services.out
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
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/out", name = "reference")
    public JAXBElement<byte[]> createReference(byte[] value) {
        return new JAXBElement<byte[]>(_Reference_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NSIResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rstyle.com/nsi/services/out", name = "nsiResponse")
    public JAXBElement<NSIResponseType> createNsiResponse(NSIResponseType value) {
        return new JAXBElement<NSIResponseType>(_NsiResponse_QNAME, NSIResponseType.class, null, value);
    }

}
