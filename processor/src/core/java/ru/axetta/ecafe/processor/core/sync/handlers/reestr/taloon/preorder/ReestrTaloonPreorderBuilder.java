/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.preorder;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 16.02.19
 * Time: 12:22
 * To change this template use File | Settings | File Templates.
 */
public class ReestrTaloonPreorderBuilder implements SectionRequestBuilder{
    private final long owner;

    public ReestrTaloonPreorderBuilder(long owner){
        this.owner = owner;
    }

    public ReestrTaloonPreorder build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest != null ? (ReestrTaloonPreorder) sectionRequest : null;
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, ReestrTaloonPreorder.SECTION_NAME);
        if (sectionElement != null) {
            return new ReestrTaloonPreorder(sectionElement, owner);
        } else
            return null;
    }
}
