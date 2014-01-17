
package generated.pos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PosPurchase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PosPurchase">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="Discount" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="SocDiscount" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="IdOfOrderDetail" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Qty" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="RPrice" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="RootMenu" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="MenuOutput" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="MenuGroup" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="MenuOrigin" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="ItemCode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="GuidOfGoods" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="IdOfRule" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PosPurchase")
public class PosPurchase {

    @XmlAttribute(name = "Discount")
    protected Long discount;
    @XmlAttribute(name = "SocDiscount")
    protected Long socDiscount;
    @XmlAttribute(name = "IdOfOrderDetail")
    protected Long idOfOrderDetail;
    @XmlAttribute(name = "Name")
    protected String name;
    @XmlAttribute(name = "Qty")
    protected Long qty;
    @XmlAttribute(name = "RPrice")
    protected Long rPrice;
    @XmlAttribute(name = "RootMenu")
    protected String rootMenu;
    @XmlAttribute(name = "MenuOutput")
    protected String menuOutput;
    @XmlAttribute(name = "Type")
    protected Integer type;
    @XmlAttribute(name = "MenuGroup")
    protected String menuGroup;
    @XmlAttribute(name = "MenuOrigin")
    protected Integer menuOrigin;
    @XmlAttribute(name = "ItemCode")
    protected String itemCode;
    @XmlAttribute(name = "GuidOfGoods")
    protected String guidOfGoods;
    @XmlAttribute(name = "IdOfRule")
    protected Long idOfRule;

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
     * Gets the value of the qty property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getQty() {
        return qty;
    }

    /**
     * Sets the value of the qty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setQty(Long value) {
        this.qty = value;
    }

    /**
     * Gets the value of the rPrice property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getRPrice() {
        return rPrice;
    }

    /**
     * Sets the value of the rPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setRPrice(Long value) {
        this.rPrice = value;
    }

    /**
     * Gets the value of the rootMenu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRootMenu() {
        return rootMenu;
    }

    /**
     * Sets the value of the rootMenu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRootMenu(String value) {
        this.rootMenu = value;
    }

    /**
     * Gets the value of the menuOutput property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMenuOutput() {
        return menuOutput;
    }

    /**
     * Sets the value of the menuOutput property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMenuOutput(String value) {
        this.menuOutput = value;
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
     * Gets the value of the menuGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMenuGroup() {
        return menuGroup;
    }

    /**
     * Sets the value of the menuGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMenuGroup(String value) {
        this.menuGroup = value;
    }

    /**
     * Gets the value of the menuOrigin property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMenuOrigin() {
        return menuOrigin;
    }

    /**
     * Sets the value of the menuOrigin property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMenuOrigin(Integer value) {
        this.menuOrigin = value;
    }

    /**
     * Gets the value of the itemCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemCode() {
        return itemCode;
    }

    /**
     * Sets the value of the itemCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemCode(String value) {
        this.itemCode = value;
    }

    /**
     * Gets the value of the guidOfGoods property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuidOfGoods() {
        return guidOfGoods;
    }

    /**
     * Sets the value of the guidOfGoods property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuidOfGoods(String value) {
        this.guidOfGoods = value;
    }

    /**
     * Gets the value of the idOfRule property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdOfRule() {
        return idOfRule;
    }

    /**
     * Sets the value of the idOfRule property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdOfRule(Long value) {
        this.idOfRule = value;
    }

}
