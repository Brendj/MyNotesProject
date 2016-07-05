/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 15.02.16
 * Time: 12:22
 * To change this template use File | Settings | File Templates.
 */
public class ReestrTaloonApprovalBuilder implements SectionRequestBuilder{
    private final long owner;

    public ReestrTaloonApprovalBuilder(long owner){
        this.owner = owner;
    }

    public ReestrTaloonApproval build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest != null ? (ReestrTaloonApproval) sectionRequest : null;
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, ReestrTaloonApproval.SECTION_NAME);
        if (sectionElement != null) {
            return new ReestrTaloonApproval(sectionElement, owner);
        } else
            return null;
    }
}
