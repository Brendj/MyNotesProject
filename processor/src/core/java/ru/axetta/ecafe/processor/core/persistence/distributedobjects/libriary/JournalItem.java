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
 * Time: 12:59
 * To change this template use File | Settings | File Templates.
 */
public class JournalItem extends LibraryDistributedObject {

    private Journal journal;
    private Fund fund;
    private Ksu1Record ksu1Record;
    private Ksu2Record ksu2Record;

    private Date date;
    private String number;
    private int cost;

    private String guidJournal;
    private String guidFund;
    private String guidKsu1Record;
    private String guidKsu2Record;
    private Set<Issuable> issuableInternal;

    @Override
    public void createProjections(Criteria criteria, int currentLimit, String currentLastGuid) {
        //criteria.createAlias("fund", "f", JoinType.LEFT_OUTER_JOIN);
        //criteria.createAlias("journal","j", JoinType.LEFT_OUTER_JOIN);
        //criteria.createAlias("ksu1Record", "k1", JoinType.LEFT_OUTER_JOIN);
        //criteria.createAlias("ksu2Record", "k2", JoinType.LEFT_OUTER_JOIN);
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("guid"), "guid");
        projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");

        //projectionList.add(Projections.property("date"), "date");
        //projectionList.add(Projections.property("number"), "number");
        //projectionList.add(Projections.property("cost"), "cost");

        //projectionList.add(Projections.property("f.guid"), "guidFund");
        //projectionList.add(Projections.property("j.guid"), "guidJournal");
        //projectionList.add(Projections.property("k1.guid"), "guidKsu1Record");
        //projectionList.add(Projections.property("k2.guid"), "guidKsu2Record");

        criteria.setProjection(projectionList);
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion, int currentLimit, String currentLastGuid) throws Exception {
        return null;//toSelfProcess(session, idOfOrg, currentMaxVersion);
    }

    @Override
    protected void appendAttributes(Element element) {}

    @Override
    public JournalItem parseAttributes(Node node) throws Exception {
        guidFund = XMLUtils.getStringAttributeValue(node, "GuidFund", 36);
        guidJournal = XMLUtils.getStringAttributeValue(node, "GuidJournal", 36);
        guidKsu1Record = XMLUtils.getStringAttributeValue(node, "GuidKsu1Record", 36);
        guidKsu2Record = XMLUtils.getStringAttributeValue(node, "GuidKsu2Record", 36);
        date = XMLUtils.getDateAttributeValue(node, "Date");
        number = XMLUtils.getStringAttributeValue(node, "Number", 10);
        cost = XMLUtils.getIntegerAttributeValue(node, "Cost");
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        Journal j = DAOUtils.findDistributedObjectByRefGUID(Journal.class, session, guidJournal);
        if (j == null) {
            DistributedObjectException distributedObjectException = new DistributedObjectException("Journal NOT_FOUND_VALUE");
            distributedObjectException.setData(guidJournal);
            throw distributedObjectException;
        }
        setJournal(j);
        Fund f = DAOUtils.findDistributedObjectByRefGUID(Fund.class, session, guidFund);
        setFund(f);
        Ksu1Record ksu1 = DAOUtils.findDistributedObjectByRefGUID(Ksu1Record.class, session, guidKsu1Record);
        setKsu1Record(ksu1);
        Ksu2Record ksu2 = DAOUtils.findDistributedObjectByRefGUID(Ksu2Record.class, session, guidKsu2Record);
        setKsu2Record(ksu2);
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner((distributedObject).getOrgOwner());
        setFund(((JournalItem) distributedObject).getFund());
        setJournal(((JournalItem) distributedObject).getJournal());
        setKsu1Record(((JournalItem) distributedObject).getKsu1Record());
        setKsu2Record(((JournalItem) distributedObject).getKsu2Record());
        setDate(((JournalItem) distributedObject).getDate());
        setNumber(((JournalItem) distributedObject).getNumber());
        setCost(((JournalItem) distributedObject).getCost());
    }

    public Set<Issuable> getIssuableInternal() {
        return issuableInternal;
    }

    public void setIssuableInternal(Set<Issuable> issuableInternal) {
        this.issuableInternal = issuableInternal;
    }


    public Journal getJournal() {
        return journal;
    }

    public void setJournal(Journal journal) {
        this.journal = journal;
    }

    public Fund getFund() {
        return fund;
    }

    public void setFund(Fund fund) {
        this.fund = fund;
    }

    public Ksu1Record getKsu1Record() {
        return ksu1Record;
    }

    public void setKsu1Record(Ksu1Record ksu1Record) {
        this.ksu1Record = ksu1Record;
    }

    public Ksu2Record getKsu2Record() {
        return ksu2Record;
    }

    public void setKsu2Record(Ksu2Record ksu2Record) {
        this.ksu2Record = ksu2Record;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return String
                .format("JournalItem{journal=%s, fund=%s, ksu1Record=%s, ksu2Record=%s, date=%s, number='%s', cost=%d}",
                        journal, fund, ksu1Record, ksu2Record, date, number, cost);
    }
}
