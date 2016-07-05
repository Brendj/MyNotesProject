/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.categories.discounts;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * User: akmukov
 * Date: 29.03.2016
 */
public class CategoriesDiscountsAndRulesBuilder implements SectionRequestBuilder{

    public CategoriesDiscountsAndRulesRequest build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest != null ? (CategoriesDiscountsAndRulesRequest) sectionRequest : null;
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, CategoriesDiscountsAndRulesRequest.SECTION_NAME);
        if (sectionElement != null) {
            return CategoriesDiscountsAndRulesRequest.build(sectionElement);
        } else
            return null;
    }
}
