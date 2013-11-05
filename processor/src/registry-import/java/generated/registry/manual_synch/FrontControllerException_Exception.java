
package generated.registry.manual_synch;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAXWS SI.
 * JAX-WS RI 2.1-02/02/2007 03:56 AM(vivekp)-FCS
 * Generated source version: 2.1
 * 
 */
@WebFault(name = "FrontControllerException", targetNamespace = "http://ru.axetta.ecafe")
public class FrontControllerException_Exception
    extends java.lang.Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private FrontControllerException faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public FrontControllerException_Exception(String message, FrontControllerException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public FrontControllerException_Exception(String message, FrontControllerException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: generated.registry.manual_synch.FrontControllerException
     */
    public FrontControllerException getFaultInfo() {
        return faultInfo;
    }

}
