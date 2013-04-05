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

    @Override
    protected void appendAttributes(Element element) {
        //setAttribute(element, "GuidFund", fund.getGuid());
        //setAttribute(element, "GuidPublication", publication.getGuid());
        //setAttribute(element, "IsNewspaper", isNewspaper);
        //setAttribute(element, "MonthCount", monthCount);
        //setAttribute(element, "Count", count);
    }

    @Override
    public Journal parseAttributes(Node node) throws Exception {

        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);

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

        Publication p = (Publication) DAOUtils.findDistributedObjectByRefGUID(session, guidPublication);
        if(p==null){
            DistributedObjectException distributedObjectException =  new DistributedObjectException("Publication NOT_FOUND_VALUE");
            distributedObjectException.setData(guidPublication);
            throw  distributedObjectException;
            //throw new DistributedObjectException("NOT_FOUND_VALUE");
        }
        setPublication(p);

        Fund f = (Fund) DAOUtils.findDistributedObjectByRefGUID(session, guidFund);
        //if(f==null) throw new DistributedObjectException("NOT_FOUND_VALUE");
        if(f!=null) setFund(f);
    }

    @Override
    public void fill(DistributedObject distributedObject) {
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
        return "Journal{" +
                "fund=" + fund +
                ", publication=" + publication +
                ", isNewspaper=" + isNewspaper +
                ", monthCount=" + monthCount +
                ", count=" + count +
                '}';
    }
}
