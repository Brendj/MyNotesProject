/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SupplierRequestDistributedObject;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.12
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class ActOfInventorization extends SupplierRequestDistributedObject /*InventoryRequestDistributedObject*/ {

    private Date dateOfAct;
    private String number;
    private String commission;
    private Set<InternalIncomingDocument> internalIncomingDocumentInternal;
    private Set<InternalDisposingDocument> InternalDisposingDocumentInternal;

    @Override
    protected boolean addReceiverRestriction(Criteria criteria, Session session, String supplierOrgId,
            boolean isReceiver) {
        return false;
    }

    @Override
    protected boolean hasWayBillLinks(Session session) {
        return false;
    }

    @Override
    public void createProjections(Criteria criteria) {
        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("dateOfAct"), "dateOfAct");
        projectionList.add(Projections.property("number"), "number");
        projectionList.add(Projections.property("commission"), "commission");

        criteria.setProjection(projectionList);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {}

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "Date", CalendarUtils.toStringFullDateTimeWithLocalTimeZone(dateOfAct));
        XMLUtils.setAttributeIfNotNull(element, "Number", number);
        XMLUtils.setAttributeIfNotNull(element, "Commission", commission);
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setDateOfAct(((ActOfInventorization) distributedObject).getDateOfAct());
        setNumber(((ActOfInventorization) distributedObject).getNumber());
        setCommission(((ActOfInventorization) distributedObject).getCommission());
    }

    @Override
    protected ActOfInventorization parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null)
            setOrgOwner(longOrgOwner);
        else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        Date dateOfActOfDifference = XMLUtils.getDateTimeAttributeValue(node, "Date");
        if (dateOfActOfDifference != null)
            setDateOfAct(dateOfActOfDifference);
        String stringNumber = XMLUtils.getStringAttributeValue(node, "Number", 128);
        if (stringNumber != null)
            setNumber(stringNumber);
        String stringCommission = XMLUtils.getStringAttributeValue(node, "Commission", 512);
        if (stringCommission != null)
            setCommission(stringCommission);
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    public Set<InternalDisposingDocument> getInternalDisposingDocumentInternal() {
        return InternalDisposingDocumentInternal;
    }

    public void setInternalDisposingDocumentInternal(Set<InternalDisposingDocument> internalDisposingDocumentInternal) {
        InternalDisposingDocumentInternal = internalDisposingDocumentInternal;
    }

    public Set<InternalIncomingDocument> getInternalIncomingDocumentInternal() {
        return internalIncomingDocumentInternal;
    }

    public void setInternalIncomingDocumentInternal(Set<InternalIncomingDocument> internalIncomingDocumentInternal) {
        this.internalIncomingDocumentInternal = internalIncomingDocumentInternal;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDateOfAct() {
        return dateOfAct;
    }

    public void setDateOfAct(Date dateOfAct) {
        this.dateOfAct = dateOfAct;
    }


}
