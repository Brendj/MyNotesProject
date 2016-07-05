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
public class GoodsBasicBasketRequest implements SectionRequest {

    public static final String SECTION_NAME = "GoodsBasicBasketDataRequest";

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    @Override
    public String toString() {
        return SECTION_NAME;
    }

    public static class Builder implements SectionRequestBuilder {

        public GoodsBasicBasketRequest build(Node envelopeNode) throws Exception {
            SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
            return sectionRequest != null ? (GoodsBasicBasketRequest) sectionRequest : null;
        }

        @Override
        public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
            Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, SECTION_NAME);
            if (sectionElement != null) {
                return new GoodsBasicBasketRequest();
            } else
                return null;
        }
    }
}
