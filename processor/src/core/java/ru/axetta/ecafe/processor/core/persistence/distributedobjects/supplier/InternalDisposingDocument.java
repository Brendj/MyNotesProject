/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SupplierRequestDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DocumentState;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.Staff;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.StateChange;
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
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.12
 * Time: 17:29
 * To change this template use File | Settings | File Templates.
 */
public class InternalDisposingDocument extends SupplierRequestDistributedObject {

    private Integer type;
    private Date date;
    private DocumentState state;
    private ActOfInventorization actOfInventorization;
    private String guidOfAI;
    private Staff staff;
    private String guidOfSt;
    private String comments;
    private Set<StateChange> stateChangeInternal;
    private Set<InternalIncomingDocument> internalIncomingDocumentInternal;
    private Set<InternalDisposingDocumentPosition> internalDisposingDocumentPositionInternal;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("staff","s", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("actOfInventorization","a", JoinType.LEFT_OUTER_JOIN);
        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("type"), "type");
        projectionList.add(Projections.property("date"), "date");
        projectionList.add(Projections.property("state"), "state");
        projectionList.add(Projections.property("comments"), "comments");

        projectionList.add(Projections.property("s.guid"), "guidOfSt");
        projectionList.add(Projections.property("a.guid"), "guidOfAI");
        criteria.setProjection(projectionList);
    }

    @Override
    protected boolean hasWayBillLinks(Session session) {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean addReceiverRestriction(Criteria criteria, Session session, String supplierOrgId, boolean isReceiver) {
        final String s = "select distinct ai.globalId from InternalIncomingDocument iid left join iid.wayBill wb left join iid.actOfInventorization ai where ";
        Query query = session.createQuery(s +(isReceiver?"wb.receiver":"wb.shipper")+"=:idOdOrg");
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
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        Staff st = DAOUtils.findDistributedObjectByRefGUID(Staff.class, session, guidOfSt);
        if(st==null) throw new DistributedObjectException("NOT_FOUND_VALUE Staff");
        setStaff(st);
        ActOfInventorization ai = DAOUtils.findDistributedObjectByRefGUID(ActOfInventorization.class, session, guidOfAI);
        if(ai!=null) {
            if(!ai.getOrgOwner().equals(this.orgOwner)){
                this.orgOwner = ai.getOrgOwner();
            }
            setActOfInventorization(ai);
        }
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "Type", type);
        XMLUtils.setAttributeIfNotNull(element, "Date", CalendarUtils.toStringFullDateTimeWithLocalTimeZone(date));
        XMLUtils.setAttributeIfNotNull(element, "State", state.ordinal());
        XMLUtils.setAttributeIfNotNull(element, "Comment", comments);
        XMLUtils.setAttributeIfNotNull(element, "GuidOfStaff", guidOfSt);
        if (isNotEmpty(guidOfAI))
            XMLUtils.setAttributeIfNotNull(element, "GuidOfInventarizationAct", guidOfAI);
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setType(((InternalDisposingDocument) distributedObject).getType());
        setDate(((InternalDisposingDocument) distributedObject).getDate());
        setState(((InternalDisposingDocument) distributedObject).getState());
        setComments(((InternalDisposingDocument) distributedObject).getComments());

        setStaff(((InternalDisposingDocument) distributedObject).getStaff());
        setGuidOfSt(((InternalDisposingDocument) distributedObject).getGuidOfSt());

        setActOfInventorization(((InternalDisposingDocument) distributedObject).getActOfInventorization());
        setGuidOfAI(((InternalDisposingDocument) distributedObject).getGuidOfAI());
    }

    @Override
    protected InternalDisposingDocument parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null)
            setOrgOwner(longOrgOwner);
        else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
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

    public ActOfInventorization getActOfInventorization() {
        return actOfInventorization;
    }

    public void setActOfInventorization(ActOfInventorization actOfInventorization) {
        this.actOfInventorization = actOfInventorization;
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
