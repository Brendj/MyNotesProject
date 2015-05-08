/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response.registry.accounts;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.LinkedList;
import java.util.List;

/**
 * User: regal
 * Date: 05.05.15
 * Time: 10:25
 */
public class VisitorItem {
    public static final String SYNC_NAME = "VI";

    private long idOfVisitor;

    private List<CardsItem> cardsItems = new LinkedList<CardsItem>();

    public long getIdOfVisitor() {
        return idOfVisitor;
    }

    public void setIdOfVisitor(long idOfVisitor) {
        this.idOfVisitor = idOfVisitor;
    }

    public List<CardsItem> getCardsItems() {
        return cardsItems;
    }

    public void setCardsItems(List<CardsItem> cardsItems) {
        this.cardsItems = cardsItems;
    }


    public Element toElement(Document document) throws Exception {
        Element element = document.createElement(SYNC_NAME);
        element.setAttribute("GlobalId", Long.toString(this.idOfVisitor));
        for (CardsItem cardsItem : cardsItems){
            element.appendChild(cardsItem.toElement(document));
        }
        return element;
    }
}
