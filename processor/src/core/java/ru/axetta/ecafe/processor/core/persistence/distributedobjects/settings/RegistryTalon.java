/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
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

/**
 * Created with IntelliJ IDEA.
 * User: a.anvarov
 */

public class RegistryTalon extends DistributedObject {

    private Date date;
    private long number;

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "Date", date);
        XMLUtils.setAttributeIfNotNull(element, "Number", number);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
    }

    @Override
    protected DistributedObject parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null)
            setOrgOwner(longOrgOwner);
        Date dateDate = XMLUtils.getDateAttributeValue(node, "Date");
        if (dateDate != null)
            setDate(dateDate);
        Long longNumber = XMLUtils.getLongAttributeValue(node, "Number");
        if (longNumber != null) {
            setNumber(longNumber);
        }

        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setNumber(((RegistryTalon) distributedObject).getNumber());
        setDate(((RegistryTalon) distributedObject).getDate());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        return toSelfProcess(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
    }

    @Override
    public void createProjections(Criteria criteria) {
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("guid"), "guid");
        projectionList.add(Projections.property("globalId"), "globalId");
        projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");

        projectionList.add(Projections.property("GlobalVersionOnCreate"), "GlobalVersionOnCreate");
        projectionList.add(Projections.property("CreatedDate"), "CreatedDate");
        projectionList.add(Projections.property("LastUpDate"), "LastUpDate");
        projectionList.add(Projections.property("DeleteDate"), "DeleteDate");
        projectionList.add(Projections.property("Date"), "Date");
        projectionList.add(Projections.property("Number"), "Number");
        criteria.setProjection(projectionList);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }
}
