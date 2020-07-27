
/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package generated.contingent.ispp;

import javax.xml.namespace.QName;
import javax.xml.ws.*;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "IsppWebServiceService", targetNamespace = "urn:contingent.mos.ru:ws:ispp", wsdlLocation = "file:/C:/tmp/contingent/ispp.wsdl.xml")
public class IsppWebServiceService
    extends Service
{

    private final static URL ISPPWEBSERVICESERVICE_WSDL_LOCATION;
    private final static WebServiceException ISPPWEBSERVICESERVICE_EXCEPTION;
    private final static QName ISPPWEBSERVICESERVICE_QNAME = new QName("urn:contingent.mos.ru:ws:ispp", "IsppWebServiceService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("file:/C:/tmp/contingent/ispp.wsdl.xml");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        ISPPWEBSERVICESERVICE_WSDL_LOCATION = url;
        ISPPWEBSERVICESERVICE_EXCEPTION = e;
    }

    public IsppWebServiceService() {
        super(__getWsdlLocation(), ISPPWEBSERVICESERVICE_QNAME);
    }

    public IsppWebServiceService(WebServiceFeature... features) {
        super(__getWsdlLocation(), ISPPWEBSERVICESERVICE_QNAME, features);
    }

    public IsppWebServiceService(URL wsdlLocation) {
        super(wsdlLocation, ISPPWEBSERVICESERVICE_QNAME);
    }

    public IsppWebServiceService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, ISPPWEBSERVICESERVICE_QNAME, features);
    }

    public IsppWebServiceService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public IsppWebServiceService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns IsppWebService
     */
    @WebEndpoint(name = "IsppWebServicePort")
    public IsppWebService getIsppWebServicePort() {
        return super.getPort(new QName("urn:contingent.mos.ru:ws:ispp", "IsppWebServicePort"), IsppWebService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns IsppWebService
     */
    @WebEndpoint(name = "IsppWebServicePort")
    public IsppWebService getIsppWebServicePort(WebServiceFeature... features) {
        return super.getPort(new QName("urn:contingent.mos.ru:ws:ispp", "IsppWebServicePort"), IsppWebService.class, features);
    }

    private static URL __getWsdlLocation() {
        if (ISPPWEBSERVICESERVICE_EXCEPTION!= null) {
            throw ISPPWEBSERVICESERVICE_EXCEPTION;
        }
        return ISPPWEBSERVICESERVICE_WSDL_LOCATION;
    }

}
