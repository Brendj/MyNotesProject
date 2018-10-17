
package generated.ru.gov.smev.artefacts.x.services.message_exchange._1;

import generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.Void;

import javax.xml.ws.WebFault;


/**
 * 
 *                     ������������� (UUID), ����������� ��������� ������������, �������� ������ timestamp.
 *                     �������� � ��������� �������:
 *                     a) � ������� ����������� ������� ����������� �����.
 *                     �������� �������: ���������� � ������������ ������� �������� �����.
 *                     b) � ���������� ����� ������� �������� �����, ��������� �������� ��������� � ������� ���� ����� ��� �����.
 *                     �������� �������: ������������� ����� UUID, ��������� ��� ���������, ��������� ���������.
 *                 
 * 
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebFault(name = "StaleMessageId", targetNamespace = "urn://x-artefacts-smev-gov-ru/services/message-exchange/types/faults/1.2")
public class StaleMessageIdException
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private Void faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public StaleMessageIdException(String message, Void faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param cause
     * @param message
     */
    public StaleMessageIdException(String message, Void faultInfo, Throwable cause) {
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
