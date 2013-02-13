/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.GoodsBasicBasket;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.12
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class GoodBasicBasketPrice extends DistributedObject {

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        Good g = (Good) DAOUtils.findDistributedObjectByRefGUID(session, guidOfGood);
        if(g == null) throw new DistributedObjectException("Good NOT_FOUND_VALUE");
        setGood(g);
        DistributedObjectException distributedObjectException = new DistributedObjectException("BasicGood NOT_FOUND_VALUE");
        distributedObjectException.setData(guidOfGoodsBasicBasket);
        GoodsBasicBasket basicGood;
        try {
            basicGood = DAOUtils.findBasicGood(session, guidOfGoodsBasicBasket);
        } catch (Exception e) {
            throw distributedObjectException;
        }
        if (basicGood == null) throw distributedObjectException;
        setGoodsBasicBasket(basicGood);
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element,"Price", price);
        if(good != null) setAttribute(element,"GuidOfGood", good.getGuid());
        if(goodsBasicBasket != null) setAttribute(element,"GuidOfBasicGood", goodsBasicBasket.getGuid());
    }
    @Override
    protected GoodBasicBasketPrice parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        Long longPrice = getLongAttributeValue(node, "Price");
        if(longPrice != null) setPrice(longPrice);
        guidOfGood = getStringAttributeValue(node,"GuidOfGood",36);
        guidOfGoodsBasicBasket = getStringAttributeValue(node,"GuidOfBasicGood",36);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((GoodBasicBasketPrice) distributedObject).getOrgOwner());
        setPrice(((GoodBasicBasketPrice) distributedObject).getPrice());
    }

    private String guidOfGood;
    private Good good;
    private String guidOfGoodsBasicBasket;
    private GoodsBasicBasket goodsBasicBasket;
    private Long price;

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getGuidOfGoodsBasicBasket() {
        return guidOfGoodsBasicBasket;
    }

    public void setGuidOfGoodsBasicBasket(String guidOfGoodsBasicBasket) {
        this.guidOfGoodsBasicBasket = guidOfGoodsBasicBasket;
    }

    public String getGuidOfGood() {
        return guidOfGood;
    }

    public void setGuidOfGood(String guidOfGood) {
        this.guidOfGood = guidOfGood;
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }

    public GoodsBasicBasket getGoodsBasicBasket() {
        return goodsBasicBasket;
    }

    public void setGoodsBasicBasket(GoodsBasicBasket goodsBasicBasket) {
        this.goodsBasicBasket = goodsBasicBasket;
    }
}
