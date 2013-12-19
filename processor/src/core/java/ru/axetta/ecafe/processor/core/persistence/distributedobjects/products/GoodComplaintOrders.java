/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfOrderDetail;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.sql.JoinType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;

public class GoodComplaintOrders extends DistributedObject {

    private GoodComplaintIterations complaintIteration;
    private String guidOfComplaintIteration;
    private Org orderOrg;
    private Long idOfOrderOrg;
    private OrderDetail orderDetail;
    private Long idOfOrderDetail;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("guidOfComplaintIteration","ci", JoinType.LEFT_OUTER_JOIN);

        criteria.createAlias("orderDetail","od", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("ci.guid"), "guidOfComplaintIteration");

        projectionList.add(Projections.property("od.compositeIdOfOrderDetail.idOfOrg"), "idOfOrderOrg");
        projectionList.add(Projections.property("od.compositeIdOfOrderDetail.idOfOrderDetail"), "idOfOrderDetail");
        criteria.setProjection(projectionList);
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion) throws Exception {
        return toSelfProcess(session, idOfOrg, currentMaxVersion);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        GoodComplaintIterations gci = DAOUtils.findDistributedObjectByRefGUID(GoodComplaintIterations.class, session, guidOfComplaintIteration);
        if (gci == null) throw new DistributedObjectException("Complaint iteration NOT_FOUND_VALUE");
        setComplaintIteration(gci);

        DistributedObjectException orgNotFoundException = new DistributedObjectException("Org NOT_FOUND_VALUE");
        orgNotFoundException.setData(String.valueOf(idOfOrderOrg));
        Org o;
        try {
            o = DAOUtils.findOrg(session, idOfOrderOrg);
        } catch (Exception e) {
            throw orgNotFoundException;
        }
        if (o == null) throw orgNotFoundException;
        setOrderOrg(o);

        DistributedObjectException orderDetailNotFoundException = new DistributedObjectException("OrderDetail NOT_FOUND_VALUE");
        orderDetailNotFoundException.setData(String.valueOf(idOfOrderDetail));
        OrderDetail od;
        CompositeIdOfOrderDetail compositeIdOfOrderDetail = new CompositeIdOfOrderDetail(getOrderOrg().getIdOfOrg(), idOfOrderDetail);
        try {
            od = DAOUtils.findOrderDetail(session, compositeIdOfOrderDetail);
        } catch (Exception e) {
            throw orderDetailNotFoundException;
        }
        if (od == null) throw orderDetailNotFoundException;
        setOrderDetail(od);
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        if(StringUtils.isNotEmpty(guidOfComplaintIteration)){
            XMLUtils.setAttributeIfNotNull(element, "GuidOfComplaintIteration", guidOfComplaintIteration);
        }
        if(idOfOrderOrg!=null){
            XMLUtils.setAttributeIfNotNull(element, "IdOfOrg", idOfOrderOrg);
        }
        if(idOfOrderDetail!=null){
            XMLUtils.setAttributeIfNotNull(element, "IdOfOrderDetail", idOfOrderDetail);
        }
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setComplaintIteration(((GoodComplaintOrders) distributedObject).getComplaintIteration());
        setOrderDetail(((GoodComplaintOrders) distributedObject).getOrderDetail());
        setOrderOrg(((GoodComplaintOrders) distributedObject).getOrderOrg());
    }

    @Override
    protected GoodComplaintOrders parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null){
            setOrgOwner(longOrgOwner);
        } else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        guidOfComplaintIteration = XMLUtils.getStringAttributeValue(node, "GuidOfComplaintIteration", 36);
        idOfOrderOrg = XMLUtils.getLongAttributeValue(node, "IdOfOrg");
        idOfOrderDetail = XMLUtils.getLongAttributeValue(node, "IdOfOrderDetail");
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    public GoodComplaintIterations getComplaintIteration() {
        return complaintIteration;
    }

    public void setComplaintIteration(GoodComplaintIterations complaintIteration) {
        this.complaintIteration = complaintIteration;
    }

    public String getGuidOfComplaintIteration() {
        return guidOfComplaintIteration;
    }

    public void setGuidOfComplaintIteration(String guidOfComplaintIteration) {
        this.guidOfComplaintIteration = guidOfComplaintIteration;
    }

    public Org getOrderOrg() {
        return orderOrg;
    }

    public void setOrderOrg(Org orderOrg) {
        this.orderOrg = orderOrg;
    }

    public Long getIdOfOrderOrg() {
        return idOfOrderOrg;
    }

    public void setIdOfOrderOrg(Long idOfOrderOrg) {
        this.idOfOrderOrg = idOfOrderOrg;
    }

    public OrderDetail getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }

    public Long getIdOfOrderDetail() {
        return idOfOrderDetail;
    }

    public void setIdOfOrderDetail(Long idOfOrderDetail) {
        this.idOfOrderDetail = idOfOrderDetail;
    }

}
