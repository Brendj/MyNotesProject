/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.request;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

public class OrgFilesRequestBuilder implements SectionRequestBuilder {

    private final Long idOfOrg;

    public OrgFilesRequestBuilder(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public OrgFilesRequest build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest != null ? (OrgFilesRequest) sectionRequest : null;
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, OrgFilesRequest.SECTION_NAME);
        if (sectionElement != null) {
            return OrgFilesRequest.build(sectionElement, idOfOrg);
        } else
            return null;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }
}
