/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 09.07.12
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
public class ProductGroup extends DistributedObject {

    private String nameOfGroup;

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element,"Name", nameOfGroup);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected ProductGroup parseAttributes(Node node) {

        String stringNameOfGroup = getStringAttributeValue(node,"Name",128);
        if(stringNameOfGroup!=null) setNameOfGroup(stringNameOfGroup);

        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setNameOfGroup(((ProductGroup) distributedObject).getNameOfGroup());
    }

    public String getNameOfGroup() {
        return nameOfGroup;
    }

    public void setNameOfGroup(String nameOfGroup) {
        this.nameOfGroup = nameOfGroup;
    }
}
