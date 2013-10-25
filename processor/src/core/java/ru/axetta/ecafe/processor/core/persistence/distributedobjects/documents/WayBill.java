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
public class WayBill extends DistributedObject {

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        Staff st = DAOUtils.findDistributedObjectByRefGUID(Staff.class, session, guidOfSt);
        if(st==null) throw new DistributedObjectException("NOT_FOUND_VALUE Staff");
        setStaff(st);
        ActOfWayBillDifference awd = DAOUtils.findDistributedObjectByRefGUID(ActOfWayBillDifference.class, session, guidOfAWD);
        if(awd!=null) setActOfWayBillDifference(awd);
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "Number", number);
        XMLUtils.setAttributeIfNotNull(element, "Date", CalendarUtils.toStringFullDateTimeWithLocalTimeZone(dateOfWayBill));
        XMLUtils.setAttributeIfNotNull(element, "State", state.ordinal());
        XMLUtils.setAttributeIfNotNull(element, "Shipper", shipper);
        XMLUtils.setAttributeIfNotNull(element, "Receiver", receiver);
        XMLUtils.setAttributeIfNotNull(element, "GuidOfStaff", staff.getGuid());
        if (actOfWayBillDifference != null) {
            XMLUtils.setAttributeIfNotNull(element, "GuidOfActOfDifference", actOfWayBillDifference.getGuid());
        }
    }

    @Override
    protected WayBill parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null){
            setOrgOwner(longOrgOwner);
        } else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        String stringNumber = XMLUtils.getStringAttributeValue(node, "Number", 128);
        if (stringNumber != null)
            setNumber(stringNumber);
        Date dateWayBill = XMLUtils.getDateTimeAttributeValue(node, "Date");
        if (dateWayBill != null)
            setDateOfWayBill(dateWayBill);
        Integer integerState = XMLUtils.getIntegerAttributeValue(node, "State");
        if (integerState != null)
            setState(DocumentState.values()[integerState]);
        String stringShipper = XMLUtils.getStringAttributeValue(node, "Shipper", 128);
        if (stringShipper != null)
            setShipper(stringShipper);
        String stringReceiver = XMLUtils.getStringAttributeValue(node, "Receiver", 128);
        if (stringReceiver != null)
            setReceiver(stringReceiver);
        guidOfSt = XMLUtils.getStringAttributeValue(node, "GuidOfStaff", 36);
        guidOfAWD = XMLUtils.getStringAttributeValue(node, "GuidOfActOfDifference", 36);
        setSendAll(SendToAssociatedOrgs.SendToMain);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setNumber(((WayBill) distributedObject).getNumber());
        setDateOfWayBill(((WayBill) distributedObject).getDateOfWayBill());
        setState(((WayBill) distributedObject).getState());
        setShipper(((WayBill) distributedObject).getShipper());
        setReceiver(((WayBill) distributedObject).getReceiver());
    }

    private String number;
    private Date dateOfWayBill;
    private DocumentState state;
    private String shipper;
    private String receiver;
    private ActOfWayBillDifference actOfWayBillDifference;
    private String guidOfAWD;
    private Staff staff;
    private String guidOfSt;
    private Set<WayBillPosition> wayBillPositionInternal;
    private Set<StateChange> stateChangeInternal;
    private Set<InternalIncomingDocument> internalIncomingDocumentInternal;

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

    public Set<WayBillPosition> getWayBillPositionInternal() {
        return wayBillPositionInternal;
    }

    public void setWayBillPositionInternal(Set<WayBillPosition> wayBillPositionInternal) {
        this.wayBillPositionInternal = wayBillPositionInternal;
    }

    public String getGuidOfSt() {
        return guidOfSt;
    }

    public void setGuidOfSt(String guidOfSt) {
        this.guidOfSt = guidOfSt;
    }

    public String getGuidOfAWD() {
        return guidOfAWD;
    }

    public void setGuidOfAWD(String guidOfAWD) {
        this.guidOfAWD = guidOfAWD;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public DocumentState getState() {
        return state;
    }

    public void setState(DocumentState state) {
        this.state = state;
    }

    public Date getDateOfWayBill() {
        return dateOfWayBill;
    }

    public void setDateOfWayBill(Date dateOfWayBill) {
        this.dateOfWayBill = dateOfWayBill;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public ActOfWayBillDifference getActOfWayBillDifference() {
        return actOfWayBillDifference;
    }

    public void setActOfWayBillDifference(ActOfWayBillDifference actOfWayBillDifference) {
        this.actOfWayBillDifference = actOfWayBillDifference;
    }

}
