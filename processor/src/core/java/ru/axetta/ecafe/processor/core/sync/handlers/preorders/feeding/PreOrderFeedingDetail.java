/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding;

import ru.axetta.ecafe.processor.core.persistence.PreorderComplex;
import ru.axetta.ecafe.processor.core.persistence.PreorderMenuDetail;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PreOrderFeedingDetail {
    private final Integer complexId;
    private final Long idOfMenu;
    private final String name;
    private final Integer qty;

    public PreOrderFeedingDetail(PreorderMenuDetail menuDetail) {
        this.idOfMenu = menuDetail.getMenuDetail().getLocalIdOfMenu();
        this.name = menuDetail.getMenuDetail().getMenuDetailName();
        this.qty = menuDetail.getAmount();
        this.complexId = null;
    }

    public PreOrderFeedingDetail(PreorderComplex complex) {
        this.complexId = complex.getComplexInfo().getIdOfComplex();
        this.name = complex.getComplexInfo().getComplexName();
        this.qty = 1;
        this.idOfMenu = null;
    }

    public Integer getComplexId() {
        return complexId;
    }

    public Long getIdOfMenu() {
        return idOfMenu;
    }

    public String getName() {
        return name;
    }

    public Integer getQty() {
        return qty;
    }

    public Element toElement(Document document) throws Exception{
        Element element = document.createElement("POD");

        if (null != complexId) {
            element.setAttribute("ComplexId", Integer.toString(complexId));
        }
        if (null != idOfMenu) {
            element.setAttribute("IdOfMenu", Long.toString(idOfMenu));
        }
        if (null != name) {
            element.setAttribute("Name", name);
        }
        if (null != qty) {
            element.setAttribute("Qty", Integer.toString(qty));
        }

        return element;
    }
}
