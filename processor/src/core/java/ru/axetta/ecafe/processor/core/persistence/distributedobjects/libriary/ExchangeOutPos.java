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
 * Date: 07.11.14
 * Time: 16:58
 * To change this template use File | Settings | File Templates.
 */
public class ExchangeOutPos extends LibraryDistributedObject {

    private Long orgExchange;
    private String school;
    private String exComment;
    private String status;
    private Integer confirmedCount;
    private Integer requiredCount;
    private Org org;
    private String guidPublication;
    private String guidExchangeOut;
    private Publication publication;
    private ExchangeOut exchangeOut;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("publication","p", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("exchangeOut","eo", JoinType.LEFT_OUTER_JOIN);
        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);
        projectionList.add(Projections.property("orgExchange"), "orgExchange");
        projectionList.add(Projections.property("school"), "school");
        projectionList.add(Projections.property("requiredCount"), "requiredCount");
        projectionList.add(Projections.property("confirmedCount"), "confirmedCount");
        projectionList.add(Projections.property("exComment"), "exComment");
        projectionList.add(Projections.property("status"), "status");
        projectionList.add(Projections.property("p.guid"), "guidPublication");
        projectionList.add(Projections.property("eo.guid"), "guidExchangeOut");
        criteria.setProjection(projectionList);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        Publication pub = DAOUtils.findDistributedObjectByRefGUID(Publication.class, session, getGuidPublication());
        if(pub==null) throw new DistributedObjectException("NOT_FOUND_VALUE Publication");
        setPublication(pub);
        ExchangeOut eo = DAOUtils.findDistributedObjectByRefGUID(ExchangeOut.class, session, getGuidExchangeOut());
        if(eo==null) throw new DistributedObjectException("NOT_FOUND_VALUE ZajOut");
        setExchangeOut(eo);
        try {
            Criteria criteria = session.createCriteria(Org.class);
            criteria.add(Restrictions.eq("idOfOrg", idOfOrg));
            setOrg((Org) criteria.uniqueResult());
            session.clear();
            if (org == null) {
                throw new DistributedObjectException("NOT_FOUND_VALUE Org");
            } else {
                //Здесь сохраняем в таблицу do_confirms записи заявок и позиций, предназначенные для отправки в другие организации
                if (exchangeOut != null) {
                    criteria = session.createCriteria(DOConfirm.class);
                    criteria.add(Restrictions.eq("distributedObjectClassName", "ExchangeIn"));
                    criteria.add(Restrictions.eq("guid", getGuidExchangeOut()));
                    criteria.add(Restrictions.eq("orgOwner", getOrgExchange()));
                    DOConfirm confirm  = (DOConfirm)criteria.uniqueResult();
                    session.clear();
                    if (confirm == null) {
                        confirm = new DOConfirm("ExchangeIn", getGuidExchangeOut(), getOrgExchange());
                        session.saveOrUpdate(confirm);
                    }
                    confirm = new DOConfirm("ExchangeInPos", getGuid(), getOrgExchange());
                    session.save(confirm);
                }
            }
        } catch (Exception e) {
            throw new DistributedObjectException(e.getMessage());
        }
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        return toSelfProcess(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "GuidPublication", getGuidPublication());
        XMLUtils.setAttributeIfNotNull(element, "GuidZaj", getGuidExchangeOut());
        XMLUtils.setAttributeIfNotNull(element, "OrgExchange", orgExchange);
        XMLUtils.setAttributeIfNotNull(element, "School", school);
        XMLUtils.setAttributeIfNotNull(element, "RequiredCount", requiredCount);
        XMLUtils.setAttributeIfNotNull(element, "ConfirmedCount", confirmedCount);
        XMLUtils.setAttributeIfNotNull(element, "Status", status);
        XMLUtils.setAttributeIfNotNull(element, "Comment", exComment);
    }

    @Override
    public ExchangeOutPos parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null)
            setOrgOwner(longOrgOwner);
        setGuidExchangeOut(XMLUtils.getStringAttributeValue(node, "GuidZaj", 36));
        setGuidPublication(XMLUtils.getStringAttributeValue(node, "GuidPublication", 36));
        setOrgExchange(XMLUtils.getLongAttributeValue(node, "OrgExchange"));
        setSchool(XMLUtils.getStringAttributeValue(node, "School", 255));
        setRequiredCount(XMLUtils.getIntegerAttributeValue(node, "RequiredCount"));
        setConfirmedCount(XMLUtils.getIntegerAttributeValue(node, "ConfirmedCount"));
        setExComment(XMLUtils.getStringAttributeValue(node, "Comment", 255));
        setStatus(XMLUtils.getStringAttributeValue(node, "Status", 30));
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setOrgExchange(((ExchangeOutPos) distributedObject).getOrgExchange());
        setSchool(((ExchangeOutPos) distributedObject).getSchool());
        setRequiredCount(((ExchangeOutPos) distributedObject).getRequiredCount());
        setConfirmedCount(((ExchangeOutPos) distributedObject).getConfirmedCount());
        setExComment(((ExchangeOut) distributedObject).getCommentIn());
    }

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public ExchangeOut getExchangeOut() {
        return exchangeOut;
    }

    public void setExchangeOut(ExchangeOut exchangeOut) {
        this.exchangeOut = exchangeOut;
    }

    public Long getOrgExchange() {
        return orgExchange;
    }

    public void setOrgExchange(Long idOfOrg) {
        this.orgExchange = idOfOrg;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public Integer getRequiredCount() {
        return requiredCount;
    }

    public void setRequiredCount(Integer requiredCount) {
        this.requiredCount = requiredCount;
    }

    public Integer getConfirmedCount() {
        return confirmedCount;
    }

    public void setConfirmedCount(Integer confirmedCount) {
        this.confirmedCount = confirmedCount;
    }

    public String getExComment() {
        return exComment;
    }

    public void setExComment(String exComment) {
        this.exComment = exComment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public String getGuidPublication() {
        return guidPublication;
    }

    public void setGuidPublication(String guidPublication) {
        this.guidPublication = guidPublication;
    }

    public String getGuidExchangeOut() {
        return guidExchangeOut;
    }

    public void setGuidExchangeOut(String guidExchangeOut) {
        this.guidExchangeOut = guidExchangeOut;
    }
}
