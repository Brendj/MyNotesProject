/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.zero.transactions;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 25.03.16
 * Time: 13:44
 * To change this template use File | Settings | File Templates.
 */
public class ZeroTransactions {
    private final List<ZeroTransactionItem> items;
    private final Long maxVersion;
    private final Long idOfOrgOwner;

    public ZeroTransactions(Node zeroTransactionsRequestNode, Long orgOwner) {
        maxVersion = XMLUtils.getLongAttributeValue(zeroTransactionsRequestNode, "V");
        idOfOrgOwner = orgOwner;
        this.items = new ArrayList<ZeroTransactionItem>();

        Node itemNode = zeroTransactionsRequestNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("ZT")) {
                ZeroTransactionItem item = new ZeroTransactionItem().build(itemNode, orgOwner);
                getItems().add(item);
            }
            itemNode = itemNode.getNextSibling();
        }
    }

    public List<ZeroTransactionItem> getItems() {
        return items;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public Long getIdOfOrgOwner() {
        return idOfOrgOwner;
    }
}
