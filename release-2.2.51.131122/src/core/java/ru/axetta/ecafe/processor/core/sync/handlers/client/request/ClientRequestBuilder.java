package ru.axetta.ecafe.processor.core.sync.handlers.client.request;

import ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations.TempCardOperation;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.13
 * Time: 18:29
 * To change this template use File | Settings | File Templates.
 */
public class ClientRequestBuilder {

    public ClientRequests build(Node clientRequestNode) throws Exception {
        return ClientRequests.build(clientRequestNode);
    }

}
