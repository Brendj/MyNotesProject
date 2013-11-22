/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.persistence.GoodsBasicBasket;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 13.02.13
 * Time: 13:14
 * To change this template use File | Settings | File Templates.
 */
public class GoodsBasicBasketDataElement {

    private String guid;
    private String nameOfGood;
    private Integer unitsScale;
    private Long netWeight;

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("GoodsBasicBasketDataElement");
        element.setAttribute("GUID", guid);
        element.setAttribute("NameOfGood", nameOfGood);
        element.setAttribute("UnitsScale", String.valueOf(unitsScale));
        element.setAttribute("NetWeight", String.valueOf(netWeight));
        return element;
    }

    public GoodsBasicBasketDataElement(GoodsBasicBasket goodsBasicBasket) {
        this.guid = goodsBasicBasket.getGuid();
        this.nameOfGood = goodsBasicBasket.getNameOfGood();
        this.netWeight = goodsBasicBasket.getNetWeight();
        this.unitsScale = goodsBasicBasket.getUnitsScale().ordinal();
    }

    public String getGuid() {
        return guid;
    }

    public String getNameOfGood() {
        return nameOfGood;
    }

    public Long getNetWeight() {
        return netWeight;
    }

    public Integer getUnitsScale() {
        return unitsScale;
    }
}
