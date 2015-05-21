/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.request.registry.accounts;

import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;

/**
 * User: shamil
 * Date: 21.05.15
 * Time: 9:45
 */
public class AccountsRegistryRequest {
    public static final String SYNC_NAME = "AccountsRegistryRequest";

    private List<AccountsRegistryRequestItem> items = new LinkedList<AccountsRegistryRequestItem>();

    public AccountsRegistryRequest(List<AccountsRegistryRequestItem> items) {
        this.items = items;
    }

    static public AccountsRegistryRequest build(Node node) throws Exception {
        Node itemNode = node.getFirstChild();
        List<AccountsRegistryRequestItem> items = new LinkedList<AccountsRegistryRequestItem>();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals(
                    AccountsRegistryRequestItem.SYNC_NAME)) {
                items.add(AccountsRegistryRequestItem.build(itemNode));
            }
            itemNode = itemNode.getNextSibling();
        }
        return new AccountsRegistryRequest(items);
    }

    public List<AccountsRegistryRequestItem> getItems() {
        return items;
    }
}
