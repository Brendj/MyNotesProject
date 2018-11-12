/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.dtiszn;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

public class ClientDiscountsDTSZNRequest implements SectionRequest {
    public static final String SECTION_NAME="ClientDiscountsDTSZNRequest";
    private final Long maxVersion;
    private final Long idOfClient;
    private final Long orgOwner;

    public ClientDiscountsDTSZNRequest(Node requestNode, Long orgOwner) {
        this.maxVersion = XMLUtils.getLongAttributeValue(requestNode, "V");
        this.idOfClient = XMLUtils.getLongAttributeValue(requestNode, "ClientId");
        this.orgOwner = orgOwner;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public Long getOrgOwner() {
        return orgOwner;
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }
}
