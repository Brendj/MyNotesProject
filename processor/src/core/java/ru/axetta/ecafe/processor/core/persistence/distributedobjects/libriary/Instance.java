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
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
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
    private String guidPublication;
    private Fund fund;
    private String guidFund;
    private InventoryBook inventoryBook;
    private String guidInventoryBook;
    private Ksu1Record ksu1Record;
    private String guidKsu1Record;
    private Ksu2Record ksu2Record;
    private String guidKsu2Record;
    private ExchangeOut exchangeOut;
    private String guidOfExchangeOut;
    private ExchangeIn exchangeIn;
    private String guidOfExchangeIn;
    private boolean inGroup;
    private String invNumber;
    private int cost;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("publication","p", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("fund","f", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("inventoryBook","ib", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("ksu1Record","k1r", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("ksu2Record","k2r", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("exchangeIn","exchangeI", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("exchangeOut","exchangeO", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("inGroup"), "inGroup");
        projectionList.add(Projections.property("invNumber"), "invNumber");
        projectionList.add(Projections.property("cost"), "cost");

        projectionList.add(Projections.property("p.guid"), "guidPublication");
        projectionList.add(Projections.property("f.guid"), "guidFund");
        projectionList.add(Projections.property("ib.guid"), "guidInventoryBook");
        projectionList.add(Projections.property("k1r.guid"), "guidKsu1Record");
        projectionList.add(Projections.property("k2r.guid"), "guidKsu2Record");
        projectionList.add(Projections.property("exchangeI.guid"), "guidOfExchangeIn");
        projectionList.add(Projections.property("exchangeO.guid"), "guidOfExchangeOut");

        criteria.setProjection(projectionList);
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        return null; //toSelfProcess(session, idOfOrg, currentMaxVersion);
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "GuidPublication", getGuidPublication());
        //XMLUtils.setAttributeIfNotNull(element, "GuidFund", getGuidFund());
        //XMLUtils.setAttributeIfNotNull(element, "GuidInventaryBook", getGuidInventoryBook());
        //XMLUtils.setAttributeIfNotNull(element, "GuidKSU1Record", getGuidKsu1Record());
        //XMLUtils.setAttributeIfNotNull(element, "GuidKSU2Record", getGuidKsu2Record());
        //XMLUtils.setAttributeIfNotNull(element, "GuidZajOut", getGuidOfExchangeOut());
        XMLUtils.setAttributeIfNotNull(element, "GuidZaj", getGuidOfExchangeIn());
        //XMLUtils.setAttributeIfNotNull(element, "InGroup", isInGroup());
        //XMLUtils.setAttributeIfNotNull(element, "InvNumber", getInvNumber());
        XMLUtils.setAttributeIfNotNull(element, "Cost", getCost());
    }

    @Override
    public Instance parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null)
            setOrgOwner(longOrgOwner);
        setGuidFund(XMLUtils.getStringAttributeValue(node, "GuidFund", 36));
        setGuidPublication(XMLUtils.getStringAttributeValue(node, "GuidPublication", 36));
        setGuidInventoryBook(XMLUtils.getStringAttributeValue(node, "GuidInventaryBook", 36));
        setGuidKsu1Record(XMLUtils.getStringAttributeValue(node, "GuidKsu1Record", 36));
        setGuidKsu2Record(XMLUtils.getStringAttributeValue(node, "GuidKsu2Record", 36));
        setInGroup(XMLUtils.getBooleanAttributeValue(node, "InGroup"));
        setInvNumber(XMLUtils.getStringAttributeValue(node, "InvNumber", 10));
        setCost(XMLUtils.getIntegerAttributeValue(node, "Cost"));
        setGuidOfExchangeIn(XMLUtils.getStringAttributeValue(node, "GuidZajIn", 36));
        setGuidOfExchangeOut(XMLUtils.getStringAttributeValue(node, "GuidZajOut", 36));
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        InventoryBook ib = DAOUtils.findDistributedObjectByRefGUID(InventoryBook.class, session, getGuidInventoryBook());
        if(ib!=null){
            setInventoryBook(ib);
        }

        if(ib!=null && getInventoryBook()!=null  && !getTagName().equalsIgnoreCase("m")) {
            boolean deleteFlag = false;
            Criteria criteria = session.createCriteria(Instance.class);
            criteria.add(Restrictions.eq("inventoryBook", ib));
            criteria.add(Restrictions.eq("invNumber", getInvNumber()));
            criteria.add(Restrictions.eq("deletedState", deleteFlag));
            Instance instance = (Instance) criteria.uniqueResult();
            session.clear();
            if(instance!=null){
                DistributedObjectException distributedObjectException =  new DistributedObjectException("Instance DATA_EXIST_VALUE inventoryBook and invNumber equals");
                distributedObjectException.setData(instance.getGuid());
                throw  distributedObjectException;
            }
        }


        if ((getGuidPublication() != null) && !getGuidPublication().equals("")) {
            Publication p = DAOUtils.findDistributedObjectByRefGUID(Publication.class, session, getGuidPublication());
            if(p==null) {
                DistributedObjectException distributedObjectException =  new DistributedObjectException("Instance NOT_FOUND_VALUE Publication \"" + getGuidPublication() + "\"");
                distributedObjectException.setData(getGuidPublication());
                throw  distributedObjectException;
            } else {
                setPublication(p);
            }
        }

        if ((getGuidFund() != null) && !getGuidFund().equals("")) {
            Fund f = DAOUtils.findDistributedObjectByRefGUID(Fund.class, session, getGuidFund());
            if(f!=null) {
                setFund(f);
            }
        }

        if ((getGuidKsu1Record() != null) && !getGuidKsu1Record().equals("")) {
            Ksu1Record ksu1 = DAOUtils.findDistributedObjectByRefGUID(Ksu1Record.class, session, getGuidKsu1Record());
            if (ksu1 != null) {
                setKsu1Record(ksu1);
            }
        }

        if ((getGuidKsu2Record() != null) && !getGuidKsu2Record().equals("")) {
            Ksu2Record ksu2 = DAOUtils.findDistributedObjectByRefGUID(Ksu2Record.class, session, getGuidKsu2Record());
            if (ksu2 != null) {
                setKsu2Record(ksu2);
            }
        }

        if ((getGuidOfExchangeOut() != null) && !getGuidOfExchangeOut().equals("")) {
            ExchangeOut exchangeOut = DAOUtils.findDistributedObjectByRefGUID(ExchangeOut.class, session, getGuidOfExchangeOut());
            if (exchangeOut == null) {
                DistributedObjectException distributedObjectException = new DistributedObjectException("Instance NOT_FOUND_VALUE ExchangeOut (Outcome) \"" + getGuidOfExchangeOut() + "\"");
                distributedObjectException.setData(getGuidOfExchangeOut());
                throw distributedObjectException;
            } else {
                setExchangeOut(exchangeOut);
            }
        }

        if ((getGuidOfExchangeIn() != null) && !getGuidOfExchangeIn().equals("")) {
            ExchangeIn exchangeIn = DAOUtils.findDistributedObjectByRefGUID(ExchangeIn.class, session, getGuidOfExchangeIn());
            if (exchangeIn == null) {
                DistributedObjectException distributedObjectException = new DistributedObjectException("ExchangeOut (Income) NOT_FOUND_VALUE");
                distributedObjectException.setData(getGuidOfExchangeIn());
                throw distributedObjectException;
            } else {
                setExchangeIn(exchangeIn);
            }
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
        setGuidInventoryBook(((Instance) distributedObject).getGuidInventoryBook());
        setKsu1Record(((Instance) distributedObject).getKsu1Record());
        setGuidKsu1Record(((Instance) distributedObject).getGuidKsu1Record());
        setKsu2Record(((Instance) distributedObject).getKsu2Record());
        setGuidKsu2Record(((Instance) distributedObject).getGuidKsu2Record());
        setInGroup(((Instance) distributedObject).isInGroup());
        setInvNumber(((Instance)distributedObject).getInvNumber());
        setCost(((Instance) distributedObject).getCost());
        setExchangeOut(((Instance) distributedObject).getExchangeOut());
        setGuidOfExchangeOut(((Instance) distributedObject).getGuidOfExchangeOut());
        setExchangeIn(((Instance) distributedObject).getExchangeIn());
        setGuidOfExchangeIn(((Instance) distributedObject).getGuidOfExchangeIn());
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

    public String getGuidInventoryBook() {
        return guidInventoryBook;
    }

    public void setGuidInventoryBook(String guidInventoryBook) {
        this.guidInventoryBook = guidInventoryBook;
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

    public ExchangeOut getExchangeOut() {
        return exchangeOut;
    }

    public void setExchangeOut(ExchangeOut exchangeOut) {
        this.exchangeOut = exchangeOut;
    }

    public String getGuidOfExchangeOut() {
        return this.guidOfExchangeOut;
    }

    public void setGuidOfExchangeOut(String guidOfExchangeOut) {
        this.guidOfExchangeOut = guidOfExchangeOut;
    }

    public ExchangeIn getExchangeIn() {
        return exchangeIn;
    }

    public void setExchangeIn(ExchangeIn exchangeIn) {
        this.exchangeIn = exchangeIn;
    }

    public String getGuidOfExchangeIn() {
        return this.guidOfExchangeIn;
    }

    public void setGuidOfExchangeIn(String guidOfExchangeIn) {
        this.guidOfExchangeIn = guidOfExchangeIn;
    }
}
