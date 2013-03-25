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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 15.08.12
 * Time: 10:34
 * To change this template use File | Settings | File Templates.
 */
public class GoodRequest extends DistributedObject {

    private Set<StateChange> stateChangeInternal;
    private Set<GoodRequestPosition> goodRequestPositionInternal;

    public Set<GoodRequestPosition> getGoodRequestPositionInternal() {
        return goodRequestPositionInternal;
    }

    public void setGoodRequestPositionInternal(Set<GoodRequestPosition> goodRequestPositionInternal) {
        this.goodRequestPositionInternal = goodRequestPositionInternal;
    }

    public Set<StateChange> getStateChangeInternal() {
        return stateChangeInternal;
    }

    public void setStateChangeInternal(Set<StateChange> stateChangeInternal) {
        this.stateChangeInternal = stateChangeInternal;
    }

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        //Staff st = DAOService.getInstance().findDistributedObjectByRefGUID(Staff.class, guidOfStaff);
        Staff st  = (Staff) DAOUtils.findDistributedObjectByRefGUID(session, guidOfStaff);
        if(st==null) throw new DistributedObjectException("NOT_FOUND_VALUE");
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
    public static final String[] GOOD_REQUEST_STATES = {"Создан", "К исполнению", "Выполнен"};

    public String getStateSelect() {
        if (state != null) {
            return GOOD_REQUEST_STATES[state];
        } else {
            return "";
        }
    }

    public String[] getGoodRequestStates() {
        return GOOD_REQUEST_STATES;
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

    public String getCreatedDateFormatted() {
        return formatDate(createdDate);
    }

    public String getLastUpdateFormatted() {
        return formatDate(lastUpdate);
    }

    public String getDeleteDateFormatted() {
        return formatDate(deleteDate);
    }

    public String getDoneDateFormatted() {
        return formatDate(doneDate);
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        if (date != null) {
            String result = sdf.format(date);
            return result;
        } else {
            return null;
        }
    }

}
