//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-325 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.01.14 at 07:18:21 PM MSK 
//


package generated.rnip.roskazna.xsd.exportincomesresponse;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.roskazna.xsd.exportincomesresponse package. 
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

    private final static QName _ExportIncomesResponse_QNAME = new QName("http://roskazna.ru/xsd/ExportIncomesResponse", "ExportIncomesResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.roskazna.xsd.exportincomesresponse
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ExportIncomesResponse.Incomes.IncometInfo }
     * 
     */
    public ExportIncomesResponse.Incomes.IncometInfo createExportIncomesResponseIncomesIncometInfo() {
        return new ExportIncomesResponse.Incomes.IncometInfo();
    }

    /**
     * Create an instance of {@link ExportIncomesResponse.Incomes }
     * 
     */
    public ExportIncomesResponse.Incomes createExportIncomesResponseIncomes() {
        return new ExportIncomesResponse.Incomes();
    }

    /**
     * Create an instance of {@link ExportIncomesResponse }
     * 
     */
    public ExportIncomesResponse createExportIncomesResponse() {
        return new ExportIncomesResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExportIncomesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://roskazna.ru/xsd/ExportIncomesResponse", name = "ExportIncomesResponse")
    public JAXBElement<ExportIncomesResponse> createExportIncomesResponse(ExportIncomesResponse value) {
        return new JAXBElement<ExportIncomesResponse>(_ExportIncomesResponse_QNAME, ExportIncomesResponse.class, null, value);
    }

}
