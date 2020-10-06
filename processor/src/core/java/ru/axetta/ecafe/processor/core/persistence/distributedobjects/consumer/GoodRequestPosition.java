/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ConsumerRequestDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
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
import java.util.List;

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
    /* старые значения всего и суточной пробы */
    private Long lastTotalCount;
    private Long lastDailySampleCount; // суточная проба
    private Long lastTempClientsCount;
    private Long tempClientsCount; //временные клиенты
    private Long netWeight;
    private Product product;
    private String guidOfP;
    private GoodRequest goodRequest;
    private String guidOfGR;
    private Good good;
    private String guidOfG;
    private Boolean notified;
    private InformationContents informationContent = InformationContents.ONLY_CURRENT_ORG;
    private Integer complexId;
    private Long idOfDish;
    private Integer feedingType;

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        if (informationContent == null || informationContent.isDefault()) {
            return super.process(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
        }
        if (DAOUtils.isSupplierByOrg(session, idOfOrg)) {
            return super.process(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
        } else {
            return toFriendlyOrgsProcess(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
        }
    }

    @Override
    public void createProjections(Criteria criteria) {
        Date validDate = CalendarUtils.startOfDay(new Date());
        criteria.createAlias("goodRequest", "gr", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("good", "g", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("product", "p", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("unitsScale"), "unitsScale");
        projectionList.add(Projections.property("totalCount"), "totalCount");
        projectionList.add(Projections.property("dailySampleCount"), "dailySampleCount"); // суточная проба
        projectionList.add(Projections.property("tempClientsCount"), "tempClientsCount");
        projectionList.add(Projections.property("netWeight"), "netWeight");
        projectionList.add(Projections.property("lastTotalCount"), "lastTotalCount");
        projectionList.add(Projections.property("lastDailySampleCount"), "lastDailySampleCount");
        projectionList.add(Projections.property("lastTempClientsCount"), "lastTempClientsCount");
        projectionList.add(Projections.property("complexId"), "complexId");
        projectionList.add(Projections.property("feedingType"), "feedingType");

        projectionList.add(Projections.property("gr.guid"), "guidOfGR");
        projectionList.add(Projections.property("g.guid"), "guidOfG");
        projectionList.add(Projections.property("p.guid"), "guidOfP");
        criteria.setProjection(projectionList);
       //criteria.add((Restrictions.ge("gr.doneDate", validDate)));
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "UnitsScale", unitsScale.ordinal());
        XMLUtils.setAttributeIfNotNull(element, "TotalCount", totalCount);
        XMLUtils.setAttributeIfNotNull(element, "DailySampleCount", dailySampleCount);  // суточная проба
        XMLUtils.setAttributeIfNotNull(element, "TempClientsCount", tempClientsCount);
        if (complexId != null && complexId != 0) {
            element.setAttribute("ComplexId", complexId.toString());
        }
        XMLUtils.setAttributeIfNotNull(element, "NetWeight", netWeight);
        if (!StringUtils.isEmpty(guidOfGR)) {
            XMLUtils.setAttributeIfNotNull(element, "GuidOfGoodsRequest", guidOfGR);
        } else {
            System.out.println(guid + ": guidOfGR " + guidOfGR);
        }
        if (!StringUtils.isEmpty(guidOfG)) {
            XMLUtils.setAttributeIfNotNull(element, "GuidOfGoods", guidOfG);
        }
        if (!StringUtils.isEmpty(guidOfP)) {
            XMLUtils.setAttributeIfNotNull(element, "GuidOfBaseProduct", guidOfP);
        }
        XMLUtils.setAttributeIfNotNull(element, "FeedingType", feedingType);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        GoodRequest gr = DAOUtils.findDistributedObjectByRefGUID(GoodRequest.class, session, guidOfGR);
        complexId = DAOUtils.getComplexIdForGoodRequestPosition(session, guid);
        if (gr == null) {
            throw new DistributedObjectException("NOT_FOUND_VALUE GOOD_REQUEST");
        }
        setGoodRequest(gr);
        Good g = DAOUtils.findDistributedObjectByRefGUID(Good.class, session, guidOfG);
        Product p = DAOUtils.findDistributedObjectByRefGUID(Product.class, session, guidOfP);
        if (g == null && p == null) {
            throw new DistributedObjectException("NOT_FOUND_VALUE PRODUCT OR GOOD");
        }
        if (g != null) {
            setGood(g);
        }
        if (p != null) {
            setProduct(p);
        }
        if (!gr.getOrgOwner().equals(orgOwner)) {
            orgOwner = gr.getOrgOwner();
        }
        if (!isGoodDate(session, orgOwner, gr.getDoneDate(), gr.getRequestType()))
        {
            GoodRequestPosition grp = DAOUtils.findDistributedObjectByRefGUID(GoodRequestPosition.class, session, guid);
            DistributedObjectException distributedObjectException = new DistributedObjectException("CANT_CHANGE_GRP_ON_DATE");
            if (grp != null)
                distributedObjectException.setData("TC="+ grp.getTotalCount() + ", DSC=" + grp.getDailySampleCount() + ", TCC=" + grp.getTempClientsCount());
            throw distributedObjectException;
        }
    }

    @Override
    protected GoodRequestPosition parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null) {
            setOrgOwner(longOrgOwner);
        } else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        Integer integerUnitsScale = XMLUtils.getIntegerAttributeValue(node, "UnitsScale");
        if (integerUnitsScale != null) {
            setUnitsScale(UnitScale.fromInteger(integerUnitsScale));
        }
        Long longTotalCount = XMLUtils.getLongAttributeValue(node, "TotalCount");
        if (longTotalCount != null) {
            setTotalCount(longTotalCount);
        }
        Integer intComplexId = XMLUtils.getIntegerAttributeValue(node, "ComplexId");
        if (intComplexId != null) {
            setComplexId(intComplexId);
        }
        Long longDailySampleCount = XMLUtils.getLongAttributeValue(node, "DailySampleCount"); // суточная проба
        if (longDailySampleCount != null) {
            setDailySampleCount(longDailySampleCount);
        }
        Long longTempClientsCount = XMLUtils.getLongAttributeValue(node, "TempClientsCount");
        if (longTempClientsCount != null) {
            setTempClientsCount(longTempClientsCount);
        }
        Long longNetWeight = XMLUtils.getLongAttributeValue(node, "NetWeight");
        if (longNetWeight != null) {
            setNetWeight(longNetWeight);
        }
        Integer intFeedingType = XMLUtils.getIntegerAttributeValue(node, "FeedingType");
        if (intFeedingType != null){
            setFeedingType(intFeedingType);
        } else {
            setFeedingType(0);
        }
        guidOfGR = XMLUtils.getStringAttributeValue(node, "GuidOfGoodsRequest", 36);
        guidOfG = XMLUtils.getStringAttributeValue(node, "GuidOfGoods", 36);
        guidOfP = XMLUtils.getStringAttributeValue(node, "GuidOfBaseProduct", 36);
        setSendAll(SendToAssociatedOrgs.SendToMain);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        final Long lastTotalCount = getTotalCount();
        final Long lastDailySampleCount = getDailySampleCount();
        final Long lastTempClientsCount = getTempClientsCount();
        setOrgOwner(distributedObject.getOrgOwner());
        setGoodRequest(((GoodRequestPosition) distributedObject).getGoodRequest());
        setGood(((GoodRequestPosition) distributedObject).getGood());
        setProduct(((GoodRequestPosition) distributedObject).getProduct());
        setUnitsScale(((GoodRequestPosition) distributedObject).getUnitsScale());
        setNetWeight(((GoodRequestPosition) distributedObject).getNetWeight());
        setTotalCount(((GoodRequestPosition) distributedObject).getTotalCount());
        setDailySampleCount(((GoodRequestPosition) distributedObject).getDailySampleCount()); // суточная проба
        setTempClientsCount(((GoodRequestPosition) distributedObject).getTempClientsCount());
        setComplexId(((GoodRequestPosition) distributedObject).getComplexId());
        setFeedingType(((GoodRequestPosition) distributedObject).getFeedingType());
        /* старые значения всего и суточной пробы */
        setLastTotalCount(lastTotalCount);
        setLastDailySampleCount(lastDailySampleCount); // суточная проба
        setLastTempClientsCount(lastTempClientsCount);
        setNotified(false);
    }

    @Override
    public void setNewInformationContent(InformationContents informationContent) {
        this.informationContent = informationContent;
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

    public void setUnitsScale(UnitScale unitsScale) {
        this.unitsScale = unitsScale;
    }

    public Boolean getFloatScale() {
        return unitsScale.equals(UnitScale.UNITS) || unitsScale.equals(UnitScale.PORTIONS);
    }

    public String getCurrentElementValue() {
        if (product != null) {
            return product.getProductName();
        } else {
            return good.getNameOfGood();
        }
    }

    public Long getCurrentElementId() {
        if (product != null) {
            return product.getGlobalId();
        } else {
            return good.getGlobalId();
        }
    }

    public Long getLastTotalCount() {
        return lastTotalCount;
    }

    public void setLastTotalCount(Long lastTotalCount) {
        this.lastTotalCount = lastTotalCount;
    }

    public Long getLastDailySampleCount() {
        return lastDailySampleCount;
    }

    public void setLastDailySampleCount(Long lastDailySampleCount) {
        this.lastDailySampleCount = lastDailySampleCount;
    }

    public Boolean getNotified() {
        return notified;
    }

    public void setNotified(Boolean notified) {
        this.notified = notified;
    }

    public Long getTempClientsCount() {
        return tempClientsCount;
    }

    public void setTempClientsCount(Long tempClientsCount) {
        this.tempClientsCount = tempClientsCount;
    }

    public Long getLastTempClientsCount() {
        return lastTempClientsCount;
    }

    public void setLastTempClientsCount(Long lastTempClientsCount) {
        this.lastTempClientsCount = lastTempClientsCount;
    }

    public Integer getComplexId() {
        return complexId;
    }

    public void setComplexId(Integer complexId) {
        this.complexId = complexId;
    }

    public Long getIdOfDish() {
        return idOfDish;
    }

    public void setIdOfDish(Long idOfDish) {
        this.idOfDish = idOfDish;
    }

    public Integer getFeedingType() {
        return feedingType;
    }

    public void setFeedingType(Integer feedingType) {
        this.feedingType = feedingType;
    }
}
