/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
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
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sasha
 * Date: 07.11.14
 * Time: 14:07
 * To change this template use File | Settings | File Templates.
 */
public class ExchangeOut extends LibraryDistributedObject {

    private String caption;
    private Date incomeDate;
    private String commentIn;
    private String school;

    @Override
    public void createProjections(Criteria criteria) {
        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("caption"), "caption");
        projectionList.add(Projections.property("incomeDate"), "incomeDate");
        projectionList.add(Projections.property("commentIn"), "commentIn");
        projectionList.add(Projections.property("school"), "school");

        criteria.setProjection(projectionList);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        // Проверка на дублирование данных
        Criteria criteria = session.createCriteria(ExchangeOut.class);
        criteria.add(Restrictions.eq("incomeDate", getIncomeDate()));
        criteria.add(Restrictions.eq("caption", getCaption()));
        List eoList = criteria.list();
        ExchangeOut eo = null;
        if(eoList != null && !eoList.isEmpty()){
            eo = (ExchangeOut) eoList.get(0);
        }
        session.clear();
        if(!(eo==null || eo.getDeletedState() || guid.equals(eo.getGuid()))){
            DistributedObjectException distributedObjectException =  new DistributedObjectException("ExchangeOut DATA_EXIST_VALUE Caption and IncomeDate equals");
            distributedObjectException.setData(eo.getGuid());
            throw  distributedObjectException;
        }
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        return toSelfProcess(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "Caption", caption);
        XMLUtils.setAttributeIfNotNull(element, "IncomeDate", new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(incomeDate));
        XMLUtils.setAttributeIfNotNull(element, "Comment", commentIn);
        XMLUtils.setAttributeIfNotNull(element, "School", school);
    }

    @Override
    public ExchangeOut parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null)
            setOrgOwner(longOrgOwner);
        setCaption(XMLUtils.getStringAttributeValue(node, "Caption", 255));
        setIncomeDate(XMLUtils.getDateTimeAttributeValue(node, "IncomeDate"));
        setCommentIn(XMLUtils.getStringAttributeValue(node, "Comment", 255));
        setSchool(XMLUtils.getStringAttributeValue(node, "School", 255));
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setCaption(((ExchangeOut) distributedObject).getCaption());
        setIncomeDate(((ExchangeOut) distributedObject).getIncomeDate());
        setCommentIn(((ExchangeOut) distributedObject).getCommentIn());
        setSchool(((ExchangeOut) distributedObject).getSchool());
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public Date getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(Date incomeDate) {
        this.incomeDate = incomeDate;
    }

    public String getCommentIn() {
        return commentIn;
    }

    public void setCommentIn(String commentIn) {
        this.commentIn = commentIn;
    }
}
