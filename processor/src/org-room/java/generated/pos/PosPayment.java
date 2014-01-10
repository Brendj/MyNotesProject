
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
 * <p>Java class for PosPayment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PosPayment">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Purchases" type="{http://soap.integra.partner.web.processor.ecafe.axetta.ru/}PosPurchase" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="CardNo" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="Time" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="OrderDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="SocDiscount" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="TrdDiscount" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="ConfirmerId" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="Grant" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="IdOfClient" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="IdOfOrder" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="IdOfCashier" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="SumByCard" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="SumByCash" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="RSum" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="IdOfPOS" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="Comments" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="OrderType" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PosPayment", propOrder = {
    "purchases"
})
public class PosPayment {

    @XmlElement(name = "Purchases")
    protected List<PosPurchase> purchases;
    @XmlAttribute(name = "CardNo")
    protected Long cardNo;
    @XmlAttribute(name = "Time")
    protected XMLGregorianCalendar time;
    @XmlAttribute(name = "OrderDate")
    protected XMLGregorianCalendar orderDate;
    @XmlAttribute(name = "SocDiscount")
    protected Long socDiscount;
    @XmlAttribute(name = "TrdDiscount")
    protected Long trdDiscount;
    @XmlAttribute(name = "ConfirmerId")
    protected Long confirmerId;
    @XmlAttribute(name = "Grant")
    protected Long grant;
    @XmlAttribute(name = "IdOfClient")
    protected Long idOfClient;
    @XmlAttribute(name = "IdOfOrder")
    protected Long idOfOrder;
    @XmlAttribute(name = "IdOfCashier")
    protected Long idOfCashier;
    @XmlAttribute(name = "SumByCard")
    protected Long sumByCard;
    @XmlAttribute(name = "SumByCash")
    protected Long sumByCash;
    @XmlAttribute(name = "RSum")
    protected Long rSum;
    @XmlAttribute(name = "IdOfPOS")
    protected Long idOfPOS;
    @XmlAttribute(name = "Comments")
    protected String comments;
    @XmlAttribute(name = "OrderType")
    protected Integer orderType;

    /**
     * Gets the value of the purchases property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the purchases property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPurchases().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PosPurchase }
     * 
     * 
     */
    public List<PosPurchase> getPurchases() {
        if (purchases == null) {
            purchases = new ArrayList<PosPurchase>();
        }
        return this.purchases;
    }

    /**
     * Gets the value of the cardNo property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCardNo() {
        return cardNo;
    }

    /**
     * Sets the value of the cardNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCardNo(Long value) {
        this.cardNo = value;
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
     * Gets the value of the orderDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOrderDate() {
        return orderDate;
    }

    /**
     * Sets the value of the orderDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOrderDate(XMLGregorianCalendar value) {
        this.orderDate = value;
    }

    /**
     * Gets the value of the socDiscount property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSocDiscount() {
        return socDiscount;
    }

    /**
     * Sets the value of the socDiscount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSocDiscount(Long value) {
        this.socDiscount = value;
    }

    /**
     * Gets the value of the trdDiscount property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTrdDiscount() {
        return trdDiscount;
    }

    /**
     * Sets the value of the trdDiscount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTrdDiscount(Long value) {
        this.trdDiscount = value;
    }

    /**
     * Gets the value of the confirmerId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getConfirmerId() {
        return confirmerId;
    }

    /**
     * Sets the value of the confirmerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setConfirmerId(Long value) {
        this.confirmerId = value;
    }

    /**
     * Gets the value of the grant property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getGrant() {
        return grant;
    }

    /**
     * Sets the value of the grant property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setGrant(Long value) {
        this.grant = value;
    }

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
     * Gets the value of the idOfOrder property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdOfOrder() {
        return idOfOrder;
    }

    /**
     * Sets the value of the idOfOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdOfOrder(Long value) {
        this.idOfOrder = value;
    }

    /**
     * Gets the value of the idOfCashier property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdOfCashier() {
        return idOfCashier;
    }

    /**
     * Sets the value of the idOfCashier property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdOfCashier(Long value) {
        this.idOfCashier = value;
    }

    /**
     * Gets the value of the sumByCard property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSumByCard() {
        return sumByCard;
    }

    /**
     * Sets the value of the sumByCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSumByCard(Long value) {
        this.sumByCard = value;
    }

    /**
     * Gets the value of the sumByCash property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSumByCash() {
        return sumByCash;
    }

    /**
     * Sets the value of the sumByCash property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSumByCash(Long value) {
        this.sumByCash = value;
    }

    /**
     * Gets the value of the rSum property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getRSum() {
        return rSum;
    }

    /**
     * Sets the value of the rSum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setRSum(Long value) {
        this.rSum = value;
    }

    /**
     * Gets the value of the idOfPOS property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdOfPOS() {
        return idOfPOS;
    }

    /**
     * Sets the value of the idOfPOS property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdOfPOS(Long value) {
        this.idOfPOS = value;
    }

    /**
     * Gets the value of the comments property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComments() {
        return comments;
    }

    /**
     * Sets the value of the comments property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComments(String value) {
        this.comments = value;
    }

    /**
     * Gets the value of the orderType property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOrderType() {
        return orderType;
    }

    /**
     * Sets the value of the orderType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOrderType(Integer value) {
        this.orderType = value;
    }

}
