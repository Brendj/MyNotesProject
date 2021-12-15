
package generated.emp_events;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for CitizenPaymentResource_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CitizenPaymentResource_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eventTypeId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="channelKind" type="{urn://subscription.api.emp.altarix.ru}ChannelKind_Type"/>
 *         &lt;element name="resourceAmount" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *         &lt;element name="periodStartTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="periodFinishTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CitizenPaymentResource_Type", propOrder = {
    "eventTypeId",
    "channelKind",
    "resourceAmount",
    "periodStartTime",
    "periodFinishTime"
})
public class CitizenPaymentResourceType {

    protected int eventTypeId;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected ChannelKindType channelKind;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger resourceAmount;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar periodStartTime;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar periodFinishTime;

    /**
     * Gets the value of the eventTypeId property.
     * 
     */
    public int getEventTypeId() {
        return eventTypeId;
    }

    /**
     * Sets the value of the eventTypeId property.
     * 
     */
    public void setEventTypeId(int value) {
        this.eventTypeId = value;
    }

    /**
     * Gets the value of the channelKind property.
     * 
     * @return
     *     possible object is
     *     {@link ChannelKindType }
     *     
     */
    public ChannelKindType getChannelKind() {
        return channelKind;
    }

    /**
     * Sets the value of the channelKind property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChannelKindType }
     *     
     */
    public void setChannelKind(ChannelKindType value) {
        this.channelKind = value;
    }

    /**
     * Gets the value of the resourceAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getResourceAmount() {
        return resourceAmount;
    }

    /**
     * Sets the value of the resourceAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setResourceAmount(BigInteger value) {
        this.resourceAmount = value;
    }

    /**
     * Gets the value of the periodStartTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPeriodStartTime() {
        return periodStartTime;
    }

    /**
     * Sets the value of the periodStartTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPeriodStartTime(XMLGregorianCalendar value) {
        this.periodStartTime = value;
    }

    /**
     * Gets the value of the periodFinishTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPeriodFinishTime() {
        return periodFinishTime;
    }

    /**
     * Sets the value of the periodFinishTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPeriodFinishTime(XMLGregorianCalendar value) {
        this.periodFinishTime = value;
    }

}
