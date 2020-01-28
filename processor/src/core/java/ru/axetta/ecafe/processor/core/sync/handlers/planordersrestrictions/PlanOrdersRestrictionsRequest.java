/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.planordersrestrictions;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * Created by i.semenov on 28.01.2020.
 */
public class PlanOrdersRestrictionsRequest implements SectionRequest {
    public static final String SECTION_NAME="PlanOrdersRestrictions";
    private final Long maxVersion;
    private final Long orgOwner;

    public PlanOrdersRestrictionsRequest(Node planOrdersRestrictionsRequestNode, Long orgOwner) {
        maxVersion = XMLUtils.getLongAttributeValue(planOrdersRestrictionsRequestNode, "V");
        this.orgOwner = orgOwner;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public Long getOrgOwner() {
        return orgOwner;
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }
}
