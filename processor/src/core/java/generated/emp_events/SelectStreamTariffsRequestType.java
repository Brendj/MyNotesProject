
package generated.emp_events;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Запрос получения списка тарифов потока
 * 
 * <p>Java class for selectStreamTariffsRequest_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="selectStreamTariffsRequest_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{urn://subscription.api.emp.altarix.ru}BaseRequest_Type">
 *       &lt;sequence>
 *         &lt;element name="streamId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "selectStreamTariffsRequest_Type", propOrder = {
    "streamId"
})
public class SelectStreamTariffsRequestType
    extends BaseRequestType
{

    protected int streamId;

    /**
     * Gets the value of the streamId property.
     * 
     */
    public int getStreamId() {
        return streamId;
    }

    /**
     * Sets the value of the streamId property.
     * 
     */
    public void setStreamId(int value) {
        this.streamId = value;
    }

}
