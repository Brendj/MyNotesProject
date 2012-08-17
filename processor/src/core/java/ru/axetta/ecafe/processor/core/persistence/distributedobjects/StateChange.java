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
 * Date: 14.08.12
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class StateChange extends DistributedObject {


    @Override
    public void preProcess() throws DistributedObjectException {
        Waybill wb = DAOService.getInstance().findDistributedObjectByRefGUID(Waybill.class,guidOfWB);
        if(wb==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setWayBill(wb);
        InternalDisposingDocument idd = DAOService.getInstance().findDistributedObjectByRefGUID(InternalDisposingDocument.class,guidOfIDD);
        if(idd==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setInternalDisposingDocument(idd);
        GoodRequest gr = DAOService.getInstance().findDistributedObjectByRefGUID(GoodRequest.class,guidOfGR);
        if(gr==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setGoodRequest(gr);
        ReturnDocument rd = DAOService.getInstance().findDistributedObjectByRefGUID(ReturnDocument.class,guidOfRD);
        if(rd==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setReturnDocument(rd);
        Staff st = DAOService.getInstance().findDistributedObjectByRefGUID(Staff.class,guidOfS);
        if(st==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setStaff(st);
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element, "StateFrom", stateFrom);
        setAttribute(element, "StateTo", stateTo);
        setAttribute(element, "Date", getDateFormat().format(date));
        setAttribute(element, "GuidOfIWB", wayBill.getGuid());
        setAttribute(element, "GuidOfIDD", internalDisposingDocument.getGuid());
        setAttribute(element, "GuidOfIGR", goodRequest.getGuid());
        setAttribute(element, "GuidOfIRD", returnDocument.getGuid());
        setAttribute(element, "GuidOfS", staff.getGuid());
    }

    @Override
    protected StateChange parseAttributes(Node node) throws ParseException, IOException {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        Long longStateFrom = getLongAttributeValue(node,"StateFrom");
        if(longStateFrom!=null) setStateFrom(longStateFrom);
        Long longStateTo = getLongAttributeValue(node,"StateTo");
        if(longStateTo!=null) setStateTo(longStateTo);
        Date dateOfInternalIncomingDocument = getDateAttributeValue(node,"Date");
        if(dateOfInternalIncomingDocument != null) setDate(dateOfInternalIncomingDocument);
        guidOfWB = getStringAttributeValue(node,"GuidOfIWB",36);
        guidOfIDD = getStringAttributeValue(node,"GuidOfIDD",36);
        guidOfGR = getStringAttributeValue(node,"GuidOfIGR",36);
        guidOfRD = getStringAttributeValue(node,"GuidOfIRD",36);
        guidOfS = getStringAttributeValue(node,"GuidOfS",36);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((StateChange) distributedObject).getOrgOwner());
        setDate(((StateChange) distributedObject).getDate());
        setStateFrom(((StateChange) distributedObject).getStateFrom());
        setStateTo(((StateChange) distributedObject).getStateTo());
    }

    private Date date;
    private Long stateFrom;
    private Long stateTo;
    private GoodRequest goodRequest;
    private String guidOfGR;
    private InternalDisposingDocument internalDisposingDocument;
    private String guidOfIDD;
    private Staff staff;
    private String guidOfS;
    private ReturnDocument returnDocument;
    private String guidOfRD;
    private Waybill wayBill;
    private String guidOfWB;

    public String getGuidOfS() {
        return guidOfS;
    }

    public void setGuidOfS(String guidOfS) {
        this.guidOfS = guidOfS;
    }

    public String getGuidOfRD() {
        return guidOfRD;
    }

    public void setGuidOfRD(String guidOfRD) {
        this.guidOfRD = guidOfRD;
    }

    public String getGuidOfGR() {
        return guidOfGR;
    }

    public void setGuidOfGR(String guidOfGR) {
        this.guidOfGR = guidOfGR;
    }

    public String getGuidOfWB() {
        return guidOfWB;
    }

    public void setGuidOfWB(String guidOfWB) {
        this.guidOfWB = guidOfWB;
    }

    public Waybill getWayBill() {
        return wayBill;
    }

    public void setWayBill(Waybill wayBill) {
        this.wayBill = wayBill;
    }

    public ReturnDocument getReturnDocument() {
        return returnDocument;
    }

    public void setReturnDocument(ReturnDocument returnDocument) {
        this.returnDocument = returnDocument;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getGuidOfIDD() {
        return guidOfIDD;
    }

    public void setGuidOfIDD(String guidOfIDD) {
        this.guidOfIDD = guidOfIDD;
    }

    public InternalDisposingDocument getInternalDisposingDocument() {
        return internalDisposingDocument;
    }

    public void setInternalDisposingDocument(InternalDisposingDocument internalDisposingDocument) {
        this.internalDisposingDocument = internalDisposingDocument;
    }

    public GoodRequest getGoodRequest() {
        return goodRequest;
    }

    public void setGoodRequest(GoodRequest goodRequest) {
        this.goodRequest = goodRequest;
    }

    public Long getStateTo() {
        return stateTo;
    }

    public void setStateTo(Long stateTo) {
        this.stateTo = stateTo;
    }

    public Long getStateFrom() {
        return stateFrom;
    }

    public void setStateFrom(Long stateFrom) {
        this.stateFrom = stateFrom;
    }
}
