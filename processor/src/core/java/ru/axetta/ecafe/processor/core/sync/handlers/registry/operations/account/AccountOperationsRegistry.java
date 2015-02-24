package ru.axetta.ecafe.processor.core.sync.handlers.registry.operations.account;

import ru.axetta.ecafe.processor.core.sync.LoadContext;

import org.w3c.dom.Node;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Shamil
 * Date: 19.02.15
 */
public class AccountOperationsRegistry {
    public static final String SYNC_NAME = "AccountOperationsRegistry";


    private final List<AccountOperationItem> operationItemList;

    public static AccountOperationsRegistry build(Node accountOperationsRegistryNode, LoadContext loadContext) throws Exception {
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

    AccountOperationsRegistry(List<AccountOperationItem> operationItemList) {
        this.operationItemList = operationItemList;
    }

    public List<AccountOperationItem> getOperationItemList() {
        return operationItemList;
    }

    public Iterator<AccountOperationItem> getOperationItemIterator() {
        return operationItemList.iterator();
    }

    @Override
    public String toString() {
        return "PaymentRegistry{" + "payments=" + operationItemList + '}';
    }
}
