/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 18.08.12
 * Time: 11:38
 * To change this template use File | Settings | File Templates.
 */
public class Fund extends DistributedObject {

    private String fundName;

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "Guid", guid);
    }

    @Override
    public Fund parseAttributes(Node node) {

        String fundName = getStringAttributeValue(node, "fundName", 128);
        if (fundName != null) {
            setFundName(fundName);
        }
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setFundName(((Fund) distributedObject).getFundName());
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    @Override
    public String toString() {
        return "Fund{" +
                "fundName='" + fundName + '\'' +
                '}';
    }
}
