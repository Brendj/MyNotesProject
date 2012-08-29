/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 18.08.12
 * Time: 12:08
 * To change this template use File | Settings | File Templates.
 */
public class RetirementReason extends DistributedObject {

    private String retirementReasonName;

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "Guid", guid);
    }

    @Override
    public RetirementReason parseAttributes(Node node) throws Exception{

        String retirementReasonName = getStringAttributeValue(node, "retirementReasonName", 45);
        if (retirementReasonName != null) {
            setRetirementReasonName(retirementReasonName);
        }
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setRetirementReasonName(((RetirementReason) distributedObject).getRetirementReasonName());
    }

    public String getRetirementReasonName() {
        return retirementReasonName;
    }

    public void setRetirementReasonName(String retirementReasonName) {
        this.retirementReasonName = retirementReasonName;
    }

    @Override
    public String toString() {
        return "RetirementReason{" +
                "retirementReasonName='" + retirementReasonName + '\'' +
                '}';
    }
}
