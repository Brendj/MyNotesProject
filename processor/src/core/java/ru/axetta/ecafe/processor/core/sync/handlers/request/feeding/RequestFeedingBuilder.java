/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.request.feeding;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

public class RequestFeedingBuilder implements SectionRequestBuilder {

    private final long owner;

    public RequestFeedingBuilder(long owner){
        this.owner = owner;
    }

    public RequestFeeding build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest != null ? (RequestFeeding) sectionRequest : null;
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, RequestFeeding.SECTION_NAME);
        if (sectionElement != null) {
            return new RequestFeeding(sectionElement, owner);
        } else
            return null;
    }
}
