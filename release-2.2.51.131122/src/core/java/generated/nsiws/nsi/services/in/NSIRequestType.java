
package generated.nsiws.nsi.services.in;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import generated.nsiws.rev110801.MessageDataType;
import generated.nsiws.rev110801.MessageType;


/**
 * <p>Java class for NSIRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NSIRequestType">
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
@XmlType(name = "NSIRequestType", propOrder = {
    "message",
    "messageData"
})
public class NSIRequestType {

    @XmlElement(name = "Message", namespace = "http://smev.gosuslugi.ru/rev110801", required = true)
    protected MessageType message;
    @XmlElement(name = "MessageData", namespace = "http://smev.gosuslugi.ru/rev110801", required = true)
    protected MessageDataType messageData;

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
     *     {@link MessageDataType }
     *     
     */
    public MessageDataType getMessageData() {
        return messageData;
    }

    /**
     * Sets the value of the messageData property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageDataType }
     *     
     */
    public void setMessageData(MessageDataType value) {
        this.messageData = value;
    }

}
