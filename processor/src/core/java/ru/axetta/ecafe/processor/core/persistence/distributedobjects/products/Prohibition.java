/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Set;

public class Prohibition extends DistributedObject {

    private Set<ProhibitionExclusion> prohibitionExclusionInternal;

    public Set<ProhibitionExclusion> getProhibitionExclusionInternal() {
        return prohibitionExclusionInternal;
    }

    public void setProhibitionExclusionInternal(Set<ProhibitionExclusion> prohibitionExclusionInternal) {
        this.prohibitionExclusionInternal = prohibitionExclusionInternal;
    }

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        DistributedObjectException distributedObjectException = new DistributedObjectException("Client NOT_FOUND_VALUE");
        distributedObjectException.setData(String.valueOf(idOfClient));
        Client c;
        try {
            c = DAOUtils.findClient(session, idOfClient);
        } catch (Exception e) {
            throw distributedObjectException;
        }
        if (c == null) throw distributedObjectException;
        setClient(c);

        Product p = DAOUtils.findDistributedObjectByRefGUID(Product.class, session, guidOfProduct);
        ProductGroup pg = DAOUtils.findDistributedObjectByRefGUID(ProductGroup.class, session, guidOfProductGroup);
        Good g = DAOUtils.findDistributedObjectByRefGUID(Good.class, session, guidOfGoods);
        GoodGroup gg = DAOUtils.findDistributedObjectByRefGUID(GoodGroup.class, session, guidOfGoodsGroup);
        if(pg != null) {
            setProductGroup(pg);
            return;
        }
        if(gg != null) {
            setGoodGroup(gg);
            return;
        }
        if(p != null) {
            setProduct(p);
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
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "IdOfClient", client.getIdOfClient());
        if (product != null)
            XMLUtils.setAttributeIfNotNull(element, "GuidOfProduct", product.getGuid());
        if (productGroup != null)
            XMLUtils.setAttributeIfNotNull(element, "GuidOfProductGroup", productGroup.getGuid());
        if (good != null)
            XMLUtils.setAttributeIfNotNull(element, "GuidOfGoods", good.getGuid());
        if (goodGroup != null)
            XMLUtils.setAttributeIfNotNull(element, "GuidOfGoodsGroup", goodGroup.getGuid());
    }

    @Override
    protected Prohibition parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null)
            setOrgOwner(longOrgOwner);
        idOfClient = XMLUtils.getLongAttributeValue(node, "IdOfClient");
        guidOfProduct = XMLUtils.getStringAttributeValue(node, "GuidOfProduct", 36);
        guidOfProductGroup = XMLUtils.getStringAttributeValue(node, "GuidOfProductGroup", 36);
        guidOfGoods = XMLUtils.getStringAttributeValue(node, "GuidOfGoods", 36);
        guidOfGoodsGroup = XMLUtils.getStringAttributeValue(node, "GuidOfGoodsGroup", 36);
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
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
