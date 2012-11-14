
package generated.msr.stoplist;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebFault(name = "incompleteTaskException", targetNamespace = "http://webservice.msr.com/sl/schema/")
public class IncompleteTaskException_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private IncompleteTaskException faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public IncompleteTaskException_Exception(String message, IncompleteTaskException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public IncompleteTaskException_Exception(String message, IncompleteTaskException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: generated.msr.stoplist.IncompleteTaskException
     */
    public IncompleteTaskException getFaultInfo() {
        return faultInfo;
    }

}
