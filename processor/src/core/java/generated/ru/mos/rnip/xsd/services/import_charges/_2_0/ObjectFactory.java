
package generated.ru.mos.rnip.xsd.services.import_charges._2_0;

import generated.ru.mos.rnip.xsd.common._2_0.ImportPackageResponseType;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.mos.rnip.xsd.services.import_charges._2_0 package. 
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

    private final static QName _ImportChargesResponse_QNAME = new QName("urn://rnip.mos.ru/xsd/services/import-charges/2.0.1", "ImportChargesResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.mos.rnip.xsd.services.import_charges._2_0
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ImportChargesRequest }
     * 
     */
    public ImportChargesRequest createImportChargesRequest() {
        return new ImportChargesRequest();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImportPackageResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://rnip.mos.ru/xsd/services/import-charges/2.0.1", name = "ImportChargesResponse")
    public JAXBElement<ImportPackageResponseType> createImportChargesResponse(ImportPackageResponseType value) {
        return new JAXBElement<ImportPackageResponseType>(_ImportChargesResponse_QNAME, ImportPackageResponseType.class, null, value);
    }

}
