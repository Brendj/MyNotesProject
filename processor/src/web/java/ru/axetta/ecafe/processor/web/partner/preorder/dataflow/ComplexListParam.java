/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import java.util.List;

/**
 * Created by i.semenov on 13.03.2018.
 */
public class ComplexListParam {

    private Long idOfComplex;
    private Integer amount;
    private List<MenuItemParam> menuItems;

    public Long getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(Long idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public List<MenuItemParam> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItemParam> menuItems) {
        this.menuItems = menuItems;
    }
}
