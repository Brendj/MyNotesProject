/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menus.calendar;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * Created by i.semenov on 14.06.2018.
 */
public class MenusCalendarRequest implements SectionRequest {
    public static final String SECTION_NAME="MenusCalendarRequest";
    private final Long maxVersion;
    private final Long idOfOrgOwner;

    public MenusCalendarRequest(Node menusCalendarRequestNode, Long orgOwner) {
        maxVersion = XMLUtils.getLongAttributeValue(menusCalendarRequestNode, "V");
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
