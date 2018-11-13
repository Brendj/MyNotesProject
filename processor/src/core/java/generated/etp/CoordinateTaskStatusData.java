
package generated.etp;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * ������ �������
 * 
 * <p>Java class for CoordinateTaskStatusData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CoordinateTaskStatusData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TaskId" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}guid"/>
 *         &lt;element name="Status" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}StatusType"/>
 *         &lt;element name="StatusNote" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Responsible" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}Person"/>
 *         &lt;element name="Department" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}Department"/>
 *         &lt;element name="PlanDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="AsyncTicket" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Result" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}TaskResult"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoordinateTaskStatusData", propOrder = {
    "taskId",
    "status",
    "statusNote",
    "responsible",
    "department",
    "planDate",
    "asyncTicket",
    "result"
})
public class CoordinateTaskStatusData {

    @XmlElement(name = "TaskId", required = true)
    protected String taskId;
    @XmlElement(name = "Status", required = true)
    protected StatusType status;
    @XmlElement(name = "StatusNote", required = true, nillable = true)
    protected String statusNote;
    @XmlElement(name = "Responsible", required = true, nillable = true)
    protected Person responsible;
    @XmlElement(name = "Department", required = true)
    protected Department department;
    @XmlElement(name = "PlanDate", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar planDate;
    @XmlElement(name = "AsyncTicket", required = true, nillable = true)
    protected String asyncTicket;
    @XmlElement(name = "Result", required = true, nillable = true)
    protected TaskResult result;

    /**
     * Gets the value of the taskId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Sets the value of the taskId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaskId(String value) {
        this.taskId = value;
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
     * Gets the value of the statusNote property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusNote() {
        return statusNote;
    }

    /**
     * Sets the value of the statusNote property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusNote(String value) {
        this.statusNote = value;
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
     * Gets the value of the planDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPlanDate() {
        return planDate;
    }

    /**
     * Sets the value of the planDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPlanDate(XMLGregorianCalendar value) {
        this.planDate = value;
    }

    /**
     * Gets the value of the asyncTicket property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAsyncTicket() {
        return asyncTicket;
    }

    /**
     * Sets the value of the asyncTicket property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAsyncTicket(String value) {
        this.asyncTicket = value;
    }

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link TaskResult }
     *     
     */
    public TaskResult getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskResult }
     *     
     */
    public void setResult(TaskResult value) {
        this.result = value;
    }

}
