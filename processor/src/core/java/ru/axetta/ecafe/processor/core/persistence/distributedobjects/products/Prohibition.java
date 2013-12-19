/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.Client;
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
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

public class Prohibition extends DistributedObject {

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("client","c", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("productGroup","pg", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("product","p", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("goodGroup","gg", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("good","g", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("c.idOfClient"), "idOfClient");
        projectionList.add(Projections.property("gg.guid"), "guidOfGoodsGroup");
        projectionList.add(Projections.property("g.guid"), "guidOfGoods");
        projectionList.add(Projections.property("pg.guid"), "guidOfProductGroup");
        projectionList.add(Projections.property("p.guid"), "guidOfProduct");
        criteria.setProjection(projectionList);
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion) throws Exception {
        return null; //toSelfProcess(session, idOfOrg, currentMaxVersion);
    }

    @Override
    protected Prohibition parseAttributes(Node node) throws Exception {
        //orgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        //idOfClient = XMLUtils.getLongAttributeValue(node, "IdOfClient");
        //guidOfProduct = XMLUtils.getStringAttributeValue(node, "GuidOfProduct", 36);
        //guidOfProductGroup = XMLUtils.getStringAttributeValue(node, "GuidOfProductGroup", 36);
        //guidOfGoods = XMLUtils.getStringAttributeValue(node, "GuidOfGoods", 36);
        //guidOfGoodsGroup = XMLUtils.getStringAttributeValue(node, "GuidOfGoodsGroup", 36);
        //setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "IdOfClient", client.getIdOfClient());
        if (isNotEmpty(guidOfProduct) || product!=null){
            if(isNotEmpty(guidOfProduct)){
                XMLUtils.setAttributeIfNotNull(element, "GuidOfProduct", guidOfProduct);
            }
            if(product!=null){
                XMLUtils.setAttributeIfNotNull(element, "GuidOfProduct", product.getGuid());
            }
        }
        if (isNotEmpty(guidOfProductGroup) || productGroup!=null){
            if(isNotEmpty(guidOfProductGroup)){
                XMLUtils.setAttributeIfNotNull(element, "GuidOfProductGroup", guidOfProductGroup);
            }
            if(productGroup!=null){
                XMLUtils.setAttributeIfNotNull(element, "GuidOfProductGroup", productGroup.getGuid());
            }
        }
        //if (product != null) XMLUtils.setAttributeIfNotNull(element, "GuidOfProduct", product.getGuid());
        //if (productGroup != null) XMLUtils.setAttributeIfNotNull(element, "GuidOfProductGroup", productGroup.getGuid());
        //if (good != null)
        //    XMLUtils.setAttributeIfNotNull(element, "GuidOfGoods", good.getGuid());
        if (isNotEmpty(guidOfGoods) || good!=null){
            if(isNotEmpty(guidOfGoods)){
                XMLUtils.setAttributeIfNotNull(element, "GuidOfGoods", guidOfGoods);
            }
            if(good!=null){
                XMLUtils.setAttributeIfNotNull(element, "GuidOfGoods", good.getGuid());
            }
        }
        if (isNotEmpty(guidOfGoodsGroup) || goodGroup!=null){
            if(isNotEmpty(guidOfGoodsGroup)){
                XMLUtils.setAttributeIfNotNull(element, "GuidOfGoodsGroup", guidOfGoodsGroup);
            }
            if(good!=null){
                XMLUtils.setAttributeIfNotNull(element, "GuidOfGoodsGroup", goodGroup.getGuid());
            }
        }
        //if (goodGroup != null) XMLUtils.setAttributeIfNotNull(element, "GuidOfGoodsGroup", goodGroup.getGuid());
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setClient(((Prohibition) distributedObject).getClient());
        setIdOfClient(((Prohibition) distributedObject).getIdOfClient());
        setProductGroup(((Prohibition) distributedObject).getProductGroup());
        setGuidOfProductGroup(((Prohibition) distributedObject).getGuidOfProductGroup());
        setProduct(((Prohibition) distributedObject).getProduct());
        setGuidOfProduct(((Prohibition) distributedObject).getGuidOfProduct());
        setGoodGroup(((Prohibition) distributedObject).getGoodGroup());
        setGuidOfGoodsGroup(((Prohibition) distributedObject).getGuidOfGoodsGroup());
        setGood(((Prohibition) distributedObject).getGood());
        setGuidOfGoods(((Prohibition) distributedObject).getGuidOfGoods());
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        //DistributedObjectException distributedObjectException = new DistributedObjectException("Client NOT_FOUND_VALUE");
        //distributedObjectException.setData(String.valueOf(idOfClient));
        //Client c;
        //try {
        //    c = DAOUtils.findClient(session, idOfClient);
        //} catch (Exception e) {
        //    throw distributedObjectException;
        //}
        //if (c == null) throw distributedObjectException;
        //setClient(c);
        //
        //Product p = DAOUtils.findDistributedObjectByRefGUID(Product.class, session, guidOfProduct);
        //ProductGroup pg = DAOUtils.findDistributedObjectByRefGUID(ProductGroup.class, session, guidOfProductGroup);
        //Good g = DAOUtils.findDistributedObjectByRefGUID(Good.class, session, guidOfGoods);
        //GoodGroup gg = DAOUtils.findDistributedObjectByRefGUID(GoodGroup.class, session, guidOfGoodsGroup);
        //if(pg != null) {
        //    setProductGroup(pg);
        //    return;
        //}
        //if(gg != null) {
        //    setGoodGroup(gg);
        //    return;
        //}
        //if(p != null) {
        //    setProduct(p);
        //    return;
        //}
        //if(g != null) {
        //    setGood(g);
        //    return;
        //}
        //throw new DistributedObjectException("NOT_FOUND_VALUE");
    }

    private Client client;
    private Long idOfClient;
    private Product product;
    private String guidOfProduct;
    private ProductGroup productGroup;
    private String guidOfProductGroup;
    private Good good;
    private String guidOfGoods;
    private GoodGroup goodGroup;
    private String guidOfGoodsGroup;
    private Set<ProhibitionExclusion> prohibitionExclusionInternal;

    public Set<ProhibitionExclusion> getProhibitionExclusionInternal() {
        return prohibitionExclusionInternal;
    }

    public void setProhibitionExclusionInternal(Set<ProhibitionExclusion> prohibitionExclusionInternal) {
        this.prohibitionExclusionInternal = prohibitionExclusionInternal;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }


    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getGuidOfProduct() {
        return guidOfProduct;
    }

    public void setGuidOfProduct(String guidOfProduct) {
        this.guidOfProduct = guidOfProduct;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductGroup getProductGroup() {
        return productGroup;
    }

    public void setProductGroup(ProductGroup productGroup) {
        this.productGroup = productGroup;
    }

    public String getGuidOfProductGroup() {
        return guidOfProductGroup;
    }

    public void setGuidOfProductGroup(String guidOfProductGroup) {
        this.guidOfProductGroup = guidOfProductGroup;
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }

    public String getGuidOfGoods() {
        return guidOfGoods;
    }

    public void setGuidOfGoods(String guidOfGoods) {
        this.guidOfGoods = guidOfGoods;
    }

    public GoodGroup getGoodGroup() {
        return goodGroup;
    }

    public void setGoodGroup(GoodGroup goodGroup) {
        this.goodGroup = goodGroup;
    }

    public String getGuidOfGoodsGroup() {
        return guidOfGoodsGroup;
    }

    public void setGuidOfGoodsGroup(String guidOfGoodsGroup) {
        this.guidOfGoodsGroup = guidOfGoodsGroup;
    }


}
