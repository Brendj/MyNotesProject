/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.registry.operations.account;

import ru.axetta.ecafe.processor.core.sync.LoadContext;

import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.10.13
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */
public class AccountOperationsRegistryBuilder {

    public AccountOperationsRegistry build(Node accountOperationsRegistryNode, LoadContext loadContext) throws Exception {
        List<AccountOperationItem> operationItemList = new LinkedList<AccountOperationItem>();
        Node itemNode = accountOperationsRegistryNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals(AccountOperationItem.SYNC_NAME)) {
                operationItemList.add(AccountOperationItem.build(itemNode, loadContext));
            }
            itemNode = itemNode.getNextSibling();
        }
        return new AccountOperationsRegistry(operationItemList);
    }

}
