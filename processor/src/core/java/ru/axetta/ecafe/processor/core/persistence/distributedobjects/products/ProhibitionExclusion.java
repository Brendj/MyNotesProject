/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ProhibitionExclusion extends DistributedObject {

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        Prohibition p = DAOUtils.findDistributedObjectByRefGUID(Prohibition.class, session, guidOfProhibition);
        if (p == null) throw new DistributedObjectException("Prohibition NOT_FOUND_VALUE");
        setProhibition(p);

        Good g = DAOUtils.findDistributedObjectByRefGUID(Good.class, session, guidOfGoods);
        GoodGroup gg =  DAOUtils.findDistributedObjectByRefGUID(GoodGroup.class, session, guidOfGoodsGroup);

        if(gg != null) {
            setGoodsGroup(gg);
            return;
        }
        if(g != null) {
            setGood(g);
            return;
        }
        throw new DistributedObjectException("NOT_FOUND_VALUE");
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element, "GuidOfProhibition", prohibition.getGuid());
        if(good != null) setAttribute(element,"GuidOfGoods", good.getGuid());
        if(goodsGroup != null) setAttribute(element, "GuidOfGoodsGroup", goodsGroup.getGuid());
    }

    @Override
    protected ProhibitionExclusion parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        guidOfProhibition = getStringAttributeValue(node, "GuidOfProhibition",36);
        guidOfGoods = getStringAttributeValue(node,"GuidOfGoods",36);
        guidOfGoodsGroup = getStringAttributeValue(node,"GuidOfGoodsGroup",36);
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((Prohibition) distributedObject).getOrgOwner());
    }

    private Prohibition prohibition;
    private String guidOfProhibition;
    private Good good;
    private String guidOfGoods;
    private GoodGroup goodsGroup;
    private String guidOfGoodsGroup;

    public Prohibition getProhibition() {
        return prohibition;
    }

    public void setProhibition(Prohibition prohibition) {
        this.prohibition = prohibition;
    }

    public String getGuidOfProhibition() {
        return guidOfProhibition;
    }

    public void setGuidOfProhibition(String guidOfProhibition) {
        this.guidOfProhibition = guidOfProhibition;
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }

    public String getGuidOfGoodsGroup() {
        return guidOfGoodsGroup;
    }

    public void setGuidOfGoodsGroup(String guidOfGoodsGroup) {
        this.guidOfGoodsGroup = guidOfGoodsGroup;
    }

    public String getGuidOfGoods() {
        return guidOfGoods;
    }

    public void setGuidOfGoods(String guidOfGoods) {
        this.guidOfGoods = guidOfGoods;
    }

    public GoodGroup getGoodsGroup() {
        return goodsGroup;
    }

    public void setGoodsGroup(GoodGroup goodsGroup) {
        this.goodsGroup = goodsGroup;
    }

}
