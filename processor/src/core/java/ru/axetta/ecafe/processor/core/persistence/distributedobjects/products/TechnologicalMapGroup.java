/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ConfigurationProviderDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 09.07.12
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
public class TechnologicalMapGroup extends ConfigurationProviderDistributedObject {

    private String nameOfGroup;
    private Set<TechnologicalMap> technologicalMapInternal;

    @Override
    public void createProjections(Criteria criteria) {

        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);
        projectionList.add(Projections.property("nameOfGroup"), "nameOfGroup");

        criteria.setProjection(projectionList);
    }

    @Override
    protected void beforeProcess(Session session, Long idOfOrg) throws DistributedObjectException {}

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "Name", nameOfGroup);
    }

    @Override
    protected TechnologicalMapGroup parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null){
            setOrgOwner(longOrgOwner);
        } else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        String stringNameOfGroup = XMLUtils.getStringAttributeValue(node, "Name", 128);
        if (stringNameOfGroup != null)
            setNameOfGroup(stringNameOfGroup);
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setNameOfGroup(((TechnologicalMapGroup) distributedObject).getNameOfGroup());
        setIdOfConfigurationProvider(((TechnologicalMapGroup) distributedObject).getIdOfConfigurationProvider());
    }

    public String getNameOfGroup() {
        return nameOfGroup;
    }

    public void setNameOfGroup(String nameOfGroup) {
        this.nameOfGroup = nameOfGroup;
    }

    public List<TechnologicalMap> getTechnologicalMaps(){
        return Collections.unmodifiableList(new ArrayList<TechnologicalMap>(getTechnologicalMapInternal()));
    }

    public void addTechnologicalMap(TechnologicalMap technologicalMap){
        technologicalMapInternal.add(technologicalMap);
    }

    public void removeTechnologicalMap(TechnologicalMap technologicalMap){
        technologicalMapInternal.remove(technologicalMap);
    }

    private Set<TechnologicalMap> getTechnologicalMapInternal() {
        return technologicalMapInternal;
    }

    private void setTechnologicalMapInternal(Set<TechnologicalMap> technologicalMapInternal) {
        this.technologicalMapInternal = technologicalMapInternal;
    }

}
