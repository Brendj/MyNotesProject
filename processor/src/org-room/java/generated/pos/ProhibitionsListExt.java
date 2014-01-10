
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
 * <p>Java class for ProhibitionsListExt complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProhibitionsListExt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Exclusions" type="{http://soap.integra.partner.web.processor.ecafe.axetta.ru/}ProhibitionExclusionsList" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Guid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="DeletedState" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="CreatedDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="ContractId" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="GuidOfProducts" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="GuidOfProductGroups" type="{http://www.w3.org/2001/XMLSchema}string" />
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
@XmlType(name = "ProhibitionsListExt", propOrder = {
    "exclusions"
})
public class ProhibitionsListExt {

    @XmlElement(name = "Exclusions")
    protected List<ProhibitionExclusionsList> exclusions;
    @XmlAttribute(name = "Guid")
    protected String guid;
    @XmlAttribute(name = "DeletedState")
    protected Boolean deletedState;
    @XmlAttribute(name = "CreatedDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar createdDate;
    @XmlAttribute(name = "ContractId")
    protected Long contractId;
    @XmlAttribute(name = "GuidOfProducts")
    protected String guidOfProducts;
    @XmlAttribute(name = "GuidOfProductGroups")
    protected String guidOfProductGroups;
    @XmlAttribute(name = "GuidOfGood")
    protected String guidOfGood;
    @XmlAttribute(name = "GuidOfGoodsGroup")
    protected String guidOfGoodsGroup;

    /**
     * Gets the value of the exclusions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the exclusions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExclusions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProhibitionExclusionsList }
     * 
     * 
     */
    public List<ProhibitionExclusionsList> getExclusions() {
        if (exclusions == null) {
            exclusions = new ArrayList<ProhibitionExclusionsList>();
        }
        return this.exclusions;
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
     * Gets the value of the contractId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getContractId() {
        return contractId;
    }

    /**
     * Sets the value of the contractId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setContractId(Long value) {
        this.contractId = value;
    }

    /**
     * Gets the value of the guidOfProducts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuidOfProducts() {
        return guidOfProducts;
    }

    /**
     * Sets the value of the guidOfProducts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuidOfProducts(String value) {
        this.guidOfProducts = value;
    }

    /**
     * Gets the value of the guidOfProductGroups property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuidOfProductGroups() {
        return guidOfProductGroups;
    }

    /**
     * Sets the value of the guidOfProductGroups property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuidOfProductGroups(String value) {
        this.guidOfProductGroups = value;
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
