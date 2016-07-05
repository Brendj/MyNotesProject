/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.special.dates;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 13.04.16
 * Time: 11:37
 */
public class SpecialDatesBuilder implements SectionRequestBuilder{
    private final long idOfOrg;

    public SpecialDatesBuilder(long idOfOrg){

        this.idOfOrg = idOfOrg;
    }
    public SpecialDates build(Node envelopeNode) throws Exception {
        SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
        return sectionRequest != null ? (SpecialDates) sectionRequest : null;
    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, SpecialDates.SECTION_NAME);
        if (sectionElement != null) {
            return new SpecialDates(sectionElement, idOfOrg);
        } else
            return null;
    }
}
