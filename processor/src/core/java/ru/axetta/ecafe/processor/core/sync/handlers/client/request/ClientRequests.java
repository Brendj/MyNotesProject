package ru.axetta.ecafe.processor.core.sync.handlers.client.request;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 02.08.13
 * Time: 12:24
 * To change this template use File | Settings | File Templates.
 */
public class ClientRequests {

    private final Boolean responseTempCardOperation;

    private ClientRequests(Boolean responseTempCardOperation) {
        this.responseTempCardOperation = responseTempCardOperation;
    }

    public static ClientRequests build(Node clientRequestNode) throws Exception{
        Node responseTempCardOperationNode = XMLUtils.findFirstChildElement(clientRequestNode, "TempCardsRequest");
        return new ClientRequests(responseTempCardOperationNode!=null);
    }

    public Boolean getResponseTempCardOperation() {
        return responseTempCardOperation;
    }
}
