/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

public class OrgSettingsBuilder implements SectionRequestBuilder {

    public OrgSettingsRequest build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest != null ? (OrgSettingsRequest) sectionRequest : null;
    }

    public OrgSettingsBuilder(Long owner){

    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, OrgSettingsRequest.SECTION_NAME);
        if (sectionElement != null) {
            return new OrgSettingsRequest(sectionElement);
        } else
            return null;
    }
}
