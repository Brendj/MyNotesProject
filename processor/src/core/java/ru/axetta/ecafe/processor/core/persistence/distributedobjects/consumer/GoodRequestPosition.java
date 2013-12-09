/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
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
 * Date: 15.08.12
 * Time: 11:00
 * Позиции требований
 */
public class GoodRequestPosition extends ConsumerRequestDistributedObject {

    private UnitScale unitsScale;
    private Long totalCount;
    private Long dailySampleCount; // суточная проба
    private Long netWeight;
    private String updateHistory;
    private Product product;
    private String guidOfP;
    private GoodRequest goodRequest;
    private String guidOfGR;
    private Good good;
    private String guidOfG;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("goodRequest","gr", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("good","g", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("product","p", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("unitsScale"), "unitsScale");
        projectionList.add(Projections.property("totalCount"), "totalCount");
        projectionList.add(Projections.property("dailySampleCount"), "dailySampleCount"); // суточная проба
        projectionList.add(Projections.property("netWeight"), "netWeight");
        projectionList.add(Projections.property("updateHistory"), "updateHistory");

        projectionList.add(Projections.property("gr.guid"), "guidOfGR");
        projectionList.add(Projections.property("g.guid"), "guidOfG");
        projectionList.add(Projections.property("p.guid"), "guidOfP");
        criteria.setProjection(projectionList);
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "UnitsScale", unitsScale.ordinal());
        XMLUtils.setAttributeIfNotNull(element, "TotalCount", totalCount);
        XMLUtils.setAttributeIfNotNull(element, "DailySampleCount", dailySampleCount);  // суточная проба
        XMLUtils.setAttributeIfNotNull(element, "NetWeight", netWeight);
        //XMLUtils.setAttributeIfNotNull(element, "UpdateHistory", updateHistory);
        if (!StringUtils.isEmpty(guidOfGR))
            XMLUtils.setAttributeIfNotNull(element, "GuidOfGoodsRequest", guidOfGR);
        else {
            System.out.println(guid+ ": guidOfGR "+guidOfGR);
        }
        if (!StringUtils.isEmpty(guidOfG))
            XMLUtils.setAttributeIfNotNull(element, "GuidOfGoods", guidOfG);
        if (!StringUtils.isEmpty(guidOfP))
            XMLUtils.setAttributeIfNotNull(element, "GuidOfBaseProduct", guidOfP);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        GoodRequest gr =  DAOUtils.findDistributedObjectByRefGUID(GoodRequest.class, session, guidOfGR);
        if(gr==null) throw new DistributedObjectException("NOT_FOUND_VALUE GOOD_REQUEST");
        setGoodRequest(gr);
        Good g = DAOUtils.findDistributedObjectByRefGUID(Good.class, session, guidOfG);
        Product p = DAOUtils.findDistributedObjectByRefGUID(Product.class, session, guidOfP);
        if(g==null && p==null) {
            throw new DistributedObjectException("NOT_FOUND_VALUE PRODUCT OR GOOD");
        }
        if(g!=null) setGood(g);
        if(p!=null) setProduct(p);
        if(!gr.getOrgOwner().equals(orgOwner)){
            orgOwner = gr.getOrgOwner();
        }
    }

    public void updateVersionFromParent(Session session){
        Criteria criteria = session.createCriteria(DOVersion.class);
        criteria.add(Restrictions.eq("distributedObjectClassName", "GoodRequest"));
        criteria.setMaxResults(1);
        DOVersion version = (DOVersion) criteria.uniqueResult();
        final long newVersion = version.getCurrentVersion()+1;
        version.setCurrentVersion(newVersion);
        session.update(version);
        goodRequest.setLastUpdate(new Date());
        goodRequest.setGlobalVersion(newVersion);
        session.update(goodRequest);
    }

    @Override
    protected GoodRequestPosition parseAttributes(Node node) throws Exception {
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
        Long longDailySampleCount = XMLUtils.getLongAttributeValue(node, "DailySampleCount"); // суточная проба
        if (longDailySampleCount != null)
            setDailySampleCount(longDailySampleCount);
        Long longNetWeight = XMLUtils.getLongAttributeValue(node, "NetWeight");
        if (longNetWeight != null)
            setNetWeight(longNetWeight);
        guidOfGR = XMLUtils.getStringAttributeValue(node, "GuidOfGoodsRequest", 36);
        guidOfG = XMLUtils.getStringAttributeValue(node, "GuidOfGoods", 36);
        guidOfP = XMLUtils.getStringAttributeValue(node, "GuidOfBaseProduct", 36);
        setSendAll(SendToAssociatedOrgs.SendToMain);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setUnitsScale(((GoodRequestPosition) distributedObject).getUnitsScale());
        setNetWeight(((GoodRequestPosition) distributedObject).getNetWeight());
        final Long lastTotalCount = getTotalCount();
        setTotalCount(((GoodRequestPosition) distributedObject).getTotalCount());
        final Long lastDailySampleCount = getDailySampleCount();
        setDailySampleCount(((GoodRequestPosition) distributedObject).getDailySampleCount()); // суточная проба
        String lastHistory = getUpdateHistory();
        Date date = getLastUpdate()!=null? getLastUpdate():getCreatedDate();
        String newHistory="";
        final String strDate = CalendarUtils.dateToString(date);
        if(lastDailySampleCount==null){
            newHistory= String.format("%s %d;", strDate, lastTotalCount/1000);
        } else {
            newHistory= String.format("%s %d %d;", strDate, lastTotalCount/1000, lastDailySampleCount/1000);
        }
        if(StringUtils.isEmpty(lastHistory)){
            setUpdateHistory(newHistory);
        } else {
            setUpdateHistory(String.format("%s%s", lastHistory, newHistory));
        }
    }

    public String getUpdateHistory() {
        return updateHistory;
    }

    public void setUpdateHistory(String updateHistory) {
        this.updateHistory = updateHistory;
    }

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

    public Long getDailySampleCount() {
        return dailySampleCount;
    }

    public void setDailySampleCount(Long dailySampleCount) {
        this.dailySampleCount = dailySampleCount;
    }

    public UnitScale getUnitsScale() {
        return unitsScale;
    }

    public Boolean getFloatScale(){
        return unitsScale.equals(UnitScale.UNITS) || unitsScale.equals(UnitScale.PORTIONS);
    }

    public void setUnitsScale(UnitScale unitsScale) {
        this.unitsScale = unitsScale;
    }

    public String getCurrentElementValue() {
        if (product != null) {
            return product.getProductName();
        } else {
            return good.getNameOfGood();
        }
    }

}
