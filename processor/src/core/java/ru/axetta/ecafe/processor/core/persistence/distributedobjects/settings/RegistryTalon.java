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

    private Date talonDate;
    private Long number;
    private RegistryTalonType talonType;

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "Date", talonDate);
        XMLUtils.setAttributeIfNotNull(element, "Number", number);
        XMLUtils.setAttributeIfNotNull(element, "Type", talonType);
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
            setTalonDate(dateDate);
        Long longNumber = XMLUtils.getLongAttributeValue(node, "Number");
        if (longNumber != null) {
            setNumber(longNumber);
        }
        Integer intType = XMLUtils.getIntegerAttributeValue(node, "Type");
        if(intType != null){
            setTalonType(RegistryTalonType.values()[intType]);
        }

        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setNumber(((RegistryTalon) distributedObject).getNumber());
        setTalonDate(((RegistryTalon) distributedObject).getTalonDate());
        setTalonType(((RegistryTalon) distributedObject).getTalonType());
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
        addDistributedObjectProjectionList(projectionList);
        projectionList.add(Projections.property("talonDate"), "talonDate");
        projectionList.add(Projections.property("number"), "number");
        projectionList.add(Projections.property("talonType"), "talonType");
        criteria.setProjection(projectionList);
    }

    public Date getTalonDate() {
        return talonDate;
    }

    public void setTalonDate(Date talonDate) {
        this.talonDate = talonDate;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public RegistryTalonType getTalonType() {
        return talonType;
    }

    public void setTalonType(RegistryTalonType talonType) {
        this.talonType = talonType;
    }
}
