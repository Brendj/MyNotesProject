
package generated.pos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ListOfComplaintOrdersExt complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ListOfComplaintOrdersExt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="IdOfOrderDetail" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="MenuDetailName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="DateOfOrder" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
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
@XmlType(name = "ListOfComplaintOrdersExt")
public class ListOfComplaintOrdersExt {

    @XmlAttribute(name = "IdOfOrderDetail")
    protected Long idOfOrderDetail;
    @XmlAttribute(name = "MenuDetailName")
    protected String menuDetailName;
    @XmlAttribute(name = "DateOfOrder")
    @XmlSchemaType(name = "anySimpleType")
    protected String dateOfOrder;
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
     * Gets the value of the idOfOrderDetail property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdOfOrderDetail() {
        return idOfOrderDetail;
    }

    /**
     * Sets the value of the idOfOrderDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdOfOrderDetail(Long value) {
        this.idOfOrderDetail = value;
    }

    /**
     * Gets the value of the menuDetailName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMenuDetailName() {
        return menuDetailName;
    }

    /**
     * Sets the value of the menuDetailName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMenuDetailName(String value) {
        this.menuDetailName = value;
    }

    /**
     * Gets the value of the dateOfOrder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateOfOrder() {
        return dateOfOrder;
    }

    /**
     * Sets the value of the dateOfOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateOfOrder(String value) {
        this.dateOfOrder = value;
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
