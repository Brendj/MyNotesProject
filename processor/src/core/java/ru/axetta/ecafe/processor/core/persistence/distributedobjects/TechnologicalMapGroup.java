/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

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
public class TechnologicalMapGroup extends DistributedObject implements IConfigProvider {

    private String nameOfGroup;
    private Set<TechnologicalMap> technologicalMapInternal;
    private Long idOfConfigurationProvider;

    public Long getIdOfConfigurationProvider() {
        return idOfConfigurationProvider;
    }

    public void setIdOfConfigurationProvider(Long idOfConfigurationProvider) {
        this.idOfConfigurationProvider = idOfConfigurationProvider;
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element,"Name", nameOfGroup);
    }

    @Override
    protected TechnologicalMapGroup parseAttributes(Node node) {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        String stringNameOfGroup = getStringAttributeValue(node,"Name",128);
        if(stringNameOfGroup!=null) setNameOfGroup(stringNameOfGroup);

        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((StateChange) distributedObject).getOrgOwner());
        setNameOfGroup(((TechnologicalMapGroup) distributedObject).getNameOfGroup());
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
