/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.foodBox;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FoodBoxAvailableItem {
    private Long idOfDish;
    private Integer availableQty;

    public Long getIdOfDish() {
        return idOfDish;
    }

    public void setIdOfDish(Long idOfDish) {
        this.idOfDish = idOfDish;
    }

    public Integer getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(Integer availableQty) {
        this.availableQty = availableQty;
    }

//    public Element toElement(Document document) throws Exception {
//        Element element = document.createElement("Record");
//        element.setAttribute("idEventEMIAS",idEventEMIAS.toString());
//        element.setAttribute("accepted", accepted == null ? "false" : accepted.toString());
//        return element;
//    }

}
