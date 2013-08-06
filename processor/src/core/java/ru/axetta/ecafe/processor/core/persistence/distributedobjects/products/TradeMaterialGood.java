/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.InternalDisposingDocumentPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.InternalIncomingDocumentPosition;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.12
 * Time: 17:29
 * To change this template use File | Settings | File Templates.
 */
public class TradeMaterialGood extends DistributedObject {

    //public static final String[] UNIT_SCALES = {"граммы", "миллиметры", "порции", "единицы"};
    private Set<InternalIncomingDocumentPosition> internalIncomingDocumentPositionInternal;
    private Set<InternalDisposingDocumentPosition> internalDisposingDocumentPositionInternal;

    public Set<InternalDisposingDocumentPosition> getInternalDisposingDocumentPositionInternal() {
        return internalDisposingDocumentPositionInternal;
    }

    public void setInternalDisposingDocumentPositionInternal(
            Set<InternalDisposingDocumentPosition> internalDisposingDocumentPositionInternal) {
        this.internalDisposingDocumentPositionInternal = internalDisposingDocumentPositionInternal;
    }

    public Set<InternalIncomingDocumentPosition> getInternalIncomingDocumentPositionInternal() {
        return internalIncomingDocumentPositionInternal;
    }

    public void setInternalIncomingDocumentPositionInternal(
            Set<InternalIncomingDocumentPosition> internalIncomingDocumentPositionInternal) {
        this.internalIncomingDocumentPositionInternal = internalIncomingDocumentPositionInternal;
    }

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        Good g  = DAOUtils.findDistributedObjectByRefGUID(Good.class, session, guidOfG);
        if(g==null) throw new DistributedObjectException("NOT_FOUND_VALUE");
        setGood(g);
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element,"GoodsCreationDate", getDateFormat().format(goodsCreationDate));
        setAttribute(element,"LifeTime", lifeTime);
        setAttribute(element,"UnitsScale", unitScale.ordinal());
        setAttribute(element,"TotalCount", totalCount);
        setAttribute(element,"NetWeight", netWeight);
        setAttribute(element,"SelfPrice", selfPrice);
        setAttribute(element,"NDS", nds);
        setAttribute(element,"GuidOfGoods", good.getGuid());
    }

    @Override
    protected TradeMaterialGood parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        Date dateNameOfGood = getDateTimeAttributeValue(node, "GoodsCreationDate");
        if(dateNameOfGood!=null) setGoodsCreationDate(dateNameOfGood);
        Long longLifeTime = getLongAttributeValue(node, "LifeTime");
        if(longLifeTime != null) setLifeTime(longLifeTime);
        Integer integerUnitsScale = getIntegerAttributeValue(node,"UnitsScale");
        if(integerUnitsScale != null) setUnitScale(UnitScale.fromInteger(integerUnitsScale));
        Long longTotalCount = getLongAttributeValue(node, "TotalCount");
        if(longTotalCount != null) setTotalCount(longTotalCount);
        Long longNetWeight = getLongAttributeValue(node,"NetWeight");
        if(longNetWeight != null) setNetWeight(longNetWeight);
        Long longSelfPrice = getLongAttributeValue(node, "SelfPrice");
        if(longSelfPrice != null) setSelfPrice(longSelfPrice);
        Long longNDS = getLongAttributeValue(node,"NDS");
        if(longNDS != null) setNds(longNDS);
        guidOfG = getStringAttributeValue(node,"GuidOfGoods",36);
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((TradeMaterialGood) distributedObject).getOrgOwner());
        setGoodsCreationDate(((TradeMaterialGood) distributedObject).getGoodsCreationDate());
        setLifeTime(((TradeMaterialGood) distributedObject).getLifeTime());
        setUnitScale(((TradeMaterialGood) distributedObject).getUnitScale());
        setTotalCount(((TradeMaterialGood) distributedObject).getTotalCount());
        setNetWeight(((TradeMaterialGood) distributedObject).getNetWeight());
        setSelfPrice(((TradeMaterialGood) distributedObject).getSelfPrice());
        setNds(((TradeMaterialGood) distributedObject).getNds());
    }

    private Good good;
    private String guidOfG;
    private Date goodsCreationDate;
    private Long lifeTime;
    private UnitScale unitScale;
    private Long totalCount;
    private Long netWeight;
    private Long selfPrice;
    private Long nds;

    public String getGuidOfG() {
        return guidOfG;
    }

    public void setGuidOfG(String guidOfG) {
        this.guidOfG = guidOfG;
    }

    public Long getNds() {
        return nds;
    }

    public void setNds(Long nds) {
        this.nds = nds;
    }

    public Long getSelfPrice() {
        return selfPrice;
    }

    public void setSelfPrice(Long selfPrice) {
        this.selfPrice = selfPrice;
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

    public UnitScale getUnitScale() {
        return unitScale;
    }

    public void setUnitScale(UnitScale unitsScale) {
        this.unitScale = unitsScale;
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

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }
}
