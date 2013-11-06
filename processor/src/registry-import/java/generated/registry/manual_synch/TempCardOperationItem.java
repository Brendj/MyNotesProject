
package generated.registry.manual_synch;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for tempCardOperationItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tempCardOperationItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idOfClient" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="idOfOperation" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="idOfOrg" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="idOfTempCard" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="idOfVisitor" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="issueExpiryDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="operationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="operationType" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tempCardOperationItem", propOrder = {
    "idOfClient",
    "idOfOperation",
    "idOfOrg",
    "idOfTempCard",
    "idOfVisitor",
    "issueExpiryDate",
    "operationDate",
    "operationType"
})
public class TempCardOperationItem {

    protected Long idOfClient;
    protected Long idOfOperation;
    protected Long idOfOrg;
    protected Long idOfTempCard;
    protected Long idOfVisitor;
    protected XMLGregorianCalendar issueExpiryDate;
    protected XMLGregorianCalendar operationDate;
    protected Integer operationType;

    /**
     * Gets the value of the idOfClient property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdOfClient() {
        return idOfClient;
    }

    /**
     * Sets the value of the idOfClient property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdOfClient(Long value) {
        this.idOfClient = value;
    }

    /**
     * Gets the value of the idOfOperation property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdOfOperation() {
        return idOfOperation;
    }

    /**
     * Sets the value of the idOfOperation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdOfOperation(Long value) {
        this.idOfOperation = value;
    }

    /**
     * Gets the value of the idOfOrg property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdOfOrg() {
        return idOfOrg;
    }

    /**
     * Sets the value of the idOfOrg property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdOfOrg(Long value) {
        this.idOfOrg = value;
    }

    /**
     * Gets the value of the idOfTempCard property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdOfTempCard() {
        return idOfTempCard;
    }

    /**
     * Sets the value of the idOfTempCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdOfTempCard(Long value) {
        this.idOfTempCard = value;
    }

    /**
     * Gets the value of the idOfVisitor property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdOfVisitor() {
        return idOfVisitor;
    }

    /**
     * Sets the value of the idOfVisitor property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdOfVisitor(Long value) {
        this.idOfVisitor = value;
    }

    /**
     * Gets the value of the issueExpiryDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getIssueExpiryDate() {
        return issueExpiryDate;
    }

    /**
     * Sets the value of the issueExpiryDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setIssueExpiryDate(XMLGregorianCalendar value) {
        this.issueExpiryDate = value;
    }

    /**
     * Gets the value of the operationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOperationDate() {
        return operationDate;
    }

    /**
     * Sets the value of the operationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOperationDate(XMLGregorianCalendar value) {
        this.operationDate = value;
    }

    /**
     * Gets the value of the operationType property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOperationType() {
        return operationType;
    }

    /**
     * Sets the value of the operationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOperationType(Integer value) {
        this.operationType = value;
    }

}
