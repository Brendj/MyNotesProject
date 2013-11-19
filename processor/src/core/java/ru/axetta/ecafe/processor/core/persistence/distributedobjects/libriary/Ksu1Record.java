/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.LibraryDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 18.08.12
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 */
public class Ksu1Record extends LibraryDistributedObject {

    private int recordNumber;
    private Date incomeDate;

    private String guidFund;
    private Fund fund;
    private String guidAccompanyingDocument;
    private AccompanyingDocument accompanyingDocument;
    private Set<JournalItem> journalItemInternal;
    private Set<Instance> instanceInternal;

    @Override
    public void createProjections(Criteria criteria, int currentLimit, String currentLastGuid) {
        //criteria.createAlias("fund", "f", JoinType.LEFT_OUTER_JOIN);
        //criteria.createAlias("accompanyingDocument","a", JoinType.LEFT_OUTER_JOIN);
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("guid"), "guid");
        projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");

        //projectionList.add(Projections.property("recordNumber"), "recordNumber");
        //projectionList.add(Projections.property("incomeDate"), "incomeDate");

        //projectionList.add(Projections.property("f.guid"), "guidFund");
        //projectionList.add(Projections.property("a.guid"), "guidAccompanyingDocument");

        criteria.setProjection(projectionList);
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion, int currentLimi, String currentLastGuidt) throws Exception {
        return null;//toSelfProcess(session, idOfOrg, currentMaxVersion);
    }

    @Override
    protected void appendAttributes(Element element) {}

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException{
        AccompanyingDocument ad = DAOUtils.findDistributedObjectByRefGUID(AccompanyingDocument.class, session, guidAccompanyingDocument);
        if(ad==null) throw new DistributedObjectException("NOT_FOUND_VALUE AccompanyingDocument");
        setAccompanyingDocument(ad);
        Fund f =  DAOUtils.findDistributedObjectByRefGUID(Fund.class, session, guidFund);
        if(f==null) throw new DistributedObjectException("NOT_FOUND_VALUE Fund");
        setFund(f);
    }

    @Override
    public Ksu1Record parseAttributes(Node node) throws Exception {
        guidFund = XMLUtils.getStringAttributeValue(node, "GuidFund", 36);
        guidAccompanyingDocument = XMLUtils.getStringAttributeValue(node, "GuidAccompanyingDocument", 36);
        incomeDate = XMLUtils.getDateAttributeValue(node, "IncomeDate");
        recordNumber = XMLUtils.getIntegerAttributeValue(node, "RecordNumber");
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((Ksu1Record) distributedObject).getOrgOwner());
        setRecordNumber(((Ksu1Record) distributedObject).getRecordNumber());
        setFund(((Ksu1Record) distributedObject).getFund());
        setAccompanyingDocument(((Ksu1Record) distributedObject).getAccompanyingDocument());
        setIncomeDate(((Ksu1Record) distributedObject).getIncomeDate());
    }

    public Set<Instance> getInstanceInternal() {
        return instanceInternal;
    }

    public void setInstanceInternal(Set<Instance> instanceInternal) {
        this.instanceInternal = instanceInternal;
    }

    public Set<JournalItem> getJournalItemInternal() {
        return journalItemInternal;
    }

    public void setJournalItemInternal(Set<JournalItem> journalItemInternal) {
        this.journalItemInternal = journalItemInternal;
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
        return String
                .format("Ksu1Record{recordNumber=%d, fund=%s, incomeDate=%s, accompanyingDocument=%s}", recordNumber,
                        fund, incomeDate, accompanyingDocument);
    }
}
