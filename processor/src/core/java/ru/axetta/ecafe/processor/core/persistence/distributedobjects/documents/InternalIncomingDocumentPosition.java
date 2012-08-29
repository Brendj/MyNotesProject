/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TradeMaterialGood;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sync.distributionsync.DistributedObjectException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.12
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class InternalIncomingDocumentPosition extends DistributedObject {

    @Override
    public void preProcess() throws DistributedObjectException {
        InternalIncomingDocument iid = DAOService.getInstance().findDistributedObjectByRefGUID(InternalIncomingDocument.class,guidOfIID);
        if(iid==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setInternalIncomingDocument(iid);
        Good g = DAOService.getInstance().findDistributedObjectByRefGUID(Good.class,guidOfG);
        if(g==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setGood(g);
        TradeMaterialGood tmg = DAOService.getInstance().findDistributedObjectByRefGUID(TradeMaterialGood.class, guidOfTMG);
        if(tmg!=null) setTradeMaterialGood(tmg);
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element,"UnitsScale", unitsScale);
        setAttribute(element,"TotalCount", totalCount);
        setAttribute(element, "NetWeight", netWeight);
        setAttribute(element,"GoodsCreationDate", getDateFormat().format(goodsCreationDate));
        setAttribute(element,"LifeTime", lifeTime);
        setAttribute(element,"IncomingPrice", incomingPrice);
        setAttribute(element,"NDS", nds);
        setAttribute(element, "GuidOfInternalIncomingDocument", internalIncomingDocument.getGuid());
        if(tradeMaterialGood != null) setAttribute(element, "GuidOfTradeMaterialGoods", tradeMaterialGood.getGuid());
        setAttribute(element, "GuidOfGoods", good.getGuid());
    }

    @Override
    protected InternalIncomingDocumentPosition parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        Date dateOfGoodsCreationDate = getDateTimeAttributeValue(node, "GoodsCreationDate");
        if(dateOfGoodsCreationDate != null) setGoodsCreationDate(dateOfGoodsCreationDate);
        Long integerUnitsScale = getLongAttributeValue(node, "UnitsScale");
        if(integerUnitsScale != null) setUnitsScale(integerUnitsScale);
        Long longTotalCount = getLongAttributeValue(node, "TotalCount");
        if(longTotalCount != null) setTotalCount(longTotalCount);
        Long longNetWeight = getLongAttributeValue(node, "NetWeight");
        if(longTotalCount != null) setNetWeight(longNetWeight);
        Long longLifeTime = getLongAttributeValue(node, "LifeTime");
        if(longLifeTime != null) setLifeTime(longLifeTime);
        Long longPrice = getLongAttributeValue(node, "IncomingPrice");
        if(longPrice != null) setIncomingPrice(longPrice);
        Long longNDS = getLongAttributeValue(node,"NDS");
        if(longNDS != null) setNds(longNDS);
        guidOfIID = getStringAttributeValue(node,"GuidOfInternalIncomingDocument",36);
        guidOfTMG = getStringAttributeValue(node,"GuidOfTradeMaterialGoods",36);
        guidOfG = getStringAttributeValue(node,"GuidOfGoods",36);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((InternalIncomingDocumentPosition) distributedObject).getOrgOwner());
        setGoodsCreationDate(((InternalIncomingDocumentPosition) distributedObject).getGoodsCreationDate());
        setLifeTime(((InternalIncomingDocumentPosition) distributedObject).getLifeTime());
        setUnitsScale(((InternalIncomingDocumentPosition) distributedObject).getUnitsScale());
        setTotalCount(((InternalIncomingDocumentPosition) distributedObject).getTotalCount());
        setNetWeight(((InternalIncomingDocumentPosition) distributedObject).getNetWeight());
        setIncomingPrice(((InternalIncomingDocumentPosition) distributedObject).getIncomingPrice());
        setNds(((InternalIncomingDocumentPosition) distributedObject).getNds());
    }

    private Date goodsCreationDate;
    private Long lifeTime;
    private Long unitsScale;
    private Long totalCount;
    private Long netWeight;
    private Long incomingPrice;
    private Long nds;
    private InternalIncomingDocument internalIncomingDocument;
    private String guidOfIID;
    private TradeMaterialGood tradeMaterialGood;
    private String guidOfTMG;
    private Good good;
    private String guidOfG;

    public String getGuidOfIID() {
        return guidOfIID;
    }

    public void setGuidOfIID(String guidOfIID) {
        this.guidOfIID = guidOfIID;
    }

    public String getGuidOfTMG() {
        return guidOfTMG;
    }

    public void setGuidOfTMG(String guidOfTMG) {
        this.guidOfTMG = guidOfTMG;
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

    public TradeMaterialGood getTradeMaterialGood() {
        return tradeMaterialGood;
    }

    public void setTradeMaterialGood(TradeMaterialGood tradeMaterialGood) {
        this.tradeMaterialGood = tradeMaterialGood;
    }

    public InternalIncomingDocument getInternalIncomingDocument() {
        return internalIncomingDocument;
    }

    public void setInternalIncomingDocument(InternalIncomingDocument internalIncomingDocument) {
        this.internalIncomingDocument = internalIncomingDocument;
    }

    public Long getNds() {
        return nds;
    }

    public void setNds(Long nds) {
        this.nds = nds;
    }

    public Long getIncomingPrice() {
        return incomingPrice;
    }

    public void setIncomingPrice(Long incomingPrice) {
        this.incomingPrice = incomingPrice;
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

    public Long getUnitsScale() {
        return unitsScale;
    }

    public void setUnitsScale(Long unitsScale) {
        this.unitsScale = unitsScale;
    }

    public Long getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(Long lifeTime) {
        this.lifeTime = lifeTime;
    }

    public Date getGoodsCreationDate() {
        return goodsCreationDate;
    }

    public void setGoodsCreationDate(Date goodsCreationDate) {
        this.goodsCreationDate = goodsCreationDate;
    }
}
