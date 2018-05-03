/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.card.request;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 25.03.16
 * Time: 17:43
 * To change this template use File | Settings | File Templates.
 */
public class CardRequestsBuilder implements SectionRequestBuilder{
    private final long idOfOrg;

    public CardRequestsBuilder(long idOfOrg){
        this.idOfOrg = idOfOrg;
    }

    public CardRequests build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest!=null? (CardRequests) sectionRequest :null;
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, CardRequests.SECTION_NAME);
        if (sectionElement != null) {
            return new CardRequests(sectionElement, idOfOrg);
        } else
            return null;
    }
}
