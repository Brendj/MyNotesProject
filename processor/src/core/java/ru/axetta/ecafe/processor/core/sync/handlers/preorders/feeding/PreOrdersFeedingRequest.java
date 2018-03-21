/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

public class PreOrdersFeedingRequest implements SectionRequest {
    public static final String SECTION_NAME="PreOrdersFeedingRequest";
    private final Long maxVersion;

    public PreOrdersFeedingRequest(Node PreOrdersFeedingRequestNode) {
        maxVersion = XMLUtils.getLongAttributeValue(PreOrdersFeedingRequestNode, "V");
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }
}
