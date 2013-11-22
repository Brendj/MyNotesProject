/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.12
 * Time: 17:29
 * To change this template use File | Settings | File Templates.
 */
public class WayBillPosition extends SupplierRequestDistributedObject {

    private UnitScale unitsScale;
    private Long totalCount;
    private Long netWeight;
    private Long grossWeight;
    private Date goodsCreationDate;
    private Long lifeTime;
    private Long price;
    private Long nds;
    private Good good;
    private String guidOfG;
    private WayBill wayBill;
    private String guidOfWB;

    @Override
    protected boolean addReceiverRestriction(Criteria criteria, Session session, String supplierOrgId, boolean isReceiver) {
        criteria.add(Restrictions.eq(isReceiver?"w.receiver":"w.shipper", supplierOrgId));
        return true;
    }

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("good","g", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("wayBill","w", JoinType.LEFT_OUTER_JOIN);
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("guid"), "guid");
        projectionList.add(Projections.property("globalId"), "globalId");
        projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");

        projectionList.add(Projections.property("unitsScale"), "unitsScale");
        projectionList.add(Projections.property("totalCount"), "totalCount");
        projectionList.add(Projections.property("netWeight"), "netWeight");
        projectionList.add(Projections.property("grossWeight"), "grossWeight");
        projectionList.add(Projections.property("goodsCreationDate"), "goodsCreationDate");
        projectionList.add(Projections.property("lifeTime"), "lifeTime");
        projectionList.add(Projections.property("price"), "price");
        projectionList.add(Projections.property("nds"), "nds");

        projectionList.add(Projections.property("g.guid"), "guidOfG");
        projectionList.add(Projections.property("w.guid"), "guidOfWB");
        criteria.setProjection(projectionList);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        Good g = DAOUtils.findDistributedObjectByRefGUID(Good.class, session, guidOfG);
        if(g==null) throw new DistributedObjectException("NOT_FOUND_VALUE Good");
        setGood(g);
        WayBill wb = DAOUtils.findDistributedObjectByRefGUID(WayBill.class, session, guidOfWB);
        if(wb==null) throw new DistributedObjectException("NOT_FOUND_VALUE WayBill");
        setWayBill(wb);
        if(!wb.getOrgOwner().equals(orgOwner)){
            orgOwner = wb.getOrgOwner();
        }
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "UnitsScale", unitsScale.ordinal());
        XMLUtils.setAttributeIfNotNull(element, "TotalCount", totalCount);
        XMLUtils.setAttributeIfNotNull(element, "NetWeight", netWeight);
        XMLUtils.setAttributeIfNotNull(element, "GrossWeight", grossWeight);
        XMLUtils.setAttributeIfNotNull(element, "GoodsCreationDate",
                CalendarUtils.toStringFullDateTimeWithLocalTimeZone(goodsCreationDate));
        XMLUtils.setAttributeIfNotNull(element, "LifeTime", lifeTime);
        XMLUtils.setAttributeIfNotNull(element, "Price", price);
        XMLUtils.setAttributeIfNotNull(element, "NDS", nds);
        XMLUtils.setAttributeIfNotNull(element, "GuidOfGoods", guidOfG);
        XMLUtils.setAttributeIfNotNull(element, "GuidOfWayBill", guidOfWB);
    }

    @Override
    protected WayBillPosition parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null)
            setOrgOwner(longOrgOwner);
        else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        Integer integerUnitsScale = XMLUtils.getIntegerAttributeValue(node, "UnitsScale");
        if (integerUnitsScale != null)
            setUnitsScale(UnitScale.fromInteger(integerUnitsScale));
        Long longTotalCount = XMLUtils.getLongAttributeValue(node, "TotalCount");
        if (longTotalCount != null)
            setTotalCount(longTotalCount);
        Long longNetWeight = XMLUtils.getLongAttributeValue(node, "NetWeight");
        if (longTotalCount != null)
            setNetWeight(longNetWeight);
        Long longGrossWeight = XMLUtils.getLongAttributeValue(node, "GrossWeight");
        if (longGrossWeight != null)
            setGrossWeight(longGrossWeight);
        Date dateNameOfGood = XMLUtils.getDateTimeAttributeValue(node, "GoodsCreationDate");
        if (dateNameOfGood != null)
            setGoodsCreationDate(dateNameOfGood);
        Long longLifeTime = XMLUtils.getLongAttributeValue(node, "LifeTime");
        if (longLifeTime != null)
            setLifeTime(longLifeTime);
        Long longPrice = XMLUtils.getLongAttributeValue(node, "Price");
        if (longPrice != null)
            setPrice(longPrice);
        Long longNDS = XMLUtils.getLongAttributeValue(node, "NDS");
        if (longNDS != null)
            setNds(longNDS);
        guidOfG = XMLUtils.getStringAttributeValue(node, "GuidOfGoods", 36);
        guidOfWB = XMLUtils.getStringAttributeValue(node, "GuidOfWayBill", 36);
        setSendAll(SendToAssociatedOrgs.SendToMain);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setGoodsCreationDate(((WayBillPosition) distributedObject).getGoodsCreationDate());
        setLifeTime(((WayBillPosition) distributedObject).getLifeTime());
        setUnitsScale(((WayBillPosition) distributedObject).getUnitsScale());
        setTotalCount(((WayBillPosition) distributedObject).getTotalCount());
        setNetWeight(((WayBillPosition) distributedObject).getNetWeight());
        setGrossWeight(((WayBillPosition) distributedObject).getGrossWeight());
        setPrice(((WayBillPosition) distributedObject).getPrice());
        setNds(((WayBillPosition) distributedObject).getNds());
    }

    public UnitScale getUnitsScale() {
        return unitsScale;
    }

    public void setUnitsScale(UnitScale unitsScale) {
        this.unitsScale = unitsScale;
    }

    public String getGuidOfG() {
        return guidOfG;
    }

    public void setGuidOfG(String guidOfG) {
        this.guidOfG = guidOfG;
    }

    public String getGuidOfWB() {
        return guidOfWB;
    }

    public void setGuidOfWB(String guidOfWB) {
        this.guidOfWB = guidOfWB;
    }

    public WayBill getWayBill() {
        return wayBill;
    }

    public void setWayBill(WayBill wayBill) {
        this.wayBill = wayBill;
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }

    public Long getNds() {
        return nds;
    }

    public void setNds(Long nds) {
        this.nds = nds;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
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

    public Long getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(Long grossWeight) {
        this.grossWeight = grossWeight;
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

}
