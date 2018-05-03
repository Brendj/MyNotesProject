/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.card.request;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;


/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 25.03.16
 * Time: 13:44
 * To change this template use File | Settings | File Templates.
 */
public class CardRequests implements SectionRequest{
    public static final String SECTION_NAME="CardRequests";
    private final Long maxVersion;
    private final Long idOfOrgOwner;

    public CardRequests(Node cardRequestsRequestNode, Long orgOwner) {
        maxVersion = XMLUtils.getLongAttributeValue(cardRequestsRequestNode, "V");
        idOfOrgOwner = orgOwner;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public Long getIdOfOrgOwner() {
        return idOfOrgOwner;
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }
}
