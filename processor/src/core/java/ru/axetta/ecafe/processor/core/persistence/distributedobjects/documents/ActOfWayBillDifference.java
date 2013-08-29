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
 * Date: 15.08.12
 * Time: 11:21
 * To change this template use File | Settings | File Templates.
 */
public class ActOfWayBillDifference extends DistributedObject {

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        //Staff st = DAOService.getInstance().findDistributedObjectByRefGUID(Staff.class, guidOfStaff);
        Staff st = DAOUtils.findDistributedObjectByRefGUID(Staff.class, session, guidOfStaff);
        if(st==null) throw new DistributedObjectException("NOT_FOUND_VALUE");
        setStaff(st);
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "Date", CalendarUtils.toStringFullDateTimeWithLocalTimeZone(date));
        XMLUtils.setAttributeIfNotNull(element, "Number", number);
        XMLUtils.setAttributeIfNotNull(element, "GuidOfStaff", staff.getGuid());
    }

    @Override
    protected ActOfWayBillDifference parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null)
            setOrgOwner(longOrgOwner);
        Date dateOfActOfDifference = XMLUtils.getDateTimeAttributeValue(node, "Date");
        if (dateOfActOfDifference != null)
            setDate(dateOfActOfDifference);
        String stringNumber = XMLUtils.getStringAttributeValue(node, "Number", 128);
        if (stringNumber != null)
            setNumber(stringNumber);
        guidOfStaff = XMLUtils.getStringAttributeValue(node, "GuidOfStaff", 36);
        setSendAll(SendToAssociatedOrgs.SendToMain);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setDate(((ActOfWayBillDifference) distributedObject).getDate());
        setNumber(((ActOfWayBillDifference) distributedObject).getNumber());
    }

    private Date date;
    private String number;
    private Staff staff;
    private String guidOfStaff;
    private Set<WayBill> wayBillInternal;
    private Set<ActOfWayBillDifferencePosition> actOfWayBillDifferencePositionInternal;

    public Set<ActOfWayBillDifferencePosition> getActOfWayBillDifferencePositionInternal() {
        return actOfWayBillDifferencePositionInternal;
    }

    public void setActOfWayBillDifferencePositionInternal(
            Set<ActOfWayBillDifferencePosition> actOfWayBillDifferencePositionInternal) {
        this.actOfWayBillDifferencePositionInternal = actOfWayBillDifferencePositionInternal;
    }

    public Set<WayBill> getWayBillInternal() {
        return wayBillInternal;
    }

    public void setWayBillInternal(Set<WayBill> wayBillInternal) {
        this.wayBillInternal = wayBillInternal;
    }

    public String getGuidOfStaff() {
        return guidOfStaff;
    }

    public void setGuidOfStaff(String guidOfStaff) {
        this.guidOfStaff = guidOfStaff;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
