/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Session;
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
public class InternalDisposingDocument extends DistributedObject {

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        Staff st = DAOUtils.findDistributedObjectByRefGUID(Staff.class, session, guidOfSt);
        if(st==null) throw new DistributedObjectException("NOT_FOUND_VALUE Staff");
        setStaff(st);
        ActOfInventarization ai = DAOUtils.findDistributedObjectByRefGUID(ActOfInventarization.class, session, guidOfAI);
        if(ai!=null) setActOfInventarization(ai);
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "Type", type);
        XMLUtils.setAttributeIfNotNull(element, "Date", CalendarUtils.toStringFullDateTimeWithLocalTimeZone(date));
        XMLUtils.setAttributeIfNotNull(element, "State", state.ordinal());
        XMLUtils.setAttributeIfNotNull(element, "Comment", comments);
        XMLUtils.setAttributeIfNotNull(element, "GuidOfStaff", staff.getGuid());
        if (actOfInventarization != null)
            XMLUtils.setAttributeIfNotNull(element, "GuidOfInventarizationAct", actOfInventarization.getGuid());
    }

    @Override
    protected InternalDisposingDocument parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null)
            setOrgOwner(longOrgOwner);
        Integer integerType = XMLUtils.getIntegerAttributeValue(node, "Type");
        if (integerType != null)
            setType(integerType);
        Date dateOfInternalDisposingDocument = XMLUtils.getDateTimeAttributeValue(node, "Date");
        if (dateOfInternalDisposingDocument != null)
            setDate(dateOfInternalDisposingDocument);
        Integer integerState = XMLUtils.getIntegerAttributeValue(node, "State");
        if (integerState != null)
            setState(DocumentState.values()[integerState]);
        String stringComments = XMLUtils.getStringAttributeValue(node, "Comment", 1024);
        if (stringComments != null)
            setComments(stringComments);
        guidOfSt = XMLUtils.getStringAttributeValue(node, "GuidOfStaff", 36);
        guidOfAI = XMLUtils.getStringAttributeValue(node, "GuidOfInventarizationAct", 36);
        setSendAll(SendToAssociatedOrgs.SendToMain);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setType(((InternalDisposingDocument) distributedObject).getType());
        setDate(((InternalDisposingDocument) distributedObject).getDate());
        setState(((InternalDisposingDocument) distributedObject).getState());
        setComments(((InternalDisposingDocument) distributedObject).getComments());
    }

    private Integer type;
    private Date date;
    private DocumentState state;
    private ActOfInventarization actOfInventarization;
    private String guidOfAI;
    private Staff staff;
    private String guidOfSt;
    private String comments;
    private Set<StateChange> stateChangeInternal;
    private Set<InternalIncomingDocument> internalIncomingDocumentInternal;
    private Set<InternalDisposingDocumentPosition> internalDisposingDocumentPositionInternal;

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


    public DocumentState getState() {
        return state;
    }

    public void setState(DocumentState state) {
        this.state = state;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public String getGuidOfSt() {
        return guidOfSt;
    }

    public void setGuidOfSt(String guidOfSt) {
        this.guidOfSt = guidOfSt;
    }

    public String getGuidOfAI() {
        return guidOfAI;
    }

    public void setGuidOfAI(String guidOfAI) {
        this.guidOfAI = guidOfAI;
    }

    public ActOfInventarization getActOfInventarization() {
        return actOfInventarization;
    }

    public void setActOfInventarization(ActOfInventarization actOfInventarization) {
        this.actOfInventarization = actOfInventarization;
    }

    public Set<InternalDisposingDocumentPosition> getInternalDisposingDocumentPositionInternal() {
        return internalDisposingDocumentPositionInternal;
    }

    public void setInternalDisposingDocumentPositionInternal(
            Set<InternalDisposingDocumentPosition> internalDisposingDocumentPositionInternal) {
        this.internalDisposingDocumentPositionInternal = internalDisposingDocumentPositionInternal;
    }

    public Set<InternalIncomingDocument> getInternalIncomingDocumentInternal() {
        return internalIncomingDocumentInternal;
    }

    public void setInternalIncomingDocumentInternal(Set<InternalIncomingDocument> internalIncomingDocumentInternal) {
        this.internalIncomingDocumentInternal = internalIncomingDocumentInternal;
    }

    public Set<StateChange> getStateChangeInternal() {
        return stateChangeInternal;
    }

    public void setStateChangeInternal(Set<StateChange> stateChangeInternal) {
        this.stateChangeInternal = stateChangeInternal;
    }
}
