package ru.axetta.ecafe.processor.core.sync.handlers.registry.operations.account;

import ru.axetta.ecafe.processor.core.sync.LoadContext;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Shamil
 * Date: 19.02.15
 */
public class AccountOperationsRegistry implements SectionRequest{
    public static final String SECTION_NAME = "AccountOperationsRegistry";
    private final List<AccountOperationItem> operationItemList;

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
        return String.format("PaymentRegistry{payments=%s}", operationItemList);
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    public static class Builder implements SectionRequestBuilder {
        private final LoadContext loadContext;

        public Builder(LoadContext loadContext) {
            this.loadContext = loadContext;
        }

        public AccountOperationsRegistry build(Node envelopeNode) throws Exception {
            SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
            return sectionRequest != null ? (AccountOperationsRegistry) sectionRequest : null;
        }

        @Override
        public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
            Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, SECTION_NAME);
            if (sectionElement!=null){
                return buildFromCorrectSection(sectionElement);
            }
            else return null;
        }

        private AccountOperationsRegistry buildFromCorrectSection(Node accountOperationsRegistryNode)
                throws Exception {
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

}
