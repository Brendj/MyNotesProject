/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.request.registry.accounts;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * User: shamil
 * Date: 21.05.15
 * Time: 10:07
 */
public class AccountsRegistryRequestBuilder implements SectionRequestBuilder{

    public AccountsRegistryRequest build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest!=null? (AccountsRegistryRequest) sectionRequest :null;
    }

@Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, AccountsRegistryRequest.SYNC_NAME);
        if (sectionElement != null) {
            return AccountsRegistryRequest.build(sectionElement);
        } else
            return null;
    }
}
