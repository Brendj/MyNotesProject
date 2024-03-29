
package generated.opc.ru.msk.schemas.uec.transaction.v1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Учетная часть транзакции
 * 
 * <p>Java class for AccountingDescriptionItemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AccountingDescriptionItemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eventStart" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="eventEnd" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="resourceCode" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="1"/>
 *               &lt;maxLength value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="resourceCodeName" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="255"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="resourceCount" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="financialDescription" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="financialDescriptionItem" type="{http://schemas.msk.ru/uec/transaction/v1}FinancialDescriptionItemType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AccountingDescriptionItemType", propOrder = {
    "eventStart",
    "eventEnd",
    "resourceCode",
    "resourceCodeName",
    "resourceCount",
    "financialDescription"
})
public class AccountingDescriptionItemType {

    protected XMLGregorianCalendar eventStart;
    protected XMLGregorianCalendar eventEnd;
    protected String resourceCode;
    protected String resourceCodeName;
    protected Double resourceCount;
    protected AccountingDescriptionItemType.FinancialDescription financialDescription;

    /**
     * Gets the value of the eventStart property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEventStart() {
        return eventStart;
    }

    /**
     * Sets the value of the eventStart property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEventStart(XMLGregorianCalendar value) {
        this.eventStart = value;
    }

    /**
     * Gets the value of the eventEnd property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEventEnd() {
        return eventEnd;
    }

    /**
     * Sets the value of the eventEnd property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEventEnd(XMLGregorianCalendar value) {
        this.eventEnd = value;
    }

    /**
     * Gets the value of the resourceCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResourceCode() {
        return resourceCode;
    }

    /**
     * Sets the value of the resourceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResourceCode(String value) {
        this.resourceCode = value;
    }

    /**
     * Gets the value of the resourceCodeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResourceCodeName() {
        return resourceCodeName;
    }

    /**
     * Sets the value of the resourceCodeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResourceCodeName(String value) {
        this.resourceCodeName = value;
    }

    /**
     * Gets the value of the resourceCount property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getResourceCount() {
        return resourceCount;
    }

    /**
     * Sets the value of the resourceCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setResourceCount(Double value) {
        this.resourceCount = value;
    }

    /**
     * Gets the value of the financialDescription property.
     * 
     * @return
     *     possible object is
     *     {@link AccountingDescriptionItemType.FinancialDescription }
     *     
     */
    public AccountingDescriptionItemType.FinancialDescription getFinancialDescription() {
        return financialDescription;
    }

    /**
     * Sets the value of the financialDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountingDescriptionItemType.FinancialDescription }
     *     
     */
    public void setFinancialDescription(AccountingDescriptionItemType.FinancialDescription value) {
        this.financialDescription = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="financialDescriptionItem" type="{http://schemas.msk.ru/uec/transaction/v1}FinancialDescriptionItemType" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "financialDescriptionItem"
    })
    public static class FinancialDescription {

        @XmlElement(required = true)
        protected List<FinancialDescriptionItemType> financialDescriptionItem;

        /**
         * Gets the value of the financialDescriptionItem property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the financialDescriptionItem property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getFinancialDescriptionItem().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link FinancialDescriptionItemType }
         * 
         * 
         */
        public List<FinancialDescriptionItemType> getFinancialDescriptionItem() {
            if (financialDescriptionItem == null) {
                financialDescriptionItem = new ArrayList<FinancialDescriptionItemType>();
            }
            return this.financialDescriptionItem;
        }

    }

}
