/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.foodBox;

import org.w3c.dom.Node;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

public class FoodBoxBuilder implements SectionRequestBuilder {

    public FoodBoxBuilder(){

    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, FoodBoxRequest.SECTION_NAME);
        if (sectionElement != null) {
            return new FoodBoxRequest(sectionElement);
        } else
            return null;
    }
}
