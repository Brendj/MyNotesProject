
package ru.msk.schemas.uec.transactionservice.v1;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "TransactionService", targetNamespace = "http://schemas.msk.ru/uec/TransactionService/v1", wsdlLocation = "file:/D:/workspace/temp/wsimport/wsimport/TransactionService.wsdl")
public class TransactionService
    extends Service
{

    private final static URL TRANSACTIONSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(ru.msk.schemas.uec.transactionservice.v1.TransactionService.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = ru.msk.schemas.uec.transactionservice.v1.TransactionService.class.getResource(".");
            url = new URL(baseUrl, "file:/D:/workspace/temp/wsimport/wsimport/TransactionService.wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'file:/D:/workspace/temp/wsimport/wsimport/TransactionService.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        TRANSACTIONSERVICE_WSDL_LOCATION = url;
    }

    public TransactionService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public TransactionService() {
        super(TRANSACTIONSERVICE_WSDL_LOCATION, new QName("http://schemas.msk.ru/uec/TransactionService/v1", "TransactionService"));
    }

    /**
     * 
     * @return
     *     returns TransactionServicePortType
     */
    @WebEndpoint(name = "TransactionServicePort")
    public TransactionServicePortType getTransactionServicePort() {
        return super.getPort(new QName("http://schemas.msk.ru/uec/TransactionService/v1", "TransactionServicePort"), TransactionServicePortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns TransactionServicePortType
     */
    @WebEndpoint(name = "TransactionServicePort")
    public TransactionServicePortType getTransactionServicePort(WebServiceFeature... features) {
        return super.getPort(new QName("http://schemas.msk.ru/uec/TransactionService/v1", "TransactionServicePort"), TransactionServicePortType.class, features);
    }

}
