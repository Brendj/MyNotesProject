
package generated.pos;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ListOfComplaintIterationsExt complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ListOfComplaintIterationsExt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProblemDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Orders" type="{http://soap.integra.partner.web.processor.ecafe.axetta.ru/}ListOfComplaintOrders" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Causes" type="{http://soap.integra.partner.web.processor.ecafe.axetta.ru/}ListOfComplaintCauses" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="IterationNumber" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="GoodComplaintIterationStatus" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="Conclusion" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Guid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="DeletedState" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="CreatedDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="OrgOwner" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListOfComplaintIterationsExt", propOrder = {
    "problemDescription",
    "orders",
    "causes"
})
public class ListOfComplaintIterationsExt {

    @XmlElement(name = "ProblemDescription")
    protected String problemDescription;
    @XmlElement(name = "Orders")
    protected List<ListOfComplaintOrders> orders;
    @XmlElement(name = "Causes")
    protected List<ListOfComplaintCauses> causes;
    @XmlAttribute(name = "IterationNumber")
    protected Integer iterationNumber;
    @XmlAttribute(name = "GoodComplaintIterationStatus")
    protected Integer goodComplaintIterationStatus;
    @XmlAttribute(name = "Conclusion")
    protected String conclusion;
    @XmlAttribute(name = "Guid")
    protected String guid;
    @XmlAttribute(name = "DeletedState")
    protected Boolean deletedState;
    @XmlAttribute(name = "CreatedDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar createdDate;
    @XmlAttribute(name = "OrgOwner")
    protected Long orgOwner;

    /**
     * Gets the value of the problemDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProblemDescription() {
        return problemDescription;
    }

    /**
     * Sets the value of the problemDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProblemDescription(String value) {
        this.problemDescription = value;
    }

    /**
     * Gets the value of the orders property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the orders property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrders().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ListOfComplaintOrders }
     * 
     * 
     */
    public List<ListOfComplaintOrders> getOrders() {
        if (orders == null) {
            orders = new ArrayList<ListOfComplaintOrders>();
        }
        return this.orders;
    }

    /**
     * Gets the value of the causes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the causes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCauses().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ListOfComplaintCauses }
     * 
     * 
     */
    public List<ListOfComplaintCauses> getCauses() {
        if (causes == null) {
            causes = new ArrayList<ListOfComplaintCauses>();
        }
        return this.causes;
    }

    /**
     * Gets the value of the iterationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIterationNumber() {
        return iterationNumber;
    }

    /**
     * Sets the value of the iterationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIterationNumber(Integer value) {
        this.iterationNumber = value;
    }

    /**
     * Gets the value of the goodComplaintIterationStatus property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getGoodComplaintIterationStatus() {
        return goodComplaintIterationStatus;
    }

    /**
     * Sets the value of the goodComplaintIterationStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setGoodComplaintIterationStatus(Integer value) {
        this.goodComplaintIterationStatus = value;
    }

    /**
     * Gets the value of the conclusion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConclusion() {
        return conclusion;
    }

    /**
     * Sets the value of the conclusion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConclusion(String value) {
        this.conclusion = value;
    }

    /**
     * Gets the value of the guid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuid() {
        return guid;
    }

    /**
     * Sets the value of the guid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuid(String value) {
        this.guid = value;
    }

    /**
     * Gets the value of the deletedState property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDeletedState() {
        return deletedState;
    }

    /**
     * Sets the value of the deletedState property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDeletedState(Boolean value) {
        this.deletedState = value;
    }

    /**
     * Gets the value of the createdDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the value of the createdDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreatedDate(XMLGregorianCalendar value) {
        this.createdDate = value;
    }

    /**
     * Gets the value of the orgOwner property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getOrgOwner() {
        return orgOwner;
    }

    /**
     * Sets the value of the orgOwner property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setOrgOwner(Long value) {
        this.orgOwner = value;
    }

}
