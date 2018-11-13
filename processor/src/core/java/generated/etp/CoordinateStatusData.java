
package generated.etp;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * ������ ���������
 * 
 * <p>Java class for CoordinateStatusData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CoordinateStatusData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ResponseDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="PlanDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="Status" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}StatusType"/>
 *         &lt;element name="Responsible" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}Person"/>
 *         &lt;element name="Documents" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}ArrayOfServiceDocument" minOccurs="0"/>
 *         &lt;element name="Contacts" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}ArrayOfBaseDeclarant" minOccurs="0"/>
 *         &lt;element name="Note" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Result" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}RequestResult" minOccurs="0"/>
 *         &lt;element name="ServiceNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Reason" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}DictionaryItem" minOccurs="0"/>
 *         &lt;element name="Department" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}Department"/>
 *         &lt;element name="CreatedByDepartment" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}Department" minOccurs="0"/>
 *         &lt;element name="StatusId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoordinateStatusData", propOrder = {
    "responseDate",
    "planDate",
    "status",
    "responsible",
    "documents",
    "contacts",
    "note",
    "result",
    "serviceNumber",
    "reason",
    "department",
    "createdByDepartment",
    "statusId"
})
public class CoordinateStatusData {

    @XmlElementRef(name = "ResponseDate", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> responseDate;
    @XmlElementRef(name = "PlanDate", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> planDate;
    @XmlElement(name = "Status", required = true)
    protected StatusType status;
    @XmlElement(name = "Responsible", required = true, nillable = true)
    protected Person responsible;
    @XmlElement(name = "Documents")
    protected ArrayOfServiceDocument documents;
    @XmlElement(name = "Contacts")
    protected ArrayOfBaseDeclarant contacts;
    @XmlElement(name = "Note")
    protected String note;
    @XmlElement(name = "Result")
    protected RequestResult result;
    @XmlElement(name = "ServiceNumber", required = true)
    protected String serviceNumber;
    @XmlElement(name = "Reason")
    protected DictionaryItem reason;
    @XmlElement(name = "Department", required = true, nillable = true)
    protected Department department;
    @XmlElement(name = "CreatedByDepartment")
    protected Department createdByDepartment;
    @XmlElement(name = "StatusId")
    protected String statusId;

    /**
     * Gets the value of the responseDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getResponseDate() {
        return responseDate;
    }

    /**
     * Sets the value of the responseDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setResponseDate(JAXBElement<XMLGregorianCalendar> value) {
        this.responseDate = value;
    }

    /**
     * Gets the value of the planDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getPlanDate() {
        return planDate;
    }

    /**
     * Sets the value of the planDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setPlanDate(JAXBElement<XMLGregorianCalendar> value) {
        this.planDate = value;
    }

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
     * Gets the value of the documents property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfServiceDocument }
     *     
     */
    public ArrayOfServiceDocument getDocuments() {
        return documents;
    }

    /**
     * Sets the value of the documents property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfServiceDocument }
     *     
     */
    public void setDocuments(ArrayOfServiceDocument value) {
        this.documents = value;
    }

    /**
     * Gets the value of the contacts property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfBaseDeclarant }
     *     
     */
    public ArrayOfBaseDeclarant getContacts() {
        return contacts;
    }

    /**
     * Sets the value of the contacts property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfBaseDeclarant }
     *     
     */
    public void setContacts(ArrayOfBaseDeclarant value) {
        this.contacts = value;
    }

    /**
     * Gets the value of the note property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the value of the note property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNote(String value) {
        this.note = value;
    }

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link RequestResult }
     *     
     */
    public RequestResult getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestResult }
     *     
     */
    public void setResult(RequestResult value) {
        this.result = value;
    }

    /**
     * Gets the value of the serviceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceNumber() {
        return serviceNumber;
    }

    /**
     * Sets the value of the serviceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceNumber(String value) {
        this.serviceNumber = value;
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

    /**
     * Gets the value of the statusId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusId() {
        return statusId;
    }

    /**
     * Sets the value of the statusId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusId(String value) {
        this.statusId = value;
    }

}
