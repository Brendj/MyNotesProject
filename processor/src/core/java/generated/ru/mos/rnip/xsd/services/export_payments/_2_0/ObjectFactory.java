
package generated.ru.mos.rnip.xsd.services.export_payments._2_0;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.mos.rnip.xsd.services.export_payments._2_0 package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.mos.rnip.xsd.services.export_payments._2_0
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ExportPaymentsResponse }
     * 
     */
    public ExportPaymentsResponse createExportPaymentsResponse() {
        return new ExportPaymentsResponse();
    }

    /**
     * Create an instance of {@link ExportPaymentsResponse.PaymentInfo }
     * 
     */
    public ExportPaymentsResponse.PaymentInfo createExportPaymentsResponsePaymentInfo() {
        return new ExportPaymentsResponse.PaymentInfo();
    }

    /**
     * Create an instance of {@link ExportPaymentsRequest }
     * 
     */
    public ExportPaymentsRequest createExportPaymentsRequest() {
        return new ExportPaymentsRequest();
    }

    /**
     * Create an instance of {@link ExportPaymentsResponse.PaymentInfo.AcknowledgmentInfo }
     * 
     */
    public ExportPaymentsResponse.PaymentInfo.AcknowledgmentInfo createExportPaymentsResponsePaymentInfoAcknowledgmentInfo() {
        return new ExportPaymentsResponse.PaymentInfo.AcknowledgmentInfo();
    }

    /**
     * Create an instance of {@link ExportPaymentsResponse.PaymentInfo.RefundInfo }
     * 
     */
    public ExportPaymentsResponse.PaymentInfo.RefundInfo createExportPaymentsResponsePaymentInfoRefundInfo() {
        return new ExportPaymentsResponse.PaymentInfo.RefundInfo();
    }

}
