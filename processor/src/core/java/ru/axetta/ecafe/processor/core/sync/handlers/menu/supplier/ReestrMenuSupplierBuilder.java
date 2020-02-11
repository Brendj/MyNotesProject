/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menu.supplier;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 10.02.20
 * Time: 12:59
 * To change this template use File | Settings | File Templates.
 */

public class ReestrMenuSupplierBuilder implements SectionRequestBuilder {
    //private final long owner;

    public ReestrMenuSupplierBuilder(){
        //this.owner = owner;
    }

    public ReestrMenuSupplier build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest != null ? (ReestrMenuSupplier) sectionRequest : null;
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, ReestrMenuSupplier.SECTION_NAME);
        if (sectionElement != null) {
            return new ReestrMenuSupplier(sectionElement);
        } else
            return null;
    }
}
