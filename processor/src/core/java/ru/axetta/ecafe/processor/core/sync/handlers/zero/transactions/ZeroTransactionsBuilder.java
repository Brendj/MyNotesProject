/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.zero.transactions;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 25.03.16
 * Time: 17:43
 * To change this template use File | Settings | File Templates.
 */
public class ZeroTransactionsBuilder implements SectionRequestBuilder{
    private final long idOfOrg;

    public ZeroTransactionsBuilder(long idOfOrg){
        this.idOfOrg = idOfOrg;
    }

    public ZeroTransactions build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest!=null? (ZeroTransactions) sectionRequest :null;
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, ZeroTransactions.SECTION_NAME);
        if (sectionElement != null) {
            return new ZeroTransactions(sectionElement, idOfOrg);
        } else
            return null;
    }
}
