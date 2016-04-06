/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.clientgroup.managers;

import org.w3c.dom.Node;
import static ru.axetta.ecafe.processor.core.utils.XMLUtils.findFirstChildElement;

/**
 * User: akmukov
 * Date: 04.04.2016
 */
public class ClientGroupManagerBuilder {
    private Node mainNode;

    public void createMainNode(Node envelopeNode) {
        mainNode = findFirstChildElement(envelopeNode, "GroupManagers");
    }

    public ClientGroupManagerRequest build() throws Exception {
        return mainNode != null ? ClientGroupManagerRequest.build(mainNode) : null;
    }

    public ClientGroupManagerRequest build(Node sectionNode) throws Exception {
        return ClientGroupManagerRequest.build(sectionNode);
    }

}
