
package generated.ru.mos.rnip.xsd.services.import_payments._2_1;

import generated.ru.mos.rnip.xsd.common._2_1.ImportPackageResponseType;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.mos.rnip.xsd.services.import_payments._2_1 package. 
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

    private final static QName _ImportPaymentsResponse_QNAME = new QName("urn://rnip.mos.ru/xsd/services/import-payments/2.1.1", "ImportPaymentsResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.mos.rnip.xsd.services.import_payments._2_1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ImportPaymentsRequest }
     * 
     */
    public ImportPaymentsRequest createImportPaymentsRequest() {
        return new ImportPaymentsRequest();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImportPackageResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://rnip.mos.ru/xsd/services/import-payments/2.1.1", name = "ImportPaymentsResponse")
    public JAXBElement<ImportPackageResponseType> createImportPaymentsResponse(ImportPackageResponseType value) {
        return new JAXBElement<ImportPackageResponseType>(_ImportPaymentsResponse_QNAME, ImportPackageResponseType.class, null, value);
    }

}
