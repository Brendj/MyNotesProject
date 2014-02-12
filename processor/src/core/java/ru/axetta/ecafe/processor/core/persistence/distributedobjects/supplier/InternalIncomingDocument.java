/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SupplierRequestDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.Staff;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.StateChange;
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
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.12
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class InternalIncomingDocument extends SupplierRequestDistributedObject {

    private Integer state;
    private Date date;
    private InternalDisposingDocument internalDisposingDocument;
    private String guidOfIDD;
    private Staff staff;
    private String guidOfS;
    private ActOfInventorization actOfInventorization;
    private String guidOfAI;
    private WayBill wayBill;
    private String guidOfWB;
    private Set<StateChange> stateChangeInternal;
    private Set<InternalIncomingDocumentPosition> internalIncomingDocumentPositionInternal;

    @Override
    protected boolean hasWayBillLinks(Session session) {
        try {
            WayBill wb  = DAOUtils.findDistributedObjectByRefGUID(WayBill.class, session, guidOfWB);
            if(wb==null) return false;
            else return true;
        } catch (DistributedObjectException e) {
            return false;
        }
    }

    @Override
    protected boolean addReceiverRestriction(Criteria criteria, Session session, String supplierOrgId, boolean isReceiver) {
        criteria.add(Restrictions.eq(isReceiver?"w.receiver":"w.shipper", supplierOrgId));
        return true;
    }

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("wayBill","w", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("internalDisposingDocument","idd", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("actOfInventorization","ai", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("staff","s", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("state"), "state");
        projectionList.add(Projections.property("date"), "date");

        projectionList.add(Projections.property("w.guid"), "guidOfWB");
        projectionList.add(Projections.property("idd.guid"), "guidOfIDD");
        projectionList.add(Projections.property("ai.guid"), "guidOfAI");
        projectionList.add(Projections.property("s.guid"), "guidOfS");
        criteria.setProjection(projectionList);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        WayBill wb  = DAOUtils.findDistributedObjectByRefGUID(WayBill.class, session, guidOfWB);
        InternalDisposingDocument idd  = DAOUtils.findDistributedObjectByRefGUID(InternalDisposingDocument.class, session, guidOfIDD);
        ActOfInventorization ai  = DAOUtils.findDistributedObjectByRefGUID(ActOfInventorization.class, session, guidOfAI);
        if(wb==null && idd==null && ai==null) throw new DistributedObjectException("NOT_FOUND_VALUE");
        if(wb!=null) {
            if(!wb.getOrgOwner().equals(this.orgOwner)){
                this.orgOwner = wb.getOrgOwner();
            }
            setWayBill(wb);
        }
        if(idd!=null){
            if(!idd.getOrgOwner().equals(this.orgOwner)){
                this.orgOwner = idd.getOrgOwner();
            }
            setInternalDisposingDocument(idd);
        }
        if(ai!=null){
            if(!ai.getOrgOwner().equals(this.orgOwner)){
                this.orgOwner = ai.getOrgOwner();
            }
            setActOfInventorization(ai);
        }

        Staff st  = DAOUtils.findDistributedObjectByRefGUID(Staff.class, session, guidOfS);
        if(st==null) throw new DistributedObjectException("NOT_FOUND_VALUE Staff");
        setStaff(st);
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "State", state);
        XMLUtils.setAttributeIfNotNull(element, "Date", CalendarUtils.toStringFullDateTimeWithLocalTimeZone(date));
        if (isNotEmpty(guidOfWB))
            XMLUtils.setAttributeIfNotNull(element, "GuidOfWayBill", guidOfWB);
        if (isNotEmpty(guidOfIDD))
            XMLUtils.setAttributeIfNotNull(element, "GuidOfDisposingDoc", guidOfIDD);
        if (isNotEmpty(guidOfWB))
            XMLUtils.setAttributeIfNotNull(element, "GuidOfInventorizationAct", guidOfWB);
        XMLUtils.setAttributeIfNotNull(element, "GuidOfStaff", guidOfS);
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setDate(((InternalIncomingDocument) distributedObject).getDate());
        setState(((InternalIncomingDocument) distributedObject).getState());

        setWayBill(((InternalIncomingDocument) distributedObject).getWayBill());
        setGuidOfWB(((InternalIncomingDocument) distributedObject).getGuidOfWB());

        setInternalDisposingDocument(((InternalIncomingDocument) distributedObject).getInternalDisposingDocument());
        setGuidOfIDD(((InternalIncomingDocument) distributedObject).getGuidOfIDD());

        setGuidOfAI(((InternalIncomingDocument) distributedObject).getGuidOfAI());
        setActOfInventorization(((InternalIncomingDocument) distributedObject).getActOfInventorization());

        setStaff(((InternalIncomingDocument) distributedObject).getStaff());
        setGuidOfS(((InternalIncomingDocument) distributedObject).getGuidOfS());
    }

    @Override
    protected InternalIncomingDocument parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null)
            setOrgOwner(longOrgOwner);
        else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        Integer integerState = XMLUtils.getIntegerAttributeValue(node, "State");
        if (integerState != null)
            setState(integerState);
        Date dateOfInternalIncomingDocument = XMLUtils.getDateTimeAttributeValue(node, "Date");
        if (dateOfInternalIncomingDocument != null)
            setDate(dateOfInternalIncomingDocument);
        guidOfWB = XMLUtils.getStringAttributeValue(node, "GuidOfWayBill", 36);
        guidOfIDD = XMLUtils.getStringAttributeValue(node, "GuidOfDisposingDoc", 36);
        guidOfAI = XMLUtils.getStringAttributeValue(node, "GuidOfInventorizationAct", 36);
        guidOfS = XMLUtils.getStringAttributeValue(node, "GuidOfStaff", 36);
        setSendAll(SendToAssociatedOrgs.SendToMain);
        return this;
    }

    public Set<InternalIncomingDocumentPosition> getInternalIncomingDocumentPositionInternal() {
        return internalIncomingDocumentPositionInternal;
    }

    public void setInternalIncomingDocumentPositionInternal(
            Set<InternalIncomingDocumentPosition> internalIncomingDocumentPositionInternal) {
        this.internalIncomingDocumentPositionInternal = internalIncomingDocumentPositionInternal;
    }

    public Set<StateChange> getStateChangeInternal() {
        return stateChangeInternal;
    }

    public void setStateChangeInternal(Set<StateChange> stateChangeInternal) {
        this.stateChangeInternal = stateChangeInternal;
    }

    public String getGuidOfS() {
        return guidOfS;
    }

    public void setGuidOfS(String guidOfS) {
        this.guidOfS = guidOfS;
    }

    public String getGuidOfAI() {
        return guidOfAI;
    }

    public void setGuidOfAI(String guidOfAI) {
        this.guidOfAI = guidOfAI;
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

    public ActOfInventorization getActOfInventorization() {
        return actOfInventorization;
    }

    public void setActOfInventorization(ActOfInventorization actOfInventorization) {
        this.actOfInventorization = actOfInventorization;
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
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
}
