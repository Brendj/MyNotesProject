
/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import ru.axetta.ecafe.processor.core.persistence.MenuDetail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PreorderMenuItemExt")
public class PreorderMenuItemExt {
    @XmlAttribute(name = "group")
    private String group;
    @XmlAttribute(name = "name")
    private String name;
    @XmlAttribute(name = "fullName")
    private String fullName;
    @XmlAttribute(name = "price")
    private Long price;
    @XmlAttribute(name = "calories")
    private Double calories;
    @XmlAttribute(name = "output")
    private String output;
    @XmlAttribute(name = "availableNow")
    private Integer availableNow;
    @XmlAttribute(name = "protein")
    private Double protein;
    @XmlAttribute(name = "fat")
    private Double fat;
    @XmlAttribute(name = "carbohydrates")
    private Double carbohydrates;
    @XmlAttribute(name = "idOfMenuDetail")
    private Long idOfMenuDetail;
    @XmlAttribute(name = "amount")
    private int amount;
    @XmlAttribute(name = "selected")
    private Boolean selected;
    @XmlAttribute(name = "state")
    private Integer state;

    public PreorderMenuItemExt() {

    }

    public PreorderMenuItemExt(MenuDetail menuDetail) {
        this.setGroup(menuDetail.getGroupName());
        this.setName(menuDetail.getShortName());
        this.setFullName(menuDetail.getMenuDetailName());
        this.setPrice(menuDetail.getPrice());
        this.setCalories(menuDetail.getCalories());
        this.setOutput(menuDetail.getMenuDetailOutput());
        this.setAvailableNow(menuDetail.getAvailableNow());
        this.setProtein(menuDetail.getProtein());
        this.setCarbohydrates(menuDetail.getCarbohydrates());
        this.setFat(menuDetail.getFat());
        this.setIdOfMenuDetail(menuDetail.getLocalIdOfMenu());
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Integer getAvailableNow() {
        return availableNow;
    }

    public void setAvailableNow(Integer availableNow) {
        this.availableNow = availableNow;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
    }

    public Double getFat() {
        return fat;
    }

    public void setFat(Double fat) {
        this.fat = fat;
    }

    public Double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(Double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public Long getIdOfMenuDetail() {
        return idOfMenuDetail;
    }

    public void setIdOfMenuDetail(Long idOfMenuDetail) {
        this.idOfMenuDetail = idOfMenuDetail;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
