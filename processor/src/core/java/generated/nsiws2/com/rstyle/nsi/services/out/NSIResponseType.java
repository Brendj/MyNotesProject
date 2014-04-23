
package generated.nsiws2.com.rstyle.nsi.services.out;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import generated.nsiws2.ru.gosuslugi.smev.rev110801.ExtMessageDataType;
import generated.nsiws2.ru.gosuslugi.smev.rev110801.MessageType;


/**
 * <p>Java class for NSIResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NSIResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://smev.gosuslugi.ru/rev110801}Message"/>
 *         &lt;element ref="{http://smev.gosuslugi.ru/rev110801}MessageData"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NSIResponseType", propOrder = {
    "message",
    "messageData"
})
public class NSIResponseType {

    @XmlElement(name = "Message", namespace = "http://smev.gosuslugi.ru/rev110801", required = true)
    protected MessageType message;
    @XmlElement(name = "MessageData", namespace = "http://smev.gosuslugi.ru/rev110801", required = true)
    protected ExtMessageDataType messageData;

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link MessageType }
     *     
     */
    public MessageType getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageType }
     *     
     */
    public void setMessage(MessageType value) {
        this.message = value;
    }

    /**
     * Gets the value of the messageData property.
     * 
     * @return
     *     possible object is
     *     {@link ExtMessageDataType }
     *     
     */
    public ExtMessageDataType getMessageData() {
        return messageData;
    }

    /**
     * Sets the value of the messageData property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtMessageDataType }
     *     
     */
    public void setMessageData(ExtMessageDataType value) {
        this.messageData = value;
    }

}
