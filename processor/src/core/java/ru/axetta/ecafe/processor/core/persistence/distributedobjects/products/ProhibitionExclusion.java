/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ProhibitionExclusion extends DistributedObject {

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        Prohibition p = (Prohibition) DAOUtils.findDistributedObjectByRefGUID(session, guidOfProhibition);
        if (p == null) throw new DistributedObjectException("Prohibition NOT_FOUND_VALUE");

        Good g = (Good) DAOUtils.findDistributedObjectByRefGUID(session, guidOfGood);
        GoodGroup gg = (GoodGroup) DAOUtils.findDistributedObjectByRefGUID(session, guidOfGoodGroup);

        if(gg != null) {
            setGoodGroup(gg);
            return;
        }
        if(g != null) {
            setGood(g);
            return;
        }
        throw new DistributedObjectException("NOT_FOUND_VALUE");

        //Good g = (Good) DAOUtils.findDistributedObjectByRefGUID(session, guidOfGood);
        //if(g == null) throw new DistributedObjectException("Good NOT_FOUND_VALUE");
        //setGood(g);
        //GoodGroup gg = (GoodGroup) DAOUtils.findDistributedObjectByRefGUID(session, guidOfGoodGroup);
        //if(gg == null) throw new DistributedObjectException("GoodGroup NOT_FOUND_VALUE");
        //setGoodGroup(gg);
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element, "GuidOfProhibition", prohibition.getGuid());
        if(good != null) setAttribute(element,"GuidOfGood", good.getGuid());
        if(goodGroup != null) setAttribute(element, "GuidOfGoodsGroup", goodGroup.getGuid());
    }

    @Override
    protected ProhibitionExclusion parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        guidOfProhibition = getStringAttributeValue(node, "GuidOfProhibition",36);
        guidOfGood = getStringAttributeValue(node,"GuidOfGood",36);
        guidOfGoodGroup = getStringAttributeValue(node,"GuidOfGoodsGroup",36);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((Prohibition) distributedObject).getOrgOwner());
    }

    private Prohibition prohibition;
    private String guidOfProhibition;
    private Good good;
    private String guidOfGood;
    private GoodGroup goodGroup;
    private String guidOfGoodGroup;

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

    public String getGuidOfGoodGroup() {
        return guidOfGoodGroup;
    }

    public void setGuidOfGoodGroup(String guidOfGoodGroup) {
        this.guidOfGoodGroup = guidOfGoodGroup;
    }

    public String getGuidOfGood() {
        return guidOfGood;
    }

    public void setGuidOfGood(String guidOfGood) {
        this.guidOfGood = guidOfGood;
    }

    public GoodGroup getGoodGroup() {
        return goodGroup;
    }

    public void setGoodGroup(GoodGroup goodGroup) {
        this.goodGroup = goodGroup;
    }

}
