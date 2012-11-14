
package generated.msr.stoplist.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for taskStateResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="taskStateResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="taskState" type="{http://webservice.msr.com/sl/schema/}taskState" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "taskStateResponse", propOrder = {
    "taskState"
})
public class TaskStateResponse {

    @XmlElement(required = true)
    protected TaskState taskState;

    /**
     * Gets the value of the taskState property.
     * 
     * @return
     *     possible object is
     *     {@link TaskState }
     *     
     */
    public TaskState getTaskState() {
        return taskState;
    }

    /**
     * Sets the value of the taskState property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskState }
     *     
     */
    public void setTaskState(TaskState value) {
        this.taskState = value;
    }

}
