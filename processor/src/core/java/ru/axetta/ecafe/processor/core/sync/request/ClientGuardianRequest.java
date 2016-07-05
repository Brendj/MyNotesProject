package ru.axetta.ecafe.processor.core.sync.request;

import ru.axetta.ecafe.processor.core.sync.response.ClientGuardianItem;
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
public class ClientGuardianRequest implements SectionRequest{
    public static final String SECTION_NAME="ClientsGuardians";
    private final List<ClientGuardianItem> clientGuardianResponseElement;
    private final Long maxVersion;

    public ClientGuardianRequest(Long maxVersion, List<ClientGuardianItem> clientGuardianResponseElement) {
        this.maxVersion = maxVersion;
        this.clientGuardianResponseElement = clientGuardianResponseElement;
    }

    public List<ClientGuardianItem> getClientGuardianResponseElement() {
        return clientGuardianResponseElement;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    static ClientGuardianRequest build(Node clientGuardianRequestNode) throws Exception {
        final Long maxVersion = XMLUtils.getLongAttributeValue(clientGuardianRequestNode, "V");
        Node itemNode = clientGuardianRequestNode.getFirstChild();
        List<ClientGuardianItem> items = new LinkedList<ClientGuardianItem>();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("CG")) {
                items.add(ClientGuardianItem.build(itemNode));
            }
            itemNode = itemNode.getNextSibling();
        }
        return new ClientGuardianRequest(maxVersion, items);
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }
}
