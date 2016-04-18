/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.special.dates;

import org.w3c.dom.Node;

import static ru.axetta.ecafe.processor.core.utils.XMLUtils.findFirstChildElement;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 13.04.16
 * Time: 11:37
 */
public class SpecialDatesBuilder {
    private Node mainNode;

    public void createMainNode(Node envelopeNode){
        mainNode = findFirstChildElement(envelopeNode, "SpecialDates");
    }

    public SpecialDates build(Node specialDatesNode, Long orgOwner) throws Exception {
        SpecialDates result = new SpecialDates(specialDatesNode, orgOwner);
        return result;
    }

}
