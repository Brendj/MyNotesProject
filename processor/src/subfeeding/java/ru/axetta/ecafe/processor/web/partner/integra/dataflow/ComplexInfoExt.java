/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import java.util.Date;

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

    public ComplexInfoExt() {
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
}
