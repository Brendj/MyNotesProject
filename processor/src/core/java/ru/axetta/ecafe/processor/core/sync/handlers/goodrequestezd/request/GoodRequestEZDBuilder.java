/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.goodrequestezd.request;

import ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request.OrgSettingsRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

public class GoodRequestEZDBuilder implements SectionRequestBuilder {
    public GoodRequestEZDBuilder(){
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, GoodRequestEZDRequest.SECTION_NAME);
        if (sectionElement != null) {
            return new GoodRequestEZDRequest(sectionElement);
        } else
            return null;
    }
}
