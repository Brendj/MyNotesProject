/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
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
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class InternalIncomingDocument extends DistributedObject {

    private Set<StateChange> stateChangeInternal;
    private Set<InternalIncomingDocumentPosition> internalIncomingDocumentPositionInternal;

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

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        //WayBill wb = DAOService.getInstance().findDistributedObjectByRefGUID(WayBill.class,guidOfWB);
        WayBill wb  = (WayBill) DAOUtils.findDistributedObjectByRefGUID(session, guidOfWB);
        //InternalDisposingDocument idd = DAOService.getInstance().findDistributedObjectByRefGUID(InternalDisposingDocument.class,guidOfIDD);
        //ActOfInventarization ai = DAOService.getInstance().findDistributedObjectByRefGUID(ActOfInventarization.class,guidOfAI);
        InternalDisposingDocument idd  = (InternalDisposingDocument) DAOUtils.findDistributedObjectByRefGUID(session, guidOfIDD);
        ActOfInventarization ai  = (ActOfInventarization) DAOUtils.findDistributedObjectByRefGUID(session, guidOfAI);
        if(wb==null && idd==null && ai==null) throw new DistributedObjectException("NOT_FOUND_VALUE");
        if(wb!=null) setWayBill(wb);
        if(idd!=null) setInternalDisposingDocument(idd);
        if(ai!=null) setActOfInventarization(ai);

        //Staff st = DAOService.getInstance().findDistributedObjectByRefGUID(Staff.class,guidOfS);
        Staff st  = (Staff) DAOUtils.findDistributedObjectByRefGUID(session, guidOfS);
        if(st==null) throw new DistributedObjectException("NOT_FOUND_VALUE Staff");
        setStaff(st);
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element, "State", state);
        setAttribute(element, "Date", getDateFormat().format(date));
        if(wayBill!=null) setAttribute(element, "GuidOfWayBill", wayBill.getGuid());
        if(internalDisposingDocument!=null) setAttribute(element, "GuidOfDisposingDoc", internalDisposingDocument.getGuid());
        if(actOfInventarization!=null) setAttribute(element, "GuidOfInventorizationAct", actOfInventarization.getGuid());
        setAttribute(element, "GuidOfStaff", staff.getGuid());
    }

    @Override
    protected InternalIncomingDocument parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        Integer integerState = getIntegerAttributeValue(node,"State");
        if(integerState!=null) setState(integerState);
        Date dateOfInternalIncomingDocument = getDateTimeAttributeValue(node, "Date");
        if(dateOfInternalIncomingDocument != null) setDate(dateOfInternalIncomingDocument);
        guidOfWB = getStringAttributeValue(node,"GuidOfWayBill",36);
        guidOfIDD = getStringAttributeValue(node,"GuidOfDisposingDoc",36);
        guidOfAI = getStringAttributeValue(node,"GuidOfInventorizationAct",36);
        guidOfS = getStringAttributeValue(node,"GuidOfStaff",36);
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((InternalIncomingDocument) distributedObject).getOrgOwner());
        setDate(((InternalIncomingDocument) distributedObject).getDate());
        setState(((InternalIncomingDocument) distributedObject).getState());
    }

    private Integer state;
    private Date date;
    private InternalDisposingDocument internalDisposingDocument;
    private String guidOfIDD;
    private Staff staff;
    private String guidOfS;
    private ActOfInventarization actOfInventarization;
    private String guidOfAI;
    private WayBill wayBill;
    private String guidOfWB;

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

    public ActOfInventarization getActOfInventarization() {
        return actOfInventarization;
    }

    public void setActOfInventarization(ActOfInventarization actOfInventarization) {
        this.actOfInventarization = actOfInventarization;
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
