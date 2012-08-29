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
 * Time: 12:23
 * To change this template use File | Settings | File Templates.
 */
public class Instance extends DistributedObject {

    private Publication publication;
    private boolean inGroup;
    private Fund fund;
    private String invNumber;
    private InventoryBook inventoryBook;
    private Ksu1Record ksu1Record;
    private Ksu2Record ksu2Record;
    private int cost;

    private String guidPublication;
    private String guidFund;
    private String guidInventaryBook;
    private String guidKsu1Record;
    private String guidKsu2Record;

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "Guid", guid);
    }

    @Override
    public Instance parseAttributes(Node node) throws Exception {

        guidFund = getStringAttributeValue(node, "guidFund", 1024);
        guidPublication = getStringAttributeValue(node, "guidPublication", 1024);
        guidInventaryBook = getStringAttributeValue(node, "guidInventaryBook", 1024);
        guidKsu1Record = getStringAttributeValue(node, "guidKsu1Record", 1024);
        guidKsu2Record = getStringAttributeValue(node, "guidKsu2Record", 1024);

        inGroup = (getIntegerAttributeValue(node, "inGroup") == 1);
        invNumber = getStringAttributeValue(node, "invNumber", 10);
        cost = getIntegerAttributeValue(node, "cost");
        return this;
    }

    @Override
    public void preProcess() {
        DAOService daoService = DAOService.getInstance();
        setPublication(daoService.findDistributedObjectByRefGUID(Publication.class, guidPublication));
        setFund(daoService.findDistributedObjectByRefGUID(Fund.class, guidFund));
        setInventoryBook(daoService.findDistributedObjectByRefGUID(InventoryBook.class, guidInventaryBook));
        setKsu1Record(daoService.findDistributedObjectByRefGUID(Ksu1Record.class, guidKsu1Record));
        setKsu2Record(daoService.findDistributedObjectByRefGUID(Ksu2Record.class, guidKsu2Record));
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setFund(((Instance) distributedObject).getFund());
        setPublication(((Instance) distributedObject).getPublication());
        setInventoryBook(((Instance) distributedObject).getInventoryBook());
        setKsu1Record(((Instance) distributedObject).getKsu1Record());
        setKsu2Record(((Instance) distributedObject).getKsu2Record());

        setInGroup(((Instance) distributedObject).isInGroup());
        setInvNumber(((Instance)distributedObject).getInvNumber());
        setCost(((Instance) distributedObject).getCost());
    }

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public boolean isInGroup() {
        return inGroup;
    }

    public void setInGroup(boolean inGroup) {
        this.inGroup = inGroup;
    }

    public Fund getFund() {
        return fund;
    }

    public void setFund(Fund fund) {
        this.fund = fund;
    }

    public String getInvNumber() {
        return invNumber;
    }

    public void setInvNumber(String invNumber) {
        this.invNumber = invNumber;
    }

    public InventoryBook getInventoryBook() {
        return inventoryBook;
    }

    public void setInventoryBook(InventoryBook inventoryBook) {
        this.inventoryBook = inventoryBook;
    }

    public Ksu1Record getKsu1Record() {
        return ksu1Record;
    }

    public void setKsu1Record(Ksu1Record ksu1Record) {
        this.ksu1Record = ksu1Record;
    }

    public Ksu2Record getKsu2Record() {
        return ksu2Record;
    }

    public void setKsu2Record(Ksu2Record ksu2Record) {
        this.ksu2Record = ksu2Record;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Instance{" +
                "publication=" + publication +
                ", inGroup=" + inGroup +
                ", fund=" + fund +
                ", invNumber='" + invNumber + '\'' +
                ", inventoryBook=" + inventoryBook +
                ", ksu1Record=" + ksu1Record +
                ", ksu2Record=" + ksu2Record +
                ", cost=" + cost +
                '}';
    }
}
