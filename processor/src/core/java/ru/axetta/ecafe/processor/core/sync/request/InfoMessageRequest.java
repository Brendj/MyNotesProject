/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.request;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

public class InfoMessageRequest implements SectionRequest {
    public static final String SECTION_NAME="InfoMessageRequest";
    private final Long maxVersion;

    private InfoMessageRequest(Long maxVersion) {
        this.maxVersion = maxVersion;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    static InfoMessageRequest build(Node infoMessageRequestNode) throws Exception {
        final Long maxVersion = XMLUtils.getLongAttributeValue(infoMessageRequestNode, "V");
        return new InfoMessageRequest(maxVersion);
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    public static class InfoMessageRequestBuilder implements SectionRequestBuilder {

        public InfoMessageRequest build(Node envelopeNode) throws Exception {
            SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
            return sectionRequest != null ? (InfoMessageRequest) sectionRequest : null;
        }

        @Override
        public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
            Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, InfoMessageRequest.SECTION_NAME);
            if (sectionElement != null) {
                return InfoMessageRequest.build(sectionElement);
            } else
                return null;
        }
    }
}
