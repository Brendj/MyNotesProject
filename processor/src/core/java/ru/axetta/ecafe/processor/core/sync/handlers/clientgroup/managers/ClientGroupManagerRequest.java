/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.clientgroup.managers;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;
import org.w3c.dom.Node;
import java.util.LinkedList;
import java.util.List;

/**
 * User: akmukov
 * Date: 04.04.2016
 */
public class ClientGroupManagerRequest {

    private final Long maxVersion;
    private final List<ClientgroupManagerItem> items;

    private ClientGroupManagerRequest(Long maxVersion, List<ClientgroupManagerItem> items) {

        this.maxVersion = maxVersion;
        this.items = items;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public List<ClientgroupManagerItem> getItems() {
        return items;
    }

    public static ClientGroupManagerRequest build(Node sectionNode) {
        final Long maxVersion = XMLUtils.getLongAttributeValue(sectionNode, "V");
        Node itemNode = sectionNode.getFirstChild();
        List<ClientgroupManagerItem> items = new LinkedList<ClientgroupManagerItem>();
        while (itemNode != null) {
            if (itemNode.getNodeType()== Node.ELEMENT_NODE && itemNode.getNodeName().equals("GMI")) {
                items.add(ClientgroupManagerItem.build(itemNode));
            }
            itemNode = itemNode.getNextSibling();
        }
        return new ClientGroupManagerRequest(maxVersion,items);
    }
}
