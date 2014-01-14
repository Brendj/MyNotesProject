package ru.axetta.ecafe.processor.core.sync.request;

import ru.axetta.ecafe.processor.core.sync.response.ClientGuardianResponseElement;
import ru.axetta.ecafe.processor.core.utils.*;

import org.w3c.dom.Node;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 13.01.14
 * Time: 13:12
 * To change this template use File | Settings | File Templates.
 */
public class ClientGuardianRequest {

    private final List<ClientGuardianResponseElement> clientGuardianResponseElement;
    private final Long maxVersion;

    public ClientGuardianRequest(Long maxVersion, List<ClientGuardianResponseElement> clientGuardianResponseElement) {
        this.maxVersion = maxVersion;
        this.clientGuardianResponseElement = clientGuardianResponseElement;
    }

    public List<ClientGuardianResponseElement> getClientGuardianResponseElement() {
        return clientGuardianResponseElement;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    static ClientGuardianRequest build(Node clientGuardianRequestNode) throws Exception {
        final Long maxVersion = XMLUtils.getLongAttributeValue(clientGuardianRequestNode, "V");
        Node itemNode = clientGuardianRequestNode.getFirstChild();
        List<ClientGuardianResponseElement> items = new LinkedList<ClientGuardianResponseElement>();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("CG")) {
                items.add(ClientGuardianResponseElement.build(itemNode));
            }
            itemNode = itemNode.getNextSibling();
        }
        return new ClientGuardianRequest(maxVersion, items);
    }

}
