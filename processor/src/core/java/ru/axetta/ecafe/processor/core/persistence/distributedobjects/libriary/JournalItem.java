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
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 18.08.12
 * Time: 12:59
 * To change this template use File | Settings | File Templates.
 */
public class JournalItem extends DistributedObject {

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

    public Set<Issuable> getIssuableInternal() {
        return issuableInternal;
    }

    public void setIssuableInternal(Set<Issuable> issuableInternal) {
        this.issuableInternal = issuableInternal;
    }


    @Override
    protected void appendAttributes(Element element) {
        //setAttribute(element, "Guid", guid);
    }

    @Override
    public JournalItem parseAttributes(Node node) throws Exception {

        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);

        guidFund = getStringAttributeValue(node, "GuidFund", 36);
        guidJournal = getStringAttributeValue(node, "GuidJournal", 36);
        guidKsu1Record = getStringAttributeValue(node, "GuidKsu1Record", 36);
        guidKsu2Record = getStringAttributeValue(node, "GuidKsu2Record", 36);

        date = getDateOnlyAttributeValue(node, "Date");
        number = getStringAttributeValue(node, "Number", 10);
        cost = getIntegerAttributeValue(node, "Cost");
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        //DAOService daoService = DAOService.getInstance();
        //setJournal(daoService.findDistributedObjectByRefGUID(Journal.class, guidJournal));
        //setFund(daoService.findDistributedObjectByRefGUID(Fund.class, guidFund));

        Journal j = (Journal) DAOUtils.findDistributedObjectByRefGUID(session, guidJournal);
        if(j==null) {
            DistributedObjectException distributedObjectException =  new DistributedObjectException("Journal NOT_FOUND_VALUE");
            distributedObjectException.setData(guidJournal);
            throw  distributedObjectException;
            //throw new DistributedObjectException("NOT_FOUND_VALUE");
        }
        setJournal(j);

        Fund f = (Fund) DAOUtils.findDistributedObjectByRefGUID(session, guidFund);
        //if(f==null) throw new DistributedObjectException("NOT_FOUND_VALUE");
        setFund(f);

        Ksu1Record ksu1 = (Ksu1Record) DAOUtils.findDistributedObjectByRefGUID(session, guidKsu1Record);
        //if(ksu1==null) throw new DistributedObjectException("NOT_FOUND_VALUE");
        setKsu1Record(ksu1);

        Ksu2Record ksu2 = (Ksu2Record) DAOUtils.findDistributedObjectByRefGUID(session, guidKsu2Record);
        //if(ksu2==null) throw new DistributedObjectException("NOT_FOUND_VALUE");
        setKsu2Record(ksu2);

        //setKsu1Record(daoService.findDistributedObjectByRefGUID(Ksu1Record.class, guidKsu1Record));
        //setKsu2Record(daoService.findDistributedObjectByRefGUID(Ksu2Record.class, guidKsu2Record));
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setFund(((JournalItem) distributedObject).getFund());
        setJournal(((JournalItem) distributedObject).getJournal());
        setKsu1Record(((JournalItem) distributedObject).getKsu1Record());
        setKsu2Record(((JournalItem) distributedObject).getKsu2Record());

        setDate(((JournalItem) distributedObject).getDate());
        setNumber(((JournalItem) distributedObject).getNumber());
        setCost(((JournalItem) distributedObject).getCost());
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
        return "JournalItem{" +
                "journal=" + journal +
                ", fund=" + fund +
                ", ksu1Record=" + ksu1Record +
                ", ksu2Record=" + ksu2Record +
                ", date=" + date +
                ", number='" + number + '\'' +
                ", cost=" + cost +
                '}';
    }
}
