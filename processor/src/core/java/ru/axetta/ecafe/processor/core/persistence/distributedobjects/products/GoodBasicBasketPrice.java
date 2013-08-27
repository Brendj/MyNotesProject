/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.GoodsBasicBasket;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

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
        Good g = DAOUtils.findDistributedObjectByRefGUID(Good.class, session, guidOfGood);
        //if(g == null) throw new DistributedObjectException("Good NOT_FOUND_VALUE");
        //setGood(g);
        if(g != null){
            setGood(g);
        }
        //DistributedObjectException distributedObjectException = new DistributedObjectException("BasicGood NOT_FOUND_VALUE");
        //distributedObjectException.setData(guidOfGoodsBasicBasket);
        //GoodsBasicBasket basicGood;
        //try {
        //    basicGood = DAOUtils.findBasicGood(session, guidOfGoodsBasicBasket);
        //} catch (Exception e) {
        //    throw distributedObjectException;
        //}
        //if (basicGood == null) throw distributedObjectException;
        //setGoodsBasicBasket(basicGood);
        GoodsBasicBasket basicGood = DAOUtils.findBasicGood(session, guidOfGoodsBasicBasket);
        if (basicGood != null) {
            setGoodsBasicBasket(basicGood);
        }
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "Price", price);
        if (good != null)
            XMLUtils.setAttributeIfNotNull(element, "GuidOfGood", good.getGuid());
        if (goodsBasicBasket != null)
            XMLUtils.setAttributeIfNotNull(element, "GuidOfBasicGood", goodsBasicBasket.getGuid());
    }

    @Override
    protected GoodBasicBasketPrice parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null)
            setOrgOwner(longOrgOwner);
        Long longPrice = XMLUtils.getLongAttributeValue(node, "Price");
        if (longPrice != null)
            setPrice(longPrice);
        guidOfGood = XMLUtils.getStringAttributeValue(node, "GuidOfGood", 36);
        guidOfGoodsBasicBasket = XMLUtils.getStringAttributeValue(node, "GuidOfBasicGood", 36);
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setPrice(((GoodBasicBasketPrice) distributedObject).getPrice());
        setGoodsBasicBasket(((GoodBasicBasketPrice) distributedObject).getGoodsBasicBasket());
        setGood(((GoodBasicBasketPrice) distributedObject).getGood());
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
