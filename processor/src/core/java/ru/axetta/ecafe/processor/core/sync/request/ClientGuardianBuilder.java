package ru.axetta.ecafe.processor.core.sync.request;

import org.w3c.dom.Node;

import static ru.axetta.ecafe.processor.core.utils.XMLUtils.findFirstChildElement;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 13.01.14
 * Time: 13:12
 * To change this template use File | Settings | File Templates.
 */
public class ClientGuardianBuilder {

    private Node mainNode;

    public void createMainNode(Node envelopeNode){
        mainNode = findFirstChildElement(envelopeNode, "ClientGuardians");
    }

    public ClientGuardianRequest build() throws Exception {
        if (mainNode != null){
            return ClientGuardianRequest.build(mainNode);
        } else {
            return null;
        }
    }

    public ClientGuardianRequest build(Node accRegistryUpdateRequest) throws Exception {
        return ClientGuardianRequest.build(accRegistryUpdateRequest);
    }

}
