/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TradeMaterialGood;
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

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.12
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class InternalIncomingDocumentPosition extends SupplierRequestDistributedObject {

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

    @Override
    protected boolean addReceiverRestriction(Criteria criteria, Session session, String supplierOrgId, boolean isReceiver) {
        criteria.createAlias("iid.wayBill","w", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq(isReceiver?"w.receiver":"w.shipper", supplierOrgId));
        return true;
    }

    @Override
    public void createProjections(Criteria criteria, int currentLimit, String currentLastGuid) {
        criteria.createAlias("good","g", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("internalIncomingDocument","iid", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("tradeMaterialGood","tmg", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("guid"), "guid");
        projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");

        projectionList.add(Projections.property("unitsScale"), "unitsScale");
        projectionList.add(Projections.property("totalCount"), "totalCount");
        projectionList.add(Projections.property("netWeight"), "netWeight");
        projectionList.add(Projections.property("goodsCreationDate"), "goodsCreationDate");
        projectionList.add(Projections.property("lifeTime"), "lifeTime");
        projectionList.add(Projections.property("incomingPrice"), "incomingPrice");
        projectionList.add(Projections.property("nds"), "nds");

        projectionList.add(Projections.property("iid.guid"), "guidOfIID");
        projectionList.add(Projections.property("g.guid"), "guidOfG");
        projectionList.add(Projections.property("tmg.guid"), "guidOfTMG");
        criteria.setProjection(projectionList);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
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
        XMLUtils.setAttributeIfNotNull(element, "GuidOfInternalIncomingDocument", guidOfIID);
        if (isNotEmpty(guidOfTMG))
            XMLUtils.setAttributeIfNotNull(element, "GuidOfTradeMaterialGoods", guidOfTMG);
        XMLUtils.setAttributeIfNotNull(element, "GuidOfGoods", guidOfG);
    }

    @Override
    protected InternalIncomingDocumentPosition parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null)
            setOrgOwner(longOrgOwner);
        else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
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
        else setNds(0L);
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
