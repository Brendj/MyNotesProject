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

import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 18.08.12
 * Time: 12:49
 * To change this template use File | Settings | File Templates.
 */
public class Journal extends LibraryDistributedObject {

    private Fund fund;
    private Publication publication;
    private boolean isNewspaper;
    private int monthCount;
    private int count;

    private String guidFund;
    private String guidPublication;
    private Set<JournalItem> journalItemInternal;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("fund","f", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("publication", "p", JoinType.LEFT_OUTER_JOIN);
        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("isNewspaper"), "isNewspaper");
        projectionList.add(Projections.property("monthCount"), "monthCount");
        projectionList.add(Projections.property("count"), "count");

        projectionList.add(Projections.property("f.guid"), "guidFund");
        projectionList.add(Projections.property("p.guid"), "guidPublication");

        criteria.setProjection(projectionList);
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion) throws Exception {
        return null;//toSelfProcess(session, idOfOrg, currentMaxVersion);
    }

    @Override
    protected void appendAttributes(Element element) {}

    @Override
    public Journal parseAttributes(Node node) throws Exception {
        guidFund = XMLUtils.getStringAttributeValue(node, "GuidFund", 36);
        guidPublication = XMLUtils.getStringAttributeValue(node, "GuidPublication", 36);
        isNewspaper = XMLUtils.getBooleanAttributeValue(node, "IsNewspaper");
        monthCount = XMLUtils.getIntegerAttributeValue(node, "MonthCount");
        count = XMLUtils.getIntegerAttributeValue(node, "Count");
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        Publication p = DAOUtils.findDistributedObjectByRefGUID(Publication.class, session, guidPublication);
        if (p == null) {
            DistributedObjectException distributedObjectException = new DistributedObjectException("Publication NOT_FOUND_VALUE");
            distributedObjectException.setData(guidPublication);
            throw distributedObjectException;
        }
        setPublication(p);
        Fund f = DAOUtils.findDistributedObjectByRefGUID(Fund.class, session, guidFund);
        if (f != null)
            setFund(f);
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((Journal) distributedObject).getOrgOwner());
        setNewspaper(((Journal) distributedObject).isNewspaper());
        setMonthCount(((Journal) distributedObject).getMonthCount());
        setCount(((Journal) distributedObject).getCount());
        setFund(((Journal) distributedObject).getFund());
        setGuidFund(((Journal) distributedObject).getGuidFund());
        setPublication(((Journal) distributedObject).getPublication());
        setGuidPublication(((Journal) distributedObject).getGuidPublication());
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
        return String.format("Journal{fund=%s, publication=%s, isNewspaper=%s, monthCount=%d, count=%d}", fund, publication, isNewspaper,
                monthCount, count);
    }

    public Set<JournalItem> getJournalItemInternal() {
        return journalItemInternal;
    }

    public void setJournalItemInternal(Set<JournalItem> journalItemInternal) {
        this.journalItemInternal = journalItemInternal;
    }

    public String getGuidFund() {
        return guidFund;
    }

    public void setGuidFund(String guidFund) {
        this.guidFund = guidFund;
    }

    public String getGuidPublication() {
        return guidPublication;
    }

    public void setGuidPublication(String guidPublication) {
        this.guidPublication = guidPublication;
    }
}
