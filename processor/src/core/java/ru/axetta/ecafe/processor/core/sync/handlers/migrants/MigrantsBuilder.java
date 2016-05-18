/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.migrants;

import org.w3c.dom.Node;

import static ru.axetta.ecafe.processor.core.utils.XMLUtils.findFirstChildElement;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 11.05.16
 * Time: 11:48
 */

public class MigrantsBuilder {
    private Node mainNode;

    public void createMainNode(Node envelopeNode){
        mainNode = findFirstChildElement(envelopeNode, "Migrants");
    }

    public Migrants build(Node migrantsNode, Long idOfOrgRegistry) throws Exception {
        Migrants result = new Migrants(migrantsNode, idOfOrgRegistry);
        return result;
    }
}
