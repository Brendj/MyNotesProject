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
 * Date: 13.07.12
 * Time: 18:49
 * To change this template use File | Settings | File Templates.
 */
public class Issuable extends LibraryDistributedObject {

    private Long barcode;
    private char type;

    private Instance instance;
    private JournalItem journalItem;
    private String guidInstance;
    private String guidJournalItem;
    private Set<Circulation> circulationInternal;

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException{
        Instance i = DAOUtils.findDistributedObjectByRefGUID(Instance.class, session, guidInstance);
        JournalItem ji = DAOUtils.findDistributedObjectByRefGUID(JournalItem.class, session, guidJournalItem);
        if((i==null && ji==null) || (i!=null && ji!=null)) throw new DistributedObjectException("NOT_FOUND_VALUE");
        if(i!=null) setInstance(i);
        if(ji!=null) setJournalItem(ji);
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion) throws Exception {
        return null;//toSelfProcess(session, idOfOrg, currentMaxVersion);
    }

    @Override
    public void createProjections(Criteria criteria) {
        //criteria.createAlias("journalItem","ji", JoinType.LEFT_OUTER_JOIN);
        //criteria.createAlias("instance", "i", JoinType.LEFT_OUTER_JOIN);
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("guid"), "guid");
        projectionList.add(Projections.property("globalId"), "globalId");
        projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");

        //projectionList.add(Projections.property("barcode"), "barcode");
        //projectionList.add(Projections.property("type"), "type");

        //projectionList.add(Projections.property("i.guid"), "guidInstance");
        //projectionList.add(Projections.property("ji.guid"), "guidJournalItem");

        criteria.setProjection(projectionList);
    }

    @Override
    protected void appendAttributes(Element element) {}

    @Override
    protected Issuable parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null)
            setOrgOwner(longOrgOwner);
        Long longBarCode = XMLUtils.getLongAttributeValue(node, "Barcode");
        if (longBarCode != null)
            setBarcode(longBarCode);
        Character charType = XMLUtils.getCharacterAttributeValue(node, "Type");
        if (charType != null)
            setType(charType);
        guidInstance = XMLUtils.getStringAttributeValue(node, "GuidInstance", 36);
        guidJournalItem = XMLUtils.getStringAttributeValue(node, "GuidJournalItem", 36);
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setBarcode(((Issuable) distributedObject).getBarcode());
        setType(((Issuable) distributedObject).getType());
        setBarcode(((Issuable) distributedObject).getBarcode());
        setType((((Issuable) distributedObject).getType()));
    }

    public Long getBarcode() {
        return barcode;
    }

    public void setBarcode(Long barcode) {
        this.barcode = barcode;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public JournalItem getJournalItem() {
        return journalItem;
    }

    public void setJournalItem(JournalItem journalItem) {
        this.journalItem = journalItem;
    }

    @Override
    public String toString() {
        return String.format("Issuable{barcode=%d, type=%s, instance=%s, journalItem=%s}", barcode, type, instance,
                journalItem);
    }

    public Set<Circulation> getCirculationInternal() {
        return circulationInternal;
    }

    public void setCirculationInternal(Set<Circulation> circulationInternal) {
        this.circulationInternal = circulationInternal;
    }
}
