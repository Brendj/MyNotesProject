
package generated.etp;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * ���������� � �������
 * 
 * <p>Java class for TaskType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TaskType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TaskId" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}guid"/>
 *         &lt;element name="TaskNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TaskDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="Responsible" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}Person"/>
 *         &lt;element name="Department" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}Department"/>
 *         &lt;element name="ServiceNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ServiceTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FunctionTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskType", propOrder = {
    "taskId",
    "taskNumber",
    "taskDate",
    "responsible",
    "department",
    "serviceNumber",
    "serviceTypeCode",
    "functionTypeCode"
})
public class TaskType {

    @XmlElement(name = "TaskId", required = true)
    protected String taskId;
    @XmlElement(name = "TaskNumber", required = true)
    protected String taskNumber;
    @XmlElement(name = "TaskDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar taskDate;
    @XmlElement(name = "Responsible", required = true, nillable = true)
    protected Person responsible;
    @XmlElement(name = "Department", required = true)
    protected Department department;
    @XmlElement(name = "ServiceNumber", required = true, nillable = true)
    protected String serviceNumber;
    @XmlElement(name = "ServiceTypeCode", required = true, nillable = true)
    protected String serviceTypeCode;
    @XmlElement(name = "FunctionTypeCode", required = true, nillable = true)
    protected String functionTypeCode;

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
     * Gets the value of the taskNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaskNumber() {
        return taskNumber;
    }

    /**
     * Sets the value of the taskNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaskNumber(String value) {
        this.taskNumber = value;
    }

    /**
     * Gets the value of the taskDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTaskDate() {
        return taskDate;
    }

    /**
     * Sets the value of the taskDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTaskDate(XMLGregorianCalendar value) {
        this.taskDate = value;
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
     * Gets the value of the serviceTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceTypeCode() {
        return serviceTypeCode;
    }

    /**
     * Sets the value of the serviceTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceTypeCode(String value) {
        this.serviceTypeCode = value;
    }

    /**
     * Gets the value of the functionTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFunctionTypeCode() {
        return functionTypeCode;
    }

    /**
     * Sets the value of the functionTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFunctionTypeCode(String value) {
        this.functionTypeCode = value;
    }

}
