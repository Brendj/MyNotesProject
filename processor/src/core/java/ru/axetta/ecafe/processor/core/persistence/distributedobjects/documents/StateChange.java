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
 * Date: 14.08.12
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class StateChange extends DistributedObject {


    @Override
    public void preProcess() throws DistributedObjectException {
        WayBill wb = DAOService.getInstance().findDistributedObjectByRefGUID(WayBill.class,guidOfWB);
        InternalDisposingDocument idd = DAOService.getInstance().findDistributedObjectByRefGUID(InternalDisposingDocument.class,guidOfIDD);
        GoodRequest gr = DAOService.getInstance().findDistributedObjectByRefGUID(GoodRequest.class,guidOfGR);
        InternalIncomingDocument iid = DAOService.getInstance().findDistributedObjectByRefGUID(InternalIncomingDocument.class,guidOfIID);
        if(wb == null && idd == null && gr==null && iid==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        if(wb!=null) setWayBill(wb);
        if(idd!=null) setInternalDisposingDocument(idd);
        if(gr!=null) setGoodRequest(gr);
        if(iid!=null) setInternalIncomingDocument(iid);
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
        if(wayBill!=null) setAttribute(element, "GuidOfWayBill", wayBill.getGuid());
        if(internalDisposingDocument!=null) setAttribute(element, "GuidOfDisposingDoc", internalDisposingDocument.getGuid());
        if(goodRequest!=null) setAttribute(element, "GuidOfGoodsRequest", goodRequest.getGuid());
        if(internalIncomingDocument!=null) setAttribute(element, "GuidOfIncomingDocument", internalIncomingDocument.getGuid());
        setAttribute(element, "GuidOfStaff", staff.getGuid());
    }

    @Override
    protected StateChange parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        Long longStateFrom = getLongAttributeValue(node,"StateFrom");
        if(longStateFrom!=null) setStateFrom(longStateFrom);
        Long longStateTo = getLongAttributeValue(node,"StateTo");
        if(longStateTo!=null) setStateTo(longStateTo);
        Date dateOfInternalIncomingDocument = getDateTimeAttributeValue(node, "Date");
        if(dateOfInternalIncomingDocument != null) setDate(dateOfInternalIncomingDocument);
        guidOfWB = getStringAttributeValue(node,"GuidOfWayBill",36);
        guidOfIDD = getStringAttributeValue(node,"GuidOfDisposingDoc",36);
        guidOfGR = getStringAttributeValue(node,"GuidOfGoodsRequest",36);
        guidOfIID = getStringAttributeValue(node,"GuidOfIncomingDocument",36);
        guidOfS = getStringAttributeValue(node,"GuidOfStaff",36);
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
    private InternalIncomingDocument internalIncomingDocument;
    private String guidOfIID;
    private WayBill wayBill;
    private String guidOfWB;

    public String getGuidOfS() {
        return guidOfS;
    }

    public void setGuidOfS(String guidOfS) {
        this.guidOfS = guidOfS;
    }

    public String getGuidOfIID() {
        return guidOfIID;
    }

    public void setGuidOfIID(String guidOfIID) {
        this.guidOfIID = guidOfIID;
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

    public WayBill getWayBill() {
        return wayBill;
    }

    public void setWayBill(WayBill wayBill) {
        this.wayBill = wayBill;
    }

    public InternalIncomingDocument getInternalIncomingDocument() {
        return internalIncomingDocument;
    }

    public void setInternalIncomingDocument(InternalIncomingDocument internalIncomingDocument) {
        this.internalIncomingDocument = internalIncomingDocument;
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
