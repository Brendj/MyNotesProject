package ru.axetta.ecafe.processor.core.sync.request;

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
    //private final Long responseClientGuardian;

    ClientRequests(Node clientRequestNode) {
        this.responseTempCardOperation = XMLUtils.findFirstChildElement(clientRequestNode, "TempCardsRequest") != null;
    }

    public Boolean getResponseTempCardOperation() {
        return responseTempCardOperation;
    }

}
