/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOConfirm;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.LibraryDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sasha
 * Date: 14.11.14
 * Time: 12:55
 * To change this template use File | Settings | File Templates.
 */
public class ExchangeInPos extends LibraryDistributedObject {

    private Integer confirmedCount;
    private Integer requiredCount;
    private Org org;
    private String guidPublication;
    private String guidExchangeIn;
    private Publication publication;
    private ExchangeIn exchangeIn;
    private String status;
    private String comment;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("publication","p", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("exchangeIn","eo", JoinType.LEFT_OUTER_JOIN);
        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);
        projectionList.add(Projections.property("confirmedCount"), "confirmedCount");
        projectionList.add(Projections.property("requiredCount"), "requiredCount");
        projectionList.add(Projections.property("p.guid"), "guidPublication");
        projectionList.add(Projections.property("eo.guid"), "guidExchangeIn");
        criteria.setProjection(projectionList);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        Publication pub = DAOUtils.findDistributedObjectByRefGUID(Publication.class, session, getGuidPublication());
        if(pub==null) throw new DistributedObjectException("NOT_FOUND_VALUE Publication");
        setPublication(pub);

        ExchangeIn eo = DAOUtils.findDistributedObjectByRefGUID(ExchangeIn.class, session, getGuidExchangeIn());
        if(eo==null) throw new DistributedObjectException("NOT_FOUND_VALUE ZajIn");
        setExchangeIn(eo);

        try {
            Query query = session.createQuery("select orgOwner from ExchangeInPos where guid = :guid");
            query.setParameter("guid", getGuid());
            Long idOrgToWrite = (Long)query.uniqueResult();
            session.clear();

            Criteria criteria = session.createCriteria(Org.class);
            criteria.add(Restrictions.eq("idOfOrg", idOrgToWrite));
            setOrg((Org) criteria.uniqueResult());
            session.clear();
            if (org == null) {
                throw new DistributedObjectException("NOT_FOUND_VALUE Org");
            }
            else {
                //Здесь сохраняем в таблицу do_confirms записи заявок и позиций, предназначенные дял отправки в другие организации
                if (getExchangeIn() != null) {
                    setStatus(exchangeIn.getStatus());
                    setComment(exchangeIn.getCommentOut());
                    if (!doExists(session, "ExchangeOut", getGuidExchangeIn(), idOrgToWrite)) {
                        DOConfirm confirm = new DOConfirm("ExchangeOut", getGuidExchangeIn(), idOrgToWrite);
                        session.save(confirm);
                    }
                    if (!doExists(session, "ExchangeOutPos", getGuid(), idOrgToWrite)) {
                        DOConfirm confirm = new DOConfirm("ExchangeOutPos", getGuid(), idOrgToWrite);
                        session.save(confirm);
                    }
                }
            }
        }
        catch (Exception e) {
            throw new DistributedObjectException(e.getMessage());
        }
    }

    private boolean doExists(Session session, String className, String guid, Long id) {
        boolean res;
        final String str = "select guid from DOConfirm where distributedObjectClassName = :className and guid = :guid and orgOwner = :id";
        Query query = session.createQuery(str);
        query.setParameter("className", className);
        query.setParameter("guid", guid);
        query.setParameter("id", id);
        if (query.uniqueResult() == null) {
             res = false;
        }
        else {
            res = true;
        }
        session.clear();
        return res;
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        return null;
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "GuidPublication", getGuidPublication());
        XMLUtils.setAttributeIfNotNull(element, "GuidZaj", getGuidExchangeIn());
        XMLUtils.setAttributeIfNotNull(element, "ConfirmedCount", confirmedCount);
        XMLUtils.setAttributeIfNotNull(element, "RequiredCount", requiredCount);
        XMLUtils.setAttributeIfNotNull(element, "Status", status);
        XMLUtils.setAttributeIfNotNull(element, "Comment", comment);
    }

    @Override
    public ExchangeInPos parseAttributes(Node node) throws Exception {
        setGuidExchangeIn(XMLUtils.getStringAttributeValue(node, "GuidZaj", 36));
        setGuidPublication(XMLUtils.getStringAttributeValue(node, "GuidPublication", 36));
        setConfirmedCount(XMLUtils.getIntegerAttributeValue(node, "ConfirmedCount"));
        setRequiredCount(XMLUtils.getIntegerAttributeValue(node, "RequiredCount"));
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        //setOrgOwner(distributedObject.getOrgOwner());
        setConfirmedCount(((ExchangeInPos) distributedObject).getConfirmedCount());
        setRequiredCount(((ExchangeInPos) distributedObject).getRequiredCount());
        setGuidPublication(((ExchangeInPos) distributedObject).getGuidPublication());
        setGuidExchangeIn(((ExchangeInPos) distributedObject).getGuidExchangeIn());
    }

    public Integer getConfirmedCount() {
        return confirmedCount;
    }

    public void setConfirmedCount(Integer confirmedCount) {
        this.confirmedCount = confirmedCount;
    }

    public Integer getRequiredCount() {
        return requiredCount;
    }

    public void setRequiredCount(Integer requiredCount) {
        this.requiredCount = requiredCount;
    }

    public String getGuidPublication() {
        return guidPublication;
    }

    public void setGuidPublication(String guidPublication) {
        this.guidPublication = guidPublication;
    }

    public String getGuidExchangeIn() {
        return guidExchangeIn;
    }

    public void setGuidExchangeIn(String guidExchangeIn) {
        this.guidExchangeIn = guidExchangeIn;
    }

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public ExchangeIn getExchangeIn() {
        return exchangeIn;
    }

    public void setExchangeIn(ExchangeIn exchangeIn) {
        this.exchangeIn = exchangeIn;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
