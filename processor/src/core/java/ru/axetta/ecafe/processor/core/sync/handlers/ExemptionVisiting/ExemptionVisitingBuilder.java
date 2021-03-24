/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.ExemptionVisiting;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

public class ExemptionVisitingBuilder implements SectionRequestBuilder {

    public ExemptionVisitingBuilder(){

    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, ExemptionVisitingRequest.SECTION_NAME);
        if (sectionElement != null) {
            return new ExemptionVisitingRequest(sectionElement);
        } else
            return null;
    }
}
