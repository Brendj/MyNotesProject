
package generated.etp;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * ������ ���������
 * 
 * <p>Java class for RequestStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Status" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}StatusType"/>
 *         &lt;element name="ReasonText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ValidityPeriod" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="Responsible" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}Person"/>
 *         &lt;element name="Department" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}Department"/>
 *         &lt;element name="Reason" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}DictionaryItem" minOccurs="0"/>
 *         &lt;element name="CreatedByDepartment" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}Department" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestStatus", propOrder = {
    "status",
    "reasonText",
    "validityPeriod",
    "responsible",
    "department",
    "reason",
    "createdByDepartment"
})
public class RequestStatus {

    @XmlElement(name = "Status", required = true)
    protected StatusType status;
    @XmlElement(name = "ReasonText")
    protected String reasonText;
    @XmlElementRef(name = "ValidityPeriod", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> validityPeriod;
    @XmlElement(name = "Responsible", required = true, nillable = true)
    protected Person responsible;
    @XmlElement(name = "Department", required = true, nillable = true)
    protected Department department;
    @XmlElement(name = "Reason")
    protected DictionaryItem reason;
    @XmlElement(name = "CreatedByDepartment")
    protected Department createdByDepartment;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link StatusType }
     *     
     */
    public StatusType getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusType }
     *     
     */
    public void setStatus(StatusType value) {
        this.status = value;
    }

    /**
     * Gets the value of the reasonText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReasonText() {
        return reasonText;
    }

    /**
     * Sets the value of the reasonText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReasonText(String value) {
        this.reasonText = value;
    }

    /**
     * Gets the value of the validityPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getValidityPeriod() {
        return validityPeriod;
    }

    /**
     * Sets the value of the validityPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setValidityPeriod(JAXBElement<XMLGregorianCalendar> value) {
        this.validityPeriod = value;
    }

    /**
     * Gets the value of the responsible property.
     * 
     * @return
     *     possible object is
     *     {@link Person }
     *     
     */
    public Person getResponsible() {
        return responsible;
    }

    /**
     * Sets the value of the responsible property.
     * 
     * @param value
     *     allowed object is
     *     {@link Person }
     *     
     */
    public void setResponsible(Person value) {
        this.responsible = value;
    }

    /**
     * Gets the value of the department property.
     * 
     * @return
     *     possible object is
     *     {@link Department }
     *     
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * Sets the value of the department property.
     * 
     * @param value
     *     allowed object is
     *     {@link Department }
     *     
     */
    public void setDepartment(Department value) {
        this.department = value;
    }

    /**
     * Gets the value of the reason property.
     * 
     * @return
     *     possible object is
     *     {@link DictionaryItem }
     *     
     */
    public DictionaryItem getReason() {
        return reason;
    }

    /**
     * Sets the value of the reason property.
     * 
     * @param value
     *     allowed object is
     *     {@link DictionaryItem }
     *     
     */
    public void setReason(DictionaryItem value) {
        this.reason = value;
    }

    /**
     * Gets the value of the createdByDepartment property.
     * 
     * @return
     *     possible object is
     *     {@link Department }
     *     
     */
    public Department getCreatedByDepartment() {
        return createdByDepartment;
    }

    /**
     * Sets the value of the createdByDepartment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Department }
     *     
     */
    public void setCreatedByDepartment(Department value) {
        this.createdByDepartment = value;
    }

}
