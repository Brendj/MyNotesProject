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
import org.hibernate.sql.JoinType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 10.07.12
 * Time: 21:07
 * To change this template use File | Settings | File Templates.
 */
public class Circulation extends LibraryDistributedObject {

    public final static int ISSUED = 0, EXTENDED = 1, LOST = 2, REFUNDED = 3;
    private Date issuanceDate;
    private Date refundDate;
    private Date realRefundDate;
    private int status;
    private String guidParentCirculation;
    private Circulation parentCirculation;
    private String guidIssuable;
    private Issuable issuable;
    private Long idOfClient;
    private Client client;
    private String guidExchangeOut;
    private ExchangeOut exchangeOut;
    private Set<Circulation> circulationInternal;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("parentCirculation","pc", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("issuable","i", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("client","c", JoinType.LEFT_OUTER_JOIN);
        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("issuanceDate"), "issuanceDate");
        projectionList.add(Projections.property("refundDate"), "refundDate");
        projectionList.add(Projections.property("realRefundDate"), "realRefundDate");
        projectionList.add(Projections.property("status"), "status");

        projectionList.add(Projections.property("c.idOfClient"), "idOfClient");
        projectionList.add(Projections.property("pc.guid"), "guidParentCirculation");
        projectionList.add(Projections.property("i.guid"), "guidIssuable");

        criteria.setProjection(projectionList);
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        return null;
    }

    @Override
    protected void appendAttributes(Element element) {
    }

    @Override
    protected Circulation parseAttributes(Node node) throws Exception {
        //guidClient = XMLUtils.getStringAttributeValue(node, "idOfClient", 36);
        setIdOfClient(XMLUtils.getLongAttributeValue(node, "IdOfClient"));
        if (getIdOfClient() == null) {
            throw new DistributedObjectException("NOT_FOUND_VALUE Client");
        }
        setGuidParentCirculation(XMLUtils.getStringAttributeValue(node, "GuidParentCirculation", 36));
        setGuidIssuable(XMLUtils.getStringAttributeValue(node, "GuidIssuable", 36));
        setIssuanceDate(XMLUtils.getDateTimeAttributeValue(node, "IssuanceDate"));
        setRefundDate(XMLUtils.getDateAttributeValue(node, "RefundDate"));
        setRealRefundDate(XMLUtils.getDateTimeAttributeValue(node, "RealRefundDate"));
        setStatus(XMLUtils.getIntegerAttributeValue(node, "Status"));
        setGuidExchangeOut(XMLUtils.getStringAttributeValue(node, "GuidZaj", 36));
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException{
        Issuable iss = DAOUtils.findDistributedObjectByRefGUID(Issuable.class, session, getGuidIssuable());
        if(iss==null) {
            DistributedObjectException distributedObjectException = new DistributedObjectException("Circulation NOT_FOUND_VALUE Issuable \"" + getGuidIssuable() +"\"");
            distributedObjectException.setData(getGuidIssuable());
            throw distributedObjectException;
        }
        setIssuable(iss);

        Circulation parentCirculation = DAOUtils.findDistributedObjectByRefGUID(Circulation.class, session, getGuidParentCirculation());
        if(parentCirculation!=null) setParentCirculation(parentCirculation);

        //Client cl = DAOUtils.findClientByRefGUID(session, guidClient);
        //if(cl==null) throw new DistributedObjectException("Client NOT_FOUND_VALUE");
        //setClient(cl);
        if (getIdOfClient() == null) {
            throw new DistributedObjectException("Circulation NOT_FOUND_VALUE Client=null");
        } else {
            Client currentClient = null;
            try{
                currentClient = DAOUtils.findClient(session, getIdOfClient());
            } catch (Exception e){
                throw new DistributedObjectException("Circulation NOT_FOUND_VALUE Client ID:\"" + getIdOfClient() +"\"");
            }
            if(currentClient==null){
                throw new DistributedObjectException("Circulation NOT_FOUND_VALUE Client ID:\"" + getIdOfClient() +"\"");
            }
            setClient(currentClient);
        }

        if ((getGuidExchangeOut() != null) && !getGuidExchangeOut().equals("")) {
            ExchangeOut exchangeOut = DAOUtils
                    .findDistributedObjectByRefGUID(ExchangeOut.class, session, getGuidExchangeOut());
            if (exchangeOut != null) {
                setExchangeOut(exchangeOut);
            } else {
                throw new DistributedObjectException(
                        "Circulation NOT_FOUND_VALUE ExchangeOut GUID:\"" + getGuidExchangeOut() + "\"");
            }
        }
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setIssuanceDate(((Circulation) distributedObject).getIssuanceDate());
        setRefundDate(((Circulation) distributedObject).getRefundDate());
        setRealRefundDate(((Circulation) distributedObject).getRealRefundDate());
        setStatus(((Circulation) distributedObject).getStatus());
        setParentCirculation(((Circulation) distributedObject).getParentCirculation());
        setGuidParentCirculation(((Circulation) distributedObject).getGuidParentCirculation());
        setIssuable(((Circulation) distributedObject).getIssuable());
        setGuidIssuable(((Circulation) distributedObject).getGuidIssuable());
        setClient(((Circulation) distributedObject).getClient());
        setIdOfClient(((Circulation) distributedObject).getIdOfClient());
        setExchangeOut(((Circulation) distributedObject).getExchangeOut());
        setGuidExchangeOut(((Circulation) distributedObject).getGuidExchangeOut());
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

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Set<Circulation> getCirculationInternal() {
        return circulationInternal;
    }

    public void setCirculationInternal(Set<Circulation> circulationInternal) {
        this.circulationInternal = circulationInternal;
    }

    public String getGuidParentCirculation() {
        return guidParentCirculation;
    }

    public void setGuidParentCirculation(String guidParentCirculation) {
        this.guidParentCirculation = guidParentCirculation;
    }

    public String getGuidIssuable() {
        return guidIssuable;
    }

    public void setGuidIssuable(String guidIssuable) {
        this.guidIssuable = guidIssuable;
    }

    public String getGuidExchangeOut() {
        return this.guidExchangeOut;
    }

    public void setGuidExchangeOut(String guidExchangeOut) {
        this.guidExchangeOut = guidExchangeOut;
    }

    public ExchangeOut getExchangeOut() {
        return this.exchangeOut;
    }

    public void setExchangeOut(ExchangeOut exchangeOut) {
        this.exchangeOut = exchangeOut;
    }
}
