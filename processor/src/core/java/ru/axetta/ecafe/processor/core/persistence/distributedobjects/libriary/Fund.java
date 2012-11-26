/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;

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
    private Boolean stud;

    public Boolean getStud() {
        return stud;
    }

    public void setStud(Boolean stud) {
        this.stud = stud;
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "FundName", fundName);
        setAttribute(element, "Stud", stud);
    }

    @Override
    public Fund parseAttributes(Node node) throws Exception{

        String fundName = getStringAttributeValue(node, "FundName", 128);
        if (fundName != null) {
            setFundName(fundName);
        }

        Boolean bollStud =  getBollAttributeValue(node, "Stud");
        if(bollStud != null){
            setStud(bollStud);
        }

        setSendAll(SendToAssociatedOrgs.DontSend);

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
