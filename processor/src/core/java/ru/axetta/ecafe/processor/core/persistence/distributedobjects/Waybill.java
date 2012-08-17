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
public class Waybill extends DistributedObject {

    @Override
    public void preProcess() throws DistributedObjectException {
        Staff st = DAOService.getInstance().findDistributedObjectByRefGUID(Staff.class, guidOfSt);
        if(st==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setStaff(st);
        ActOfWaybillDifference awd = DAOService.getInstance().findDistributedObjectByRefGUID(ActOfWaybillDifference.class, guidOfAWD);
        if(awd==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setActOfWaybillDifference(awd);
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element,"Number", number);
        setAttribute(element,"DateWayBill", getDateFormat().format(dateOfWayBill));
        setAttribute(element,"State", state);
        setAttribute(element, "Shipper", shipper);
        setAttribute(element,"Receiver", receiver);
        setAttribute(element, "GuidOfS", staff.getGuid());
        setAttribute(element, "GuidOfAWD", actOfWaybillDifference.getGuid());
    }

    @Override
    protected Waybill parseAttributes(Node node) throws ParseException, IOException {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        String stringNumber = getStringAttributeValue(node, "Number", 128);
        if(stringNumber != null) setNumber(stringNumber);
        Date dateWayBill = getDateAttributeValue(node, "DateWayBill");
        if(dateWayBill!=null) setDateOfWayBill(dateWayBill);
        Integer integerState = getIntegerAttributeValue(node, "State");
        if(integerState != null) setState(integerState);
        String stringShipper = getStringAttributeValue(node, "Shipper", 128);
        if(stringShipper != null) setShipper(stringShipper);
        String stringReceiver = getStringAttributeValue(node, "Receiver", 128);
        if(stringReceiver != null) setReceiver(stringReceiver);
        guidOfSt = getStringAttributeValue(node,"GuidOfS",36);
        guidOfAWD = getStringAttributeValue(node,"GuidOfAWD",36);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((Waybill) distributedObject).getOrgOwner());
        setNumber(((Waybill) distributedObject).getNumber());
        setDateOfWayBill(((Waybill) distributedObject).getDateOfWayBill());
        setState(((Waybill) distributedObject).getState());
        setShipper(((Waybill) distributedObject).getShipper());
        setReceiver(((Waybill) distributedObject).getReceiver());
    }

    private String number;
    private Date dateOfWayBill;
    private Integer state;
    private String shipper;
    private String receiver;
    private ActOfWaybillDifference actOfWaybillDifference;
    private String guidOfAWD;
    private Staff staff;
    private String guidOfSt;

    public String getGuidOfSt() {
        return guidOfSt;
    }

    public void setGuidOfSt(String guidOfSt) {
        this.guidOfSt = guidOfSt;
    }

    public String getGuidOfAWD() {
        return guidOfAWD;
    }

    public void setGuidOfAWD(String guidOfAWD) {
        this.guidOfAWD = guidOfAWD;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getDateOfWayBill() {
        return dateOfWayBill;
    }

    public void setDateOfWayBill(Date dateOfWayBill) {
        this.dateOfWayBill = dateOfWayBill;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public ActOfWaybillDifference getActOfWaybillDifference() {
        return actOfWaybillDifference;
    }

    public void setActOfWaybillDifference(ActOfWaybillDifference actOfWaybillDifference) {
        this.actOfWaybillDifference = actOfWaybillDifference;
    }

}
