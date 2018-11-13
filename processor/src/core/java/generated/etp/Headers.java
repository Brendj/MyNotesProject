
package generated.etp;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * ��������� �������
 * 
 * <p>Java class for Headers complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Headers">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FromSystemCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ToSystemCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MessageId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RelatesTo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RequestDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Headers", propOrder = {
    "fromSystemCode",
    "toSystemCode",
    "messageId",
    "relatesTo",
    "requestDateTime"
})
public class Headers {

    @XmlElement(name = "FromSystemCode")
    protected String fromSystemCode;
    @XmlElement(name = "ToSystemCode")
    protected String toSystemCode;
    @XmlElement(name = "MessageId")
    protected String messageId;
    @XmlElement(name = "RelatesTo")
    protected String relatesTo;
    @XmlElement(name = "RequestDateTime", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar requestDateTime;

    /**
     * Gets the value of the fromSystemCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromSystemCode() {
        return fromSystemCode;
    }

    /**
     * Sets the value of the fromSystemCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromSystemCode(String value) {
        this.fromSystemCode = value;
    }

    /**
     * Gets the value of the toSystemCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToSystemCode() {
        return toSystemCode;
    }

    /**
     * Sets the value of the toSystemCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToSystemCode(String value) {
        this.toSystemCode = value;
    }

    /**
     * Gets the value of the messageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Sets the value of the messageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageId(String value) {
        this.messageId = value;
    }

    /**
     * Gets the value of the relatesTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelatesTo() {
        return relatesTo;
    }

    /**
     * Sets the value of the relatesTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelatesTo(String value) {
        this.relatesTo = value;
    }

    /**
     * Gets the value of the requestDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRequestDateTime() {
        return requestDateTime;
    }

    /**
     * Sets the value of the requestDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRequestDateTime(XMLGregorianCalendar value) {
        this.requestDateTime = value;
    }

}
