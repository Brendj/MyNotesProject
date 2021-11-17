
package generated.emp_events;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StreamTariffRule_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StreamTariffRule_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eventTypeId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="channelKind" type="{urn://subscription.api.emp.altarix.ru}ChannelKind_Type"/>
 *         &lt;element name="resourceAmount" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *         &lt;element name="periodInterval" type="{urn://subscription.api.emp.altarix.ru}PeriodInterval_Type"/>
 *         &lt;element name="periodDuration" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StreamTariffRule_Type", propOrder = {
    "eventTypeId",
    "channelKind",
    "resourceAmount",
    "periodInterval",
    "periodDuration"
})
public class StreamTariffRuleType {

    protected int eventTypeId;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected ChannelKindType channelKind;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger resourceAmount;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected PeriodIntervalType periodInterval;
    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger periodDuration;

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
     * Gets the value of the periodInterval property.
     * 
     * @return
     *     possible object is
     *     {@link PeriodIntervalType }
     *     
     */
    public PeriodIntervalType getPeriodInterval() {
        return periodInterval;
    }

    /**
     * Sets the value of the periodInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link PeriodIntervalType }
     *     
     */
    public void setPeriodInterval(PeriodIntervalType value) {
        this.periodInterval = value;
    }

    /**
     * Gets the value of the periodDuration property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPeriodDuration() {
        return periodDuration;
    }

    /**
     * Sets the value of the periodDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPeriodDuration(BigInteger value) {
        this.periodDuration = value;
    }

}
