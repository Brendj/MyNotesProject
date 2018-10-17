
package generated.ru.gov.smev.artefacts.x.services.message_exchange.types._1;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.2}SmevAsyncProcessingMessage" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "smevAsyncProcessingMessage"
})
@XmlRootElement(name = "GetStatusResponse")
public class GetStatusResponse {

    @XmlElement(name = "SmevAsyncProcessingMessage")
    protected SmevAsyncProcessingMessage smevAsyncProcessingMessage;

    /**
     * Gets the value of the smevAsyncProcessingMessage property.
     * 
     * @return
     *     possible object is
     *     {@link SmevAsyncProcessingMessage }
     *     
     */
    public SmevAsyncProcessingMessage getSmevAsyncProcessingMessage() {
        return smevAsyncProcessingMessage;
    }

    /**
     * Sets the value of the smevAsyncProcessingMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link SmevAsyncProcessingMessage }
     *     
     */
    public void setSmevAsyncProcessingMessage(SmevAsyncProcessingMessage value) {
        this.smevAsyncProcessingMessage = value;
    }

}
