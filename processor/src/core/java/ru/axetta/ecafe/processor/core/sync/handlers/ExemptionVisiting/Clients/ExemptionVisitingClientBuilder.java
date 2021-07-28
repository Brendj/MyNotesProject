/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.ExemptionVisiting.Clients;

import ru.axetta.ecafe.processor.core.sync.handlers.emias.EmiasRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

public class ExemptionVisitingClientBuilder implements SectionRequestBuilder {

    public ExemptionVisitingClientBuilder(){
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, ExemptionVisitingClientRequest.SECTION_NAME);
        if (sectionElement != null) {
            return new ExemptionVisitingClientRequest(sectionElement);
        } else
            return null;
    }
}
