/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.balance.hold;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

public class ClientBalanceHoldBuilder implements SectionRequestBuilder {
    private final long owner;

    public ClientBalanceHoldBuilder(long owner){
        this.owner = owner;
    }

    public ClientBalanceHoldRequest build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest != null ? (ClientBalanceHoldRequest) sectionRequest : null;
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, ClientBalanceHoldRequest.SECTION_NAME);
        if (sectionElement != null) {
            return new ClientBalanceHoldRequest(sectionElement, owner);
        } else
            return null;
    }
}
