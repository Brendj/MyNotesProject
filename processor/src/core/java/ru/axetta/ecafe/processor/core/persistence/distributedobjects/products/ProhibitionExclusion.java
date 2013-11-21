/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.sql.JoinType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

public class ProhibitionExclusion extends DistributedObject {

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("prohibition","p", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("goodsGroup","gg", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("good","g", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("guid"), "guid");
        projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");

        projectionList.add(Projections.property("gg.guid"), "guidOfGoodsGroup");
        projectionList.add(Projections.property("g.guid"), "guidOfGoods");
        projectionList.add(Projections.property("p.guid"), "guidOfProhibition");
        criteria.setProjection(projectionList);
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion) throws Exception {
        return null; //toSelfProcess(session, idOfOrg, currentMaxVersion);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        //Prohibition p = DAOUtils.findDistributedObjectByRefGUID(Prohibition.class, session, guidOfProhibition);
        //if (p == null) throw new DistributedObjectException("Prohibition NOT_FOUND_VALUE");
        //setProhibition(p);
        //
        //Good g = DAOUtils.findDistributedObjectByRefGUID(Good.class, session, guidOfGoods);
        //GoodGroup gg =  DAOUtils.findDistributedObjectByRefGUID(GoodGroup.class, session, guidOfGoodsGroup);
        //
        //if(gg != null) {
        //    setGoodsGroup(gg);
        //    return;
        //}
        //if(g != null) {
        //    setGood(g);
        //    return;
        //}
        //throw new DistributedObjectException("NOT_FOUND_VALUE");
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        if (isNotEmpty(guidOfProhibition) || prohibition!=null){
            if(isNotEmpty(guidOfProhibition)){
                XMLUtils.setAttributeIfNotNull(element, "GuidOfProhibition", guidOfProhibition);
            }
            if(prohibition!=null){
                XMLUtils.setAttributeIfNotNull(element, "GuidOfProhibition", prohibition.getGuid());
            }
        }

        if (isNotEmpty(guidOfGoods) || good!=null){
            if(isNotEmpty(guidOfGoods)){
                XMLUtils.setAttributeIfNotNull(element, "GuidOfGoods", guidOfGoods);
            }
            if(good!=null){
                XMLUtils.setAttributeIfNotNull(element, "GuidOfGoods", good.getGuid());
            }
        }
        if (isNotEmpty(guidOfGoodsGroup) || goodsGroup!=null){
            if(isNotEmpty(guidOfGoodsGroup)){
                XMLUtils.setAttributeIfNotNull(element, "GuidOfGoodsGroup", guidOfGoodsGroup);
            }
            if(good!=null){
                XMLUtils.setAttributeIfNotNull(element, "GuidOfGoodsGroup", goodsGroup.getGuid());
            }
        }
        //if (good != null)
        //    XMLUtils.setAttributeIfNotNull(element, "GuidOfGoods", good.getGuid());
        //if (goodsGroup != null)
        //    XMLUtils.setAttributeIfNotNull(element, "GuidOfGoodsGroup", goodsGroup.getGuid());
    }

    @Override
    protected ProhibitionExclusion parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null){
            setOrgOwner(longOrgOwner);
        } else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        guidOfProhibition = XMLUtils.getStringAttributeValue(node, "GuidOfProhibition", 36);
        //guidOfGoods = XMLUtils.getStringAttributeValue(node, "GuidOfGoods", 36);
        //guidOfGoodsGroup = XMLUtils.getStringAttributeValue(node, "GuidOfGoodsGroup", 36);
        //setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
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
