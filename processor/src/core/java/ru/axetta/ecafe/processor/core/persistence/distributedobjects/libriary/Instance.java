/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Set;

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
    private Set<Issuable> issuableInternal;

    public Set<Issuable> getIssuableInternal() {
        return issuableInternal;
    }

    public void setIssuableInternal(Set<Issuable> issuableInternal) {
        this.issuableInternal = issuableInternal;
    }

    @Override
    protected void appendAttributes(Element element) {
        //setAttribute(element, "GuidFund", fund.getGuid());
        //setAttribute(element, "GuidPublication", publication.getGuid());
        //setAttribute(element, "GuidInventaryBook", inventoryBook.getGuid());
        //setAttribute(element, "GuidKsu1Record", ksu1Record.getGuid());
        //setAttribute(element, "GuidKsu2Record", ksu2Record.getGuid());
        //setAttribute(element, "InGroup", inGroup);
        //setAttribute(element, "InvNumber", invNumber);
        //setAttribute(element, "Cost", cost);
    }

    @Override
    public Instance parseAttributes(Node node) throws Exception {

        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);

        guidFund = getStringAttributeValue(node, "GuidFund", 36);
        guidPublication = getStringAttributeValue(node, "GuidPublication", 36);
        guidInventaryBook = getStringAttributeValue(node, "GuidInventaryBook", 36);
        guidKsu1Record = getStringAttributeValue(node, "GuidKsu1Record", 36);
        guidKsu2Record = getStringAttributeValue(node, "GuidKsu2Record", 36);

        inGroup = getBollAttributeValue(node, "InGroup");
        invNumber = getStringAttributeValue(node, "InvNumber", 10);
        cost = getIntegerAttributeValue(node, "Cost");

        setSendAll(SendToAssociatedOrgs.Send);
        return this;
    }

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        //DAOService daoService = DAOService.getInstance();
        Publication p = (Publication) DAOUtils.findDistributedObjectByRefGUID(session, guidPublication);
        if(p==null) {
            DistributedObjectException distributedObjectException =  new DistributedObjectException("Publication NOT_FOUND_VALUE");
            distributedObjectException.setData(guidPublication);
            throw  distributedObjectException;
            //throw new DistributedObjectException("NOT_FOUND_VALUE");
        } else {
            setPublication(p);
        }

        Fund f = (Fund) DAOUtils.findDistributedObjectByRefGUID(session, guidFund);
        if(f==null) {
            //throw new DistributedObjectException("NOT_FOUND_VALUE");
        } else {
            setFund(f);
        }

        InventoryBook ib = (InventoryBook) DAOUtils.findDistributedObjectByRefGUID(session, guidInventaryBook);
        if(ib==null){
            //throw new DistributedObjectException("NOT_FOUND_VALUE");
        } else {
            setInventoryBook(ib);
        }

        Ksu1Record ksu1 = (Ksu1Record) DAOUtils.findDistributedObjectByRefGUID(session, guidKsu1Record);
        if(ksu1==null){
            //throw new DistributedObjectException("NOT_FOUND_VALUE");
        } else {
            setKsu1Record(ksu1);
        }

        Ksu2Record ksu2 = (Ksu2Record) DAOUtils.findDistributedObjectByRefGUID(session, guidKsu2Record);
        if(ksu2==null) {
            //throw new DistributedObjectException("NOT_FOUND_VALUE");
        } else {
            setKsu2Record(ksu2);
        }
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
