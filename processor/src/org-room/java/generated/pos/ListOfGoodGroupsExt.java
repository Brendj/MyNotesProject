
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
 * <p>Java class for ListOfGoodGroupsExt complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ListOfGoodGroupsExt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Goods" type="{http://soap.integra.partner.web.processor.ecafe.axetta.ru/}ListOfGoods" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="NameOfGoodsGroup" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Guid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="DeletedState" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="OrgOwner" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="CreatedDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListOfGoodGroupsExt", propOrder = {
    "goods"
})
public class ListOfGoodGroupsExt {

    @XmlElement(name = "Goods")
    protected List<ListOfGoods> goods;
    @XmlAttribute(name = "NameOfGoodsGroup")
    protected String nameOfGoodsGroup;
    @XmlAttribute(name = "Guid")
    protected String guid;
    @XmlAttribute(name = "DeletedState")
    protected Boolean deletedState;
    @XmlAttribute(name = "OrgOwner")
    protected Long orgOwner;
    @XmlAttribute(name = "CreatedDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar createdDate;

    /**
     * Gets the value of the goods property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the goods property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGoods().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ListOfGoods }
     * 
     * 
     */
    public List<ListOfGoods> getGoods() {
        if (goods == null) {
            goods = new ArrayList<ListOfGoods>();
        }
        return this.goods;
    }

    /**
     * Gets the value of the nameOfGoodsGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameOfGoodsGroup() {
        return nameOfGoodsGroup;
    }

    /**
     * Sets the value of the nameOfGoodsGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameOfGoodsGroup(String value) {
        this.nameOfGoodsGroup = value;
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

}
