/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfOrderDetail;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class GoodComplaintOrders extends DistributedObject {

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        GoodComplaintIterations gci = (GoodComplaintIterations) DAOUtils
                .findDistributedObjectByRefGUID(session, guidOfComplaintIteration);
        if (gci == null) throw new DistributedObjectException("Complaint iteration NOT_FOUND_VALUE");
        setComplaintIteration(gci);

        DistributedObjectException distributedObjectException = new DistributedObjectException("OrderDetail NOT_FOUND_VALUE");
        distributedObjectException.setData(String.valueOf(idOfOrderDetail));
        OrderDetail od;
        Long idOfOrg = getOrgOwner();
        if (idOfOrg == null) {
            throw distributedObjectException;
        }
        CompositeIdOfOrderDetail compositeIdOfOrderDetail = new CompositeIdOfOrderDetail(getOrgOwner(), idOfOrderDetail);
        try {
            od = DAOUtils.findOrderDetail(session, compositeIdOfOrderDetail);
        } catch (Exception e) {
            throw distributedObjectException;
        }
        if (od == null) throw distributedObjectException;
        setOrderDetail(od);
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element, "GuidOfComplaintIteration", complaintIteration.getGuid());
        setAttribute(element, "IdOfOrderDetail", orderDetail.getCompositeIdOfOrderDetail().getIdOfOrderDetail());
    }

    @Override
    protected GoodComplaintOrders parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null) setOrgOwner(longOrgOwner);
        guidOfComplaintIteration = getStringAttributeValue(node, "GuidOfComplaintIteration", 36);
        idOfOrderDetail = getLongAttributeValue(node, "IdOfOrderDetail");
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
    }

    private GoodComplaintIterations complaintIteration;
    private String guidOfComplaintIteration;
    private OrderDetail orderDetail;
    private Long idOfOrderDetail;

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
