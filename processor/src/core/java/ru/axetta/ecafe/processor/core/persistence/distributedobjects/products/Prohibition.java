/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Prohibition extends DistributedObject {

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        DistributedObjectException distributedObjectException = new DistributedObjectException("Client NOT_FOUND_VALUE");
        distributedObjectException.setData(String.valueOf(idOfClient));
        Client c;
        try {
            c = (Client) DAOUtils.findClient(session, idOfClient);
        } catch (Exception e) {
            throw distributedObjectException;
        }
        if (c == null) throw distributedObjectException;

        Product p = (Product) DAOUtils.findDistributedObjectByRefGUID(session, guidOfProduct);
        ProductGroup pg = (ProductGroup) DAOUtils.findDistributedObjectByRefGUID(session, guidOfProductGroup);
        Good g = (Good) DAOUtils.findDistributedObjectByRefGUID(session, guidOfGood);
        GoodGroup gg = (GoodGroup) DAOUtils.findDistributedObjectByRefGUID(session, guidOfGoodGroup);
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
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element, "IdOfClient", client.getIdOfClient());
        if(product != null) setAttribute(element,"GuidOfProducts", product.getGuid());
        if(productGroup != null) setAttribute(element,"GuidOfProductGroups", productGroup.getGuid());
        if(good != null) setAttribute(element,"GuidOfGood", good.getGuid());
        if(goodGroup != null) setAttribute(element,"GuidOfGoodsGroup", goodGroup.getGuid());
    }

    @Override
    protected Prohibition parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        idOfClient = getLongAttributeValue(node, "IdOfClient");
        guidOfProduct = getStringAttributeValue(node,"GuidOfProducts",36);
        guidOfProductGroup = getStringAttributeValue(node,"GuidOfProductGroups",36);
        guidOfGood = getStringAttributeValue(node,"GuidOfGood",36);
        guidOfGoodGroup = getStringAttributeValue(node,"GuidOfGoodsGroup",36);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((Prohibition) distributedObject).getOrgOwner());
    }

    private Client client;
    private Long idOfClient;
    private Product product;
    private String guidOfProduct;
    private ProductGroup productGroup;
    private String guidOfProductGroup;
    private Good good;
    private String guidOfGood;
    private GoodGroup goodGroup;
    private String guidOfGoodGroup;

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

    public String getGuidOfGoodGroup() {
        return guidOfGoodGroup;
    }

    public void setGuidOfGoodGroup(String guidOfGoodGroup) {
        this.guidOfGoodGroup = guidOfGoodGroup;
    }


}
