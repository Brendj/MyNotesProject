/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import ru.axetta.ecafe.processor.core.persistence.ComplexInfo;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodType;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtComplex;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 21.07.15
 * Time: 12:02
 */

@XmlRootElement(name = "MenuWithComplexesExt")
@XmlAccessorType(XmlAccessType.FIELD)
public class MenuWithComplexesExt {

    @XmlAttribute(name = "idOfComplexInfo")
    private Long idOfComplexInfo;
    @XmlAttribute(name = "idOfComplex")
    private int idOfComplex;
    @XmlAttribute(name = "complexName")
    private String complexName;
    @XmlAttribute(name = "menuDate")
    @XmlSchemaType(name = "dateTime")
    private Date menuDate;
    @XmlAttribute(name = "currentPrice")
    private Long currentPrice;
    @XmlAttribute(name = "usedSubscriptionFeeding")
    private int usedSubscriptionFeeding;
    @XmlAttribute(name = "usedVariableFeeding")
    private int usedVariableFeeding;
    @XmlAttribute(name = "isDiscountComplex")
    private int isDiscountComplex;
    @XmlAttribute(name = "goodType")
    private Integer goodType;
    @XmlElement(name = "MenuDetail")
    private List<MenuItemExt> menuItemExtList;
    @XmlAttribute(name = "modevisible")
    private Integer modevisible;
    @XmlAttribute(name = "usedspecialmenu")
    private Integer usedspecialmenu;

    public MenuWithComplexesExt() {
    }

    public MenuWithComplexesExt(ComplexInfo complexInfo) {
        this.idOfComplexInfo = complexInfo.getIdOfComplexInfo();
        this.idOfComplex = complexInfo.getIdOfComplex();
        this.complexName = complexInfo.getComplexName();
        this.menuDate = complexInfo.getMenuDate();
        this.currentPrice = complexInfo.getCurrentPrice();
        this.usedSubscriptionFeeding =
                complexInfo.getUsedSubscriptionFeeding() == null || complexInfo.getUsedSubscriptionFeeding() == 0 ? 0
                        : 1;
        this.usedVariableFeeding =
                complexInfo.getUsedVariableFeeding() == null || complexInfo.getUsedVariableFeeding() == 0 ? 0
                        : 1;
        this.isDiscountComplex = complexInfo.getModeFree();
        this.goodType = getGoodType(complexInfo);
        this.modevisible = complexInfo.getModeVisible();
        this.usedspecialmenu = complexInfo.getUsedSpecialMenu();
    }

    public MenuWithComplexesExt(WtComplex wtComplex, Org org, Date date, int isDiscountComplex) {
        this.idOfComplexInfo = wtComplex.getIdOfComplex();
        this.idOfComplex = wtComplex.getIdOfComplex().intValue();
        this.complexName = wtComplex.getWtDietType().getDescription();
        this.menuDate = date;
        this.currentPrice = (wtComplex.getPrice() == null) ? 0L : wtComplex.getPrice()
                .multiply(new BigDecimal(100)).longValue();
        this.usedSubscriptionFeeding = 0;
        this.usedVariableFeeding = 0;
        this.isDiscountComplex = isDiscountComplex;
        this.modevisible = 1;
        this.usedspecialmenu = org.getPreordersEnabled() && wtComplex.getIsPortal() ? 1 : 0;
    }

    private Integer getGoodType(ComplexInfo complexInfo) {
        if (complexInfo.getGood() == null || complexInfo.getGood().getGoodType() == null || complexInfo.getGood().getGoodType() == GoodType.UNSPECIFIED) {
            return null;
        }
        return complexInfo.getGood().getGoodType().getCode();
    }

    public Long getIdOfComplexInfo() {
        return idOfComplexInfo;
    }

    public void setIdOfComplexInfo(Long idOfComplexInfo) {
        this.idOfComplexInfo = idOfComplexInfo;
    }

    public int getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(int idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public Date getMenuDate() {
        return menuDate;
    }

    public void setMenuDate(Date menuDate) {
        this.menuDate = menuDate;
    }

    public Long getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Long currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int getUsedSubscriptionFeeding() {
        return usedSubscriptionFeeding;
    }

    public void setUsedSubscriptionFeeding(int usedSubscriptionFeeding) {
        this.usedSubscriptionFeeding = usedSubscriptionFeeding;
    }

    public List<MenuItemExt> getMenuItemExtList() {
        return menuItemExtList;
    }

    public void setMenuItemExtList(List<MenuItemExt> menuItemExtList) {
        this.menuItemExtList = menuItemExtList;
    }

    public int getUsedVariableFeeding() {
        return usedVariableFeeding;
    }

    public void setUsedVariableFeeding(int usedVariableFeeding) {
        this.usedVariableFeeding = usedVariableFeeding;
    }

    public int getIsDiscountComplex() {
        return isDiscountComplex;
    }

    public void setIsDiscountComplex(int isDiscountComplex) {
        this.isDiscountComplex = isDiscountComplex;
    }

    public Integer getGoodType() {
        return goodType;
    }

    public void setGoodType(Integer goodType) {
        this.goodType = goodType;
    }

    public Integer getUsedspecialmenu() {
        return usedspecialmenu;
    }

    public void setUsedspecialmenu(Integer usedspecialmenu) {
        this.usedspecialmenu = usedspecialmenu;
    }

    public Integer getModevisible() {
        return modevisible;
    }

    public void setModevisible(Integer modevisible) {
        this.modevisible = modevisible;
    }
}
