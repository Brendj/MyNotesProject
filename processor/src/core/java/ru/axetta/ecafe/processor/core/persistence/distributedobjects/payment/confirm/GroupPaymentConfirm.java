/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.payment.confirm;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 22.02.13
 * Time: 13:02
 * To change this template use File | Settings | File Templates.
 */
public class GroupPaymentConfirm extends DistributedObject {

    private Long confirmerId;
    private Set<GroupPaymentConfirmPosition> groupPaymentConfirmPositions;

    public Set<GroupPaymentConfirmPosition> getGroupPaymentConfirmPositions() {
        return groupPaymentConfirmPositions;
    }

    void setGroupPaymentConfirmPositions(Set<GroupPaymentConfirmPosition> groupPaymentConfirmPositions) {
        this.groupPaymentConfirmPositions = groupPaymentConfirmPositions;
    }

    Long getConfirmerId() {
        return confirmerId;
    }

    void setConfirmerId(Long confirmerId) {
        this.confirmerId = confirmerId;
    }

    @Override
    protected void appendAttributes(Element element) {}

    @Override
    protected DistributedObject parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        Long longConfirmerId = getLongAttributeValue(node, "ConfirmerId");
        if(longConfirmerId != null) setConfirmerId(longConfirmerId);
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((GroupPaymentConfirm) distributedObject).getOrgOwner());
        setConfirmerId(((GroupPaymentConfirm) distributedObject).getConfirmerId());
    }



}
