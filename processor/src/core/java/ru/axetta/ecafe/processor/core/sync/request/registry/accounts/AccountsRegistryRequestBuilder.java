/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.request.registry.accounts;

import org.w3c.dom.Node;

import static ru.axetta.ecafe.processor.core.utils.XMLUtils.findFirstChildElement;

/**
 * User: shamil
 * Date: 21.05.15
 * Time: 10:07
 */
public class AccountsRegistryRequestBuilder {
    private Node mainNode;

    public void createMainNode(Node envelopeNode){
        mainNode = findFirstChildElement(envelopeNode, AccountsRegistryRequest.SYNC_NAME);
    }

    public AccountsRegistryRequest build() throws Exception {
        if (mainNode != null){
            return AccountsRegistryRequest.build(mainNode);
        } else {
            return null;
        }
    }

    public AccountsRegistryRequest build(Node node) throws Exception {
        return AccountsRegistryRequest.build(node);
    }

}
