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
import org.hibernate.sql.JoinType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 18.08.12
 * Time: 12:15
 * To change this template use File | Settings | File Templates.
 */
public class Ksu2Record extends LibraryDistributedObject {

    private Integer recordNumber;
    private Date retirementDate;

    private Fund fund;
    private RetirementReason retirementReason;

    private String guidFund;
    private String guidRetirementReason;
    private Set<JournalItem> journalItemInternal;
    private Set<Instance> instanceInternal;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("fund", "f", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("retirementReason","r", JoinType.LEFT_OUTER_JOIN);
        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);
        projectionList.add(Projections.property("recordNumber"), "recordNumber");
        projectionList.add(Projections.property("retirementDate"), "retirementDate");

        projectionList.add(Projections.property("f.guid"), "guidFund");
        projectionList.add(Projections.property("r.guid"), "guidRetirementReason");

        criteria.setProjection(projectionList);
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion) throws Exception {
        return toSelfProcess(session, idOfOrg, currentMaxVersion);
    }

    @Override
    protected void appendAttributes(Element element) {}

    @Override
    public Ksu2Record parseAttributes(Node node) throws Exception {
        guidFund = XMLUtils.getStringAttributeValue(node, "GuidFund", 36);
        guidRetirementReason = XMLUtils.getStringAttributeValue(node, "GuidRetirementReason", 36);
        retirementDate = XMLUtils.getDateAttributeValue(node, "RetirementDate");
        recordNumber = XMLUtils.getIntegerAttributeValue(node, "RecordNumber");
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException{
        RetirementReason rr = DAOUtils.findDistributedObjectByRefGUID(RetirementReason.class, session, guidRetirementReason);
        if(rr==null) {
            DistributedObjectException distributedObjectException = new DistributedObjectException("RetirementReason NOT_FOUND_VALUE");
            distributedObjectException.setData(guidRetirementReason);
            throw distributedObjectException;
        }
        setRetirementReason(rr);

        Fund f = DAOUtils.findDistributedObjectByRefGUID(Fund.class, session, guidFund);
        if(f==null){
            DistributedObjectException distributedObjectException = new DistributedObjectException("Fund NOT_FOUND_VALUE");
            distributedObjectException.setData(guidFund);
            throw distributedObjectException;
        }
        setFund(f);
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setRecordNumber(((Ksu2Record) distributedObject).getRecordNumber());
        setFund(((Ksu2Record) distributedObject).getFund());
        setGuidFund(((Ksu2Record) distributedObject).getGuidFund());
        setRetirementReason(((Ksu2Record) distributedObject).getRetirementReason());
        setGuidRetirementReason(((Ksu2Record) distributedObject).getGuidRetirementReason());
        setRetirementDate(((Ksu2Record) distributedObject).getRetirementDate());
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

    public String getGuidFund() {
        return guidFund;
    }

    public void setGuidFund(String guidFund) {
        this.guidFund = guidFund;
    }

    public String getGuidRetirementReason() {
        return guidRetirementReason;
    }

    public void setGuidRetirementReason(String guidRetirementReason) {
        this.guidRetirementReason = guidRetirementReason;
    }

    @Override
    public String toString() {
        return String
                .format("Ksu2Record{recordNumber=%d, fund=%s, retirementDate=%s, retirementReason=%s}", recordNumber,
                        fund, retirementDate, retirementReason);
    }
}
