/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxPreorder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

public class FoodBoxPreorderNewItemItem {

    private Long idOfDish;
    private Integer price;
    private Integer qty;


    public FoodBoxPreorderNewItemItem() {
    }

    public FoodBoxPreorderNewItemItem(Long IdOfDish, Integer price, Integer qty) {
        this.idOfDish = IdOfDish;
        this.price = price;
        this.qty = qty;
    }
    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "IdOfDish", idOfDish);
        XMLUtils.setAttributeIfNotNull(element, "Price", price);
        XMLUtils.setAttributeIfNotNull(element, "Qty", qty);
        return element;
    }

    public Long getIdOfDish() {
        return idOfDish;
    }

    public void setIdOfDish(Long idOfDish) {
        this.idOfDish = idOfDish;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}
