
package generated.spb.register;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 			suid - ID ������� (bigint64)
 * 			class - ���������� ��������� ��� ������ (����� ���������, ������, �������); ��� ����������� ���������� ����; ��� �������������� ���������� ����; ��� �������� �������� ���� �����
 *                         dob - ���� ��������
 *                     
 * 
 * <p>Java class for pupil complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="pupil">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="suid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="surname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="patronymic" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="class" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dob" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="portal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="event" type="{http://85.143.161.170:8080/webservice/food_benefits_full/wsdl}event"/>
 *         &lt;element name="benefit" type="{http://85.143.161.170:8080/webservice/food_benefits_full/wsdl}benefit" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pupil", propOrder = {
    "suid",
    "surname",
    "name",
    "patronymic",
    "clazz",
    "dob",
    "portal",
    "event",
    "benefit"
})
public class Pupil {

    @XmlElement(required = true)
    protected String suid;
    @XmlElement(required = true)
    protected String surname;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String patronymic;
    @XmlElement(name = "class", required = true)
    protected String clazz;
    @XmlElement(required = true)
    protected String dob;
    protected String portal;
    @XmlElement(required = true)
    protected Event event;
    protected Benefit benefit;

    /**
     * Gets the value of the suid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSuid() {
        return suid;
    }

    /**
     * Sets the value of the suid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSuid(String value) {
        this.suid = value;
    }

    /**
     * Gets the value of the surname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the value of the surname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSurname(String value) {
        this.surname = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the patronymic property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPatronymic() {
        return patronymic;
    }

    /**
     * Sets the value of the patronymic property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPatronymic(String value) {
        this.patronymic = value;
    }

    /**
     * Gets the value of the clazz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * Sets the value of the clazz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClazz(String value) {
        this.clazz = value;
    }

    /**
     * Gets the value of the dob property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDob() {
        return dob;
    }

    /**
     * Sets the value of the dob property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDob(String value) {
        this.dob = value;
    }

    /**
     * Gets the value of the portal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPortal() {
        return portal;
    }

    /**
     * Sets the value of the portal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPortal(String value) {
        this.portal = value;
    }

    /**
     * Gets the value of the event property.
     * 
     * @return
     *     possible object is
     *     {@link Event }
     *     
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Sets the value of the event property.
     * 
     * @param value
     *     allowed object is
     *     {@link Event }
     *     
     */
    public void setEvent(Event value) {
        this.event = value;
    }

    /**
     * Gets the value of the benefit property.
     * 
     * @return
     *     possible object is
     *     {@link Benefit }
     *     
     */
    public Benefit getBenefit() {
        return benefit;
    }

    /**
     * Sets the value of the benefit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Benefit }
     *     
     */
    public void setBenefit(Benefit value) {
        this.benefit = value;
    }

}
