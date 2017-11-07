/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good;

import ru.axetta.ecafe.processor.core.persistence.GoodsBasicBasket;

/**
 * Created by i.semenov on 31.10.2017.
 */
public class BasicGoodListItem {
    private Boolean selected;
    private Long idOfBasicGood;
    private String name;
    private Long netWeight;

    public BasicGoodListItem(GoodsBasicBasket basicBasket) {
        this.idOfBasicGood = basicBasket.getIdOfBasicGood();
        this.name = basicBasket.getNameOfGood();
        this.netWeight = basicBasket.getNetWeight();
        this.selected = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BasicGoodListItem)) {
            return false;
        }
        final BasicGoodListItem item = (BasicGoodListItem) o;
        return idOfBasicGood.equals(item.getIdOfBasicGood());
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Long getIdOfBasicGood() {
        return idOfBasicGood;
    }

    public void setIdOfBasicGood(Long idOfBasicGood) {
        this.idOfBasicGood = idOfBasicGood;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(Long netWeight) {
        this.netWeight = netWeight;
    }
}
