/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

public class TurnstileSettingsRequestBuilder implements SectionRequestBuilder {

    private final long owner;

    public TurnstileSettingsRequestBuilder(long owner) {
        this.owner = owner;
    }

    public TurnstileSettingsRequest build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest != null ? (TurnstileSettingsRequest) sectionRequest : null;
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, TurnstileSettingsRequest.SECTION_NAME);
        if (sectionElement != null) {
            return new TurnstileSettingsRequest(sectionElement, owner);
        } else {
            return null;
        }
    }

}
