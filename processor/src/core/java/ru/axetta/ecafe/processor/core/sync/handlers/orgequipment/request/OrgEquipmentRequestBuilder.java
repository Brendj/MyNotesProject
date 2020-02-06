/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.orgequipment.request;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

public class OrgEquipmentRequestBuilder implements SectionRequestBuilder {

    private final long owner;

    public OrgEquipmentRequestBuilder(long owner) {
        this.owner = owner;
    }

    public OrgEquipmentRequest build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest != null ? (OrgEquipmentRequest) sectionRequest : null;
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, OrgEquipmentRequest.SECTION_NAME);
        if (sectionElement != null) {
            return new OrgEquipmentRequest(sectionElement, owner);
        } else {
            return null;
        }
    }

}
