/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 15.08.12
 * Time: 11:00
 * To change this template use File | Settings | File Templates.
 */
public class GoodRequestPosition extends DistributedObject {

    public static final String[] UNIT_SCALES = {"граммы", "миллиметры", "порции", "единицы"};

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        GoodRequest gr =  (GoodRequest) DAOUtils.findDistributedObjectByRefGUID(session, guidOfGR);
        if(gr==null) throw new DistributedObjectException("NOT_FOUND_VALUE GoodRequest");
        setGoodRequest(gr);
        Good g = (Good) DAOUtils.findDistributedObjectByRefGUID(session, guidOfG);
        Product p =(Product) DAOUtils.findDistributedObjectByRefGUID(session, guidOfP);
        if(g==null && p==null) {
            throw new DistributedObjectException("NOT_FOUND_VALUE Product or Good");
        }
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
    protected GoodRequestPosition parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        Integer integerUnitsScale = getIntegerAttributeValue(node, "UnitsScale");
        if(integerUnitsScale!=null) setUnitsScale(UnitScale.fromInteger(integerUnitsScale));
        Long longTotalCount = getLongAttributeValue(node, "TotalCount");
        if(longTotalCount != null) setTotalCount(longTotalCount);
        Long longNetWeight = getLongAttributeValue(node, "NetWeight");
        if( longNetWeight != null) setNetWeight(longNetWeight);
        guidOfGR = getStringAttributeValue(node,"GuidOfGoodsRequest",36);
        guidOfG = getStringAttributeValue(node,"GuidOfGoods",36);
        guidOfP = getStringAttributeValue(node,"GuidOfBaseProduct",36);
        setSendAll(SendToAssociatedOrgs.SendToMain);
        return this;
    }
    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((GoodRequestPosition) distributedObject).getOrgOwner());
        setUnitsScale(((GoodRequestPosition) distributedObject).getUnitsScale());
        setNetWeight(((GoodRequestPosition) distributedObject).getNetWeight());
        setTotalCount(((GoodRequestPosition) distributedObject).getTotalCount());
    }

    private UnitScale unitsScale;
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

    public String getTotalCountPattern(){
        //if(unitsScale>1){
        //    return "#0";
        //} else {
            return "#0.000";
        //}
    }

    public UnitScale getUnitsScale() {
        return unitsScale;
    }

    public void setUnitsScale(UnitScale unitsScale) {
        this.unitsScale = unitsScale;
    }

    public String getUnitsScaleValue() {
        //if ((unitsScale != null) && (unitsScale >= 0) && (unitsScale <= UNIT_SCALES.length)) {
        //    return UNIT_SCALES[unitsScale];
        //} else {
        //    return "";
        //}
        return unitsScale.toString();
    }

    public String getCurrentElementValue() {
        if (product != null) {
            return product.getProductName();
        } else {
            return good.getNameOfGood();
        }
    }

}
