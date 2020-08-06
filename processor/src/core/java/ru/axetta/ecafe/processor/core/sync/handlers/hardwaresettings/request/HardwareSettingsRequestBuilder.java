/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

public class HardwareSettingsRequestBuilder implements SectionRequestBuilder {

    private final long owner;

    public HardwareSettingsRequestBuilder(long owner) {
        this.owner = owner;
    }

    public HardwareSettingsRequest build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest != null ? (HardwareSettingsRequest) sectionRequest : null;
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, HardwareSettingsRequest.SECTION_NAME);
        if (sectionElement != null) {
            return new HardwareSettingsRequest(sectionElement, owner);
        } else {
            return null;
        }
    }

}
