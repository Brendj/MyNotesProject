/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.Client;
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

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 18.08.12
 * Time: 16:45
 * To change this template use File | Settings | File Templates.
 */
public class LibVisit extends LibraryDistributedObject {

    private Client client;
    private Date date;
    private int source;
    private Long idOfClient;

    @Override
    public void createProjections(Criteria criteria, int currentLimit, String currentLastGuid) {
        //criteria.createAlias("client", "c", JoinType.LEFT_OUTER_JOIN);
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("guid"), "guid");
        projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");

        //projectionList.add(Projections.property("date"), "date");
        //projectionList.add(Projections.property("source"), "source");

        //projectionList.add(Projections.property("c.idOfClient"), "idOfClient");

        criteria.setProjection(projectionList);
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion, int currentLimit, String currentLastGuid) throws DistributedObjectException {
        return null;//toSelfProcess(session, idOfOrg, currentMaxVersion);
    }

    @Override
    protected void appendAttributes(Element element) {}

    @Override
    public LibVisit parseAttributes(Node node) throws Exception {
        idOfClient = XMLUtils.getLongAttributeValue(node, "IdOfClient");
        date = XMLUtils.getDateTimeAttributeValue(node, "date");
        source = XMLUtils.getIntegerAttributeValue(node, "source");
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException{
        if (idOfClient != null) {
            try{
                setClient(DAOUtils.findClient(session, idOfClient));
            } catch (Exception e){
                throw new DistributedObjectException("NOT_FOUND_VALUE");
            }
        }
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setClient(((LibVisit) distributedObject).getClient());
        setDate(((LibVisit) distributedObject).getDate());
        setSource(((LibVisit) distributedObject).getSource());
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return String.format("LibVisit{client=%s, date=%s, source=%d}", client, date, source);
    }
}
