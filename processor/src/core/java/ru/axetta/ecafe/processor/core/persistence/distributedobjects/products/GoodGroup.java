/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.sync.distributionsync.DistributedObjectException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.12
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class GoodGroup extends DistributedObject {

    @Override
    public void preProcess() throws DistributedObjectException {

    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element,"Name", NameOfGoodsGroup);
    }

    @Override
    protected GoodGroup parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        String stringNameOfGoodsGroup = getStringAttributeValue(node,"Name",128);
        if(stringNameOfGoodsGroup!=null) setNameOfGoodsGroup(stringNameOfGoodsGroup);
        return this;
    }
    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((GoodGroup) distributedObject).getOrgOwner());
        setNameOfGoodsGroup(((GoodGroup) distributedObject).getNameOfGoodsGroup());
    }

    private String NameOfGoodsGroup;
    private Set<Good> goodInternal;

    public List<Good> getGoods(){
        return Collections.unmodifiableList(new ArrayList<Good>(getGoodInternal()));
    }

    public void addProduct(Good good){
        getGoodInternal().add(good);
    }

    public void removeProduct(Good good){
        getGoodInternal().remove(good);
    }

    private Set<Good> getGoodInternal() {
        return goodInternal;
    }

    private void setGoodInternal(Set<Good> goodInternal) {
        this.goodInternal = goodInternal;
    }

    public String getNameOfGoodsGroup() {
        return NameOfGoodsGroup;
    }

    public void setNameOfGoodsGroup(String nameOfGoodsGroup) {
        NameOfGoodsGroup = nameOfGoodsGroup;
    }

}
