/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import ru.axetta.ecafe.processor.core.persistence.ComplexInfo;

import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 10.01.14
 * Time: 14:44
 */

@XmlRootElement(name = "ComplexInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ComplexInfoExt {

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
    @XmlElement(name = "MenuDetail")
    private List<MenuItemExt> menuItemExtList;

    public ComplexInfoExt() {
    }

    public ComplexInfoExt(ComplexInfo complexInfo) {
        this.idOfComplexInfo = complexInfo.getIdOfComplexInfo();
        this.idOfComplex = complexInfo.getIdOfComplex();
        this.complexName = complexInfo.getComplexName();
        this.menuDate = complexInfo.getMenuDate();
        this.currentPrice = complexInfo.getCurrentPrice();
        this.usedSubscriptionFeeding =
                complexInfo.getUsedSubscriptionFeeding() == null || complexInfo.getUsedSubscriptionFeeding() == 0 ? 0
                        : 1;
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
}
