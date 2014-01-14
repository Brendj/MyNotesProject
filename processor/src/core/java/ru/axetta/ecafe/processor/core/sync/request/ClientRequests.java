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
        //Node responseClientGuardianNode = XMLUtils.findFirstChildElement(clientRequestNode, "ClientGuardianRequest");
        //if(responseClientGuardianNode!=null){
        //    Long v = XMLUtils.getLongAttributeValue(responseClientGuardianNode, "V");
        //    responseClientGuardian = (v==null?0L:v);
        //} else {
        //    responseClientGuardian=null;
        //}
        //this.responseClientGuardian = XMLUtils.findFirstChildElement(clientRequestNode, "ClientGuardianRequest") != null;
    }

    public Boolean getResponseTempCardOperation() {
        return responseTempCardOperation;
    }

    //public Long getResponseClientGuardian() {
    //    return responseClientGuardian;
    //}

    //public static ClientRequests build(Node clientRequestNode) throws Exception{
    //    Node responseTempCardOperationNode = XMLUtils.findFirstChildElement(clientRequestNode, "TempCardsRequest");
    //    return new ClientRequests(responseTempCardOperationNode!=null);
    //}

    //public Boolean getResponseClientGuardian() {
    //    return responseClientGuardian;
    //}

}
