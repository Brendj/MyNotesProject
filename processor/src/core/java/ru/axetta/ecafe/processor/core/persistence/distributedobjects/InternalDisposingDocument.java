/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.DateType;
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
 * Date: 14.08.12
 * Time: 17:29
 * To change this template use File | Settings | File Templates.
 */
public class InternalDisposingDocument extends DistributedObject {

    @Override
    public void preProcess() throws DistributedObjectException {
        Staff st = DAOService.getInstance().findDistributedObjectByRefGUID(Staff.class, guidOfSt);
        if(st==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setStaff(st);
        ActOfInventarization ai = DAOService.getInstance().findDistributedObjectByRefGUID(ActOfInventarization.class, guidOfAI);
        if(ai==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setActOfInventarization(ai);
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element,"Type", type);
        setAttribute(element,"Date", getDateFormat().format(date));
        setAttribute(element,"State", state);
        setAttribute(element, "GuidOfS", staff.getGuid());
        setAttribute(element, "GuidOfAI", actOfInventarization.getGuid());
    }

    @Override
    protected InternalDisposingDocument parseAttributes(Node node) throws ParseException, IOException {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        Integer integerType = getIntegerAttributeValue(node, "Type");
        if(integerType != null) setType(integerType);
        Date dateOfInternalDisposingDocument = getDateAttributeValue(node, "Date");
        if(dateOfInternalDisposingDocument!=null) setDate(dateOfInternalDisposingDocument);
        Integer integerState = getIntegerAttributeValue(node, "State");
        if(integerState != null) setState(integerState);
        guidOfSt = getStringAttributeValue(node,"GuidOfS",36);
        guidOfAI = getStringAttributeValue(node,"GuidOfAI",36);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((InternalDisposingDocument) distributedObject).getOrgOwner());
        setType(((InternalDisposingDocument) distributedObject).getType());
        setDate(((InternalDisposingDocument) distributedObject).getDate());
        setState(((InternalDisposingDocument) distributedObject).getState());
    }

    private Integer type;
    private Date date;
    private Integer state;
    private ActOfInventarization actOfInventarization;
    private String guidOfAI;
    private Staff staff;
    private String guidOfSt;

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
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
}
