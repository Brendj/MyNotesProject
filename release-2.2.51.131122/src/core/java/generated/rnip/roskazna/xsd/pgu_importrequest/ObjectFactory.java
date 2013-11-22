//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-325 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.01.14 at 07:18:21 PM MSK 
//


package generated.rnip.roskazna.xsd.pgu_importrequest;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import generated.rnip.catalog.ServiceCatalogType;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.roskazna.xsd.pgu_importrequest package. 
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

    private final static QName _ImportRequest_QNAME = new QName("http://roskazna.ru/xsd/PGU_ImportRequest", "ImportRequest");
    private final static QName _ImportRequestCatalogChanges_QNAME = new QName("", "Changes");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.roskazna.xsd.pgu_importrequest
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ImportRequest }
     * 
     */
    public ImportRequest createImportRequest() {
        return new ImportRequest();
    }

    /**
     * Create an instance of {@link ImportRequest.Catalog }
     * 
     */
    public ImportRequest.Catalog createImportRequestCatalog() {
        return new ImportRequest.Catalog();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImportRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/xsd/PGU_ImportRequest", name = "ImportRequest")
    public JAXBElement<ImportRequest> createImportRequest(ImportRequest value) {
        return new JAXBElement<ImportRequest>(_ImportRequest_QNAME, ImportRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceCatalogType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Changes", scope = ImportRequest.Catalog.class)
    public JAXBElement<ServiceCatalogType> createImportRequestCatalogChanges(ServiceCatalogType value) {
        return new JAXBElement<ServiceCatalogType>(_ImportRequestCatalogChanges_QNAME, ServiceCatalogType.class, ImportRequest.Catalog.class, value);
    }

}
