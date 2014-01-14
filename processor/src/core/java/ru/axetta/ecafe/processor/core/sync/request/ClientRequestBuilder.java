package ru.axetta.ecafe.processor.core.sync.request;

import org.w3c.dom.Node;

import static ru.axetta.ecafe.processor.core.utils.XMLUtils.findFirstChildElement;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.13
 * Time: 18:29
 * To change this template use File | Settings | File Templates.
 */
public class ClientRequestBuilder {

    private Node mainNode;

    public void createMainNode(Node envelopeNode){
        mainNode = findFirstChildElement(envelopeNode, "ClientRequests");
    }

    public ClientRequests build() throws Exception {
        if (mainNode != null){
            return build(mainNode);
        } else {
            return null;
        }
    }

    public ClientRequests build(Node clientRequestNode) throws Exception {
        return new ClientRequests(clientRequestNode);
    }

}
