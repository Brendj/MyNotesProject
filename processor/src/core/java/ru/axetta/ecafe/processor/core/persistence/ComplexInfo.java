/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;

import java.util.Date;

public class ComplexInfo {

    private Long idOfComplexInfo;
    private int idOfComplex;
    private Org org;
    private int modeFree; //Режим бесплатного питания (0-выключен, 1-включен)
    private int modeGrant; // Режим дотации (0-выключен, 1 - включен)
    private int modeOfAdd; //Режим добавления блюд из комплекса ( 0 – режим добавления всех блюд, 1 – режи добавления по 1 блюду, 2 – режим фиксированная цена,3 – режим свободный выбор)
    private Integer modeVisible; //признак видимости комплекса в кассире (1 – Виден, 0 - нет)
    private Integer useTrDiscount = 0;
    private String complexName;
    private Date menuDate;
    private ComplexInfoDiscountDetail discountDetail;
    private MenuDetail menuDetail;
    private Long currentPrice;
    private Good good;
    private Integer usedSubscriptionFeeding; //Признак использования комплекса в абонементном питании. 1 – может быть использован, 0 – не может быть использован
    private Integer usedVariableFeeding; //Признак использования комплекса в вариативном питании. 1 – может быть использован, 0 – не может быть использован
    private Integer rootComplex; //Идентификатор комплекса-родителя для связывания комплексов в группы
    private Integer usedSpecialMenu; //Признак использования комплекса в предзаказах

    public static final int SET_DISHES_COMPLEX = 4;

    protected ComplexInfo() {}

    public ComplexInfo(int idOfComplex, Org org, Date menuDate, int modeFree, int modeGrant, int modeOfAdd,
            String complexName) {
        this.idOfComplex = idOfComplex;
        this.org = org;
        this.menuDate = menuDate;
        this.modeFree = modeFree;
        this.modeGrant = modeGrant;
        this.modeOfAdd = modeOfAdd;
        this.complexName = complexName;

    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public int getModeOfAdd() {
        return modeOfAdd;
    }

    public void setModeOfAdd(int modeOfAdd) {
        this.modeOfAdd = modeOfAdd;
    }

    public int getModeGrant() {
        return modeGrant;
    }

    public void setModeGrant(int modeGrant) {
        this.modeGrant = modeGrant;
    }

    public int getModeFree() {
        return modeFree;
    }

    public void setModeFree(int modeFree) {
        this.modeFree = modeFree;
    }

    public Integer getUseTrDiscount() {
        return useTrDiscount;
    }

    public void setUseTrDiscount(Integer useTrDiscount) {
        this.useTrDiscount = useTrDiscount;
    }

    public Integer getModeVisible() {
        return modeVisible;
    }

    public void setModeVisible(Integer modeVisible) {
        this.modeVisible = modeVisible;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public ComplexInfoDiscountDetail getDiscountDetail() {
        return discountDetail;
    }

    public void setDiscountDetail(ComplexInfoDiscountDetail discountDetail) {
        this.discountDetail = discountDetail;
    }

    public int getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(int idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public Long getIdOfComplexInfo() {
        return idOfComplexInfo;
    }

    public void setIdOfComplexInfo(Long idOfComplexInfo) {
        this.idOfComplexInfo = idOfComplexInfo;
    }

    public MenuDetail getMenuDetail() {
        return menuDetail;
    }

    public void setMenuDetail(MenuDetail menuDetail) {
        this.menuDetail = menuDetail;
    }

    public Long getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Long currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }

    public Date getMenuDate() {
        return menuDate;
    }

    public void setMenuDate(Date menuDate) {
        this.menuDate = menuDate;
    }

    public Integer getUsedSubscriptionFeeding() {
        return usedSubscriptionFeeding;
    }

    public void setUsedSubscriptionFeeding(Integer usedSubscriptionFeeding) {
        this.usedSubscriptionFeeding = usedSubscriptionFeeding;
    }

    public Integer getUsedVariableFeeding() {
        return usedVariableFeeding;
    }

    public void setUsedVariableFeeding(Integer usedVariableFeeding) {
        this.usedVariableFeeding = usedVariableFeeding;
    }

    public Integer getRootComplex() {
        return rootComplex;
    }

    public void setRootComplex(Integer rootComplex) {
        this.rootComplex = rootComplex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ComplexInfo that = (ComplexInfo) o;

        if (idOfComplexInfo != that.idOfComplexInfo) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (idOfComplexInfo ^ (idOfComplexInfo >>> 32));
    }

    @Override
    public String toString() {
        return "ComplexInfo{" + "idOfComplexInfo=" + idOfComplexInfo + ", idOfComplex=" + idOfComplex + ", org=" + org
                + ", modeFree=" + modeFree + ", modeGrant=" + modeGrant + ", modeOfAdd=" + modeOfAdd + ", useTrDiscount=" + useTrDiscount
                + ", discountDetail:" + discountDetail + ", currentPrice=" + currentPrice + ", modeVisible=" + modeVisible + '}';
    }

    public Integer getUsedSpecialMenu() {
        return usedSpecialMenu;
    }

    public void setUsedSpecialMenu(Integer usedSpecialMenu) {
        this.usedSpecialMenu = usedSpecialMenu;
    }
}
