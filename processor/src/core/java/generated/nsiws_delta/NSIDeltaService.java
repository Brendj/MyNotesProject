
package generated.nsiws_delta;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAXWS SI.
 * JAX-WS RI 2.1-02/02/2007 03:56 AM(vivekp)-FCS
 * Generated source version: 2.1
 * 
 */
//@WebService(name = "NSIDeltaService", targetNamespace = "http://rstyle.com/nsi/delta/service")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface NSIDeltaService {


    /**
     * 
     * @param receiveNSIDeltaRequest
     * @return
     *     returns generated.nsiws_delta.ReceiveNSIDeltaResponseType
     */
    @WebMethod
    @WebResult(name = "receiveNSIDeltaResponse", targetNamespace = "http://rstyle.com/nsi/delta/service", partName = "receiveNSIDeltaResponse")
    public ReceiveNSIDeltaResponseType receiveNSIDelta(
        @WebParam(name = "receiveNSIDeltaRequest", targetNamespace = "http://rstyle.com/nsi/delta/service", partName = "receiveNSIDeltaRequest")
        ReceiveNSIDeltaRequestType receiveNSIDeltaRequest);

}
