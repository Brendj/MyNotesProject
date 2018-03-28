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
    private final String guid;

    public PreOrderFeedingDetail(PreorderMenuDetail menuDetail, Integer complexId, String guid) {
        this.idOfMenu = menuDetail.getMenuDetail().getLocalIdOfMenu();
        this.name = menuDetail.getMenuDetail().getMenuDetailName();
        this.qty = menuDetail.getAmount();
        this.complexId = complexId;
        this.guid = guid;
    }

    public PreOrderFeedingDetail(PreorderComplex complex) {
        this.complexId = complex.getArmComplexId() == null ? complex.getComplexInfo().getIdOfComplex() : complex.getArmComplexId();
        this.name = complex.getComplexInfo().getComplexName();
        this.qty = complex.getAmount();
        this.idOfMenu = null;
        this.guid = complex.getGuid();
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

    public String getGuid() {
        return guid;
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
        if (null != guid) {
            element.setAttribute("Guid", guid);
        }

        return element;
    }
}
