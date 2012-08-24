/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sync.distributionsync.DistributedObjectException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 15.08.12
 * Time: 11:00
 * To change this template use File | Settings | File Templates.
 */
public class GoodRequestPosition extends DistributedObject {

    @Override
    public void preProcess() throws DistributedObjectException {
        GoodRequest gr = DAOService.getInstance().findDistributedObjectByRefGUID(GoodRequest.class, guidOfGR);
        if(gr==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setGoodRequest(gr);
        Good g = DAOService.getInstance().findDistributedObjectByRefGUID(Good.class, guidOfG);
        Product p = DAOService.getInstance().findDistributedObjectByRefGUID(Product.class, guidOfP);
        if(g==null && p==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        if(g!=null) setGood(g);
        if(p!=null) setProduct(p);
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element,"UnitsScale", unitsScale);
        setAttribute(element,"TotalCount", totalCount);
        setAttribute(element,"NetWeight", netWeight);
        setAttribute(element,"GuidOfGoodsRequest", goodRequest.getGuid());
        if(good!=null) setAttribute(element,"GuidOfGoods", good.getGuid());
        if(product!=null) setAttribute(element,"GuidOfBaseProduct", product.getGuid());
    }

    @Override
    protected GoodRequestPosition parseAttributes(Node node) throws ParseException, IOException {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        Integer integerUnitsScale = getIntegerAttributeValue(node, "UnitsScale");
        if(integerUnitsScale!=null) setUnitsScale(integerUnitsScale);
        Long longTotalCount = getLongAttributeValue(node, "TotalCount");
        if(longTotalCount != null) setTotalCount(longTotalCount);
        Long longNetWeight = getLongAttributeValue(node, "NetWeight");
        if( longNetWeight != null) setNetWeight(longNetWeight);
        guidOfGR = getStringAttributeValue(node,"GuidOfGoodsRequest",36);
        guidOfG = getStringAttributeValue(node,"GuidOfGoods",36);
        guidOfP = getStringAttributeValue(node,"GuidOfBaseProduct",36);
        return this;
    }
    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((GoodRequestPosition) distributedObject).getOrgOwner());
        setUnitsScale(((GoodRequestPosition) distributedObject).getUnitsScale());
        setNetWeight(((GoodRequestPosition) distributedObject).getNetWeight());
        setTotalCount(((GoodRequestPosition) distributedObject).getTotalCount());
    }

    private Integer unitsScale;
    private Long totalCount;
    private Long netWeight;
    private Product product;
    private String guidOfP;
    private GoodRequest goodRequest;
    private String guidOfGR;
    private Good good;
    private String guidOfG;

    public String getGuidOfP() {
        return guidOfP;
    }

    public void setGuidOfP(String guidOfP) {
        this.guidOfP = guidOfP;
    }

    public String getGuidOfGR() {
        return guidOfGR;
    }

    public void setGuidOfGR(String guidOfGR) {
        this.guidOfGR = guidOfGR;
    }

    public String getGuidOfG() {
        return guidOfG;
    }

    public void setGuidOfG(String guidOfG) {
        this.guidOfG = guidOfG;
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }

    public GoodRequest getGoodRequest() {
        return goodRequest;
    }

    public void setGoodRequest(GoodRequest goodRequest) {
        this.goodRequest = goodRequest;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Long getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(Long netWeight) {
        this.netWeight = netWeight;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getUnitsScale() {
        return unitsScale;
    }

    public void setUnitsScale(Integer unitsScale) {
        this.unitsScale = unitsScale;
    }

}
