/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.request;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * User: akmukov
 * Date: 30.06.2016
 */
public class OrgOwnerDataRequest implements SectionRequest {
    public static final String SECTION_NAME="OrgOwnerDataRequest";

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    @Override
    public String toString() {
        return getRequestSectionName();
    }

    public static class Builder implements SectionRequestBuilder {

        public OrgOwnerDataRequest build(Node envelopeNode) throws Exception {
            SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
            return sectionRequest != null ? (OrgOwnerDataRequest) sectionRequest : null;
        }

        @Override
        public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
            Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, SECTION_NAME);
            if (sectionElement != null) {
                return new OrgOwnerDataRequest();
            } else
                return null;
        }
    }
}