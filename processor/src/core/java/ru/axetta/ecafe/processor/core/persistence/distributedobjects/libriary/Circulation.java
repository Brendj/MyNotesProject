/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 10.07.12
 * Time: 21:07
 * To change this template use File | Settings | File Templates.
 */
public class Circulation extends DistributedObject {

    public final static int ISSUED = 0, EXTENDED = 1, LOST = 2, REFUNDED = 3;

    @Override
    protected void appendAttributes(Element element) {
    }

    @Override
    protected Circulation parseAttributes(Node node) throws Exception {
        //guidClient = XMLUtils.getStringAttributeValue(node, "GuidClient", 36);
        idOfClient = XMLUtils.getLongAttributeValue(node, "IdOfClient");
        guidParentCirculation = XMLUtils.getStringAttributeValue(node, "GuidParentCirculation", 36);
        guidIssuable = XMLUtils.getStringAttributeValue(node, "GuidIssuable", 36);
        issuanceDate = XMLUtils.getDateTimeAttributeValue(node, "IssuanceDate");
        refundDate = XMLUtils.getDateAttributeValue(node, "RefundDate");
        realRefundDate = XMLUtils.getDateTimeAttributeValue(node, "RealRefundDate");
        status = XMLUtils.getIntegerAttributeValue(node, "Status");
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    @Override
    public void preProcess(Session session) throws DistributedObjectException{
        Issuable iss = DAOUtils.findDistributedObjectByRefGUID(Issuable.class, session, guidIssuable);
        if(iss==null) throw new DistributedObjectException("Issuable NOT_FOUND_VALUE");
        setIssuable(iss);

        Circulation parentCirculation = DAOUtils.findDistributedObjectByRefGUID(Circulation.class, session, guidParentCirculation);
        if(parentCirculation!=null) setParentCirculation(parentCirculation);

        //Client cl = DAOUtils.findClientByRefGUID(session, guidClient);
        Client cl = null;
        try {
            cl = DAOUtils.findClient(session, idOfClient);
        } catch (Exception e) {
            throw new DistributedObjectException(e.getMessage());
        }
        if(cl==null) throw new DistributedObjectException("Client NOT_FOUND_VALUE");
        setClient(cl);

    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setIssuanceDate(((Circulation) distributedObject).getIssuanceDate());
        setRefundDate(((Circulation) distributedObject).getRefundDate());
        setRealRefundDate(((Circulation) distributedObject).getRealRefundDate());
        setStatus(((Circulation) distributedObject).getStatus());
        setParentCirculation(((Circulation) distributedObject).getParentCirculation());
        setIssuable(((Circulation) distributedObject).getIssuable());
        setClient(((Circulation) distributedObject).getClient());
    }

    private Circulation parentCirculation;
    private Issuable issuable;
    private Date issuanceDate;
    private Date refundDate;
    private Date realRefundDate;
    private int status;

    //private String guidClient;
    private Long idOfClient;

    private String guidParentCirculation;
    private String guidIssuable;
    private Client client;
    private Set<Circulation> circulationInternal;

    public Set<Circulation> getCirculationInternal() {
        return circulationInternal;
    }

    public void setCirculationInternal(Set<Circulation> circulationInternal) {
        this.circulationInternal = circulationInternal;
    }

    public Circulation getParentCirculation() {
        return parentCirculation;
    }

    public void setParentCirculation(Circulation parentCirculation) {
        this.parentCirculation = parentCirculation;
    }

    public Issuable getIssuable() {
        return issuable;
    }

    public void setIssuable(Issuable issuable) {
        this.issuable = issuable;
    }

    public Date getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(Date issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public Date getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(Date refundDate) {
        this.refundDate = refundDate;
    }

    public Date getRealRefundDate() {
        return realRefundDate;
    }

    public void setRealRefundDate(Date realRefundDate) {
        this.realRefundDate = realRefundDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
