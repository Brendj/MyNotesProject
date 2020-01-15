/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.goodrequestezd.request;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes.OrgSettingsDataTypes;
import ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request.OrgSettingItemSyncPOJO;
import ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request.OrgSettingSyncPOJO;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;

public class GoodRequestEZDRequest implements SectionRequest {

    public static final String SECTION_NAME = "GoodRequestEZDRequest";
    private Long maxVersion;

    public GoodRequestEZDRequest(Node sectionElement) {
        this.maxVersion = XMLUtils.getLongAttributeValue(sectionElement, "V");
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(Long maxVersion) {
        this.maxVersion = maxVersion;
    }

}
