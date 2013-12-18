/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TradeMaterialGood;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
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

import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.12
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class InternalDisposingDocumentPosition extends SupplierRequestDistributedObject {

    private UnitScale unitsScale;
    /* писано в действительности: фактический */
    private Long totalCount;
    private Long netWeight;
    private Long disposePrice;
    private Long nds;
    private InternalDisposingDocument internalDisposingDocument;
    private String guidOfIDD;
    private TradeMaterialGood tradeMaterialGood;
    private String guidOfTMG;
    private String guidOfGood;
    private Good good;
    /* должно было списаться: планировалось */
    private Long totalCountMust;

    @Override
    @SuppressWarnings("unchecked")
    protected boolean addReceiverRestriction(Criteria criteria, Session session, String supplierOrgId, boolean isReceiver) {
        final String s = "select distinct ai.globalId from InternalIncomingDocument iid left join iid.wayBill wb left join iid.actOfInventorization ai where ";
        final String queryString = s +(isReceiver?"wb.receiver":"wb.shipper")+"=:idOfOrg";
        Query query = session.createQuery(queryString);
        query.setParameter("idOfOrg", supplierOrgId);
        List<Long> ids = query.list();
        if(ids!=null && !ids.isEmpty()) {
            criteria.createAlias("idd.actOfInventorization","a", JoinType.LEFT_OUTER_JOIN);
            criteria.add(Restrictions.in("a.globalId", ids));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("internalDisposingDocument","idd", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("tradeMaterialGood","tmg", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("good","g", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("guid"), "guid");
        projectionList.add(Projections.property("globalId"), "globalId");
        projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");

        projectionList.add(Projections.property("unitsScale"), "unitsScale");
        projectionList.add(Projections.property("totalCount"), "totalCount");
        projectionList.add(Projections.property("totalCountMust"), "totalCountMust");
        projectionList.add(Projections.property("netWeight"), "netWeight");
        projectionList.add(Projections.property("disposePrice"), "disposePrice");
        projectionList.add(Projections.property("nds"), "nds");

        projectionList.add(Projections.property("idd.guid"), "guidOfIDD");
        projectionList.add(Projections.property("tmg.guid"), "guidOfTMG");
        projectionList.add(Projections.property("g.guid"), "guidOfGood");
        criteria.setProjection(projectionList);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        InternalDisposingDocument idd = DAOUtils.findDistributedObjectByRefGUID(InternalDisposingDocument.class, session, guidOfIDD);
        if (idd == null)
            throw new DistributedObjectException("NOT_FOUND_InternalDisposingDocument");
        setInternalDisposingDocument(idd);
        TradeMaterialGood tmg = DAOUtils.findDistributedObjectByRefGUID(TradeMaterialGood.class, session, guidOfTMG);
        if (tmg != null)
            setTradeMaterialGood(tmg);
        Good g = DAOUtils.findDistributedObjectByRefGUID(Good.class, session, guidOfGood);
        if (g == null)
            throw new DistributedObjectException("NOT_FOUND_Good");
        setGood(g);
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "UnitsScale", unitsScale.ordinal());
        XMLUtils.setAttributeIfNotNull(element, "TotalCount", totalCount);
        XMLUtils.setAttributeIfNotNull(element, "TotalCountMust", totalCountMust);
        XMLUtils.setAttributeIfNotNull(element, "NetWeight", netWeight);
        XMLUtils.setAttributeIfNotNull(element, "DisposePrice", disposePrice);
        XMLUtils.setAttributeIfNotNull(element, "NDS", nds);
        XMLUtils.setAttributeIfNotNull(element, "GuidOfInternalDisposingDocument", guidOfIDD);
        if (isNotEmpty(guidOfTMG))
            XMLUtils.setAttributeIfNotNull(element, "GuidOfTradeMaterialGoods", guidOfTMG);
        XMLUtils.setAttributeIfNotNull(element, "GuidOfGoods", guidOfGood);
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setUnitsScale(((InternalDisposingDocumentPosition) distributedObject).getUnitsScale());
        setTotalCount(((InternalDisposingDocumentPosition) distributedObject).getTotalCount());
        setTotalCountMust(((InternalDisposingDocumentPosition) distributedObject).getTotalCountMust());
        setNetWeight(((InternalDisposingDocumentPosition) distributedObject).getNetWeight());
        setDisposePrice(((InternalDisposingDocumentPosition) distributedObject).getDisposePrice());
        setNds(((InternalDisposingDocumentPosition) distributedObject).getNds());

        setGood(((InternalDisposingDocumentPosition) distributedObject).getGood());
        setGuidOfGood(((InternalDisposingDocumentPosition) distributedObject).getGuidOfGood());

        setGuidOfTMG(((InternalDisposingDocumentPosition) distributedObject).getGuidOfTMG());
        setTradeMaterialGood(((InternalDisposingDocumentPosition) distributedObject).getTradeMaterialGood());

        setGuidOfIDD(((InternalDisposingDocumentPosition) distributedObject).getGuidOfIDD());
        setInternalDisposingDocument(((InternalDisposingDocumentPosition) distributedObject).getInternalDisposingDocument());
    }

    @Override
    protected InternalDisposingDocumentPosition parseAttributes(Node node) throws Exception {
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
        Long longTotalCountMust = XMLUtils.getLongAttributeValue(node, "TotalCountMust");
        if (longTotalCountMust != null)
            setTotalCountMust(longTotalCountMust);
        Long longNetWeight = XMLUtils.getLongAttributeValue(node, "NetWeight");
        if (longNetWeight != null)
            setNetWeight(longNetWeight);
        Long longDisposePrice = XMLUtils.getLongAttributeValue(node, "DisposePrice");
        if (longDisposePrice != null)
            setDisposePrice(longDisposePrice);
        Long longNDS = XMLUtils.getLongAttributeValue(node, "NDS");
        if (longNDS != null)
            setNds(longNDS);
        else setNds(0L);
        guidOfIDD = XMLUtils.getStringAttributeValue(node, "GuidOfInternalDisposingDocument", 36);
        guidOfTMG = XMLUtils.getStringAttributeValue(node, "GuidOfTradeMaterialGoods", 36);
        guidOfGood = XMLUtils.getStringAttributeValue(node, "GuidOfGoods", 36);
        setSendAll(SendToAssociatedOrgs.SendToMain);
        return this;
    }

    public Long getTotalCountMust() {
        return totalCountMust;
    }

    public void setTotalCountMust(Long totalCountMust) {
        this.totalCountMust = totalCountMust;
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }

    public String getGuidOfGood() {
        return guidOfGood;
    }

    public void setGuidOfGood(String guidOfGood) {
        this.guidOfGood = guidOfGood;
    }

    public String getGuidOfIDD() {
        return guidOfIDD;
    }

    public void setGuidOfIDD(String guidOfIDD) {
        this.guidOfIDD = guidOfIDD;
    }

    public String getGuidOfTMG() {
        return guidOfTMG;
    }

    public void setGuidOfTMG(String guidOfTMG) {
        this.guidOfTMG = guidOfTMG;
    }

    public TradeMaterialGood getTradeMaterialGood() {
        return tradeMaterialGood;
    }

    public void setTradeMaterialGood(TradeMaterialGood tradeMaterialGood) {
        this.tradeMaterialGood = tradeMaterialGood;
    }

    public InternalDisposingDocument getInternalDisposingDocument() {
        return internalDisposingDocument;
    }

    public void setInternalDisposingDocument(InternalDisposingDocument internalDisposingDocument) {
        this.internalDisposingDocument = internalDisposingDocument;
    }

    public Long getNds() {
        return nds;
    }

    public void setNds(Long nds) {
        this.nds = nds;
    }

    public Long getDisposePrice() {
        return disposePrice;
    }

    public void setDisposePrice(Long disposePrice) {
        this.disposePrice = disposePrice;
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
}
