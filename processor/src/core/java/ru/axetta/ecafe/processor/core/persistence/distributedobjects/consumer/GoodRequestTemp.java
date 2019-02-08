/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ConsumerRequestDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DocumentState;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.Staff;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.StateChange;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 15.08.12
 * Time: 10:34
 * To change this template use File | Settings | File Templates.
 */
public class GoodRequestTemp extends ConsumerRequestDistributedObject {

    private Date dateOfGoodsRequest;
    private String number;
    private Date doneDate;
    private String comment;
    private String guidOfStaff;
    private Staff staff;
    private DocumentState state;
    private Integer requestType;
    private Set<StateChange> stateChangeInternal;
    private Set<GoodRequestPositionTemp> goodRequestPositionInternal;
    private InformationContents informationContent = InformationContents.ONLY_CURRENT_ORG;


    @Override
    public void createProjections(Criteria criteria) {

    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {

    }

    @Override
    protected void appendAttributes(Element element) {

    }

    @Override
    protected GoodRequestTemp parseAttributes(Node node) throws Exception {
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {

    }

    @Override
    public void setNewInformationContent(InformationContents informationContent) {
        this.informationContent = informationContent;
    }

    public DocumentState getState() {
        return state;
    }

    public void setState(DocumentState state) {
        this.state = state;
    }

    public void setIntState(Integer value) {
        this.state = DocumentState.values()[value];
    }

    public Integer getIntState() {
        return this.state.ordinal();
    }

    public List<GoodRequestPositionTemp> getGoodRequestPosition(){
        return new ArrayList<GoodRequestPositionTemp>(getGoodRequestPositionInternal());
    }

    public Set<GoodRequestPositionTemp> getGoodRequestPositionInternal() {
        return goodRequestPositionInternal;
    }

    public void setGoodRequestPositionInternal(Set<GoodRequestPositionTemp> goodRequestPositionInternal) {
        this.goodRequestPositionInternal = goodRequestPositionInternal;
    }

    public Set<StateChange> getStateChangeInternal() {
        return stateChangeInternal;
    }

    public void setStateChangeInternal(Set<StateChange> stateChangeInternal) {
        this.stateChangeInternal = stateChangeInternal;
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

    public Integer getRequestType() {
        return requestType;
    }

    public void setRequestType(Integer requestType) {
        this.requestType = requestType;
    }
}
