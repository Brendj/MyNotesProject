/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding.status;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * Created by nuc on 14.02.2020.
 */
public class PreorderFeedingStatusRequest implements SectionRequest {
    public static final String SECTION_NAME="PreOrdersFeedingStatus";
    private final Long maxVersion;
    private final Long orgOwner;

    public PreorderFeedingStatusRequest(Node preordersFeedingStatusRequestNode, Long orgOwner) {
        maxVersion = XMLUtils.getLongAttributeValue(preordersFeedingStatusRequestNode, "V");
        this.orgOwner = orgOwner;
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public Long getOrgOwner() {
        return orgOwner;
    }
}
