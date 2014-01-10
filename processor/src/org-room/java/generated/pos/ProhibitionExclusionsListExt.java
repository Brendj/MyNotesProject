
package generated.pos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ProhibitionExclusionsListExt complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProhibitionExclusionsListExt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="Guid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="DeletedState" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="CreatedDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="GuidOfGood" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="GuidOfGoodsGroup" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProhibitionExclusionsListExt")
public class ProhibitionExclusionsListExt {

    @XmlAttribute(name = "Guid")
    protected String guid;
    @XmlAttribute(name = "DeletedState")
    protected Boolean deletedState;
    @XmlAttribute(name = "CreatedDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar createdDate;
    @XmlAttribute(name = "GuidOfGood")
    protected String guidOfGood;
    @XmlAttribute(name = "GuidOfGoodsGroup")
    protected String guidOfGoodsGroup;

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
     * Gets the value of the guidOfGood property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuidOfGood() {
        return guidOfGood;
    }

    /**
     * Sets the value of the guidOfGood property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuidOfGood(String value) {
        this.guidOfGood = value;
    }

    /**
     * Gets the value of the guidOfGoodsGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuidOfGoodsGroup() {
        return guidOfGoodsGroup;
    }

    /**
     * Sets the value of the guidOfGoodsGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuidOfGoodsGroup(String value) {
        this.guidOfGoodsGroup = value;
    }

}
