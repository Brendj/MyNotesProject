/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ConfigurationProviderDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier.InternalDisposingDocumentPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier.InternalIncomingDocumentPosition;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.sql.JoinType;
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
public class TradeMaterialGood extends ConfigurationProviderDistributedObject {

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("good","g", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("guid"), "guid");
        projectionList.add(Projections.property("globalId"), "globalId");
        projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");

        projectionList.add(Projections.property("goodsCreationDate"), "goodsCreationDate");
        projectionList.add(Projections.property("lifeTime"), "lifeTime");
        projectionList.add(Projections.property("unitScale"), "unitScale");
        projectionList.add(Projections.property("totalCount"), "totalCount");
        projectionList.add(Projections.property("netWeight"), "netWeight");
        projectionList.add(Projections.property("nds"), "nds");

        projectionList.add(Projections.property("g.guid"), "guidOfG");

        criteria.setProjection(projectionList);
    }

    @Override
    protected void beforeProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        Good g  = DAOUtils.findDistributedObjectByRefGUID(Good.class, session, guidOfG);
        if(g==null) throw new DistributedObjectException("NOT_FOUND_VALUE");
        setGood(g);
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "GoodsCreationDate",
                CalendarUtils.toStringFullDateTimeWithLocalTimeZone(goodsCreationDate));
        XMLUtils.setAttributeIfNotNull(element, "LifeTime", lifeTime);
        XMLUtils.setAttributeIfNotNull(element, "UnitsScale", unitScale.ordinal());
        XMLUtils.setAttributeIfNotNull(element, "TotalCount", totalCount);
        XMLUtils.setAttributeIfNotNull(element, "NetWeight", netWeight);
        XMLUtils.setAttributeIfNotNull(element, "SelfPrice", selfPrice);
        XMLUtils.setAttributeIfNotNull(element, "NDS", nds);
        XMLUtils.setAttributeIfNotNull(element, "GuidOfGoods", guidOfG);
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setGoodsCreationDate(((TradeMaterialGood) distributedObject).getGoodsCreationDate());
        setLifeTime(((TradeMaterialGood) distributedObject).getLifeTime());
        setUnitScale(((TradeMaterialGood) distributedObject).getUnitScale());
        setTotalCount(((TradeMaterialGood) distributedObject).getTotalCount());
        setNetWeight(((TradeMaterialGood) distributedObject).getNetWeight());
        setSelfPrice(((TradeMaterialGood) distributedObject).getSelfPrice());
        setNds(((TradeMaterialGood) distributedObject).getNds());
        setGood(((TradeMaterialGood) distributedObject).getGood());
        setGuidOfG(((TradeMaterialGood) distributedObject).getGuidOfG());
        setIdOfConfigurationProvider(((TradeMaterialGood) distributedObject).getIdOfConfigurationProvider());
    }

    @Override
    protected TradeMaterialGood parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null){
            setOrgOwner(longOrgOwner);
        } else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        Date dateNameOfGood = XMLUtils.getDateTimeAttributeValue(node, "GoodsCreationDate");
        if (dateNameOfGood != null)
            setGoodsCreationDate(dateNameOfGood);
        Long longLifeTime = XMLUtils.getLongAttributeValue(node, "LifeTime");
        if (longLifeTime != null)
            setLifeTime(longLifeTime);
        Integer integerUnitsScale = XMLUtils.getIntegerAttributeValue(node, "UnitsScale");
        if (integerUnitsScale != null)
            setUnitScale(UnitScale.fromInteger(integerUnitsScale));
        Long longTotalCount = XMLUtils.getLongAttributeValue(node, "TotalCount");
        if (longTotalCount != null)
            setTotalCount(longTotalCount);
        Long longNetWeight = XMLUtils.getLongAttributeValue(node, "NetWeight");
        if (longNetWeight != null)
            setNetWeight(longNetWeight);
        Long longSelfPrice = XMLUtils.getLongAttributeValue(node, "SelfPrice");
        if (longSelfPrice != null)
            setSelfPrice(longSelfPrice);
        Long longNDS = XMLUtils.getLongAttributeValue(node, "NDS");
        if (longNDS != null)
            setNds(longNDS);
        guidOfG = XMLUtils.getStringAttributeValue(node, "GuidOfGoods", 36);
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
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
