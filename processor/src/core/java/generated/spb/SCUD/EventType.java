
package generated.spb.SCUD;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for eventType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eventType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="organizationUid" type="{http://petersburgedu.ru/service/webservice/scudTypes}guidType"/>
 *         &lt;element name="systemUid" type="{http://petersburgedu.ru/service/webservice/scudTypes}notNullStringType"/>
 *         &lt;element name="readerUid" type="{http://petersburgedu.ru/service/webservice/scudTypes}notNullStringType"/>
 *         &lt;element name="studentUid" type="{http://petersburgedu.ru/service/webservice/scudTypes}notNullStringType"/>
 *         &lt;element name="cardUid" type="{http://petersburgedu.ru/service/webservice/scudTypes}notNullStringType"/>
 *         &lt;element name="directionType" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://petersburgedu.ru/service/webservice/scudTypes}notNullStringType">
 *               &lt;enumeration value="input"/>
 *               &lt;enumeration value="output"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="eventDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eventType", namespace = "http://petersburgedu.ru/service/webservice/scudTypes", propOrder = {
    "organizationUid",
    "systemUid",
    "readerUid",
    "studentUid",
    "cardUid",
    "directionType",
    "eventDate"
})
public class EventType {

    @XmlElement(required = true)
    protected String organizationUid;
    @XmlElement(required = true)
    protected String systemUid;
    @XmlElement(required = true)
    protected String readerUid;
    @XmlElement(required = true)
    protected String studentUid;
    @XmlElement(required = true)
    protected String cardUid;
    protected String directionType;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar eventDate;

    /**
     * Gets the value of the organizationUid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganizationUid() {
        return organizationUid;
    }

    /**
     * Sets the value of the organizationUid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganizationUid(String value) {
        this.organizationUid = value;
    }

    /**
     * Gets the value of the systemUid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSystemUid() {
        return systemUid;
    }

    /**
     * Sets the value of the systemUid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSystemUid(String value) {
        this.systemUid = value;
    }

    /**
     * Gets the value of the readerUid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReaderUid() {
        return readerUid;
    }

    /**
     * Sets the value of the readerUid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReaderUid(String value) {
        this.readerUid = value;
    }

    /**
     * Gets the value of the studentUid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStudentUid() {
        return studentUid;
    }

    /**
     * Sets the value of the studentUid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStudentUid(String value) {
        this.studentUid = value;
    }

    /**
     * Gets the value of the cardUid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardUid() {
        return cardUid;
    }

    /**
     * Sets the value of the cardUid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardUid(String value) {
        this.cardUid = value;
    }

    /**
     * Gets the value of the directionType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDirectionType() {
        return directionType;
    }

    /**
     * Sets the value of the directionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDirectionType(String value) {
        this.directionType = value;
    }

    /**
     * Gets the value of the eventDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEventDate() {
        return eventDate;
    }

    /**
     * Sets the value of the eventDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEventDate(XMLGregorianCalendar value) {
        this.eventDate = value;
    }

}
