
package generated.ru.gov.smev.artefacts.x.services.message_exchange._1;

import generated.ru.gov.smev.artefacts.x.services.message_exchange.types.faults._1.QuoteLimitExceeded;

import javax.xml.ws.WebFault;


/**
 * 
 *                     ��������� ������ �������� �������� �������� ����� ����.
 *                 
 * 
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebFault(name = "QuoteLimitExceeded", targetNamespace = "urn://x-artefacts-smev-gov-ru/services/message-exchange/types/faults/1.2")
public class QuoteLimitExceededException
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private QuoteLimitExceeded faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public QuoteLimitExceededException(String message, QuoteLimitExceeded faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param cause
     * @param message
     */
    public QuoteLimitExceededException(String message, QuoteLimitExceeded faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: ru.gov.smev.artefacts.x.services.message_exchange.types.faults._1.QuoteLimitExceeded
     */
    public QuoteLimitExceeded getFaultInfo() {
        return faultInfo;
    }

}
