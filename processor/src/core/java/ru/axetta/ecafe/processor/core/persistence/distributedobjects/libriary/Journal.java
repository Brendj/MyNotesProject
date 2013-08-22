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

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 18.08.12
 * Time: 12:49
 * To change this template use File | Settings | File Templates.
 */
public class Journal extends DistributedObject {

    @Override
    protected void appendAttributes(Element element) {}

    @Override
    public Journal parseAttributes(Node node) throws Exception {

        guidFund = getStringAttributeValue(node, "GuidFund", 36);
        guidPublication = getStringAttributeValue(node, "GuidPublication", 36);

        isNewspaper = (getIntegerAttributeValue(node, "IsNewspaper") == 1);
        monthCount = getIntegerAttributeValue(node, "MonthCount");
        count = getIntegerAttributeValue(node, "Count");
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void preProcess(Session session) throws DistributedObjectException{

        Publication p = DAOUtils.findDistributedObjectByRefGUID(Publication.class, session, guidPublication);
        if(p==null){
            DistributedObjectException distributedObjectException =  new DistributedObjectException("Publication NOT_FOUND_VALUE");
            distributedObjectException.setData(guidPublication);
            throw  distributedObjectException;
        }
        setPublication(p);

        Fund f = DAOUtils.findDistributedObjectByRefGUID(Fund.class, session, guidFund);
        if(f!=null) setFund(f);
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((Journal) distributedObject).getOrgOwner());
        setFund(((Journal) distributedObject).getFund());
        setPublication(((Journal) distributedObject).getPublication());

        setNewspaper(((Journal) distributedObject).isNewspaper());
        setMonthCount(((Journal) distributedObject).getMonthCount());
        setCount(((Journal) distributedObject).getCount());
    }

    public Fund getFund() {
        return fund;
    }

    public void setFund(Fund fund) {
        this.fund = fund;
    }

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public boolean isNewspaper() {
        return isNewspaper;
    }

    public void setNewspaper(boolean newspaper) {
        isNewspaper = newspaper;
    }

    public int getMonthCount() {
        return monthCount;
    }

    public void setMonthCount(int monthCount) {
        this.monthCount = monthCount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return String
                .format("Journal{fund=%s, publication=%s, isNewspaper=%s, monthCount=%d, count=%d}", fund, publication,
                        isNewspaper, monthCount, count);
    }

    private Fund fund;
    private Publication publication;
    private boolean isNewspaper;
    private int monthCount;
    private int count;

    private String guidFund;
    private String guidPublication;
    private Set<JournalItem> journalItemInternal;

    public Set<JournalItem> getJournalItemInternal() {
        return journalItemInternal;
    }

    public void setJournalItemInternal(Set<JournalItem> journalItemInternal) {
        this.journalItemInternal = journalItemInternal;
    }
}
