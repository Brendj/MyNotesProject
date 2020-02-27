/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding.status;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 15.02.16
 * Time: 12:22
 * To change this template use File | Settings | File Templates.
 */
public class PreorderFeedingStatusBuilder implements SectionRequestBuilder {
    private final long owner;

    public PreorderFeedingStatusBuilder(long owner){
        this.owner = owner;
    }

    public PreorderFeedingStatusRequest build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest != null ? (PreorderFeedingStatusRequest) sectionRequest : null;
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, PreorderFeedingStatusRequest.SECTION_NAME);
        if (sectionElement != null) {
            return new PreorderFeedingStatusRequest(sectionElement, owner);
        } else
            return null;
    }
}
