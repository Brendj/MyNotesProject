/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval;

import org.w3c.dom.Node;

import static ru.axetta.ecafe.processor.core.utils.XMLUtils.findFirstChildElement;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 15.02.16
 * Time: 12:22
 * To change this template use File | Settings | File Templates.
 */
public class ReestrTaloonApprovalBuilder {

    private Node mainNode;

    public void createMainNode(Node envelopeNode){
        mainNode = findFirstChildElement(envelopeNode, "ReestrTaloonApproval");
    }

    public ReestrTaloonApproval build(Node reestrTaloonApprovalNode, Long orgOwner) throws Exception {
        ReestrTaloonApproval result = new ReestrTaloonApproval(reestrTaloonApprovalNode, orgOwner);
        return result;
    }
}
