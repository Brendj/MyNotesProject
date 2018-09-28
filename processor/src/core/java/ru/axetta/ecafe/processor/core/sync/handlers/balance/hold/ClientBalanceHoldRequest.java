/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.balance.hold;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

public class ClientBalanceHoldRequest implements SectionRequest {
    public static final String SECTION_NAME="ClientBalanceHoldRequest";
    private final Long maxVersion;
    private final Long orgOwner;

    public ClientBalanceHoldRequest(Node clientBalanceHoldRequestNode, Long orgOwner) {
        maxVersion = XMLUtils.getLongAttributeValue(clientBalanceHoldRequestNode, "V");
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
