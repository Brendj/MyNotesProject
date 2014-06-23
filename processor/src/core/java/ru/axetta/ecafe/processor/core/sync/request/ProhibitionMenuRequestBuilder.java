/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.request;

import ru.axetta.ecafe.processor.core.sync.response.ClientGuardianItem;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;

import static ru.axetta.ecafe.processor.core.utils.XMLUtils.findFirstChildElement;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 13.01.14
 * Time: 13:12
 * To change this template use File | Settings | File Templates.
 */
public class ProhibitionMenuRequestBuilder {

    private Node mainNode;

    public void createMainNode(Node envelopeNode){
        mainNode = findFirstChildElement(envelopeNode, "ProhibitionsMenuRequest");
    }

    public ProhibitionMenuRequest build() throws Exception {
        if (mainNode != null){
            return ProhibitionMenuRequest.build(mainNode);
        } else {
            return null;
        }
    }

    public ProhibitionMenuRequest build(Node accRegistryUpdateRequest) throws Exception {
        return ProhibitionMenuRequest.build(accRegistryUpdateRequest);
    }

}
