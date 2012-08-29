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
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 */
public class Ksu1Record extends DistributedObject {

    private int recordNumber;
    private Fund fund;
    private Date incomeDate;
    private AccompanyingDocument accompanyingDocument;

    private String guidFund;
    private String guidAccompanyingDocument;

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "Guid", guid);
    }

    @Override
    public Ksu1Record parseAttributes(Node node) throws Exception {

        guidFund = getStringAttributeValue(node, "guidFund", 1024);
        guidAccompanyingDocument = getStringAttributeValue(node, "guidAccompanyingDocument", 1024);
        incomeDate = getDateTimeAttributeValue(node, "incomeDate");
        recordNumber = getIntegerAttributeValue(node, "recordNumber");
        return this;
    }

    @Override
    public void preProcess() {
        DAOService daoService = DAOService.getInstance();
        setAccompanyingDocument(
                daoService.findDistributedObjectByRefGUID(AccompanyingDocument.class, guidAccompanyingDocument));
        setFund(daoService.findDistributedObjectByRefGUID(Fund.class, guidFund));
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setRecordNumber(((Ksu1Record) distributedObject).getRecordNumber());
        setFund(((Ksu1Record) distributedObject).getFund());
        setAccompanyingDocument(((Ksu1Record) distributedObject).getAccompanyingDocument());
        setIncomeDate(((Ksu1Record) distributedObject).getIncomeDate());
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

    public Date getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(Date incomeDate) {
        this.incomeDate = incomeDate;
    }

    public AccompanyingDocument getAccompanyingDocument() {
        return accompanyingDocument;
    }

    public void setAccompanyingDocument(AccompanyingDocument accompanyingDocument) {
        this.accompanyingDocument = accompanyingDocument;
    }

    @Override
    public String toString() {
        return "Ksu1Record{" +
                "recordNumber=" + recordNumber +
                ", fund=" + fund +
                ", incomeDate=" + incomeDate +
                ", accompanyingDocument=" + accompanyingDocument +
                '}';
    }
}
