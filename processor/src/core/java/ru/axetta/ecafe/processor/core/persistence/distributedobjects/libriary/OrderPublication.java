/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
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
import org.hibernate.sql.JoinType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sasha
 * Date: 03.03.15
 * Time: 17:33
 * To change this template use File | Settings | File Templates.
 */
public class OrderPublication extends LibraryDistributedObject{

    private String guidPublication;
    private Publication publication;
    private Client client;
    private Long idOfClient;
    private String status;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("publication","p", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("client","c", JoinType.LEFT_OUTER_JOIN);
        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);
        projectionList.add(Projections.property("p.guid"), "guidPublication");
        projectionList.add(Projections.property("c.idOfClient"), "idOfClient");
        projectionList.add(Projections.property("status"), "status");
        criteria.setProjection(projectionList);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        Publication pub = DAOUtils.findDistributedObjectByRefGUID(Publication.class, session, getGuidPublication());
        if(pub==null) throw new DistributedObjectException("NOT_FOUND_VALUE Publication");
        setPublication(pub);

        if (getIdOfClient() == null) {
            throw new DistributedObjectException("Order_Publication NOT_FOUND_VALUE Client=null");
        } else {
            Client currentClient = null;
            try{
                currentClient = DAOUtils.findClient(session, getIdOfClient());
            } catch (Exception e){
                throw new DistributedObjectException("Order_Publication NOT_FOUND_VALUE Client GUID:\"" + currentClient.getClientGUID() +"\"");
            }
            if(currentClient==null){
                throw new DistributedObjectException("Order_Publication NOT_FOUND_VALUE Client GUID:\"" + currentClient.getClientGUID() +"");
            }
            setClient(currentClient);
        }
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        return toSelfProcess(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "GuidPublication", getGuidPublication());
        XMLUtils.setAttributeIfNotNull(element, "IdOfClient", getIdOfClient());
        XMLUtils.setAttributeIfNotNull(element, "OrderDate", new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(getCreatedDate()));
    }

    @Override
    public OrderPublication parseAttributes(Node node) throws Exception {
        setGuidPublication(XMLUtils.getStringAttributeValue(node, "GuidPublication", 36));
        setIdOfClient(XMLUtils.getLongAttributeValue(node, "IdOfClient"));
        setStatus(XMLUtils.getStringAttributeValue(node, "Status", 255));
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setIdOfClient(((OrderPublication) distributedObject).getIdOfClient());
        setGuidPublication(((OrderPublication) distributedObject).getGuidPublication());
        setStatus(((OrderPublication) distributedObject).getStatus());
    }

    public String getGuidPublication() {
        return guidPublication;
    }

    public void setGuidPublication(String guidPublication) {
        this.guidPublication = guidPublication;
    }

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
