/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menus.calendar;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * Created by i.semenov on 14.06.2018.
 */
public class MenusCalendarSupplierBuilder implements SectionRequestBuilder {
    public MenusCalendarSupplierBuilder() {

    }

    @Override
    public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
        Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, MenusCalendarSupplierRequest.SECTION_NAME);
        if (sectionElement != null) {
            return new MenusCalendarSupplierRequest(sectionElement);
        } else
            return null;
    }
}
