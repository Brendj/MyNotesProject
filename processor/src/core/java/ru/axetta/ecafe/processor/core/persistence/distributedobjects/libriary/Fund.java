/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.LibraryDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 18.08.12
 * Time: 11:38
 * To change this template use File | Settings | File Templates.
 */
public class Fund extends LibraryDistributedObject {

    private String fundName;
    private Boolean stud;
    private Set<Ksu2Record> ksu2RecordInternal;
    private Set<Ksu1Record> ksu1RecordInternal;
    private Set<JournalItem> journalItemInternal;
    private Set<Journal> journalInternal;
    private Set<Instance> instanceInternal;

    @Override
    public void createProjections(Criteria criteria) {
        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("fundName"), "fundName");
        projectionList.add(Projections.property("stud"), "stud");

        criteria.setProjection(projectionList);
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        return null; //toSelfProcess(session, idOfOrg, currentMaxVersion);
    }

    @Override
    protected void appendAttributes(Element element) {}

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {}

    @Override
    public Fund parseAttributes(Node node) throws Exception {
        String fundName = XMLUtils.getStringAttributeValue(node, "FundName", 128);
        if (fundName != null)
            setFundName(fundName);
        Boolean bollStud = XMLUtils.getBooleanAttributeValue(node, "Stud");
        if (bollStud != null)
            setStud(bollStud);
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setFundName(((Fund) distributedObject).getFundName());
        setStud(((Fund) distributedObject).getStud());
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    @Override
    public String toString() {
        return String.format("Fund{fundName='%s'}", fundName);
    }

    public Set<Instance> getInstanceInternal() {
        return instanceInternal;
    }

    public void setInstanceInternal(Set<Instance> instanceInternal) {
        this.instanceInternal = instanceInternal;
    }

    public Set<Journal> getJournalInternal() {
        return journalInternal;
    }

    public void setJournalInternal(Set<Journal> journalInternal) {
        this.journalInternal = journalInternal;
    }

    public Set<JournalItem> getJournalItemInternal() {
        return journalItemInternal;
    }

    public void setJournalItemInternal(Set<JournalItem> journalItemInternal) {
        this.journalItemInternal = journalItemInternal;
    }

    public Set<Ksu1Record> getKsu1RecordInternal() {
        return ksu1RecordInternal;
    }

    public void setKsu1RecordInternal(Set<Ksu1Record> ksu1RecordInternal) {
        this.ksu1RecordInternal = ksu1RecordInternal;
    }

    public Set<Ksu2Record> getKsu2RecordInternal() {
        return ksu2RecordInternal;
    }

    public void setKsu2RecordInternal(Set<Ksu2Record> ksu2RecordInternal) {
        this.ksu2RecordInternal = ksu2RecordInternal;
    }

    public Boolean getStud() {
        return stud;
    }

    public void setStud(Boolean stud) {
        this.stud = stud;
    }
}
