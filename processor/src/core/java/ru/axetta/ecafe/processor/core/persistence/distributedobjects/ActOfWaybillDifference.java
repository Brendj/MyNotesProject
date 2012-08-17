/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sync.distributionsync.DistributedObjectException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 15.08.12
 * Time: 11:21
 * To change this template use File | Settings | File Templates.
 */
public class ActOfWaybillDifference extends DistributedObject {

    @Override
    public void preProcess() throws DistributedObjectException {
        Staff st = DAOService.getInstance().findDistributedObjectByRefGUID(Staff.class, guidOfStaff);
        if(st==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setStaff(st);
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element,"Date", getDateFormat().format(date));
        setAttribute(element, "Number", number);
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element, "GuidOfS", staff.getGuid());
    }

    @Override
    protected ActOfWaybillDifference parseAttributes(Node node) throws ParseException, IOException {
        Date dateOfActOfDifference = getDateAttributeValue(node, "Date");
        if(dateOfActOfDifference != null) setDate(dateOfActOfDifference);
        String stringNumber = getStringAttributeValue(node, "Number",128);
        if(stringNumber != null) setNumber(stringNumber);
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        guidOfStaff = getStringAttributeValue(node,"GuidOfS",36);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setDate(((ActOfWaybillDifference) distributedObject).getDate());
        setNumber(((ActOfWaybillDifference) distributedObject).getNumber());
        setOrgOwner(((ActOfWaybillDifference) distributedObject).getOrgOwner());
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
