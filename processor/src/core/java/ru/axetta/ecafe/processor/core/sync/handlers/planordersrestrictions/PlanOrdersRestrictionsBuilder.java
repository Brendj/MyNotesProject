/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.planordersrestrictions;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * Created by i.semenov on 03.02.2020.
 */
public class PlanOrdersRestrictionsBuilder implements SectionRequestBuilder {
    private final long owner;

    public PlanOrdersRestrictionsBuilder(long owner){
        this.owner = owner;
    }

    public PlanOrdersRestrictionsRequest build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest != null ? (PlanOrdersRestrictionsRequest) sectionRequest : null;
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, PlanOrdersRestrictionsRequest.SECTION_NAME);
        if (sectionElement != null) {
            return new PlanOrdersRestrictionsRequest(sectionElement, owner);
        } else
            return null;
    }
}
