/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 18.08.12
 * Time: 12:15
 * To change this template use File | Settings | File Templates.
 */
public class Ksu2Record extends DistributedObject {

    private Integer recordNumber;
    private Fund fund;
    private Date retirementDate;
    private RetirementReason retirementReason;

    private String guidFund;
    private String guidRetirementReason;

    @Override
    protected void appendAttributes(Element element) {
        //setAttribute(element, "GuidFund", guid);
        //setAttribute(element, "GuidRetirementReason", guid);
        //setAttribute(element, "RetirementDate", getDateFormat().format(retirementDate));
        //setAttribute(element, "RecordNumber", recordNumber);
    }

    @Override
    public Ksu2Record parseAttributes(Node node) throws Exception {

        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);

        guidFund = getStringAttributeValue(node, "GuidFund", 36);
        guidRetirementReason = getStringAttributeValue(node, "GuidRetirementReason", 36);
        retirementDate = getDateOnlyAttributeValue(node, "RetirementDate");
        recordNumber = getIntegerAttributeValue(node, "RecordNumber");
        setSendAll(true);
        return this;
    }

    @Override
    public void preProcess(Session session) throws DistributedObjectException{
        //DAOService daoService = DAOService.getInstance();
        //setRetirementReason(daoService.findDistributedObjectByRefGUID(RetirementReason.class, guidRetirementReason));
        RetirementReason rr = (RetirementReason) DAOUtils.findDistributedObjectByRefGUID(session, guidRetirementReason);
        if(rr==null) {
            DistributedObjectException distributedObjectException = new DistributedObjectException("RetirementReason NOT_FOUND_VALUE");
            distributedObjectException.setData(guidRetirementReason);
            throw distributedObjectException;
        }
        setRetirementReason(rr);

        //setFund(daoService.findDistributedObjectByRefGUID(Fund.class, guidFund));
        Fund f = (Fund) DAOUtils.findDistributedObjectByRefGUID(session, guidFund);
        if(f==null){
            DistributedObjectException distributedObjectException = new DistributedObjectException("Fund NOT_FOUND_VALUE");
            distributedObjectException.setData(guidFund);
            throw distributedObjectException;
            //throw new DistributedObjectException("NOT_FOUND_VALUE");
        }
        setFund(f);
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setRecordNumber(((Ksu2Record) distributedObject).getRecordNumber());
        setFund(((Ksu2Record) distributedObject).getFund());
        setRetirementReason(((Ksu2Record) distributedObject).getRetirementReason());
        setRetirementDate(((Ksu2Record) distributedObject).getRetirementDate());
    }

    public Integer getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(Integer recordNumber) {
        this.recordNumber = recordNumber;
    }

    public Fund getFund() {
        return fund;
    }

    public void setFund(Fund fund) {
        this.fund = fund;
    }

    public Date getRetirementDate() {
        return retirementDate;
    }

    public void setRetirementDate(Date retirementDate) {
        this.retirementDate = retirementDate;
    }

    public RetirementReason getRetirementReason() {
        return retirementReason;
    }

    public void setRetirementReason(RetirementReason retirementReason) {
        this.retirementReason = retirementReason;
    }

    @Override
    public String toString() {
        return "Ksu2Record{" +
                "recordNumber=" + recordNumber +
                ", fund=" + fund +
                ", retirementDate=" + retirementDate +
                ", retirementReason=" + retirementReason +
                '}';
    }
}
