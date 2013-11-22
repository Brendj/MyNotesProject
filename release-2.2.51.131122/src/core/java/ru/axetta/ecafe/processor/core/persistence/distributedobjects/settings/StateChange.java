/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier.InternalDisposingDocument;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier.InternalIncomingDocument;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier.WayBill;
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
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.12
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class StateChange extends DistributedObject {

    private Date date;
    private Long stateFrom;
    private Long stateTo;
    private GoodRequest goodRequest;
    private String guidOfGR;
    private InternalDisposingDocument internalDisposingDocument;
    private String guidOfIDD;
    private Staff staff;
    private String guidOfS;
    private InternalIncomingDocument internalIncomingDocument;
    private String guidOfIID;
    private WayBill wayBill;
    private String guidOfWB;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("wayBill","w", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("internalDisposingDocument","idd", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("goodRequest","g", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("internalIncomingDocument","iid", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("staff","s", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("guid"), "guid");
        projectionList.add(Projections.property("globalId"), "globalId");
        projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");

        projectionList.add(Projections.property("stateFrom"), "stateFrom");
        projectionList.add(Projections.property("stateTo"), "stateTo");
        projectionList.add(Projections.property("date"), "date");

        projectionList.add(Projections.property("w.guid"), "guidOfWB");
        projectionList.add(Projections.property("idd.guid"), "guidOfIDD");
        projectionList.add(Projections.property("g.guid"), "guidOfGR");
        projectionList.add(Projections.property("iid.guid"), "guidOfIID");
        projectionList.add(Projections.property("s.guid"), "guidOfS");

        criteria.setProjection(projectionList);
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion) throws Exception {
        return toSelfProcess(session, idOfOrg, currentMaxVersion);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        WayBill wb = DAOUtils.findDistributedObjectByRefGUID(WayBill.class, session, guidOfWB);
        InternalDisposingDocument idd = DAOUtils.findDistributedObjectByRefGUID(InternalDisposingDocument.class, session, guidOfIDD);
        GoodRequest gr = DAOUtils.findDistributedObjectByRefGUID(GoodRequest.class, session, guidOfGR);
        InternalIncomingDocument iid = DAOUtils.findDistributedObjectByRefGUID(InternalIncomingDocument.class, session, guidOfIID);
        if(wb == null && idd == null && gr==null && iid==null){
            throw new DistributedObjectException("NOT_FOUND_VALUE WayBill or InternalDisposingDocument or GoodRequest or InternalIncomingDocument");
        }
        if(wb!=null) setWayBill(wb);
        if(idd!=null) setInternalDisposingDocument(idd);
        if(gr!=null) setGoodRequest(gr);
        if(iid!=null) setInternalIncomingDocument(iid);
        Staff st = DAOUtils.findDistributedObjectByRefGUID(Staff.class, session, guidOfS);
        if(st==null) throw new DistributedObjectException("NOT_FOUND_VALUE Staff");
        setStaff(st);

    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "StateFrom", stateFrom);
        XMLUtils.setAttributeIfNotNull(element, "StateTo", stateTo);
        XMLUtils.setAttributeIfNotNull(element, "Date", CalendarUtils.toStringFullDateTimeWithLocalTimeZone(date));
        if (isNotEmpty(guidOfWB))
            XMLUtils.setAttributeIfNotNull(element, "GuidOfWayBill", guidOfWB);
        if (isNotEmpty(guidOfIDD))
            XMLUtils.setAttributeIfNotNull(element, "GuidOfDisposingDoc", guidOfIDD);
        if (isNotEmpty(guidOfGR))
            XMLUtils.setAttributeIfNotNull(element, "GuidOfGoodsRequest", guidOfGR);
        if (isNotEmpty(guidOfIID))
            XMLUtils.setAttributeIfNotNull(element, "GuidOfIncomingDocument", guidOfIID);
        XMLUtils.setAttributeIfNotNull(element, "GuidOfStaff", guidOfS);
    }

    @Override
    protected StateChange parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null)
            setOrgOwner(longOrgOwner);
        Long longStateFrom = XMLUtils.getLongAttributeValue(node, "StateFrom");
        if (longStateFrom != null)
            setStateFrom(longStateFrom);
        Long longStateTo = XMLUtils.getLongAttributeValue(node, "StateTo");
        if (longStateTo != null)
            setStateTo(longStateTo);
        Date dateOfInternalIncomingDocument = XMLUtils.getDateTimeAttributeValue(node, "Date");
        if (dateOfInternalIncomingDocument != null)
            setDate(dateOfInternalIncomingDocument);
        guidOfWB = XMLUtils.getStringAttributeValue(node, "GuidOfWayBill", 36);
        guidOfIDD = XMLUtils.getStringAttributeValue(node, "GuidOfDisposingDoc", 36);
        guidOfGR = XMLUtils.getStringAttributeValue(node, "GuidOfGoodsRequest", 36);
        guidOfIID = XMLUtils.getStringAttributeValue(node, "GuidOfIncomingDocument", 36);
        guidOfS = XMLUtils.getStringAttributeValue(node, "GuidOfStaff", 36);
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setDate(((StateChange) distributedObject).getDate());
        setStateFrom(((StateChange) distributedObject).getStateFrom());
        setStateTo(((StateChange) distributedObject).getStateTo());
    }

    public String getGuidOfS() {
        return guidOfS;
    }

    public void setGuidOfS(String guidOfS) {
        this.guidOfS = guidOfS;
    }

    public String getGuidOfIID() {
        return guidOfIID;
    }

    public void setGuidOfIID(String guidOfIID) {
        this.guidOfIID = guidOfIID;
    }

    public String getGuidOfGR() {
        return guidOfGR;
    }

    public void setGuidOfGR(String guidOfGR) {
        this.guidOfGR = guidOfGR;
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

    public InternalIncomingDocument getInternalIncomingDocument() {
        return internalIncomingDocument;
    }

    public void setInternalIncomingDocument(InternalIncomingDocument internalIncomingDocument) {
        this.internalIncomingDocument = internalIncomingDocument;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getGuidOfIDD() {
        return guidOfIDD;
    }

    public void setGuidOfIDD(String guidOfIDD) {
        this.guidOfIDD = guidOfIDD;
    }

    public InternalDisposingDocument getInternalDisposingDocument() {
        return internalDisposingDocument;
    }

    public void setInternalDisposingDocument(InternalDisposingDocument internalDisposingDocument) {
        this.internalDisposingDocument = internalDisposingDocument;
    }

    public GoodRequest getGoodRequest() {
        return goodRequest;
    }

    public void setGoodRequest(GoodRequest goodRequest) {
        this.goodRequest = goodRequest;
    }

    public Long getStateTo() {
        return stateTo;
    }

    public void setStateTo(Long stateTo) {
        this.stateTo = stateTo;
    }

    public Long getStateFrom() {
        return stateFrom;
    }

    public void setStateFrom(Long stateFrom) {
        this.stateFrom = stateFrom;
    }
}
