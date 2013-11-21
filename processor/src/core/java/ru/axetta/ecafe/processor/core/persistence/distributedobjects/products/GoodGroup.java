/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ConfigurationProviderDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
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
public class GoodGroup extends ConfigurationProviderDistributedObject {

    private String nameOfGoodsGroup;
    private Set<Good> goodInternal;
    private Set<ProhibitionExclusion> prohibitionExclusionInternal;
    private Set<Prohibition> prohibitionInternal;

    @Override
    public void createProjections(Criteria criteria) {
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("guid"), "guid");
        projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");
        projectionList.add(Projections.property("nameOfGoodsGroup"), "nameOfGoodsGroup");
        criteria.setProjection(projectionList);
    }

    @Override
    protected void beforeProcess(Session session, Long idOfOrg) throws DistributedObjectException {}

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "Name", nameOfGoodsGroup);
    }

    @Override
    protected GoodGroup parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null){
            setOrgOwner(longOrgOwner);
        } else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        String stringNameOfGoodsGroup = XMLUtils.getStringAttributeValue(node, "Name", 128);
        if(stringNameOfGoodsGroup!=null) setNameOfGoodsGroup(stringNameOfGoodsGroup);
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setNameOfGoodsGroup(((GoodGroup) distributedObject).getNameOfGoodsGroup());
        setIdOfConfigurationProvider(((GoodGroup) distributedObject).getIdOfConfigurationProvider());
    }

    public Set<Prohibition> getProhibitionInternal() {
        return prohibitionInternal;
    }

    public void setProhibitionInternal(Set<Prohibition> prohibitionInternal) {
        this.prohibitionInternal = prohibitionInternal;
    }

    public Set<ProhibitionExclusion> getProhibitionExclusionInternal() {
        return prohibitionExclusionInternal;
    }

    public void setProhibitionExclusionInternal(Set<ProhibitionExclusion> prohibitionExclusionInternal) {
        this.prohibitionExclusionInternal = prohibitionExclusionInternal;
    }

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
        return nameOfGoodsGroup;
    }

    public void setNameOfGoodsGroup(String nameOfGoodsGroup) {
        this.nameOfGoodsGroup = nameOfGoodsGroup;
    }
}
