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
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.12
 * Time: 17:29
 * To change this template use File | Settings | File Templates.
 */
public class ActOfWayBillDifferencePosition extends SupplierRequestDistributedObject {

    private Good good;
    private String guidOfG;
    private Date goodsCreationDate;
    private Long lifeTime;
    private UnitScale unitsScale;
    private Long totalCount;
    private Long netWeight;
    private Long grossWeight;
    private Long price;
    private Long nds;
    private ActOfWayBillDifference actOfWayBillDifference;
    private String guidOfAWD;

    @Override
    @SuppressWarnings("unchecked")
    protected boolean addReceiverRestriction(Criteria criteria, Session session, String supplierOrgId, boolean isReceiver) {
        final String s = "select distinct ad.globalId from WayBill wb left join wb.actOfWayBillDifference ad where ";
        final String queryString = s +(isReceiver?"wb.receiver":"wb.shipper")+"=:idOdOrg and ad!=null";
        Query query = session.createQuery(queryString);
        query.setParameter("idOdOrg", supplierOrgId);
        List<Long> ids = query.list();
        if(ids!=null && !ids.isEmpty()) {
            criteria.add(Restrictions.in("a.globalId", ids));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("good","g", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("actOfWayBillDifference","a", JoinType.LEFT_OUTER_JOIN);
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
        projectionList.add(Projections.property("a.guid"), "guidOfAWD");
        criteria.setProjection(projectionList);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        Good g  = DAOUtils.findDistributedObjectByRefGUID(Good.class, session, guidOfG);
        if(g==null) {
            DistributedObjectException distributedObjectException = new DistributedObjectException("NOT_FOUND GOOD");
            distributedObjectException.setData(guidOfG);
            throw distributedObjectException;
        }
        setGood(g);
        ActOfWayBillDifference awd  = DAOUtils.findDistributedObjectByRefGUID(ActOfWayBillDifference.class, session, guidOfAWD);
        if(awd==null) {
            DistributedObjectException distributedObjectException = new DistributedObjectException("NOT_FOUND_"+actOfWayBillDifference.getClass().getSimpleName().toUpperCase());
            distributedObjectException.setData(guidOfG);
            throw distributedObjectException;
        }
        setActOfWayBillDifference(awd);
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "UnitsScale", unitsScale.ordinal());
        XMLUtils.setAttributeIfNotNull(element, "TotalCount", totalCount);
        XMLUtils.setAttributeIfNotNull(element, "NetWeight", netWeight);
        XMLUtils.setAttributeIfNotNull(element, "GrossWeight", grossWeight);
        XMLUtils.setAttributeIfNotNull(element, "GoodsCreationDate", CalendarUtils.toStringFullDateTimeWithLocalTimeZone(goodsCreationDate));
        XMLUtils.setAttributeIfNotNull(element, "LifeTime", lifeTime);
        XMLUtils.setAttributeIfNotNull(element, "Price", price);
        XMLUtils.setAttributeIfNotNull(element, "NDS", nds);
        XMLUtils.setAttributeIfNotNull(element, "GuidOfGoods", guidOfG);
        XMLUtils.setAttributeIfNotNull(element, "GuidOfActOfDifference", guidOfAWD);
    }

    @Override
    protected ActOfWayBillDifferencePosition parseAttributes(Node node) throws Exception {
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
        guidOfAWD = XMLUtils.getStringAttributeValue(node, "GuidOfActOfDifference", 36);
        setSendAll(SendToAssociatedOrgs.SendToMain);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setGoodsCreationDate(((ActOfWayBillDifferencePosition) distributedObject).getGoodsCreationDate());
        setLifeTime(((ActOfWayBillDifferencePosition) distributedObject).getLifeTime());
        setUnitsScale(((ActOfWayBillDifferencePosition) distributedObject).getUnitsScale());
        setTotalCount(((ActOfWayBillDifferencePosition) distributedObject).getTotalCount());
        setNetWeight(((ActOfWayBillDifferencePosition) distributedObject).getNetWeight());
        setGrossWeight(((ActOfWayBillDifferencePosition) distributedObject).getGrossWeight());
        setPrice(((ActOfWayBillDifferencePosition) distributedObject).getPrice());
        setNds(((ActOfWayBillDifferencePosition) distributedObject).getNds());
    }

    public UnitScale getUnitsScale() {
        return unitsScale;
    }

    public void setUnitsScale(UnitScale unitsScale) {
        this.unitsScale = unitsScale;
    }


    public String getGuidOfAWD() {
        return guidOfAWD;
    }

    public void setGuidOfAWD(String guidOfAWD) {
        this.guidOfAWD = guidOfAWD;
    }

    public ActOfWayBillDifference getActOfWayBillDifference() {
        return actOfWayBillDifference;
    }

    public void setActOfWayBillDifference(ActOfWayBillDifference actOfWayBillDifference) {
        this.actOfWayBillDifference = actOfWayBillDifference;
    }

    public Long getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(Long grossWeight) {
        this.grossWeight = grossWeight;
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

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
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
