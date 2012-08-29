/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sync.distributionsync.DistributedObjectException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 15.08.12
 * Time: 10:34
 * To change this template use File | Settings | File Templates.
 */
public class GoodRequest extends DistributedObject {

    @Override
    public void preProcess() throws DistributedObjectException {
        Staff st = DAOService.getInstance().findDistributedObjectByRefGUID(Staff.class, guidOfStaff);
        if(st==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setStaff(st);
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element,"Date", getDateFormat().format(dateOfGoodsRequest));
        setAttribute(element,"Number", number);
        setAttribute(element,"State", state);
        setAttribute(element,"DoneDate", getDateFormat().format(doneDate));
        setAttribute(element,"Comment", comment);
        setAttribute(element, "GuidOfStaff", staff.getGuid());
    }

    @Override
    protected GoodRequest parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        Date dateDateOfGoodsRequest = getDateTimeAttributeValue(node, "Date");
        String stringNumber = getStringAttributeValue(node, "Number", 128);
        if(stringNumber != null) setNumber(stringNumber);
        Integer integerState = getIntegerAttributeValue(node,"State");
        if(integerState != null) setState(integerState);
        if(dateDateOfGoodsRequest!=null) setDateOfGoodsRequest(dateDateOfGoodsRequest);
        Date dateDoneDate = getDateTimeAttributeValue(node, "DoneDate");
        if(dateDoneDate!=null) setDoneDate(dateDoneDate);
        String stringComment = getStringAttributeValue(node, "Comment", 128);
        if(stringComment != null) setComment(stringComment);
        guidOfStaff = getStringAttributeValue(node,"GuidOfStaff",36);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((GoodRequest) distributedObject).getOrgOwner());
        setDateOfGoodsRequest(((GoodRequest) distributedObject).getDateOfGoodsRequest());
        setNumber(((GoodRequest) distributedObject).getNumber());
        setState(((GoodRequest) distributedObject).getState());
        setDoneDate(((GoodRequest) distributedObject).getDoneDate());
        setComment(((GoodRequest) distributedObject).getComment());
    }

    private Date dateOfGoodsRequest;
    private String number;
    private Integer state;
    private Date doneDate;
    private String comment;
    private String guidOfStaff;
    private Staff staff;

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(Date doneDate) {
        this.doneDate = doneDate;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDateOfGoodsRequest() {
        return dateOfGoodsRequest;
    }

    public void setDateOfGoodsRequest(Date dateOfGoodsRequest) {
        this.dateOfGoodsRequest = dateOfGoodsRequest;
    }

}
