/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.report.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.04.13
 * Time: 15:51
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "goodsCode",
        "nameOfGood",
        "fullName",
        "lifeTime",
        "unitsScale",
        "totalCount",
        "netWeight",
        "selfPrice",
        "nds"
})
public class TradeMaterialGoodItem {

    @XmlElement
    protected String goodsCode;
    @XmlElement
    protected String nameOfGood;
    @XmlElement
    protected String fullName;
    @XmlElement
    protected Long lifeTime;
    @XmlElement
    protected String unitsScale;
    @XmlElement
    protected Long totalCount;
    @XmlElement
    protected Long netWeight;
    @XmlElement
    protected Long selfPrice;
    @XmlElement
    protected Long nds;

    public TradeMaterialGoodItem() {
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getNameOfGood() {
        return nameOfGood;
    }

    public void setNameOfGood(String nameOfGood) {
        this.nameOfGood = nameOfGood;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(Long lifeTime) {
        this.lifeTime = lifeTime;
    }

    public String getUnitsScale() {
        return unitsScale;
    }

    public void setUnitsScale(String unitsScale) {
        this.unitsScale = unitsScale;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(Long netWeight) {
        this.netWeight = netWeight;
    }

    public Long getSelfPrice() {
        return selfPrice;
    }

    public void setSelfPrice(Long selfPrice) {
        this.selfPrice = selfPrice;
    }

    public Long getNds() {
        return nds;
    }

    public void setNds(Long nds) {
        this.nds = nds;
    }
}

