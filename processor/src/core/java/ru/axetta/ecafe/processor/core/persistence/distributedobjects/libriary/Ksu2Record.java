/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

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

    private int recordNumber;
    private Fund fund;
    private Date retirementDate;
    private RetirementReason retirementReason;

    private String guidFund;
    private String guidRetirementReason;

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "Guid", guid);
    }

    @Override
    public Ksu2Record parseAttributes(Node node) throws Exception {

        guidFund = getStringAttributeValue(node, "guidFund", 1024);
        guidRetirementReason = getStringAttributeValue(node, "guidRetirementReason", 1024);
        retirementDate = getDateTimeAttributeValue(node, "retirementDate");
        recordNumber = getIntegerAttributeValue(node, "recordNumber");
        return this;
    }

    @Override
    public void preProcess() {
        DAOService daoService = DAOService.getInstance();
        setRetirementReason(daoService.findDistributedObjectByRefGUID(RetirementReason.class, guidRetirementReason));
        setFund(daoService.findDistributedObjectByRefGUID(Fund.class, guidFund));
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setRecordNumber(((Ksu2Record) distributedObject).getRecordNumber());
        setFund(((Ksu2Record) distributedObject).getFund());
        setRetirementReason(((Ksu2Record) distributedObject).getRetirementReason());
        setRetirementDate(((Ksu2Record) distributedObject).getRetirementDate());
    }

    public int getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(int recordNumber) {
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
