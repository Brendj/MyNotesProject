/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.payment.confirm;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 22.02.13
 * Time: 13:02
 * To change this template use File | Settings | File Templates.
 */
public class GroupPaymentConfirmPosition extends DistributedObject {

    private Long idOfOrder;
    private GroupPaymentConfirm groupPaymentConfirm;
    private String guidOfGroupPaymentConfirm;

    public GroupPaymentConfirm getGroupPaymentConfirm() {
        return groupPaymentConfirm;
    }

    public void setGroupPaymentConfirm(GroupPaymentConfirm groupPaymentConfirm) {
        this.groupPaymentConfirm = groupPaymentConfirm;
    }

    Long getIdOfOrder() {
        return idOfOrder;
    }

    void setIdOfOrder(Long idOfOrder) {
        this.idOfOrder = idOfOrder;
    }

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        GroupPaymentConfirm gps = (GroupPaymentConfirm) DAOUtils.findDistributedObjectByRefGUID(session, guidOfGroupPaymentConfirm);
        if(gps == null) throw new DistributedObjectException("GroupPaymentConfirm NOT_FOUND_VALUE");
        setGroupPaymentConfirm(gps);
    }

    @Override
    protected void appendAttributes(Element element) {}

    @Override
    protected DistributedObject parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        Long longIdOfOrder = getLongAttributeValue(node, "IdOfOrder");
        if(longIdOfOrder != null) setIdOfOrder(longIdOfOrder);
        guidOfGroupPaymentConfirm = getStringAttributeValue(node,"GuidOfConfirm",36);
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((GroupPaymentConfirmPosition) distributedObject).getOrgOwner());
        setIdOfOrder(((GroupPaymentConfirmPosition) distributedObject).getIdOfOrder());
        setGroupPaymentConfirm(((GroupPaymentConfirmPosition) distributedObject).getGroupPaymentConfirm());
    }



}
