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
public class InternalIncomingDocument extends DistributedObject {

    @Override
    public void preProcess() throws DistributedObjectException {
        Waybill wb = DAOService.getInstance().findDistributedObjectByRefGUID(Waybill.class,guidOfWB);
        if(wb==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setWayBill(wb);
        InternalDisposingDocument idd = DAOService.getInstance().findDistributedObjectByRefGUID(InternalDisposingDocument.class,guidOfIDD);
        if(idd==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setInternalDisposingDocument(idd);
        ActOfInventarization ai = DAOService.getInstance().findDistributedObjectByRefGUID(ActOfInventarization.class,guidOfAI);
        if(ai==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setActOfInventarization(ai);
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
        setAttribute(element, "State", state);
        setAttribute(element, "Date", getDateFormat().format(date));
        setAttribute(element, "GuidOfWayBill", wayBill.getGuid());
        setAttribute(element, "GuidOfDisposingDoc", internalDisposingDocument.getGuid());
        setAttribute(element, "GuidOfInventorizationAct", actOfInventarization.getGuid());
        setAttribute(element, "GuidOfReturnDoc", returnDocument.getGuid());
        setAttribute(element, "GuidOfStaff", staff.getGuid());
    }

    @Override
    protected InternalIncomingDocument parseAttributes(Node node) throws ParseException, IOException {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        Integer integerState = getIntegerAttributeValue(node,"State");
        if(integerState!=null) setState(integerState);
        Date dateOfInternalIncomingDocument = getDateAttributeValue(node,"Date");
        if(dateOfInternalIncomingDocument != null) setDate(dateOfInternalIncomingDocument);
        guidOfWB = getStringAttributeValue(node,"GuidOfWayBill",36);
        guidOfIDD = getStringAttributeValue(node,"GuidOfDisposingDoc",36);
        guidOfAI = getStringAttributeValue(node,"GuidOfInventorizationAct",36);
        guidOfRD = getStringAttributeValue(node,"GuidOfReturnDoc",36);
        guidOfS = getStringAttributeValue(node,"GuidOfStaff",36);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((InternalIncomingDocument) distributedObject).getOrgOwner());
        setDate(((InternalIncomingDocument) distributedObject).getDate());
        setState(((InternalIncomingDocument) distributedObject).getState());
    }

    private Integer state;
    private Date date;
    private InternalDisposingDocument internalDisposingDocument;
    private String guidOfIDD;
    private Staff staff;
    private String guidOfS;
    private ReturnDocument returnDocument;
    private String guidOfRD;
    private ActOfInventarization actOfInventarization;
    private String guidOfAI;
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

    public String getGuidOfAI() {
        return guidOfAI;
    }

    public void setGuidOfAI(String guidOfAI) {
        this.guidOfAI = guidOfAI;
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

    public ActOfInventarization getActOfInventarization() {
        return actOfInventarization;
    }

    public void setActOfInventarization(ActOfInventarization actOfInventarization) {
        this.actOfInventarization = actOfInventarization;
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
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
}
