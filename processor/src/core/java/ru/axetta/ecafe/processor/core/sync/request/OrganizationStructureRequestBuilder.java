/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.request;

import org.w3c.dom.Node;

import static ru.axetta.ecafe.processor.core.utils.XMLUtils.findFirstChildElement;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 23.12.15
 * Time: 15:54
 * To change this template use File | Settings | File Templates.
 */
public class OrganizationStructureRequestBuilder {
    private Node mainNode;

    public void createMainNode(Node envelopeNode){
        mainNode = findFirstChildElement(envelopeNode, "OrganizationStructureRequest");
    }

    public OrganizationStructureRequest build() throws Exception {
        if (mainNode != null){
            return OrganizationStructureRequest.build(mainNode);
        } else {
            return null;
        }
    }

    public OrganizationStructureRequest build(Node organizationStructureRequest) throws Exception {
        return OrganizationStructureRequest.build(organizationStructureRequest);
    }

}
