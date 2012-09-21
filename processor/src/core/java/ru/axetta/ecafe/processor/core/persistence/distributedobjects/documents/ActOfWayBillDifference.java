/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;

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
        Staff st = (Staff) DAOUtils.findDistributedObjectByRefGUID(session, guidOfStaff);
        if(st==null) throw new DistributedObjectException("NOT_FOUND_VALUE");
        setStaff(st);
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element,"Date", getDateFormat().format(date));
        setAttribute(element, "Number", number);
        setAttribute(element, "GuidOfStaff", staff.getGuid());
    }

    @Override
    protected ActOfWayBillDifference parseAttributes(Node node) throws Exception{
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        Date dateOfActOfDifference = getDateTimeAttributeValue(node, "Date");
        if(dateOfActOfDifference != null) setDate(dateOfActOfDifference);
        String stringNumber = getStringAttributeValue(node, "Number",128);
        if(stringNumber != null) setNumber(stringNumber);
        guidOfStaff = getStringAttributeValue(node,"GuidOfStaff",36);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((ActOfWayBillDifference) distributedObject).getOrgOwner());
        setDate(((ActOfWayBillDifference) distributedObject).getDate());
        setNumber(((ActOfWayBillDifference) distributedObject).getNumber());
    }

    private Date date;
    private String number;
    private Staff staff;
    private String guidOfStaff;

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
