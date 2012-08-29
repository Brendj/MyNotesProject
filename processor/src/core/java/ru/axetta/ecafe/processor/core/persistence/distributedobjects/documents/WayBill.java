/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.WayBillInterface;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sync.distributionsync.DistributedObjectException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.12
 * Time: 17:29
 * To change this template use File | Settings | File Templates.
 */
public class WayBill extends DistributedObject implements WayBillInterface {

    @Override
    public void preProcess() throws DistributedObjectException {
        Staff st = DAOService.getInstance().findDistributedObjectByRefGUID(Staff.class, guidOfSt);
        if(st==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setStaff(st);
        ActOfWaybillDifference awd = DAOService.getInstance().findDistributedObjectByRefGUID(ActOfWaybillDifference.class, guidOfAWD);
        if(awd!=null) setActOfWaybillDifference(awd);
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element,"Number", number);
        setAttribute(element,"Date", getDateFormat().format(dateOfWayBill));
        setAttribute(element,"State", state);
        setAttribute(element, "Shipper", shipper);
        setAttribute(element,"Receiver", receiver);
        setAttribute(element, "GuidOfStaff", staff.getGuid());
        if(actOfWaybillDifference!=null){
            setAttribute(element, "GuidOfActOfDifference", actOfWaybillDifference.getGuid());
        }
    }

    @Override
    protected WayBill parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        String stringNumber = getStringAttributeValue(node, "Number", 128);
        if(stringNumber != null) setNumber(stringNumber);
        Date dateWayBill = getDateTimeAttributeValue(node, "Date");
        if(dateWayBill!=null) setDateOfWayBill(dateWayBill);
        Integer integerState = getIntegerAttributeValue(node, "State");
        if(integerState != null) setState(integerState);
        String stringShipper = getStringAttributeValue(node, "Shipper", 128);
        if(stringShipper != null) setShipper(stringShipper);
        String stringReceiver = getStringAttributeValue(node, "Receiver", 128);
        if(stringReceiver != null) setReceiver(stringReceiver);
        guidOfSt = getStringAttributeValue(node,"GuidOfStaff",36);
        guidOfAWD = getStringAttributeValue(node,"GuidOfActOfDifference",36);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((WayBill) distributedObject).getOrgOwner());
        setNumber(((WayBill) distributedObject).getNumber());
        setDateOfWayBill(((WayBill) distributedObject).getDateOfWayBill());
        setState(((WayBill) distributedObject).getState());
        setShipper(((WayBill) distributedObject).getShipper());
        setReceiver(((WayBill) distributedObject).getReceiver());
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
