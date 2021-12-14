/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import ru.axetta.ecafe.processor.core.persistence.ComplexInfo;

import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.List;

/**
 * Created by i.semenov on 13.03.2018.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PreorderComplexItemExt")
public class PreorderComplexItemExt implements Comparable {
    @XmlAttribute(name = "type")
    private String type;
    @XmlAttribute(name = "idOfComplexInfo")
    private Integer idOfComplexInfo;
    @XmlAttribute(name = "complexName")
    private String complexName;
    @XmlAttribute(name = "currentPrice")
    private Long currentPrice;
    @XmlAttribute(name = "amount")
    private int amount;
    /*@XmlAttribute(name = "selected")
    private Boolean selected;*/
    @XmlAttribute(name = "complexType")
    private Integer complexType;
    @XmlAttribute(name = "discount")
    private Boolean discount;
    @XmlAttribute(name = "state")
    private Integer state;
    @XmlAttribute(name = "isRegular")
    private Boolean isRegular;
    @XmlAttribute(name = "creatorRole")
    private Integer creatorRole;
    @XmlAttribute(name = "deleted")
    private Boolean deleted;
    @XmlElement(name = "menuItem")
    private List<PreorderMenuItemExt> menuItemExtList;

    private Integer modeVisible;
    private Date preorderDate;

    public PreorderComplexItemExt() {

    }

    public PreorderComplexItemExt(ComplexInfo ci) {
        this.setIdOfComplexInfo(ci.getIdOfComplex());
        this.setComplexName(ci.getComplexName());
        this.setCurrentPrice(ci.getCurrentPrice());
        this.setComplexType(ci.getModeOfAdd());
        this.setDiscount(ci.getModeFree() == 1 ? true : false);
    }

    public PreorderComplexItemExt(Integer idOfComplex, String complexName, Long currentPrice, Integer modeOfAdd, Integer modeFree, Integer modeVisible) {
        this.setIdOfComplexInfo(idOfComplex);
        this.setComplexName(complexName);
        this.setCurrentPrice(currentPrice);
        this.setComplexType(modeOfAdd);
        this.setDiscount(modeFree == 1 ? true : false);
        this.setModeVisible(modeVisible);
    }

    public PreorderComplexItemExt(Integer idOfComplex, String complexName, Long currentPrice, Integer modeOfAdd, Integer modeFree) {
        this.setIdOfComplexInfo(idOfComplex);
        this.setComplexName(complexName);
        this.setCurrentPrice(currentPrice);
        this.setComplexType(modeOfAdd);
        this.setDiscount(modeFree == 1 ? true : false);
    }

    public PreorderComplexItemExt(Integer idOfComplex, String complexName, Long currentPrice, Integer complexType,
            Boolean discount, Integer amount, Integer deleteState, Boolean isRegular) {
        this.setIdOfComplexInfo(idOfComplex);
        this.setComplexName(complexName);
        this.setCurrentPrice(currentPrice);
        this.setComplexType(complexType);
        this.setDiscount(discount);
        this.setAmount(amount);
        this.setState(deleteState);
        this.setIsRegular(isRegular);
    }

    public List<PreorderMenuItemExt> getMenuItemExtList() {
        return menuItemExtList;
    }

    public void setMenuItemExtList(List<PreorderMenuItemExt> menuItemExtList) {
        this.menuItemExtList = menuItemExtList;
    }

    public Integer getIdOfComplexInfo() {
        return idOfComplexInfo;
    }

    public void setIdOfComplexInfo(Integer idOfComplexInfo) {
        this.idOfComplexInfo = idOfComplexInfo;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public Long getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Long currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    /*public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }*/

    public Integer getComplexType() {
        return complexType;
    }

    public void setComplexType(Integer complexType) {
        this.complexType = complexType;
    }

    public Boolean getDiscount() {
        return discount;
    }

    public void setDiscount(Boolean discount) {
        this.discount = discount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof PreorderComplexItemExt)) {
            return 1;
        }

        PreorderComplexItemExt item = (PreorderComplexItemExt) o;
        if (this.type.equals(item.getType())) {
            return 0;
        }

        if (this.getType().equals("За счет средств бюджета города Москвы"))
            return -1;

        return 1;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Boolean getIsRegular() {
        return isRegular;
    }

    public void setIsRegular(Boolean regular) {
        isRegular = regular;
    }

    public Integer getModeVisible() {
        return modeVisible;
    }

    public void setModeVisible(Integer modeVisible) {
        this.modeVisible = modeVisible;
    }

    public Date getPreorderDate() {
        return preorderDate;
    }

    public void setPreorderDate(Date preorderDate) {
        this.preorderDate = preorderDate;
    }

    public Integer getCreatorRole() {
        return creatorRole;
    }

    public void setCreatorRole(Integer creatorRole) {
        this.creatorRole = creatorRole;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
