package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.10.13
 * Time: 10:20
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PosPurchase")
public class PosPurchase {

    @XmlAttribute(name = "Discount")
    private Long discount;
    @XmlAttribute(name = "SocDiscount")
    private Long socDiscount;
    @XmlAttribute(name = "IdOfOrderDetail")
    private Long idOfOrderDetail;
    @XmlAttribute(name = "Name")
    private String name;
    @XmlAttribute(name = "Qty")
    private Long qty;
    @XmlAttribute(name = "RPrice")
    private Long rPrice;
    @XmlAttribute(name = "RootMenu")
    private String rootMenu;
    @XmlAttribute(name = "MenuOutput")
    private String menuOutput;
    @XmlAttribute(name = "Type")
    private Integer type;
    @XmlAttribute(name = "MenuGroup")
    private String menuGroup;
    @XmlAttribute(name = "MenuOrigin")
    private Integer menuOrigin;
    @XmlAttribute(name = "ItemCode")
    private String itemCode;
    @XmlAttribute(name = "GuidOfGoods")
    private String guidOfGoods;
    @XmlAttribute(name = "IdOfRule")
    private Long idOfRule;
    @XmlAttribute(name = "IdOfMenu")
    private Long idOfMenu;
    @XmlAttribute(name = "Manufacturer")
    private String manufacturer;

    public Long getDiscount() {
        return discount;
    }

    public void setDiscount(Long discount) {
        this.discount = discount;
    }

    public Long getSocDiscount() {
        return socDiscount;
    }

    public void setSocDiscount(Long socDiscount) {
        this.socDiscount = socDiscount;
    }

    public Long getIdOfOrderDetail() {
        return idOfOrderDetail;
    }

    public void setIdOfOrderDetail(Long idOfOrderDetail) {
        this.idOfOrderDetail = idOfOrderDetail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public Long getrPrice() {
        return rPrice;
    }

    public void setrPrice(Long rPrice) {
        this.rPrice = rPrice;
    }

    public String getRootMenu() {
        return rootMenu;
    }

    public void setRootMenu(String rootMenu) {
        this.rootMenu = rootMenu;
    }

    public String getMenuOutput() {
        return menuOutput;
    }

    public void setMenuOutput(String menuOutput) {
        this.menuOutput = menuOutput;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMenuGroup() {
        return menuGroup;
    }

    public void setMenuGroup(String menuGroup) {
        this.menuGroup = menuGroup;
    }

    public Integer getMenuOrigin() {
        return menuOrigin;
    }

    public void setMenuOrigin(Integer menuOrigin) {
        this.menuOrigin = menuOrigin;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getGuidOfGoods() {
        return guidOfGoods;
    }

    public void setGuidOfGoods(String guidOfGoods) {
        this.guidOfGoods = guidOfGoods;
    }

    public Long getIdOfRule() {
        return idOfRule;
    }

    public void setIdOfRule(Long idOfRule) {
        this.idOfRule = idOfRule;
    }

    public Long getIdOfMenu() {
        return idOfMenu;
    }

    public void setIdOfMenu(Long idOfMenu) {
        this.idOfMenu = idOfMenu;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
}
