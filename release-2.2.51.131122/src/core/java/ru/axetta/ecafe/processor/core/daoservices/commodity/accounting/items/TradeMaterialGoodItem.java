/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.items;


/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.04.13
 * Time: 15:51
 * To change this template use File | Settings | File Templates.
 */
public class TradeMaterialGoodItem {

    protected String goodsCode;
    private String nameOfGood;
    private String fullName;
    private Long lifeTime;
    private String unitsScale;
    private Long totalCount;
    private Long netWeight;
    private Long selfPrice;
    private Long nds;

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

