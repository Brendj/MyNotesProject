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
public class DirectivesRequest implements SectionRequest {

    public static final String SECTION_NAME = "DirectivesRequest";
    private final Boolean tradeConfigChangedSuccess;

    public DirectivesRequest(Node directivesRequestNode) throws Exception {
        this.tradeConfigChangedSuccess = XMLUtils.getBooleanAttributeValue(directivesRequestNode, "TRADE_ACCOUNT_CONFIG_CHANGED_SUCCESS");
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    @Override
    public String toString() {
        return SECTION_NAME;
    }

    public static class Builder implements SectionRequestBuilder {

        public DirectivesRequest build(Node envelopeNode) throws Exception {
            SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
            return sectionRequest != null ? (DirectivesRequest) sectionRequest : null;
        }

        @Override
        public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
            Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, SECTION_NAME);
            if (sectionElement != null) {
                return new DirectivesRequest(sectionElement);
            } else
                return null;
        }
    }

    public Boolean getTradeConfigChangedSuccess() {
        return tradeConfigChangedSuccess;
    }
}
