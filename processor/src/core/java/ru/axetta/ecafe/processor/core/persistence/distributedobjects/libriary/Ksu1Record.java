/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
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
    public void preProcess(Session session) throws DistributedObjectException{
        //DAOService daoService = DAOService.getInstance();
        //setAccompanyingDocument(
        //        daoService.findDistributedObjectByRefGUID(AccompanyingDocument.class, guidAccompanyingDocument));
        AccompanyingDocument ad = (AccompanyingDocument) DAOUtils.findDistributedObjectByRefGUID(session, guidAccompanyingDocument);
        if(ad==null) throw new DistributedObjectException("NOT_FOUND_VALUE");
        setAccompanyingDocument(ad);
        //setFund(daoService.findDistributedObjectByRefGUID(Fund.class, guidFund));
        Fund f = (Fund) DAOUtils.findDistributedObjectByRefGUID(session, guidFund);
        if(f==null) throw new DistributedObjectException("NOT_FOUND_VALUE");
        setFund(f);
    }

    @Override
    protected void appendAttributes(Element element) {
        //setAttribute(element, "GuidFund", guidFund);
        //setAttribute(element, "GuidAccompanyingDocument", guidAccompanyingDocument);
        //setAttribute(element, "IncomeDate", incomeDate);
        //setAttribute(element, "RecordNumber", recordNumber);
    }

    @Override
    public Ksu1Record parseAttributes(Node node) throws Exception {

        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);

        guidFund = getStringAttributeValue(node, "GuidFund", 36);
        guidAccompanyingDocument = getStringAttributeValue(node, "GuidAccompanyingDocument", 36);
        incomeDate = getDateOnlyAttributeValue(node, "IncomeDate");
        recordNumber = getIntegerAttributeValue(node, "RecordNumber");
        setSendAll(SendToAssociatedOrgs.Send);
        return this;
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
