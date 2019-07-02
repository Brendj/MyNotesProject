
package generated.ru.gov.smev.artefacts.x.services.message_exchange._1;

import generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.Void;

import javax.xml.ws.WebFault;


/**
 * 
 * 					Идентификатор, присвоенный сообщению отправителем, не является корректным строковым представлением
 * 					UUID, вариант 1 (см. RFC-4122).
 * 				
 * 
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebFault(name = "InvalidMessageIdFormat", targetNamespace = "urn://x-artefacts-smev-gov-ru/services/message-exchange/types/faults/1.2")
public class InvalidMessageIdFormatException
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private Void faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public InvalidMessageIdFormatException(String message, Void faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public InvalidMessageIdFormatException(String message, Void faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.Void
     */
    public Void getFaultInfo() {
        return faultInfo;
    }

}
