/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 18.08.12
 * Time: 12:49
 * To change this template use File | Settings | File Templates.
 */
public class Journal extends DistributedObject {

    private Fund fund;
    private Publication publication;
    private boolean isNewspaper;
    private int monthCount;
    private int count;

    private String guidFund;
    private String guidPublication;

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "Guid", guid);
    }

    @Override
    public Journal parseAttributes(Node node) throws Exception {

        guidFund = getStringAttributeValue(node, "guidFund", 1024);
        guidPublication = getStringAttributeValue(node, "guidPublication", 1024);

        isNewspaper = (getIntegerAttributeValue(node, "isNewspaper") == 1);
        monthCount = getIntegerAttributeValue(node, "monthCount");
        count = getIntegerAttributeValue(node, "count");
        return this;
    }

    @Override
    public void preProcess() {
        DAOService daoService = DAOService.getInstance();
        setPublication(daoService.findDistributedObjectByRefGUID(Publication.class, guidPublication));
        setFund(daoService.findDistributedObjectByRefGUID(Fund.class, guidFund));
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setFund(((Journal) distributedObject).getFund());
        setPublication(((Journal) distributedObject).getPublication());

        setNewspaper(((Journal) distributedObject).isNewspaper());
        setMonthCount(((Journal) distributedObject).getMonthCount());
        setCount(((Journal) distributedObject).getCount());
    }

    public Fund getFund() {
        return fund;
    }

    public void setFund(Fund fund) {
        this.fund = fund;
    }

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public boolean isNewspaper() {
        return isNewspaper;
    }

    public void setNewspaper(boolean newspaper) {
        isNewspaper = newspaper;
    }

    public int getMonthCount() {
        return monthCount;
    }

    public void setMonthCount(int monthCount) {
        this.monthCount = monthCount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Journal{" +
                "fund=" + fund +
                ", publication=" + publication +
                ", isNewspaper=" + isNewspaper +
                ", monthCount=" + monthCount +
                ", count=" + count +
                '}';
    }
}
