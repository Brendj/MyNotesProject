/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TradeMaterialGood;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Session;
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
    public void preProcess(Session session) throws DistributedObjectException {
        InternalIncomingDocument iid  = DAOUtils.findDistributedObjectByRefGUID(InternalIncomingDocument.class, session, guidOfIID);
        if(iid==null) throw new DistributedObjectException("NOT_FOUND_VALUE InternalIncomingDocument");
        setInternalIncomingDocument(iid);
        Good g  = DAOUtils.findDistributedObjectByRefGUID(Good.class, session, guidOfG);
        if(g==null) throw new DistributedObjectException("NOT_FOUND_VALUE Good");
        setGood(g);
        TradeMaterialGood tmg = DAOUtils.findDistributedObjectByRefGUID(TradeMaterialGood.class, session, guidOfTMG);
        if(tmg!=null) setTradeMaterialGood(tmg);
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "UnitsScale", unitsScale.ordinal());
        XMLUtils.setAttributeIfNotNull(element, "TotalCount", totalCount);
        XMLUtils.setAttributeIfNotNull(element, "NetWeight", netWeight);
        XMLUtils.setAttributeIfNotNull(element, "GoodsCreationDate",
                CalendarUtils.toStringFullDateTimeWithLocalTimeZone(goodsCreationDate));
        XMLUtils.setAttributeIfNotNull(element, "LifeTime", lifeTime);
        XMLUtils.setAttributeIfNotNull(element, "IncomingPrice", incomingPrice);
        XMLUtils.setAttributeIfNotNull(element, "NDS", nds);
        XMLUtils.setAttributeIfNotNull(element, "GuidOfInternalIncomingDocument", internalIncomingDocument.getGuid());
        if (tradeMaterialGood != null)
            XMLUtils.setAttributeIfNotNull(element, "GuidOfTradeMaterialGoods", tradeMaterialGood.getGuid());
        XMLUtils.setAttributeIfNotNull(element, "GuidOfGoods", good.getGuid());
    }

    @Override
    protected InternalIncomingDocumentPosition parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null)
            setOrgOwner(longOrgOwner);
        Date dateOfGoodsCreationDate = XMLUtils.getDateTimeAttributeValue(node, "GoodsCreationDate");
        if (dateOfGoodsCreationDate != null)
            setGoodsCreationDate(dateOfGoodsCreationDate);
        Integer integerUnitsScale = XMLUtils.getIntegerAttributeValue(node, "UnitsScale");
        if (integerUnitsScale != null)
            setUnitsScale(UnitScale.fromInteger(integerUnitsScale));
        Long longTotalCount = XMLUtils.getLongAttributeValue(node, "TotalCount");
        if (longTotalCount != null)
            setTotalCount(longTotalCount);
        Long longNetWeight = XMLUtils.getLongAttributeValue(node, "NetWeight");
        if (longTotalCount != null)
            setNetWeight(longNetWeight);
        Long longLifeTime = XMLUtils.getLongAttributeValue(node, "LifeTime");
        if (longLifeTime != null)
            setLifeTime(longLifeTime);
        Long longPrice = XMLUtils.getLongAttributeValue(node, "IncomingPrice");
        if (longPrice != null)
            setIncomingPrice(longPrice);
        Long longNDS = XMLUtils.getLongAttributeValue(node, "NDS");
        if (longNDS != null)
            setNds(longNDS);
        guidOfIID = XMLUtils.getStringAttributeValue(node, "GuidOfInternalIncomingDocument", 36);
        guidOfTMG = XMLUtils.getStringAttributeValue(node, "GuidOfTradeMaterialGoods", 36);
        guidOfG = XMLUtils.getStringAttributeValue(node, "GuidOfGoods", 36);
        setSendAll(SendToAssociatedOrgs.SendToMain);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
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
    private UnitScale unitsScale;
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

    public UnitScale getUnitsScale() {
        return unitsScale;
    }

    public void setUnitsScale(UnitScale unitsScale) {
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
