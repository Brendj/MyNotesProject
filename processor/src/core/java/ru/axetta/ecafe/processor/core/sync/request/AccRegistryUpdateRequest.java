/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.request;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 29.10.13
 * Time: 18:43
 * To change this template use File | Settings | File Templates.
 */
public class AccRegistryUpdateRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccRegistryUpdateRequest.class);
    private final List<Long> clientIds;

    AccRegistryUpdateRequest(List<Long> clientIds) {
        this.clientIds = clientIds;
    }

    public List<Long> getClientIds() {
        return clientIds;
    }

    static AccRegistryUpdateRequest build(Node accRegistryUpdateRequestParseRequestNode) throws Exception {
        final List<Long> clientIds = new LinkedList<Long>();
        Node itemNode = accRegistryUpdateRequestParseRequestNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("CI")) {
                Long clientId = XMLUtils.getLongAttributeValue(itemNode, "IdOfClient");
                clientIds.add(clientId);
                LOGGER.info("counts "+ clientIds.size());
            }
            itemNode = itemNode.getNextSibling();
        }
        return new AccRegistryUpdateRequest(clientIds);
    }


}
