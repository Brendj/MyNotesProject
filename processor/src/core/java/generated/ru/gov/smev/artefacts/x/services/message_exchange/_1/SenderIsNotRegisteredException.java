
package generated.ru.gov.smev.artefacts.x.services.message_exchange._1;

import generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.Void;

import javax.xml.ws.WebFault;


/**
 * 
 * 					Отправитель сообщения не зарегистрирован в СМЭВ.
 * 					Возможная причина - смена X-400 имени при получении нового сертификата.
 * 					Остальные возможные причины - неправильная настройка СМЭВ.
 * 				
 * 
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebFault(name = "SenderIsNotRegistered", targetNamespace = "urn://x-artefacts-smev-gov-ru/services/message-exchange/types/faults/1.2")
public class SenderIsNotRegisteredException
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
    public SenderIsNotRegisteredException(String message, Void faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public SenderIsNotRegisteredException(String message, Void faultInfo, Throwable cause) {
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
