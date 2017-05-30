
package generated.spb.register;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated.spb.register2 package. 
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

    public final static QName _Password_QNAME = new QName("http://85.143.161.170:8080/webservice/food_benefits_full/wsdl", "password");
    public final static QName _Login_QNAME = new QName("http://85.143.161.170:8080/webservice/food_benefits_full/wsdl", "login");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated.spb.register2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Schools }
     * 
     */
    public Schools createSchools() {
        return new Schools();
    }

    /**
     * Create an instance of {@link Pupils }
     * 
     */
    public Pupils createPupils() {
        return new Pupils();
    }

    /**
     * Create an instance of {@link Benefit }
     * 
     */
    public Benefit createBenefit() {
        return new Benefit();
    }

    /**
     * Create an instance of {@link QuerySchool }
     * 
     */
    public QuerySchool createQuerySchool() {
        return new QuerySchool();
    }

    /**
     * Create an instance of {@link Pupil }
     * 
     */
    public Pupil createPupil() {
        return new Pupil();
    }

    /**
     * Create an instance of {@link School }
     * 
     */
    public School createSchool() {
        return new School();
    }

    /**
     * Create an instance of {@link Event }
     * 
     */
    public Event createEvent() {
        return new Event();
    }

    /**
     * Create an instance of {@link Query }
     * 
     */
    public Query createQuery() {
        return new Query();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://85.143.161.170:8080/webservice/food_benefits_full/wsdl", name = "password")
    public JAXBElement<String> createPassword(String value) {
        return new JAXBElement<String>(_Password_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://85.143.161.170:8080/webservice/food_benefits_full/wsdl", name = "login")
    public JAXBElement<String> createLogin(String value) {
        return new JAXBElement<String>(_Login_QNAME, String.class, null, value);
    }

}
