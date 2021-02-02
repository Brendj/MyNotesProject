/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.GoodsBasicBasket;
import ru.axetta.ecafe.processor.core.persistence.MenuDetail;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ConfigurationProviderDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.sql.JoinType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.12
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class GoodBasicBasketPrice extends ConfigurationProviderDistributedObject {

    private String guidOfGood;
    private Good good;
    private String guidOfGoodsBasicBasket;
    private GoodsBasicBasket goodsBasicBasket;
    private Long price;
    private MenuDetail menuDetail;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("good","g", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("goodsBasicBasket","bg", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("price"), "price");

        projectionList.add(Projections.property("g.guid"), "guidOfGood");
        projectionList.add(Projections.property("bg.guid"), "guidOfGoodsBasicBasket");
        criteria.setProjection(projectionList);
    }

    @Override
    protected void beforeProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        Good g = DAOUtils.findDistributedObjectByRefGUID(Good.class, session, guidOfGood);
        if(g != null){
            setGood(g);
        }
        GoodsBasicBasket basicGood = DAOUtils.findBasicGood(session, guidOfGoodsBasicBasket);
        if (basicGood != null) {
            setGoodsBasicBasket(basicGood);
        }
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "Price", price);
        if(StringUtils.isNotEmpty(guidOfGood)){
            XMLUtils.setAttributeIfNotNull(element, "GuidOfGood", guidOfGood);
        }
        if(StringUtils.isNotEmpty(guidOfGoodsBasicBasket)){
            XMLUtils.setAttributeIfNotNull(element, "GuidOfBasicGood", guidOfGoodsBasicBasket);
        }
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setPrice(((GoodBasicBasketPrice) distributedObject).getPrice());
        setGoodsBasicBasket(((GoodBasicBasketPrice) distributedObject).getGoodsBasicBasket());
        setGood(((GoodBasicBasketPrice) distributedObject).getGood());
    }

    @Override
    protected GoodBasicBasketPrice parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null){
            setOrgOwner(longOrgOwner);
        } else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        Long longPrice = XMLUtils.getLongAttributeValue(node, "Price");
        if (longPrice != null)
            setPrice(longPrice);
        guidOfGood = XMLUtils.getStringAttributeValue(node, "GuidOfGood", 36);
        guidOfGoodsBasicBasket = XMLUtils.getStringAttributeValue(node, "GuidOfBasicGood", 36);
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    public static GoodBasicBasketPrice save(Session session, GoodBasicBasketPrice goodBasicBasketPrice) {
        Long version = DAOUtils.getDistributedObjectVersion(session, GoodBasicBasketPrice.class.getSimpleName());
        if(goodBasicBasketPrice.getGlobalId()==null){
            goodBasicBasketPrice.setGlobalVersion(version);
            session.persist(goodBasicBasketPrice);
        } else {
            goodBasicBasketPrice.setGlobalVersion(version);
            session.merge(goodBasicBasketPrice);
        }
        return goodBasicBasketPrice;
    }

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

    public MenuDetail getMenuDetail() {
        return menuDetail;
    }

    public void setMenuDetail(MenuDetail menuDetail) {
        this.menuDetail = menuDetail;
    }
}
