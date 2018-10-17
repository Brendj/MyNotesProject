
package generated.ru.gov.smev.artefacts.x.services.message_exchange.types._1;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


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
 *         &lt;element ref="{urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.2}AsyncProcessingStatus"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "asyncProcessingStatus"
})
@XmlRootElement(name = "AsyncProcessingStatusData")
public class AsyncProcessingStatusData {

    @XmlElement(name = "AsyncProcessingStatus", required = true)
    protected AsyncProcessingStatus asyncProcessingStatus;
    @XmlAttribute(name = "Id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the asyncProcessingStatus property.
     * 
     * @return
     *     possible object is
     *     {@link AsyncProcessingStatus }
     *     
     */
    public AsyncProcessingStatus getAsyncProcessingStatus() {
        return asyncProcessingStatus;
    }

    /**
     * Sets the value of the asyncProcessingStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link AsyncProcessingStatus }
     *     
     */
    public void setAsyncProcessingStatus(AsyncProcessingStatus value) {
        this.asyncProcessingStatus = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
