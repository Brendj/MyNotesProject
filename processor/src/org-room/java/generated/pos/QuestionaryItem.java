
package generated.pos;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for QuestionaryItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QuestionaryItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Answer" type="{http://soap.integra.partner.web.processor.ecafe.axetta.ru/}AnswerItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="IdOfQuestionary" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="Question" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Description" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Status" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="ViewDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="CheckedAnswer" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestionaryItem", propOrder = {
    "answer"
})
public class QuestionaryItem {

    @XmlElement(name = "Answer")
    protected List<AnswerItem> answer;
    @XmlAttribute(name = "IdOfQuestionary")
    protected Long idOfQuestionary;
    @XmlAttribute(name = "Question", required = true)
    protected String question;
    @XmlAttribute(name = "Name", required = true)
    protected String name;
    @XmlAttribute(name = "Description")
    protected String description;
    @XmlAttribute(name = "Status")
    protected Integer status;
    @XmlAttribute(name = "Type")
    protected Integer type;
    @XmlAttribute(name = "ViewDate")
    protected XMLGregorianCalendar viewDate;
    @XmlAttribute(name = "CheckedAnswer")
    protected Long checkedAnswer;

    /**
     * Gets the value of the answer property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the answer property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnswer().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AnswerItem }
     * 
     * 
     */
    public List<AnswerItem> getAnswer() {
        if (answer == null) {
            answer = new ArrayList<AnswerItem>();
        }
        return this.answer;
    }

    /**
     * Gets the value of the idOfQuestionary property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdOfQuestionary() {
        return idOfQuestionary;
    }

    /**
     * Sets the value of the idOfQuestionary property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdOfQuestionary(Long value) {
        this.idOfQuestionary = value;
    }

    /**
     * Gets the value of the question property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Sets the value of the question property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuestion(String value) {
        this.question = value;
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
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setStatus(Integer value) {
        this.status = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setType(Integer value) {
        this.type = value;
    }

    /**
     * Gets the value of the viewDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getViewDate() {
        return viewDate;
    }

    /**
     * Sets the value of the viewDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setViewDate(XMLGregorianCalendar value) {
        this.viewDate = value;
    }

    /**
     * Gets the value of the checkedAnswer property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCheckedAnswer() {
        return checkedAnswer;
    }

    /**
     * Sets the value of the checkedAnswer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCheckedAnswer(Long value) {
        this.checkedAnswer = value;
    }

}
