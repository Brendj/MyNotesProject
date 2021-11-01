
package generated.emp_events;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for StreamSetting_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StreamSetting_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="kind" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sendStartTime" type="{http://www.w3.org/2001/XMLSchema}time" minOccurs="0"/>
 *         &lt;element name="sendStopTime" type="{http://www.w3.org/2001/XMLSchema}time" minOccurs="0"/>
 *         &lt;element name="sendExcludeDays" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StreamSetting_Type", propOrder = {
    "kind",
    "sendStartTime",
    "sendStopTime",
    "sendExcludeDays"
})
public class StreamSettingType {

    @XmlElement(required = true)
    protected String kind;
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar sendStartTime;
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar sendStopTime;
    protected Integer sendExcludeDays;

    /**
     * Gets the value of the kind property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKind() {
        return kind;
    }

    /**
     * Sets the value of the kind property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKind(String value) {
        this.kind = value;
    }

    /**
     * Gets the value of the sendStartTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSendStartTime() {
        return sendStartTime;
    }

    /**
     * Sets the value of the sendStartTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSendStartTime(XMLGregorianCalendar value) {
        this.sendStartTime = value;
    }

    /**
     * Gets the value of the sendStopTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSendStopTime() {
        return sendStopTime;
    }

    /**
     * Sets the value of the sendStopTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSendStopTime(XMLGregorianCalendar value) {
        this.sendStopTime = value;
    }

    /**
     * Gets the value of the sendExcludeDays property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSendExcludeDays() {
        return sendExcludeDays;
    }

    /**
     * Sets the value of the sendExcludeDays property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSendExcludeDays(Integer value) {
        this.sendExcludeDays = value;
    }

}
