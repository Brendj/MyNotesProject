/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.LibraryDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 18.08.12
 * Time: 12:23
 * To change this template use File | Settings | File Templates.
 */
public class Instance extends LibraryDistributedObject {

    private Set<Issuable> issuableInternal;
    private Publication publication;
    private Fund fund;
    private InventoryBook inventoryBook;
    private Ksu1Record ksu1Record;
    private String guidPublication;
    private String guidFund;
    private String guidInventaryBook;
    private String guidKsu1Record;

    private String guidKsu2Record;
    private Ksu2Record ksu2Record;
    private boolean inGroup;

    private String invNumber;
    private int cost;


    @Override
    public void createProjections(Criteria criteria) {
        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);
        criteria.setProjection(projectionList);
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion) throws Exception {
        return null; //toSelfProcess(session, idOfOrg, currentMaxVersion);
    }

    @Override
    protected void appendAttributes(Element element) {}

    @Override
    public Instance parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null)
            setOrgOwner(longOrgOwner);
        guidFund = XMLUtils.getStringAttributeValue(node, "GuidFund", 36);
        guidPublication = XMLUtils.getStringAttributeValue(node, "GuidPublication", 36);
        guidInventaryBook = XMLUtils.getStringAttributeValue(node, "GuidInventaryBook", 36);
        guidKsu1Record = XMLUtils.getStringAttributeValue(node, "GuidKsu1Record", 36);
        guidKsu2Record = XMLUtils.getStringAttributeValue(node, "GuidKsu2Record", 36);
        inGroup = XMLUtils.getBooleanAttributeValue(node, "InGroup");
        invNumber = XMLUtils.getStringAttributeValue(node, "InvNumber", 10);
        cost = XMLUtils.getIntegerAttributeValue(node, "Cost");
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        Publication p = DAOUtils.findDistributedObjectByRefGUID(Publication.class, session, guidPublication);
        if(p==null) {
            DistributedObjectException distributedObjectException =  new DistributedObjectException("Publication NOT_FOUND_VALUE");
            distributedObjectException.setData(guidPublication);
            throw  distributedObjectException;
        } else {
            setPublication(p);
        }

        Fund f = DAOUtils.findDistributedObjectByRefGUID(Fund.class, session, guidFund);
        if(f!=null) {
            setFund(f);
        }

        InventoryBook ib = DAOUtils.findDistributedObjectByRefGUID(InventoryBook.class, session, guidInventaryBook);
        if(ib!=null){
            setInventoryBook(ib);
        }

        Ksu1Record ksu1 = DAOUtils.findDistributedObjectByRefGUID(Ksu1Record.class, session, guidKsu1Record);
        if(ksu1!=null){
            setKsu1Record(ksu1);
        }

        Ksu2Record ksu2 = DAOUtils.findDistributedObjectByRefGUID(Ksu2Record.class, session, guidKsu2Record);
        if(ksu2!=null) {
            setKsu2Record(ksu2);
        }
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setFund(((Instance) distributedObject).getFund());
        setGuidFund(((Instance) distributedObject).getGuidFund());
        setPublication(((Instance) distributedObject).getPublication());
        setGuidPublication(((Instance) distributedObject).getGuidPublication());
        setInventoryBook(((Instance) distributedObject).getInventoryBook());
        setGuidInventaryBook(((Instance) distributedObject).getGuidInventaryBook());
        setKsu1Record(((Instance) distributedObject).getKsu1Record());
        setGuidKsu1Record(((Instance) distributedObject).getGuidKsu1Record());
        setKsu2Record(((Instance) distributedObject).getKsu2Record());
        setGuidKsu2Record(((Instance) distributedObject).getGuidKsu2Record());
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

    public Set<Issuable> getIssuableInternal() {
        return issuableInternal;
    }

    public void setIssuableInternal(Set<Issuable> issuableInternal) {
        this.issuableInternal = issuableInternal;
    }


    public String getGuidPublication() {
        return guidPublication;
    }

    public void setGuidPublication(String guidPublication) {
        this.guidPublication = guidPublication;
    }

    public String getGuidFund() {
        return guidFund;
    }

    public void setGuidFund(String guidFund) {
        this.guidFund = guidFund;
    }

    public String getGuidInventaryBook() {
        return guidInventaryBook;
    }

    public void setGuidInventaryBook(String guidInventaryBook) {
        this.guidInventaryBook = guidInventaryBook;
    }

    public String getGuidKsu1Record() {
        return guidKsu1Record;
    }

    public void setGuidKsu1Record(String guidKsu1Record) {
        this.guidKsu1Record = guidKsu1Record;
    }

    public String getGuidKsu2Record() {
        return guidKsu2Record;
    }

    public void setGuidKsu2Record(String guidKsu2Record) {
        this.guidKsu2Record = guidKsu2Record;
    }
}
