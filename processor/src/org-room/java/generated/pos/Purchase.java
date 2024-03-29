
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
 * <p>Java class for Purchase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Purchase">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="E" type="{http://soap.integra.partner.web.processor.ecafe.axetta.ru/}PurchaseElement" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Time" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="Sum" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="Discount" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="Donation" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="ByCash" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="ByCard" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="IdOfCard" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Purchase", propOrder = {
    "e"
})
public class Purchase {

    @XmlElement(name = "E")
    protected List<PurchaseElement> e;
    @XmlAttribute(name = "Time")
    protected XMLGregorianCalendar time;
    @XmlAttribute(name = "Sum")
    protected Long sum;
    @XmlAttribute(name = "Discount")
    protected Long discount;
    @XmlAttribute(name = "Donation")
    protected Long donation;
    @XmlAttribute(name = "ByCash")
    protected Long byCash;
    @XmlAttribute(name = "ByCard")
    protected Long byCard;
    @XmlAttribute(name = "IdOfCard")
    protected Long idOfCard;

    /**
     * Gets the value of the e property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the e property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PurchaseElement }
     * 
     * 
     */
    public List<PurchaseElement> getE() {
        if (e == null) {
            e = new ArrayList<PurchaseElement>();
        }
        return this.e;
    }

    /**
     * Gets the value of the time property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTime() {
        return time;
    }

    /**
     * Sets the value of the time property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTime(XMLGregorianCalendar value) {
        this.time = value;
    }

    /**
     * Gets the value of the sum property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSum() {
        return sum;
    }

    /**
     * Sets the value of the sum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSum(Long value) {
        this.sum = value;
    }

    /**
     * Gets the value of the discount property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDiscount() {
        return discount;
    }

    /**
     * Sets the value of the discount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDiscount(Long value) {
        this.discount = value;
    }

    /**
     * Gets the value of the donation property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDonation() {
        return donation;
    }

    /**
     * Sets the value of the donation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDonation(Long value) {
        this.donation = value;
    }

    /**
     * Gets the value of the byCash property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getByCash() {
        return byCash;
    }

    /**
     * Sets the value of the byCash property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setByCash(Long value) {
        this.byCash = value;
    }

    /**
     * Gets the value of the byCard property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getByCard() {
        return byCard;
    }

    /**
     * Sets the value of the byCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setByCard(Long value) {
        this.byCard = value;
    }

    /**
     * Gets the value of the idOfCard property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdOfCard() {
        return idOfCard;
    }

    /**
     * Sets the value of the idOfCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdOfCard(Long value) {
        this.idOfCard = value;
    }

}
