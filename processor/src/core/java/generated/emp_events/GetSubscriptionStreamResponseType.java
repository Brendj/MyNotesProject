
package generated.emp_events;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Ответ на запрос получения информации о канале подписки
 * 
 * <p>Java class for getSubscriptionStreamResponse_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getSubscriptionStreamResponse_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{urn://subscription.api.emp.altarix.ru}BaseResponse_Type">
 *       &lt;sequence>
 *         &lt;element name="return" type="{urn://subscription.api.emp.altarix.ru}Stream_Type"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getSubscriptionStreamResponse_Type", propOrder = {
    "_return"
})
public class GetSubscriptionStreamResponseType
    extends BaseResponseType
{

    @XmlElement(name = "return", required = true, nillable = true)
    protected StreamType _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link StreamType }
     *     
     */
    public StreamType getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link StreamType }
     *     
     */
    public void setReturn(StreamType value) {
        this._return = value;
    }

}
