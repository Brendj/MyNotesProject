/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

public class SyncSettingsRequestBuilder implements SectionRequestBuilder {
    private Long owner;

    public SyncSettingsRequest build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest != null ? (SyncSettingsRequest) sectionRequest : null;
    }

    public SyncSettingsRequestBuilder(Long owner){
        this.owner = owner;
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, SyncSettingsRequest.SECTION_NAME);
        if (sectionElement != null) {
            return new SyncSettingsRequest(sectionElement);
        } else {
            return null;
        }
    }
}
