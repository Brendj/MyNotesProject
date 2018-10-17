
package generated.ru.mos.rnip.xsd.organization._2_0;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.mos.rnip.xsd.organization._2_0 package. 
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

    private final static QName _RefundPayer_QNAME = new QName("http://rnip.mos.ru/xsd/Organization/2.0.1", "RefundPayer");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.mos.rnip.xsd.organization._2_0
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UBPOrganizationType }
     * 
     */
    public UBPOrganizationType createUBPOrganizationType() {
        return new UBPOrganizationType();
    }

    /**
     * Create an instance of {@link Payee }
     * 
     */
    public Payee createPayee() {
        return new Payee();
    }

    /**
     * Create an instance of {@link OrganizationType }
     * 
     */
    public OrganizationType createOrganizationType() {
        return new OrganizationType();
    }

    /**
     * Create an instance of {@link LsvTOFKType }
     * 
     */
    public LsvTOFKType createLsvTOFKType() {
        return new LsvTOFKType();
    }

    /**
     * Create an instance of {@link PaymentOrgType }
     * 
     */
    public PaymentOrgType createPaymentOrgType() {
        return new PaymentOrgType();
    }

    /**
     * Create an instance of {@link LsvFOType }
     * 
     */
    public LsvFOType createLsvFOType() {
        return new LsvFOType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UBPOrganizationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rnip.mos.ru/xsd/Organization/2.0.1", name = "RefundPayer")
    public JAXBElement<UBPOrganizationType> createRefundPayer(UBPOrganizationType value) {
        return new JAXBElement<UBPOrganizationType>(_RefundPayer_QNAME, UBPOrganizationType.class, null, value);
    }

}
