/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import ru.axetta.ecafe.processor.core.persistence.ComplexInfo;

import java.util.List;

/**
 * Created by i.semenov on 13.03.2018.
 */
public class PreorderComplexItemExt {
    private Long idOfComplexInfo;
    private String complexName;
    private Long currentPrice;
    private int amount;
    private Boolean selected;
    private List<PreorderMenuItemExt> menuItemExtList;

    public PreorderComplexItemExt() {

    }

    public PreorderComplexItemExt(ComplexInfo ci) {
        this.setIdOfComplexInfo(ci.getIdOfComplexInfo());
        this.setComplexName(ci.getComplexName());
        this.setCurrentPrice(ci.getCurrentPrice());
    }

    public List<PreorderMenuItemExt> getMenuItemExtList() {
        return menuItemExtList;
    }

    public void setMenuItemExtList(List<PreorderMenuItemExt> menuItemExtList) {
        this.menuItemExtList = menuItemExtList;
    }

    public Long getIdOfComplexInfo() {
        return idOfComplexInfo;
    }

    public void setIdOfComplexInfo(Long idOfComplexInfo) {
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

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
