/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.dtiszn;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

public class ClientDiscountDTSZNBuilder implements SectionRequestBuilder {
    private final long owner;

    public ClientDiscountDTSZNBuilder(long owner){
        this.owner = owner;
    }

    public ClientDiscountsDTSZNRequest build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest != null ? (ClientDiscountsDTSZNRequest) sectionRequest : null;
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, ClientDiscountsDTSZNRequest.SECTION_NAME);
        if (sectionElement != null) {
            return new ClientDiscountsDTSZNRequest(sectionElement, owner);
        } else
            return null;
    }
}