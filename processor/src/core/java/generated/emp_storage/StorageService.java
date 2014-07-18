
package generated.emp_storage;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAXWS SI.
 * JAX-WS RI 2.1-02/02/2007 03:56 AM(vivekp)-FCS
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "StorageService", targetNamespace = "http://emp.mos.ru/schemas/storage/", wsdlLocation = "http://Inv5379-NB:8088/mockStorageBinding?wsdl")
public class StorageService
    extends Service
{

    private final static URL STORAGESERVICE_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("http://Inv5379-NB:8088/mockStorageBinding?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        STORAGESERVICE_WSDL_LOCATION = url;
    }

    public StorageService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public StorageService() {
        super(STORAGESERVICE_WSDL_LOCATION, new QName("http://emp.mos.ru/schemas/storage/", "StorageService"));
    }

    /**
     * 
     * @return
     *     returns StoragePortType
     */
    @WebEndpoint(name = "StoragePort")
    public StoragePortType getStoragePort() {
        return (StoragePortType)super.getPort(new QName("http://emp.mos.ru/schemas/storage/", "StoragePort"), StoragePortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns StoragePortType
     */
    @WebEndpoint(name = "StoragePort")
    public StoragePortType getStoragePort(WebServiceFeature... features) {
        return (StoragePortType)super.getPort(new QName("http://emp.mos.ru/schemas/storage/", "StoragePort"), StoragePortType.class, features);
    }

}
